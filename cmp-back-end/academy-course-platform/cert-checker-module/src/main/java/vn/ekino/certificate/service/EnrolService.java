package vn.ekino.certificate.service;

import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.UserManager;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import vn.ekino.certificate.dto.EnrolDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.SemesterDto;
import vn.ekino.certificate.dto.enumeration.EnrolStatus;
import vn.ekino.certificate.dto.enumeration.ParticipantStatus;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.SemesterRepository;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class EnrolService {
    private final EnrolProgramRepository enrolProgramRepository;
    private final UserRepository userRepository;
    private final PublishingService publishingService;
    private final ProgramService programService;
    private final SemesterRepository semesterRepository;

    private static final String ROLE = "academy-user-role";

    @Inject
    public EnrolService(EnrolProgramRepository enrolProgramRepository, UserRepository userRepository, PublishingService publishingService, ProgramService programService, SemesterRepository semesterRepository) {
        this.enrolProgramRepository = enrolProgramRepository;
        this.userRepository = userRepository;
        this.publishingService = publishingService;
        this.programService = programService;
        this.semesterRepository = semesterRepository;
    }

    public int enrolProgram(EnrolDto model) {
        UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
        User user = userManager.getUser(model.getEmail());

        if (user == null) {
            user = userManager.createUser("/admin", model.getEmail(), model.getPassword());
            userManager.setProperty(user, "enabled", String.valueOf(false));
            userManager.setProperty(user, "active", String.valueOf(false));
            userManager.setProperty(user, "email", model.getEmail());
            userManager.setProperty(user, "title", model.getUsername());
        } else if (enrolProgramRepository.findByProgramAndUserAndParticipantStatus(user.getIdentifier(), model.getProgramId(), ParticipantStatus.ON_GOING.getStatus()).isPresent()) {
            return HttpStatus.SC_ACCEPTED;
        }
        userManager.setProperty(user, "program", model.getProgramId());

        if (!user.hasRole(ROLE)) {
            userManager.addRole(user, ROLE);
        }

        String programName = programService.getProgramById(model.getProgramId()).map(ProgramDto::getNodeName).orElse("");

        String enrollType = Constants.Semester.FULL_PROGRAM;
        var currentLocalDate = LocalDateTime.now().toLocalDate();

        var semesters = semesterRepository.findByProgram(model.getProgramId()).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, SemesterDto.class).get())
                .sorted(Comparator.comparing(SemesterDto::getTitle))
                .collect(Collectors.toList());

        var currentSemester = semesters.stream()
                .filter(itm -> itm.getStartDate().toLocalDate().compareTo(currentLocalDate) <= 0
                        && itm.getEndDate().toLocalDate().compareTo(currentLocalDate) >= 0
                ).findFirst().get();

        if (Constants.Semester.SEMESTER_2.equals(currentSemester.getTitle())){
            enrollType = Constants.Semester.SEMESTER_2;
        }

        String nodeName = String
                .format("%s-%s-%s",
                        programName,
                        model.getEmail(),
                        LocalDateTime.now().format(TimeUtils.DATE_TIME_FORMATTER))
                .replace(" ", "-").replace(":", "-");


        Node node = enrolProgramRepository.findByProgramUser(user.getIdentifier(), model.getProgramId())
                .orElse(enrolProgramRepository.createNode(nodeName).get());
        try {
            PropertyUtil.setProperty(node, "program", model.getProgramId());
            PropertyUtil.setProperty(node, "user", user.getIdentifier());
            PropertyUtil.setProperty(node, "name", nodeName);
            PropertyUtil.setProperty(node, "enrollStatus", EnrolStatus.ENROLLED.getStatus());
            PropertyUtil.setProperty(node, "isParticipant", true);
            PropertyUtil.setProperty(node, "enrolDate", Calendar.getInstance());
            PropertyUtil.setProperty(node, "enrollType", enrollType);

        } catch (RepositoryException e) {
            log.warn("Can't set property because {}", e.getMessage());
            return HttpStatus.SC_UNPROCESSABLE_ENTITY;
        }
        enrolProgramRepository.save(node);

        List<Node> nodeList = new ArrayList<>();
        nodeList.add(userRepository.findById(user.getIdentifier()).get());
        nodeList.add(node);
        return publishingService.publish(nodeList) ? HttpStatus.SC_CREATED : HttpStatus.SC_UNPROCESSABLE_ENTITY;
    }
}
