package vn.ekino.certificate.service;

import com.beust.jcommander.internal.Lists;
import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.dto.CategoryDto;
import vn.ekino.certificate.dto.CourseCompulsoryDto;
import vn.ekino.certificate.dto.CourseDto;
import vn.ekino.certificate.dto.HomeworkDto;
import vn.ekino.certificate.dto.MaterialDto;
import vn.ekino.certificate.dto.PhaseDto;
import vn.ekino.certificate.dto.ProgramCourseDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.SemesterDto;
import vn.ekino.certificate.dto.SessionDto;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.model.data.CalendarData;
import vn.ekino.certificate.model.data.Course;
import vn.ekino.certificate.model.data.Phase;
import vn.ekino.certificate.model.data.Program;
import vn.ekino.certificate.model.data.Session;
import vn.ekino.certificate.repository.AttendanceRepository;
import vn.ekino.certificate.repository.CourseRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.SemesterRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.NodeUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class ProgramCourseService {

    private static final String JCR_UUID = JcrConstants.JCR_UUID;
    private static final String EMPTY_STRING = "";
    private static final String DURATION = "duration";
    private static final String COURSE_NAME = "courseName";

    private int totalCourseOfCategory;

    private final ProgramCourseRepository programCourseRepository;
    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;
    private final AttendanceRepository attendanceRepository;
    private final EnrolProgramService enrolProgramService;
    private final PhaseRepository phaseRepository;
    private final SemesterRepository semesterRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    List<String> courseInformation = new ArrayList<>();
    String semesterTitle;
    Course course;

    private final NodeNameHelper nodeNameHelper;

    private User currentUser;

    @Inject
    public ProgramCourseService(ProgramCourseRepository programCourseRepository,
                                CourseRepository courseRepository,
                                ProgramRepository programRepository,
                                AttendanceRepository attendanceRepository, EnrolProgramService enrolProgramService,
                                PhaseRepository phaseRepository,
                                SemesterRepository semesterRepository,
                                EnrolProgramRepository enrolProgramRepository,
                                NodeNameHelper nodeNameHelper) {
        this.programCourseRepository = programCourseRepository;
        this.courseRepository = courseRepository;
        this.programRepository = programRepository;
        this.attendanceRepository = attendanceRepository;
        this.enrolProgramService = enrolProgramService;
        this.phaseRepository = phaseRepository;
        this.semesterRepository = semesterRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.nodeNameHelper = nodeNameHelper;
    }

    public User getCurrentUser() {
        return this.currentUser == null ? MgnlContext.getUser() : this.currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<CourseCompulsoryDto> getCurrentCoursesOfUser() {

        return getCurrentProgramOfUser()
                .map(program -> findAllCourseCompulsoryByProgramId(program.getUuid()))
                .orElse(Collections.emptyList());
    }

    public List<CourseCompulsoryDto> getCurrentCourseCompulsoriesOfUserByCategory(String categoryId, int offset, int limit) {

        return getCurrentProgramOfUser()
                .map(program -> getListCourseCompulsoryInProgramByCategory(program.getUuid(), categoryId, offset, limit))
                .orElse(Collections.emptyList());
    }

    public Optional<ProgramDto> getCurrentProgramOfUser() {
        return enrolProgramService.getListApprovedEnrolProgramByUserId(getCurrentUser().getIdentifier())
                .stream()
                .sorted(Comparator.comparing(ProgramDto::getPhaseStartDate).reversed())
                .filter(program -> {
                    PhaseDto phaseDto = program.getPhase();
                    if (phaseDto != null) {
                        return phaseDto.getStartDate() != null && phaseDto.getEndDate() != null;
                    }
                    return false; }).findFirst();

    }

    public List<ProgramDto> getListProgramOfCurrentUser() {
        return enrolProgramService.getListApprovedEnrolProgramByUserId(getCurrentUser().getIdentifier());
    }

    public List<ProgramDto> getListProgramOfParticipant() {
        return enrolProgramService.getListEnrolProgramsOfParticipantByUserId(getCurrentUser().getIdentifier());
    }

    public List<ProgramCourseDto> getProgramCoursesOfUserByPhase(String userId, String phaseId) {
        boolean isTrainer = getCurrentUser().getAllRoles().contains(Constants.TRAINER_ROLE);
        boolean isSupervisor = getCurrentUser().getAllRoles().contains(Constants.SUPERVISOR_ROLE);
        List<ProgramCourseDto> result = new ArrayList<>();
        try {
            programCourseRepository.findAll().forEach(programCourseNode -> {
                ProgramCourseDto programCourse = MapperUtils.nodeToObject(programCourseNode, ProgramCourseDto.class).get();
                List<Node> courseCompulsoryNodes = NodeUtils.getSubNodes(programCourseNode);

                courseCompulsoryNodes.forEach(courseCompulsoryNode -> {
                    CourseCompulsoryDto courseCompulsory = MapperUtils.nodeToObject(courseCompulsoryNode, CourseCompulsoryDto.class).get();
                    List<Node> sessionNodes = NodeUtils.getSubNodes(courseCompulsoryNode);
                    for (Node sessionNode : sessionNodes) {
                        if (isTrainer && !isSupervisor) {
                            if (userId.equals(PropertyUtil.getString(sessionNode, "trainer"))) {
                                MapperUtils.nodeToObject(sessionNode, SessionDto.class).ifPresent(sessionDto -> courseCompulsory.getSessions().add(sessionDto));
                            }
                        } else if (isSupervisor) {
                            MapperUtils.nodeToObject(sessionNode, SessionDto.class).ifPresent(sessionDto -> courseCompulsory.getSessions().add(sessionDto));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(courseCompulsory.getSessions())) {
                        programCourse.getCourseList().add(courseCompulsory);
                    }
                });
                result.add(programCourse);

            });
        } catch (Exception e) {
            log.error("exception in program: {}", e.getMessage());
        }

        return result.stream()
                .filter(programCourseDto -> (phaseId).equals(programCourseDto.getProgram().getPhase().getUuid()))
                .collect(Collectors.toList());
    }

    public List<String> getCourseInformation() {
        var optionalDto = getCurrentProgramOfUser();
        optionalDto.ifPresent(this::setCourseInformation);
        return this.courseInformation;
    }

    private void setCourseInformation(ProgramDto program) {
        List<String> courseInfo = new ArrayList<>();
        courseInfo.add(program.getPhase() != null ? program.getPhase().getNodeName()
                : "");
        courseInfo.add(program.getGroup() != null ? program.getGroup().getDisplayName()
                : "");

        String fullProgramDateRange = program.getPhase().getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL) + " - " + program.getPhase().getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
        courseInfo.add(fullProgramDateRange);

        String semester1DateRange = "";
        String semester2DateRange = "";

        var semesterList = semesterRepository.findByProgram(program.getUuid()).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, SemesterDto.class).get())
                .sorted(Comparator.comparing(SemesterDto::getTitle))
                .collect(Collectors.toList());

        if (semesterList.size() > 1) {
            SemesterDto semester1Dto = semesterList.get(0);
            SemesterDto semester2Dto = semesterList.get(1);
            semester1DateRange = String.format("%s - %s",
                    semester1Dto.getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL),
                    semester1Dto.getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
            semester2DateRange = String.format("%s - %s",
                    semester2Dto.getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL),
                    semester2Dto.getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
        }

        courseInfo.add(semester1DateRange);
        courseInfo.add(semester2DateRange);

//        var user = getCurrentUser();
//        String fullProgramDateRange = program.getPhase().getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL) + " - " + program.getPhase().getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
//        var enrolProgramDto = enrolProgramRepository.getAllByUserApproved(user.getIdentifier())
//                .stream()
//                .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get())
//                .filter(itm -> startDatePhase == itm.getProgram().getPhase().getStartDate().getYear())
//                .findFirst().orElse(null);
//        var enrolDate = enrolProgramDto.getEnrollDate().toLocalDate();
//        var currentLocalDate = LocalDate.now();
//        var semesterList = semesterRepository.findByProgram(program.getUuid()).stream()
//                .map(itm -> MapperUtils.nodeToObject(itm, SemesterDto.class).get())
//                .sorted(Comparator.comparing(SemesterDto::getTitle))
//                .collect(Collectors.toList());
//        var semesterOptional = semesterList.stream()
//                .filter(itm -> itm.getStartDate().toLocalDate().compareTo(currentLocalDate) <= 0
//                        && itm.getEndDate().toLocalDate().compareTo(currentLocalDate) >= 0
//                ).findFirst();
//
//        var listNode = programCourseRepository.findByProgramId(program.getUuid())
//                .map(NodeUtils::getSubNodes).get();
//
//        if (semesterOptional.isPresent()) {
//            var semesterDto = semesterOptional.get();
//            this.semesterTitle = semesterDto.getTitle();
//            dateRange = String.format("%s: %s - %s", semesterDto.getTitle(),
//                    semesterDto.getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL),
//                    semesterDto.getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
//            if (Constants.Semester.SEMESTER_2.equals(semesterDto.getTitle()) && semesterList.size() > 1) {
//                var semester1 = semesterList.get(0);
//                if (enrolDate.compareTo(semester1.getStartDate().toLocalDate()) < 0) {
//                    dateRange = program.getPhase().getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL) + " - " + program.getPhase().getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
//                }
//            }
//        }
//        courseInfo.add(dateRange);
//
//
//        courseInfo.add(program.getPhase().getStartDate() != null
//                ? DateFormat.getDateInstance().format(TimeUtils.toCalendar(program.getPhase().getStartDate()).getTime())
//                : "");
//        courseInfo.add(program.getPhase().getEndDate()
//                != null ? DateFormat.getDateInstance().format(TimeUtils.toCalendar(program.getPhase().getEndDate()).getTime())
//                : "");

        this.courseInformation = courseInfo;
    }

    private LocalDateTime getCurrentDate() {
        return TimeUtils.toLocalDateTime(Calendar.getInstance());
    }

//    public Map<String, Course> getTotalInfo() {
//        Map<String, Course> map = new HashMap<>();
//        String programId = getCurrentProgramOfUser().get().getUuid();
//        var listNode = programCourseRepository.findByProgramId(programId)
//                .map(NodeUtils::getSubNodes).get();
//        int totalHour = 0;
//        int totalCourse1 = 0, totalHour1 = 0;
//        int totalCourse2 = 0, totalHour2 = 0;
//        for (Node node : listNode) {
//            String semester = PropertyUtil.getString(node, "semester");
//            if (Constants.Semester.SEMESTER_1.equals(semester)) {
//                totalHour1 += NodeUtils.getSubNodes(node).size();
//                totalCourse1++;
//            }
//            if (Constants.Semester.SEMESTER_2.equals(semester)) {
//                totalHour2 += NodeUtils.getSubNodes(node).size();
//                totalCourse2++;
//            }
//            totalHour += NodeUtils.getSubNodes(node).size();
//        }
//        map.put("All", Course.builder().totalCourse(listNode.size()).totalHours(totalHour).build());
//        map.put(Constants.Semester.SEMESTER_1, Course.builder().totalCourse(totalCourse1).totalHours(totalHour1).build());
//        map.put(Constants.Semester.SEMESTER_2, Course.builder().totalCourse(totalCourse2).totalHours(totalHour2).build());
//        return map;
//    }

    public int getHoursOfCourse(String courseId) {
        int hours = 0;
        String programId = getCurrentProgramOfUser().get().getUuid();
        var listNode = programCourseRepository.findByProgramId(programId)
                .map(NodeUtils::getSubNodes).get();
        for (Node node : listNode) {
            String courseNameValue = PropertyUtil.getString(node, "courseName");
            if (courseNameValue.equals(courseId)) {
                hours += NodeUtils.getSubNodes(node).size();
                return hours;
            }
        }
        return hours;
    }
//    public long getTotalCoursesOfProgram() {
//        return getCurrentProgramOfUser().map(program -> getTotalCoursesOfProgram(program))
//                .orElse(0l);
//    }
//
//    private long getTotalCoursesOfProgram(ProgramDto program) {
//        return programCourseRepository.findByProgramId(program.getUuid())
//                .map(programCourseNode -> NodeUtils.getSubNodes(programCourseNode).size())
//                .orElse(0);
//
//    }

//    public long getTotalHoursOfProgram() {
//        return this.course.getTotalHours();
//    }

    public List<SelectFieldOptionDefinition> getCourseSelectOptions(Date selectedDate) {
        Map<String, String> mapCourse = programCourseRepository.findAllCourseAndTypeBySessionDate(selectedDate);
        if (mapCourse.isEmpty())
            return Collections.emptyList();
        List<SelectFieldOptionDefinition> options = Lists.newArrayList();
        for (Map.Entry<String, String> courseId : mapCourse.entrySet()) {
            SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
            optionDefinition.setValue(courseId.getKey());
            optionDefinition.setLabel(courseId.getValue());
            options.add(optionDefinition);
        }
        return options;
    }

    public List<SelectFieldOptionDefinition> getSessionSelectOptions(Date selectedDate, String courseUUID) {
        Map<String, String> sessions = programCourseRepository.findAllSessionByCourse(selectedDate, courseUUID);
        if (!sessions.isEmpty()) {
            List<SelectFieldOptionDefinition> options = new ArrayList<>();
            for (Map.Entry<String, String> session : sessions.entrySet()) {
                SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
                optionDefinition.setValue(session.getKey());
                optionDefinition.setLabel(session.getValue());
                options.add(optionDefinition);
            }

            return options;
        }

        return Collections.emptyList();
    }

    private List<CourseCompulsoryDto> findAllCourseCompulsoryByProgramId(String programId) {

        return programCourseRepository.findByProgramId(programId).map(programCourseNode ->
                NodeUtils.getSubNodes(programCourseNode)
                        .stream()


                        .map(node -> MapperUtils.nodeToObject(node, CourseCompulsoryDto.class).get())
                        .collect(Collectors.toList())
        ).orElse(Collections.emptyList());
    }

    private List<CourseCompulsoryDto> getListCourseCompulsoryInProgramByCategory(String programId, String categoryId, int offset, int limit) {
        List<CourseCompulsoryDto> result = new ArrayList<>();
        programCourseRepository.findByProgramId(programId).ifPresent(programCourseNode -> {
            List<Node> courseNodes = courseRepository.findByIdsAndCategory(getCourseIdsOfCurrentProgram(programCourseNode), categoryId);

            setTotalCourseOfCategory(courseNodes.size());

            List<String> courseIds = getIdsByNodes(courseNodes, offset, limit);

            result.addAll(NodeUtils.getSubNodes(programCourseNode)
                    .stream()
                    .filter(courseCompulsoryNode ->

                            StringUtils.isNotEmpty(PropertyUtil.getString(courseCompulsoryNode, COURSE_NAME, EMPTY_STRING))
                                    && courseIds.contains(PropertyUtil.getString(courseCompulsoryNode, COURSE_NAME, EMPTY_STRING))
                    )
                    .map(node -> MapperUtils.nodeToObject(node, CourseCompulsoryDto.class).get())
                    .collect(Collectors.toList()));
        });

        return result;
    }

    private List<String> getIdsByNodes(List<Node> courseNodes, int offset, int limit) {
        return courseNodes
                .stream()
                .map(node -> PropertyUtil.getString(node, JCR_UUID, EMPTY_STRING))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<String> getCourseIdsOfCurrentProgram(Node programCourseNode) {
        return NodeUtils.getSubNodes(programCourseNode)
                .stream()
                .map(courseCompulsoryNode
                        -> PropertyUtil.getString(courseCompulsoryNode, COURSE_NAME, EMPTY_STRING))
                .collect(Collectors.toList());
    }

    public Optional<CourseCompulsoryDto> findCourseCompulsoryById(String uuid) {
        return programCourseRepository.findCourseCompulsoryNodeById(uuid)
                .map(courseCompulsoryNode -> {
                    CourseCompulsoryDto result = MapperUtils.nodeToObject(courseCompulsoryNode, CourseCompulsoryDto.class)
                            .map(courseCompulsory -> {
                                String courseId = PropertyUtil.getString(courseCompulsoryNode, COURSE_NAME, EMPTY_STRING);

                                return courseRepository.findById(courseId)
                                        .map(courseNode -> {
                                            CourseDto course = courseCompulsory.getCourseDetail();
                                            updateMaterialLinks(course, courseNode);
                                            updateHomeworkLinks(course, courseNode);
                                            courseCompulsory.setSessions(toSessions(NodeUtils.getSubNodes(courseCompulsoryNode)));
                                            return courseCompulsory;
                                        })
                                        .orElse(courseCompulsory);
                            })
                            .orElse(null);
                    return result;
                });
    }

    private List<SessionDto> toSessions(List<Node> sessionNodes) {
        return sessionNodes.stream()
                .map(sessionNode ->
                        MapperUtils
                                .nodeToObject(sessionNode, SessionDto.class)
                                .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void updateHomeworkLinks(CourseDto course, Node courseNode) {
        NodeUtils.getChildNode(courseNode, "homeworkLinks").ifPresent(homeworkLinksNode ->
                course.setHomeworkLinks(NodeUtils.getSubNodes(homeworkLinksNode)
                        .stream()
                        .map(node -> MapperUtils.nodeToObject(node, HomeworkDto.class).get())
                        .collect(Collectors.toList()))
        );
    }

    private void updateMaterialLinks(CourseDto course, Node courseNode) {
        NodeUtils.getChildNode(courseNode, "materialLinks").ifPresent(materialLinksNode ->
                course.setMaterialLinks(NodeUtils.getSubNodes(materialLinksNode)
                        .stream()
                        .map(node -> MapperUtils.nodeToObject(node, MaterialDto.class).get())
                        .collect(Collectors.toList()))
        );
    }

    public List<CourseCompulsoryDto> getListPrerequisiteClassesByCourseCompulsoryId(String courseCompulsoryId) {
        return programCourseRepository.findCourseCompulsoryNodeById(courseCompulsoryId)
                .map(courseCompulsoryNode ->
                        NodeUtils.getParentNode(courseCompulsoryNode)
                                .map(programCourseNode ->
                                        getListPrerequisiteClasses(NodeUtils.getSubNodes(programCourseNode),
                                                getPrerequisiteCourseIds(courseCompulsoryNode))
                                ).orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());
    }

    private List<CourseCompulsoryDto> getListPrerequisiteClasses(List<Node> courseCompulsoryNodes, List<String> courseIds) {
        return courseCompulsoryNodes
                .stream()
                .filter(node -> courseIds.contains(PropertyUtil.getString(node, "courseName", EMPTY_STRING)))
                .map(node -> MapperUtils.nodeToObject(node, CourseCompulsoryDto.class).get())
                .peek(itm -> itm.setReadMoreLink(nodeNameHelper.getValidatedName(itm.getCourseDetail().getNodeName())))
                .collect(Collectors.toList());
    }

    private List<String> getPrerequisiteCourseIds(Node courseCompulsoryNode) {
        CourseCompulsoryDto courseCompulsory = MapperUtils.nodeToObject(courseCompulsoryNode, CourseCompulsoryDto.class).get();
        return courseCompulsory.getCourseDetail()
                .getPrerequisites()
                .stream()
                .map(CourseDto::getUuid)
                .collect(Collectors.toList());
    }

    public Optional<ProgramDto> getProgramInformation(String courseCompulsoryId) {
        return programCourseRepository
                .findCourseCompulsoryNodeById(courseCompulsoryId)
                .flatMap(courseCompulsoryNode -> NodeUtils.getParentNode(courseCompulsoryNode)
                        .flatMap(programCourseNode ->
                                programRepository
                                        .findById(PropertyUtil.getString(programCourseNode, "program", EMPTY_STRING))
                                        .flatMap(programNode -> MapperUtils.nodeToObject(programNode, ProgramDto.class))
                        ));
    }

    public int getTotalCourseOfCategory() {
        return totalCourseOfCategory;
    }

    private void setTotalCourseOfCategory(int totalCourseOfCategory) {
        this.totalCourseOfCategory = totalCourseOfCategory;
    }

    public void getAllData4Schedule(Map<String, Object> map) {
        List<Phase> listPhase = new ArrayList<>((List<Phase>) map.get("listPhase"));
        listPhase.forEach(itm -> {
            List<ProgramDto> programDtoList = new ArrayList<>();
            phaseRepository.findAllByPhaseCategory(itm.getId())
                    .forEach(p -> {
                        String phaseId = PropertyUtil.getString(p, JcrConstants.JCR_UUID);
                        programDtoList.addAll(programRepository.findProgramByPhase(phaseId).stream()
                                .map(node -> MapperUtils.nodeToObject(node, ProgramDto.class).get())
                                .collect(Collectors.toList()));
                    });
            Map<CategoryDto, List<ProgramDto>> groupProgram = programDtoList.stream().collect(Collectors.groupingBy(ProgramDto::getGroup));
            List<Program> programs = groupProgram.entrySet().stream()
                    .map(this::mapProgram).collect(Collectors.toList());
            itm.setPrograms(programs);
        });
        map.put("objData", new Gson().toJson(CalendarData.builder().phases(listPhase)));
    }

    private Program mapProgram(Map.Entry<CategoryDto, List<ProgramDto>> entry) {
        return Program.builder()
                .id(entry.getKey().getUuid())
                .groupProgram(entry.getKey().getUuid())
                .title(entry.getKey().getDisplayName())
                .listSession(getListSession(entry.getValue()))
                .build();
    }

    private List<Session> getListSession(List<ProgramDto> list) {
        User user = MgnlContext.getUser();
        Collection<String> userRoles = user.getRoles();
        return list.stream()
                .map(itm -> {
                    var nodeCourse = programCourseRepository.findByProgramId(itm.getUuid())
                            .stream()
                            .map(NodeUtils::getSubNodes)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    List<Session> sessionList = new ArrayList<>();
                    nodeCourse.forEach(c -> {
                        var allSessions = NodeUtils.getSubNodes(c).stream()
                                .map(s -> MapperUtils.nodeToObject(s, SessionDto.class).get())
                                .collect(Collectors.toList());

                        var sessions = allSessions.stream()
                                .filter(s -> {
                                    if (userRoles.contains(Constants.TRAINER_ROLE) && !userRoles.contains(Constants.SUPERVISOR_ROLE)) {
                                        return user.getIdentifier().equals(Optional.ofNullable(s.getTrainer()).map(UserDto::getUuid).orElse(StringUtils.EMPTY));
                                    }
                                    return true;
                                })
                                .sorted(Comparator.comparing(SessionDto::getDate))
                                .collect(Collectors.toList());

                        for (int i = 0; i < sessions.size(); i++) {
                            var session = sessions.get(i);
                            boolean roleTrainer = user.getIdentifier().equals(session.getTrainer().getUuid());
                            String sessionName = MapperUtils.nodeToObject(c, CourseCompulsoryDto.class).get().getCourseDetail().getNodeName();
                            sessionList.add(mapSession(session, sessionName, session.getTitle(), roleTrainer));
                        }
                    });
                    return sessionList;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Session mapSession(SessionDto dto, String name, String session, boolean roleTrainer) {
        return Session.builder()
                .id(dto.getUuid())
                .name(nodeNameHelper.getValidatedName(name))
                .title(name)
                .date(TimeUtils.toString(dto.getDate().toLocalDate()))
                .session(session)
                .roleTrainer(roleTrainer)
                .build();
    }

}
