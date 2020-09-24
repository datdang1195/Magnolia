package vn.ekino.certificate.service;

import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.PhaseDto;
import vn.ekino.certificate.dto.SemesterDto;
import vn.ekino.certificate.dto.enumeration.EnrolStatus;
import vn.ekino.certificate.model.data.TitleInformation;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.SemesterRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class MyProgressService {
    private final PhaseRepository phaseRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    private final ParticipantService participantService;
    private final ProgramRepository programRepository;
    private final SemesterRepository semesterRepository;

    private User currentUser;

    @Inject
    public MyProgressService(PhaseRepository phaseRepository
            , EnrolProgramRepository enrolProgramRepository
            , ParticipantService participantService
            , ProgramRepository programRepository
            , SemesterRepository semesterRepository) {
        this.phaseRepository = phaseRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.participantService = participantService;
        this.programRepository = programRepository;
        this.semesterRepository = semesterRepository;
    }

    public User getCurrentUser() {
        return this.currentUser == null ? MgnlContext.getUser() : this.currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    public Map<String, Object> getMyProgressInformation(String year) {
        Map<String, Object> result = new HashMap<>();
        final LocalDateTime currentDate = LocalDateTime.now();
        var listPhase = phaseRepository.findAll().stream()
                .filter(itm -> year.equals(String.valueOf(PropertyUtil.getDate(itm, "startDate").get(Calendar.YEAR)))
                        || year.equals(String.valueOf(PropertyUtil.getDate(itm, "endDate").get(Calendar.YEAR))))
                .map(itm -> MapperUtils.nodeToObject(itm, PhaseDto.class).get())
                .sorted(Comparator.comparing(PhaseDto::getStartDate).reversed())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(listPhase)) {
            return result;
        }
        result.put("isOnlyEnrollSemester1", false);
        result.put("isOnlyEnrollSemester2", false);
        result.put("isFullProgram", false);
        PhaseDto phaseDto = null;
        if (listPhase.size() == 1) {
            phaseDto = listPhase.get(0);
        } else {
            for (PhaseDto itm : listPhase) {
                if (currentDate.compareTo(itm.getStartDate()) >= 0 && currentDate.compareTo(itm.getEndDate()) <= 0) {
                    if (checkPhaseHasApproveProgram(itm.getUuid())) {
                        phaseDto = itm;
                        break;
                    }
                }
            }
            if (phaseDto == null) {
                phaseDto = listPhase.get(listPhase.size() - 1);
            }
        }
        int startDatePhase = phaseDto.getStartDate().getYear();
        var enrolProgramDto = enrolProgramRepository.getAllByUserApproved(getCurrentUser().getIdentifier())
                .stream()
                .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get())
                .filter(itm -> startDatePhase == itm.getProgram().getPhase().getStartDate().getYear())
                .findFirst().orElse(null);
        if (enrolProgramDto == null) {
            return result;
        }
        String dateRange = phaseDto.getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL) + " - " + phaseDto.getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
        var programDto = enrolProgramDto.getProgram();
        var semesterList = semesterRepository.findByProgram(programDto.getUuid()).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, SemesterDto.class).get())
                .sorted(Comparator.comparing(SemesterDto::getTitle))
                .collect(Collectors.toList());
        result.put("semesterList", new Gson().toJson(semesterList));
        result.put("semesterLists", semesterList);

        String enrollType = enrolProgramDto.getEnrollType();
        if (Constants.Semester.SEMESTER_1.equals(enrollType)) {
            result.put("isOnlyEnrollSemester1", true);
        } else if (Constants.Semester.SEMESTER_2.equals(enrollType)) {
            result.put("isOnlyEnrollSemester2", true);
        } else {
            result.put("isFullProgram", true);
        }

        var titleInformation = new TitleInformation();
        titleInformation.setPhaseName(phaseDto.getNodeName());
        titleInformation.setProgramName(enrolProgramDto.getProgram().getGroup().getDisplayName());
        titleInformation.setDateRange(dateRange);
        titleInformation.setStatus(enrolProgramDto.getProgram().getStatus().getDisplayName());
        result.put("titleInformation", titleInformation);
        participantService.buildInformation(result, enrolProgramDto);
        return result;
    }

    private boolean checkPhaseHasApproveProgram(String phaseId) {
        return programRepository.findProgramByPhase(phaseId)
                .stream()
                .anyMatch(node -> enrolProgramRepository.findAllUserByProgram(PropertyUtil.getString(node, JcrConstants.JCR_UUID))
                        .stream()
                        .filter(itm -> getCurrentUser().getIdentifier().equals(PropertyUtil.getString(itm, "user")))
                        .anyMatch(itm -> EnrolStatus.APPROVED.getStatus().equals(PropertyUtil.getString(itm, "enrollStatus"))));
    }
}
