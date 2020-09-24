package vn.ekino.certificate.service;

import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.CalendarDto;
import vn.ekino.certificate.dto.CourseCompulsoryDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.SessionDto;
import vn.ekino.certificate.model.data.CalendarData;
import vn.ekino.certificate.repository.AttendanceRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.util.CommonUtils;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.NodeUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class CalendarService {

    private final ProgramCourseService programCourseService;
    private final ProgramCourseRepository programCourseRepository;
    private final AttendanceRepository attendanceRepository;
    private final CertificateServicesModule servicesModule;
    private final CategoryService categoryService;
    private final NodeNameHelper nodeNameHelper;

    private User currentUser;

    @Inject
    public CalendarService(ProgramCourseService programCourseService
            , ProgramCourseRepository programCourseRepository
            , AttendanceRepository attendanceRepository
            , CertificateServicesModule servicesModule
            , CategoryService categoryService
            , NodeNameHelper nodeNameHelper) {
        this.programCourseService = programCourseService;
        this.programCourseRepository = programCourseRepository;
        this.attendanceRepository = attendanceRepository;
        this.servicesModule = servicesModule;
        this.categoryService = categoryService;
        this.nodeNameHelper = nodeNameHelper;
    }

    public User getCurrentUser() {
        return currentUser == null ? MgnlContext.getUser() : currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getCalendar4Participant() {
        List<CalendarDto> calendarDtos = new ArrayList<>();

        CommonUtils.streamOf(this.getProgramCoursesAndUpdateSession())
                .forEach(c -> {
                    String id = c.getUuid();
                    String name = c.getCourseDetail().getNodeName();
                    CommonUtils.streamOf(c.getSessions())
                            .forEach(s -> {
                                CalendarDto calendarDto = new CalendarDto();
                                calendarDto.setId(id);
                                calendarDto.setName(Optional.ofNullable(nodeNameHelper.getValidatedName(name)).orElse("Course-not-found"));
                                calendarDto.setTitle(Optional.ofNullable(name).orElse("Course not found"));
                                calendarDto.setSession(s.getTitle());
                                calendarDto.setDate(TimeUtils.toString(s.getDate().toLocalDate()));
                                calendarDto.setAttendance(checkAttendance(s.getDate(), c.getCourseDetail().getUuid()));
                                calendarDto.setType(s.getType());
                                calendarDtos.add(calendarDto);
                            });
                });

        return new Gson().toJson(calendarDtos);
    }

    public Map<String, Object> getDataSchedule() {
        Map<String, Object> map = categoryService.getPhaseAndGroup();
        map.put("objData", new Gson().toJson(CalendarData.builder().phases(List.of())));
        List<String> roles = List.of(Constants.SUPERVISOR_ROLE, Constants.TRAINER_ROLE);
        if (getCurrentUser().getRoles().stream().anyMatch(roles::contains)) {
            programCourseService.getAllData4Schedule(map);
        }
        return map;
    }

    private List<CourseCompulsoryDto> getProgramCoursesAndUpdateSession() {
        List<CourseCompulsoryDto> result = new ArrayList<>();
        List<ProgramDto> programs = programCourseService.getListProgramOfParticipant();
        if (CollectionUtils.isNotEmpty(programs)) {
            programs.forEach(program ->
                    programCourseRepository
                            .findByProgramId(program.getUuid())
                            .ifPresent(programCourseNode ->
                                    NodeUtils.getSubNodes(programCourseNode)
                                            .forEach(courseCompulsoryNode ->
                                                    MapperUtils.nodeToObject(courseCompulsoryNode, CourseCompulsoryDto.class)
                                                            .ifPresent(courseCompulsory -> {
                                                                // update session for course.
                                                                courseCompulsory.setSessions(toSessionDtosInMonth(program,
                                                                        NodeUtils.getSubNodes(courseCompulsoryNode)).stream()
                                                                        .filter(sessionDto -> !(sessionDto.getTrainer().getUuid().equals(getCurrentUser().getIdentifier())))
                                                                        .collect(Collectors.toList()));
                                                                result.add(courseCompulsory);
                                                            })
                                            )
                            )
            );
        }
        return result;
    }

    private List<SessionDto> toSessionDtosInMonth(ProgramDto program, List<Node> sessionNodes) {
        return sessionNodes.stream()
                .map(sessionNode ->
                        MapperUtils.nodeToObject(sessionNode, SessionDto.class).orElse(null))
                .filter(Objects::nonNull)
                .filter(session -> Objects.nonNull(session.getDate()))
                .filter(session
                        -> session.getDate().isAfter(program.getPhase().getStartDate().minusDays(1))
                        && session.getDate().isBefore(program.getPhase().getEndDate().plusDays(1)))
                .collect(Collectors.toList());
    }

    private int checkAttendance(LocalDateTime dateTime, String courseId) {
        User user = MgnlContext.getUser();
        if (LocalDate.now().isBefore(dateTime.toLocalDate()) || !user.getAllRoles().contains(servicesModule.getParticipantRole())) {
            return -1;
        }
        String userId = user.getIdentifier();
        var result = attendanceRepository.findUserAttendance(TimeUtils.toString(dateTime.toLocalDate()), courseId);
        if (result.isPresent()) {
            var node = result.get();
            try {
                if (node.hasProperty("users")) {
                    var list = PropertyUtil.getValuesStringList(node.getProperty("users").getValues());
                    return list.contains(userId) ? 0 : 1;
                }
                return 1;
            } catch (RepositoryException e) {
                log.warn("can't get property because {}", e.getMessage());
                return -1;
            }
        }
        return -1;
    }
}
