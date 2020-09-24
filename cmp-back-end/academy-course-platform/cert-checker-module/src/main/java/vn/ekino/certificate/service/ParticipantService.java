package vn.ekino.certificate.service;

import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.jooq.lambda.Seq;
import vn.ekino.certificate.dto.AttendanceDto;
import vn.ekino.certificate.dto.AttitudeAssessmentDto;
import vn.ekino.certificate.dto.CategoryDto;
import vn.ekino.certificate.dto.CourseCompulsoryDto;
import vn.ekino.certificate.dto.CourseDto;
import vn.ekino.certificate.dto.CourseResultDto;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.GeneratedCertificateDto;
import vn.ekino.certificate.dto.OJTProjectDto;
import vn.ekino.certificate.dto.OJTUserAssessmentDto;
import vn.ekino.certificate.dto.OJTUserResultDto;
import vn.ekino.certificate.dto.PhaseDto;
import vn.ekino.certificate.dto.ProgramCourseDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.QuizDto;
import vn.ekino.certificate.dto.SemesterDto;
import vn.ekino.certificate.dto.SessionDto;
import vn.ekino.certificate.dto.UserAttitudeResultDto;
import vn.ekino.certificate.dto.UserEvaluationDto;
import vn.ekino.certificate.dto.enumeration.CourseStatus;
import vn.ekino.certificate.model.data.Course;
import vn.ekino.certificate.model.data.OjtProject;
import vn.ekino.certificate.model.data.OjtTraining;
import vn.ekino.certificate.model.data.Participant;
import vn.ekino.certificate.model.data.Phase;
import vn.ekino.certificate.model.data.Program;
import vn.ekino.certificate.model.data.Year;
import vn.ekino.certificate.repository.AttendanceRepository;
import vn.ekino.certificate.repository.AttitudeAssessmentRepository;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.CourseResultRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.GeneratedCertificateRepository;
import vn.ekino.certificate.repository.OJTProjectRepository;
import vn.ekino.certificate.repository.OJTUserAssessmentRepository;
import vn.ekino.certificate.repository.OJTUserResultRepository;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.SemesterRepository;
import vn.ekino.certificate.repository.UserAttitudeResultRepository;
import vn.ekino.certificate.repository.UserEvaluationRepository;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.NodeUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ParticipantService {
    private final ProgramCourseRepository programCourseRepository;
    private final AttendanceRepository attendanceRepository;
    private final OJTUserResultRepository ojtUserResultRepository;
    private final UserEvaluationRepository userEvaluationRepository;
    private final CourseResultRepository courseResultRepository;
    private final OJTUserAssessmentRepository ojtUserAssessmentRepository;
    private final UserAttitudeResultRepository userAttitudeResultRepository;
    private final AttitudeAssessmentRepository attitudeAssessmentRepository;
    private final GeneratedCertificateRepository generatedCertificateRepository;
    private final PhaseRepository phaseRepository;
    private final CategoryRepository categoryRepository;
    private final ProgramRepository programRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    private final OJTProjectRepository ojtProjectRepository;
    private final UserRepository userRepository;
    private final ProgramCourseService programCourseService;
    private final AuthenticateService authenticateService;
    private final SemesterRepository semesterRepository;
    private final NodeNameHelper nodeNameHelper;

    private User currentUser;

    @Inject
    public ParticipantService(ProgramCourseRepository programCourseRepository,
                              AttendanceRepository attendanceRepository, OJTUserResultRepository ojtUserResultRepository,
                              UserEvaluationRepository userEvaluationRepository, CourseResultRepository courseResultRepository,
                              OJTUserAssessmentRepository ojtUserAssessmentRepository,
                              UserAttitudeResultRepository userAttitudeResultRepository,
                              AttitudeAssessmentRepository attitudeAssessmentRepository,
                              GeneratedCertificateRepository generatedCertificateRepository, PhaseRepository phaseRepository,
                              CategoryRepository categoryRepository, ProgramRepository programRepository,
                              EnrolProgramRepository enrolProgramRepository, OJTProjectRepository ojtProjectRepository,
                              UserRepository userRepository, ProgramCourseService programCourseService,
                              AuthenticateService authenticateService, SemesterRepository semesterRepository,
                              NodeNameHelper nodeNameHelper) {
        this.programCourseRepository = programCourseRepository;
        this.attendanceRepository = attendanceRepository;
        this.ojtUserResultRepository = ojtUserResultRepository;
        this.userEvaluationRepository = userEvaluationRepository;
        this.courseResultRepository = courseResultRepository;
        this.ojtUserAssessmentRepository = ojtUserAssessmentRepository;
        this.userAttitudeResultRepository = userAttitudeResultRepository;
        this.attitudeAssessmentRepository = attitudeAssessmentRepository;
        this.generatedCertificateRepository = generatedCertificateRepository;
        this.phaseRepository = phaseRepository;
        this.categoryRepository = categoryRepository;
        this.programRepository = programRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.ojtProjectRepository = ojtProjectRepository;
        this.userRepository = userRepository;
        this.programCourseService = programCourseService;
        this.authenticateService = authenticateService;
        this.semesterRepository = semesterRepository;
        this.nodeNameHelper = nodeNameHelper;
    }

    public User getCurrentUser() {
        return currentUser == null ? MgnlContext.getUser() : currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    public String getListYear() {
        var result = phaseRepository.findAll().stream()
                .map(itm -> new Year(PropertyUtil.getDate(itm, "startDate").get(Calendar.YEAR)))
                .sorted(Comparator.comparingInt(Year::getYear)).distinct().collect(Collectors.toList());
        return new Gson().toJson(result);
    }

    public List<Phase> getListPhaseByYear(int year) {
        var phaseCategories = categoryRepository.findAllPhaseCategories().stream()
                .map(itm -> MapperUtils.nodeToObject(itm, CategoryDto.class).get()).collect(Collectors.toList());
        var listPhaseByYear = phaseRepository.findAll().stream()
                .filter(itm -> (year == PropertyUtil.getDate(itm, "startDate").get(Calendar.YEAR)
                        && TimeUtils.toLocalDateTime(PropertyUtil.getDate(itm, "startDate")).toLocalDate()
                        .isBefore(LocalDate.now()))
                        || (year == PropertyUtil.getDate(itm, "endDate").get(Calendar.YEAR)
                        && TimeUtils.toLocalDateTime(PropertyUtil.getDate(itm, "endDate")).toLocalDate()
                        .isAfter(LocalDate.now())))
                .map(itm -> MapperUtils.nodeToObject(itm, PhaseDto.class).get())
                .sorted(Comparator.comparing(PhaseDto::getStartDate).reversed()).collect(Collectors.toList());
        return getListPhase(phaseCategories, listPhaseByYear);
    }

    public void buildInformation(Map<String, Object> result, EnrolProgramDto enrolProgramDto) {
        var userEnrolProgramId = enrolProgramDto.getUuid();
        var courseSemesters1 = buildCourse(enrolProgramDto, Constants.Semester.SEMESTER_1);
        result.put("courseSemesters1", courseSemesters1);
        var courseSemesters2 = buildCourse(enrolProgramDto, Constants.Semester.SEMESTER_2);
        result.put("courseSemesters2", courseSemesters2);

        OjtTraining ojtTraining = new OjtTraining();
        String ojtUserResultId = StringUtils.EMPTY;
        var nodeOjtUserResult = ojtUserResultRepository.findByUserEnrolProgram(userEnrolProgramId);
        if (nodeOjtUserResult.isPresent()) {
            var ojtUserResultDto = MapperUtils.nodeToObject(nodeOjtUserResult.get(), OJTUserResultDto.class).get();
            ojtUserResultId = ojtUserResultDto.getUuid();
            var ojtProjectDto = ojtUserResultDto.getOjtProject();
            ojtTraining.setProjectName(ojtProjectDto != null ? ojtProjectDto.getProjectName() : "");
            ojtTraining.setRole(ojtUserResultDto.getRole().getDisplayName());
            ojtTraining.setStatus(ojtProjectDto != null ? ojtProjectDto.getProjectStatus().getDisplayName() : "");
            ojtTraining.setMentor(ojtUserResultDto.getMentor().getFullName());
            ojtTraining.setScore(ojtUserResultDto.getOjtEvaluation());
            ojtTraining.setComment(ojtUserResultDto.getComment());
            ojtTraining.setId(ojtProjectDto != null ? ojtProjectDto.getUuid() : "");
            ojtTraining.setUriName(ojtProjectDto != null ? ojtProjectDto.getUriName() : "");
        }
        result.put("ojtTraining", ojtTraining);

        var listSummary = userEvaluationRepository.findListByUserEnrolProgram(userEnrolProgramId)
                .stream()
                .map(itm -> MapperUtils.nodeToObject(itm, UserEvaluationDto.class).get())
                .collect(Collectors.toList());
        result.put("summary", null);
        result.put("summarySemester1", null);
        result.put("summarySemester2", null);
        for (UserEvaluationDto node : listSummary) {
            if (Constants.Semester.FULL_PROGRAM.equals(node.getSemester())) {
                result.put("summary", node);
            }
            if (Constants.Semester.SEMESTER_1.equals(node.getSemester())) {
                result.put("summarySemester1", node);
            }
            if (Constants.Semester.SEMESTER_2.equals(node.getSemester())) {
                result.put("summarySemester2", node);
            }
        }

        result.put("courseList", buildCourseResult(userEnrolProgramId));
        result.put("courseListString", new Gson().toJson(buildCourseResult(userEnrolProgramId)));

        List<OJTUserAssessmentDto> listOjtUserAssessment = new ArrayList<>();
        if (StringUtils.isNotEmpty(ojtUserResultId)) {
            listOjtUserAssessment = ojtUserAssessmentRepository.findByOjtUserResult(ojtUserResultId).stream()
                    .map(itm -> MapperUtils.nodeToObject(itm, OJTUserAssessmentDto.class).get())
                    .collect(Collectors.toList());
        }
        result.put("listOjtUserAssessment", listOjtUserAssessment);

        var userAttitudeResultDto = new UserAttitudeResultDto();
        Map<String, List<AttitudeAssessmentDto>> attitudeAssessmentList = new HashMap<>();
        var nodeUserAttitudeResult = userAttitudeResultRepository.findByUserEnrolProgram(userEnrolProgramId);
        if (nodeUserAttitudeResult.isPresent()) {
            userAttitudeResultDto = MapperUtils.nodeToObject(nodeUserAttitudeResult.get(), UserAttitudeResultDto.class)
                    .get();
            attitudeAssessmentList = attitudeAssessmentRepository
                    .findByAttitudeUserResult(userAttitudeResultDto.getUuid()).stream()
                    .map(itm -> MapperUtils.nodeToObject(itm, AttitudeAssessmentDto.class).get())
                    .collect(Collectors.groupingBy(AttitudeAssessmentDto::getAssessmentGroupName));
        }
        result.put("userAttitudeResult", userAttitudeResultDto);
        result.put("attitudeList", attitudeAssessmentList);
        String certPath = null;
        var nodeCertificate = generatedCertificateRepository.findByEnrolProgram(userEnrolProgramId);
        if (nodeCertificate.isPresent()) {
            GeneratedCertificateDto dto = MapperUtils.nodeToObject(nodeCertificate.get(), GeneratedCertificateDto.class)
                    .get();
            if (dto.getGeneratedFiles().size() > 0) {
                certPath = dto.getGeneratedFiles().get(0).getLink();
            }
        }
        result.put("certPath", certPath);
    }

    private List<CourseResultDto> buildCourseResult(String enrolProgramId){

        return courseResultRepository.findByUserEnrolProgram(enrolProgramId).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, CourseResultDto.class).get())
                .peek(itm -> {
                    var programCourseNodeOpt = programCourseRepository.findByProgramId(itm.getProgram().getProgram().getUuid());

                    if (programCourseNodeOpt.isPresent()) {
                        var programCourseNode = programCourseNodeOpt.get();
                        var courseNameId = itm.getCourse().getUuid();
                        var listNode = NodeUtils.getSubNodes(programCourseNode);
                        var semester = listNode.stream()
                                .filter(course -> courseNameId.equals(PropertyUtil.getString(course, "courseName")))
                                .findFirst();

                        List<QuizDto> results = new ArrayList<>();
                        var courseResultNodeOpt = courseResultRepository.findByCourseAndEnrolProgram(enrolProgramId, courseNameId);

                        String quizNum = "";
                        if(courseResultNodeOpt.isPresent()){
                            quizNum = PropertyUtil.getString(courseResultNodeOpt.get(), "numQuiz");
                        }

                        if (!NodeUtils.getSubNodes(courseResultNodeOpt.get()).isEmpty()){
                            var quizzes = NodeUtils.getSubNodes(courseResultNodeOpt.get())
                                    .stream()
                                    .findFirst()
                                    .get();
                            List<Node> quizzesList= NodeUtils.getSubNodes(quizzes);
                            if (quizzesList!=null){
                                quizzesList.forEach(node -> {
                                    results.add(MapperUtils.nodeToObject(node, QuizDto.class).get());
                                });
                            }
                        }

                        if (semester.isPresent()) {
                            itm.setSemester(PropertyUtil.getString(semester.get(), "semester"));
                        }
                        itm.setQuizzes(results);
                        if(quizNum != null) {
                            itm.setNumberOfQuizzes(Integer.parseInt(quizNum));
                        }
                    }
                }).collect(Collectors.toList());
    }

    private Course buildCourse(EnrolProgramDto enrolProgramDto, String semesterTitle) {
        var course = new Course();
        if (enrolProgramDto == null) {
            return course;
        }
        boolean haveCancelDate = enrolProgramDto.getCancelDate() != null;

        var nodeProgramCourse = programCourseRepository.findByProgramId(enrolProgramDto.getProgram().getUuid());
        if (nodeProgramCourse.isPresent()) {
            var programCourse = buildProgramCourse(nodeProgramCourse.get());
            var listCourse = programCourse.getCourseList().stream()
                    .filter(itm -> itm.getSemester() == null || semesterTitle.equals(itm.getSemester()))
                    .collect(Collectors.toList());
            int completed = 0, inProgress = 0, todo = 0, totalHours = 0, attended = 0, absent = 0, inProgressHours = 0;
            for (CourseCompulsoryDto itm : listCourse) {
                var listSession = itm.getSessions();
                var courseStatus = itm.getCourseStatus().getDisplayName();
                boolean haveSession = listSession.size() > 0;
                if (CourseStatus.FINISHED.getDisplayName().equals(courseStatus)) {
                    if (haveSession) {
                        if (!haveCancelDate || listSession.get(0).getDate().toLocalDate()
                                .compareTo(enrolProgramDto.getCancelDate().toLocalDate()) < 0) {
                            completed++;
                        }
                    }
                } else if (CourseStatus.OPEN.getDisplayName().equals(courseStatus)) {
                    todo++;
                    for (SessionDto session : listSession) {
                        inProgressHours += Integer.valueOf(session.getDuration());
                    }
                } else {
                    inProgress++;
                }
                for (SessionDto session : listSession) {
                    totalHours += Integer.valueOf(session.getDuration());
                    var attendance = attendanceRepository.findUserAttendance(
                            TimeUtils.toString(session.getDate().toLocalDate()), itm.getCourseDetail().getUuid());
                    if (attendance.isPresent()) {
                        var attendanceDto = MapperUtils.nodeToObject(attendance.get(), AttendanceDto.class).get();
                        if (attendanceDto.getUsers().contains(enrolProgramDto.getUser().getUuid())) {
                            absent++;
                        } else {
                            if (!haveCancelDate || session.getDate().toLocalDate()
                                    .compareTo(enrolProgramDto.getCancelDate().toLocalDate()) < 0) {
                                attended++;
                            }
                        }
                    }
                }
            }
            course.setTotalCourse(listCourse.size());
            course.setCompleted(completed);
            course.setInProgress(inProgress);
            course.setTodo(todo);
            course.setTotalHours(totalHours);
            course.setAttended(attended);
            course.setAbsent(absent);
            course.setInProgressHours(inProgressHours);

            if (haveCancelDate && LocalDate.now().compareTo(enrolProgramDto.getCancelDate().toLocalDate()) >= 0) {
                course.setInProgress(-1);
                course.setTodo(-1);
                course.setInProgressHours(-1);
            }
        }
        return course;
    }

    private Course buildCourse4ProgramStatus(EnrolProgramDto enrolProgramDto, String semesterTitle) {
        var course = new Course();
        if (enrolProgramDto == null) {
            return course;
        }
        var nodeProgramCourse = programCourseRepository.findByProgramId(enrolProgramDto.getProgram().getUuid());
        if (nodeProgramCourse.isPresent()) {
            var programCourse = buildProgramCourse(nodeProgramCourse.get());
            var listCourse = programCourse.getCourseList().stream()
                    .filter(itm -> semesterTitle.equals(itm.getSemester()))
                    .collect(Collectors.toList());
            int completed = 0, inProgress = 0, todo = 0, totalHours = 0, attended = 0, absent = 0, inProgressHours = 0;
            for (CourseCompulsoryDto itm : listCourse) {
                var listSession = itm.getSessions();
                var courseStatus = itm.getCourseStatus().getDisplayName();
                boolean haveSession = listSession.size() > 0;
                if (CourseStatus.FINISHED.getDisplayName().equals(courseStatus)) {
                    if (haveSession) {
                        completed++;
                    }
                } else if (CourseStatus.OPEN.getDisplayName().equals(courseStatus)) {
                    todo++;
                } else {
                    inProgress++;
                }
                for (SessionDto session : listSession) {
                    totalHours += Integer.valueOf(session.getDuration());
                }
            }
            course.setTotalCourse(listCourse.size());
            course.setCompleted(completed);
            course.setInProgress(inProgress);
            course.setTodo(todo);
            course.setTotalHours(totalHours);
        }
        return course;
    }

    public List<Phase> buildData4ProgramStatus(List<Phase> phases) {
        var list = phases.stream().filter(itm -> !itm.isDisabled()).collect(Collectors.toList());
        list.forEach(this::mapData4Phase);
        return list;
    }

    public List<Phase> buildData4CourseStatus(List<Phase> phases) {
        try {
            var list = phases.stream().filter(itm -> !itm.isDisabled()).collect(Collectors.toList());
            list.forEach(itm -> {
                long start = System.currentTimeMillis();
                mapPrograms4Phase(itm);
                float elapsedTimeSec = (System.currentTimeMillis()-start)/1000F;
                log.info("end buildData4CourseStatus {}", itm.getFullName());
                log.info("total: {} second", elapsedTimeSec);
            });
            return list;
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    private void mapPrograms4Phase(Phase phase) {
        phase.setPrograms(getPrograms4Phase(phase));
    }

    private List<Program> getPrograms4Phase(Phase phase) {
        List<Program> programs;
        programCourseService.setCurrentUser(getCurrentUser());

        programs = programCourseService.getProgramCoursesOfUserByPhase(getCurrentUser().getIdentifier(), phase.getId())
                .stream().map(this::mapCourses4Program).collect(Collectors.toList());
        return programs;
    }

    private Program mapCourses4Program(ProgramCourseDto programCourseDto) {
        List<CourseCompulsoryDto> courseList = programCourseDto.getCourseList();
        List<Course> courses = new ArrayList<>();

        for (CourseCompulsoryDto courseCompulsoryDto : courseList) {
            CourseDto courseDto = courseCompulsoryDto.getCourseDetail();
            List<SessionDto> sessionList = courseCompulsoryDto.getSessions();

            Course course = Course.builder().courseId(courseCompulsoryDto.getUuid()).courseName(courseDto.getNodeName())
                    .status(courseCompulsoryDto.getCourseStatus().getNodeName())
                    .compulsory(courseCompulsoryDto.getCompulsory()).desc(courseDto.getDescription())
                    .uriName(nodeNameHelper.getValidatedName(courseDto.getNodeName()))
                    .semester(StringUtils.isEmpty(courseCompulsoryDto.getSemester()) ? "-"
                            : courseCompulsoryDto.getSemester())
                    .build();

            String supervisor = "";
            String trainer = "";
            int totalDuration = 0;

            if (CollectionUtils.isNotEmpty(sessionList)) {
                supervisor = sessionList.get(0).getSupervisor().getFullName();
                trainer = sessionList.get(0).getTrainer().getFullName();
                totalDuration = sessionList.stream().map(itm -> Integer.valueOf(itm.getDuration())).reduce(0,
                        Integer::sum);
            }

            course.setSupervisor(supervisor);
            course.setTrainer(trainer);
            course.setDuration(Integer.toString(totalDuration));
            course.setMyCourse(
                    StringUtils.isNotEmpty(trainer) && trainer.equals(getCurrentUser().getProperty("title")));

            var participants = enrolProgramRepository.findAllUserByProgram(programCourseDto.getProgram().getUuid())
                    .stream().filter(this::filterParticipant)
                    .map(node -> this.mapParticipants4Course(programCourseDto, node, courseDto.getUuid()))
                    .collect(Collectors.toList());

            course.setParticipants(participants);
            courses.add(course);
        }
        return Program.builder().id(programCourseDto.getProgram().getUuid())
                .name(programCourseDto.getProgram().getGroup().getNodeName())
                .startDate(programCourseDto.getProgram().getPhase().getStartDate()
                        .format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL))
                .endDate(programCourseDto.getProgram().getPhase().getEndDate()
                        .format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL))
                .courses(courses).build();
    }

    private boolean filterParticipant(Node node) {
        String userId = PropertyUtil.getString(node, "user", StringUtils.EMPTY);
        boolean isParticipant = PropertyUtil.getBoolean(node, "isParticipant", false);
        User user = authenticateService.getExistingUserById(userId).get();
        return user.getAllRoles().contains(Constants.PARTICIPANT_ROLE) && isParticipant;
    }

    private Participant mapParticipants4Course(ProgramCourseDto programCourseDto, Node enrolProgramNode,
                                               String courseId) {
        String userId = PropertyUtil.getString(enrolProgramNode, "user", StringUtils.EMPTY);
        User user = authenticateService.getExistingUserById(userId).get();

        Participant participant = new Participant();
        List<QuizDto> quizzes = new ArrayList<>();
        courseResultRepository
                .findByCourseAndEnrolProgram(PropertyUtil.getString(enrolProgramNode, JcrConstants.JCR_UUID), courseId)
                .ifPresent(node -> {
                    var quizzesNodeOpt = NodeUtils.getSubNodes(node).stream().findFirst();
                    if (quizzesNodeOpt.isPresent()){
                        var listQuizzesNode = NodeUtils.getSubNodes(quizzesNodeOpt.get());
                        if(listQuizzesNode != null) {
                            listQuizzesNode.stream().forEach(quizNode -> quizzes.add(MapperUtils.nodeToObject(quizNode, QuizDto.class).get()));
                        }
                    }
                    participant.setHomework(PropertyUtil.getString(node, "homework"));
                    participant.setNumberOfQuizzes(PropertyUtil.getString(node, "numQuiz"));
                    participant.setQuizzes(quizzes);
                    participant.setScore(PropertyUtil.getString(node, "score"));
                });
        participant.setFullName(user.getProperty("title"));
        participant.setEmail(user.getName());
        participant.setAttendant(getAttendantByCourse(userId, programCourseDto, courseId, enrolProgramNode));
        return participant;
    }

    private String getAttendantByCourse(String userId, ProgramCourseDto programCourseDto, String courseId,
                                        Node enrolProgramNode) {
        var courseDetail = programCourseDto.getCourseList().stream()
                .filter(itm -> courseId.equals(itm.getCourseDetail().getUuid())).findFirst();
        if (courseDetail.isPresent()) {
            var course = courseDetail.get();
            var listSession = course.getSessions();
            int totalHours, attended = 0;
            totalHours = listSession.size();
            boolean firstSession = true;
            for (SessionDto session : listSession) {
                try {
                    var sessionDate = session.getDate().toLocalDate();
                    var cancelDate = TimeUtils
                            .toLocalDate(PropertyUtil.getDate(enrolProgramNode, "cancelDate", Calendar.getInstance()));
                    if (!enrolProgramNode.hasProperty("cancelDate") || cancelDate.compareTo(sessionDate) >= 0) {
                        var attendance = attendanceRepository
                                .findUserAttendance(TimeUtils.toString(session.getDate().toLocalDate()), courseId);
                        if (attendance.isPresent()) {
                            var nodeAttendance = attendance.get();
                            if (!nodeAttendance.hasProperty("users")
                                    || (nodeAttendance.hasProperty("users") && !PropertyUtil
                                    .getValuesStringList(
                                            PropertyUtil.getProperty(nodeAttendance, "users").getValues())
                                    .contains(userId))) {
                                attended++;
                            }
                        }
                    } else if (firstSession) {
                        return "-";
                    }
                    firstSession = false;
                } catch (RepositoryException e) {
                    log.warn(e.getMessage());
                }
            }
            return String.format("%s/%s", attended, totalHours);
        }
        return null;
    }

    private ProgramCourseDto buildProgramCourse(Node node) {
        var result = MapperUtils.nodeToObject(node, ProgramCourseDto.class).get();
        List<CourseCompulsoryDto> courses = new ArrayList<>();
        var list = NodeUtils.getSubNodes(node);
        for (Node itm : list) {
            var course = MapperUtils.nodeToObject(itm, CourseCompulsoryDto.class).get();
            course.setSessions(NodeUtils.getSubNodes(itm).stream()
                    .map(item -> MapperUtils.nodeToObject(item, SessionDto.class).get()).collect(Collectors.toList()));
            courses.add(course);
        }
        result.setCourseList(courses);
        return result;
    }

    private List<Phase> getListPhase(List<CategoryDto> categoryList, List<PhaseDto> phaseList) {
        Seq<CategoryDto> s1 = Seq.seq(categoryList);
        Seq<PhaseDto> s2 = Seq.seq(phaseList);
        var list = s1.leftOuterJoin(s2, (v1, v2) -> v1.getUuid().equals(v2.getPhase().getUuid())).map(itm -> {
            boolean disabled = itm.v2 == null;
            Phase phase = new Phase();
            phase.setFullName(itm.v2 != null ? itm.v2.getNodeName() : "");
            phase.setName(itm.v1.getDisplayName());
            phase.setDisabled(disabled);
            phase.setId(itm.v2 != null ? itm.v2.getUuid() : itm.v1.getUuid());
            return phase;
        }).collect(Collectors.toList());
        List<Phase> result = new ArrayList<>();
        for (int i = list.size() - 1; i > -1; i--) {
            result.add(list.get(i));
        }
        return result;
    }

    private void mapData4Phase(Phase phase) {
        String phaseId = phase.getId();
        var programs = programRepository.findProgramByPhase(phaseId).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, ProgramDto.class).get()).map(this::mapProgram)
                .collect(Collectors.toList());
        phase.setPrograms(programs);
    }

    public List<Course> getCourseSummaryWithSemesters(EnrolProgramDto enrolProgramDto) {
        List<Course> courses = new ArrayList<>();
        if (enrolProgramDto == null) {
            return courses;
        }
        courses.add(buildCourse4ProgramStatus(enrolProgramDto, Constants.Semester.SEMESTER_1));
        courses.add(buildCourse4ProgramStatus(enrolProgramDto, Constants.Semester.SEMESTER_2));
        return courses;
    }

    private Program mapProgram(ProgramDto dto) {
        final LocalDate currentLocalDate = LocalDate.now();
        String semesterTitle = StringUtils.EMPTY;
        String programId = dto.getUuid();
        var semesterList = semesterRepository.findByProgram(programId).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, SemesterDto.class).get())
                .sorted(Comparator.comparing(SemesterDto::getTitle)).collect(Collectors.toList());
        var semesterOptional = semesterList.stream()
                .filter(itm -> itm.getStartDate().toLocalDate().compareTo(currentLocalDate) <= 0
                        && itm.getEndDate().toLocalDate().compareTo(currentLocalDate) >= 0)
                .findFirst();
        if (semesterOptional.isPresent()) {
            var semesterDto = semesterOptional.get();
            semesterTitle = semesterDto.getTitle();
        }

        var list = enrolProgramRepository.findAllUserByProgram(dto.getUuid()).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get()).collect(Collectors.toList());
        return Program.builder().id(dto.getUuid()).name(dto.getGroup().getDisplayName())
                .startDate(dto.getPhase().getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL))
                .endDate(dto.getPhase().getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL))
                .participants(list.stream().filter(itm -> itm.getUser().getRoles().contains(Constants.PARTICIPANT_ROLE))
                        .map(enrol -> {
                            var user = enrol.getUser();
                            String link = String.format("?year=%s&phase=%s&program=%s&user=%s",
                                    dto.getPhase().getStartDate().getYear(), dto.getPhase().getUuid(), dto.getUuid(),
                                    user.getUuid());
                            var enrolProgramDto = enrolProgramRepository.findByProgramUser(user.getUuid(), dto.getUuid());
                            String enrollType = MapperUtils.nodeToObject(enrolProgramDto.get(), EnrolProgramDto.class).get().getEnrollType();
                            return Participant.builder().id(user.getUuid()).name(user.getFullName())
                                    .profile(Optional
                                            .ofNullable(user.getProfile()).map(CategoryDto::getDisplayName).orElse("-"))
                                    .semester(enrollType)
                                    .team(Optional.ofNullable(user.getDepartment()).map(CategoryDto::getDisplayName)
                                            .orElse("-"))
                                    .link(link).status(StringUtils.isEmpty(enrol.getParticipantStatus()) ? "Check here"
                                            : enrol.getParticipantStatus())
                                    .build();
                        }).collect(Collectors.toList()))
                .course(buildCourse(list.stream().findFirst().orElse(null), semesterTitle))
                .courses(getCourseSummaryWithSemesters(list.stream().findFirst().orElse(null)))
                .onJobTraining(ojtProjectRepository.findAllByProgram(dto.getUuid()).stream()
                        .map(itm -> MapperUtils.nodeToObject(itm, OJTProjectDto.class).get())
                        .map(itm -> OjtProject.builder().id(itm.getUuid()).projectName(itm.getProjectName())
                                .projectLead(itm.getProjectLead().getFullName())
                                .projectStatus(itm.getProjectStatus().getDisplayName())
                                .startDate(itm.getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL))
                                .endDate(itm.getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL))
                                .participants(ojtUserResultRepository.findByOJTProjectID(itm.getUuid()).size())
                                .uriName(nodeNameHelper.getValidatedName(itm.getProjectName())).build())
                        .collect(Collectors.toList()))
                .build();
    }
}
