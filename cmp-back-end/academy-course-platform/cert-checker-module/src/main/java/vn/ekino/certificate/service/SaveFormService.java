package vn.ekino.certificate.service;

import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.AssessmentCriteriaDto;
import vn.ekino.certificate.dto.CourseDto;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.OJTProjectDto;
import vn.ekino.certificate.dto.OJTUserResultDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.UserAttitudeResultDto;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.dto.enumeration.EnrollProgramStatusEnum;
import vn.ekino.certificate.repository.AssessmentCriteriaRepository;
import vn.ekino.certificate.repository.AttendanceRepository;
import vn.ekino.certificate.repository.CourseRepository;
import vn.ekino.certificate.repository.CourseResultRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.InternProgramRepository;
import vn.ekino.certificate.repository.InternshipRepository;
import vn.ekino.certificate.repository.NewsRepository;
import vn.ekino.certificate.repository.NotificationRepository;
import vn.ekino.certificate.repository.OJTProjectRepository;
import vn.ekino.certificate.repository.OJTUserAssessmentRepository;
import vn.ekino.certificate.repository.OJTUserResultRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.UserAttitudeAssessmentRepository;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SaveFormService {
    private final EnrolProgramRepository enrolProgramRepository;
    private final OJTProjectRepository ojtProjectRepository;
    private final OJTUserResultRepository ojtUserResultRepository;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final UserAttitudeResultRepository userAttitudeResultRepository;
    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;
    private final InternProgramRepository internProgramRepository;
    private final ProgramService programService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ProgramCourseRepository programCourseRepository;
    private final MailService mailService;
    private final EventChangeService eventChangeService;
    private final CertificateServicesModule certificateServicesModule;
    private final NodeNameHelper nodeNameHelper;
    private final PublishingService publishingService;

    @Inject
    public SaveFormService(EnrolProgramRepository enrolProgramRepository,
                           OJTProjectRepository ojtProjectRepository,
                           OJTUserResultRepository ojtUserResultRepository,
                           AssessmentCriteriaRepository assessmentCriteriaRepository,
                           UserAttitudeResultRepository userAttitudeResultRepository,
                           CourseRepository courseRepository,
                           ProgramRepository programRepository,
                           InternProgramRepository internProgramRepository, ProgramService programService,
                           UserRepository userRepository,
                           NotificationRepository notificationRepository,
                           ProgramCourseRepository programCourseRepository,
                           MailService mailService,
                           EventChangeService eventChangeService,
                           NodeNameHelper nodeNameHelper,
                           CertificateServicesModule certificateServicesModule,
                           PublishingService publishingService) {
        this.enrolProgramRepository = enrolProgramRepository;
        this.ojtProjectRepository = ojtProjectRepository;
        this.ojtUserResultRepository = ojtUserResultRepository;
        this.assessmentCriteriaRepository = assessmentCriteriaRepository;
        this.userAttitudeResultRepository = userAttitudeResultRepository;
        this.courseRepository = courseRepository;
        this.programRepository = programRepository;
        this.internProgramRepository = internProgramRepository;
        this.programService = programService;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.programCourseRepository = programCourseRepository;
        this.mailService = mailService;
        this.eventChangeService = eventChangeService;
        this.nodeNameHelper = nodeNameHelper;
        this.certificateServicesModule = certificateServicesModule;
        this.publishingService = publishingService;
    }

    public Node execute(Node node, boolean sendMail, boolean notify) throws RepositoryException {
        String workspace = node.getSession().getWorkspace().getName();
        switch (workspace) {
            case EnrolProgramRepository.ENROL_PROGRAM_WORKSPACE:
                generateName4Enrol(node, sendMail);
                break;
            case InternshipRepository.INTERNSHIP_WORKSPACE:
                generateName4Internship(node);
                break;
            case OJTUserResultRepository.OJT_USER_RESULT_WORKSPACE:
                generateName4OjtUser(node);
                break;
            case OJTUserAssessmentRepository.WORKSPACE:
                generateName4OjtUserAssessment(node);
                break;
            case UserAttitudeResultRepository.USER_ATTITUDE_RESULT_WORKSPACE:
                generateName4UserAttitude(node);
                break;
            case UserAttitudeAssessmentRepository.USER_ATTITUDE_ASSESSMENT_WORKSPACE:
                generateName4UserAttitudeAssessment(node);
                break;
            case CourseResultRepository.COURSE_RESULT_WORKSPACE:
                generateName4CourseResult(node);
                if (notify) {
                    saveNotification4CourseResult(node);
                } break;
            case UserEvaluationRepository.WORKSPACE:
                generateName4UserEvaluation(node);
                break;
            case OJTProjectRepository.OJT_PROJECT_WORKSPACE:
                generateName4OjtProject(node);
                if (notify) {
                    saveNotification4OJTProject(node);
                } break;
            case AttendanceRepository.ATTENDANCE_WORKSPACE:
                setListUser4Attendance(node);
                break;
            case NewsRepository.NEWS_WORKSPACE:
                if (notify) {
                    saveNotification4News(node);
                } break;
            case CourseRepository.COURSE_WORKSPACE:
                if (notify) {
                    saveNotification4Course(node);
                } break;
            case NotificationRepository.NOTIFICATION_WORKSPACE:
                save4Notification(node);
        }
        return node;
    }

    public void checkDuplicateData(JcrNodeAdapter item) throws ActionExecutionException {
        String workspace = item.getWorkspace();
        String nodeType = item.getPrimaryNodeTypeName();
        Map<String, List<String>> filtersCondition = new HashMap<>();
        switch (workspace) {
            case EnrolProgramRepository.ENROL_PROGRAM_WORKSPACE:
                getCondition4EnrolProgram(filtersCondition, item);
                break;
            case CourseResultRepository.COURSE_RESULT_WORKSPACE:
                getCondition4CourseResult(filtersCondition, item);
                break;
            case OJTUserResultRepository.OJT_USER_RESULT_WORKSPACE:
                getCondition4OjtUser(filtersCondition, item);
                break;
            case OJTUserAssessmentRepository.WORKSPACE:
                getCondition4OjtUserAssessment(filtersCondition, item);
                break;
            case UserAttitudeResultRepository.USER_ATTITUDE_RESULT_WORKSPACE:
                getCondition4UserAttitude(filtersCondition, item);
                break;
            case UserAttitudeAssessmentRepository.USER_ATTITUDE_ASSESSMENT_WORKSPACE:
                getCondition4UserAttitudeAssessment(filtersCondition, item);
                break;
            case UserEvaluationRepository.WORKSPACE:
                getCondition4UserEvaluation(filtersCondition, item);
                break;
        }
        if (filtersCondition.size() > 0) {
            var unique = userRepository.findByMultiValue(workspace, nodeType, filtersCondition);
            if (unique.isPresent()) {
                if (item.isNew()) {
                    throw new ActionExecutionException("Duplicate data, please check again");
                }
            }
        }
    }

    //region Generate node name
    private Node generateName4Enrol(Node node, boolean sendMail) throws RepositoryException {
        String programName = programService.getProgramById(PropertyUtil.getString(node, "program", StringUtils.EMPTY))
                .map(ProgramDto::getNodeName)
                .orElse(StringUtils.EMPTY);
        String newNodeName = String
                .format("%s-%s",
                        programName,
                        LocalDateTime.now().format(TimeUtils.DATE_TIME_FORMATTER))
                .replace(" ", "-").replace(":", "-");
        String userId = PropertyUtil.getString(node, "user", StringUtils.EMPTY);

        var userNode = userRepository.findById(userId);
        if (userNode.isPresent()) {
            UserDto userDto = MapperUtils.nodeToObject(userNode.get(), UserDto.class).get();
            newNodeName = String
                    .format("%s-%s-%s",
                            programName,
                            userDto.getNodeName(),
                            LocalDateTime.now().format(TimeUtils.DATE_TIME_FORMATTER))
                    .replace(" ", "-").replace(":", "-");
            if (sendMail) {
                String enrolledStatus = PropertyUtil.getString(node, "enrollStatus", StringUtils.EMPTY);
                if (EnrollProgramStatusEnum.APPROVED.getStatus().equals(enrolledStatus)) {
                    mailService.sendMail(userDto, EnrollProgramStatusEnum.APPROVED.getStatus());
                } else if (EnrollProgramStatusEnum.REFUSED.getStatus().equals(enrolledStatus)) {
                    mailService.sendMail(userDto, EnrollProgramStatusEnum.REFUSED.getStatus());
                }
            }
        }
        PropertyUtil.setProperty(node, "name", newNodeName);
        return node;
    }

    private Node generateName4Internship(Node node) throws RepositoryException {
        String programName = internProgramRepository.findById(PropertyUtil.getString(node, "internProgram", StringUtils.EMPTY)).get().getName();
        String newNodeName = String
                .format("%s-%s",
                        programName,
                        LocalDateTime.now().format(TimeUtils.DATE_TIME_FORMATTER))
                .replace(" ", "-").replace(":", "-");
        String userId = PropertyUtil.getString(node, "user", StringUtils.EMPTY);

        var userNode = userRepository.findById(userId);
        if (userNode.isPresent()) {
            UserDto userDto = MapperUtils.nodeToObject(userNode.get(), UserDto.class).get();
            newNodeName = String
                    .format("%s-%s-%s",
                            programName,
                            userDto.getNodeName(),
                            LocalDateTime.now().format(TimeUtils.DATE_TIME_FORMATTER))
                    .replace(" ", "-").replace(":", "-");
        }
        PropertyUtil.setProperty(node, "name", newNodeName);
        return node;
    }

    private Node generateName4OjtUser(Node node) throws RepositoryException {
        var userEnrolProgram = PropertyUtil.getString(node, "userEnrolProgram");
        var enrolDto = MapperUtils.nodeToObject(enrolProgramRepository.findById(userEnrolProgram).get(), EnrolProgramDto.class).get();
        var ojtProjectId = PropertyUtil.getString(node, "ojtProject");
        var ojtProjectDto = MapperUtils.nodeToObject(ojtProjectRepository.findById(ojtProjectId).get(), OJTProjectDto.class).get();
        String nodeName = String.format("%s-%s-%s", enrolDto.getProgram().getNodeName(), ojtProjectDto.getProjectName(), enrolDto.getUser().getNodeName());
        PropertyUtil.setProperty(node, "name", nodeName);
        return node;
    }

    private Node generateName4OjtUserAssessment(Node node) throws RepositoryException {
        var ojtUserResultId = PropertyUtil.getString(node, "ojtUserResult");
        var ojtUserResultDto = MapperUtils.nodeToObject(ojtUserResultRepository.findById(ojtUserResultId).get(), OJTUserResultDto.class).get();
        var assessmentId = PropertyUtil.getString(node, "assessment");
        var assessmentDto = MapperUtils.nodeToObject(assessmentCriteriaRepository.findById(assessmentId).get(), AssessmentCriteriaDto.class).get();
        String nodeName = String.format("%s-%s-%s-%s", ojtUserResultDto.getUserEnrolProgram().getProgram().getNodeName()
                , ojtUserResultDto.getOjtProject().getProjectName()
                , ojtUserResultDto.getUserEnrolProgram().getUser().getNodeName()
                , assessmentDto.getNodeName());
        PropertyUtil.setProperty(node, "name", nodeName);
        return node;
    }

    private Node generateName4UserAttitude(Node node) throws RepositoryException {
        var userEnrolProgram = PropertyUtil.getString(node, "userEnrolProgram");
        var enrolDto = MapperUtils.nodeToObject(enrolProgramRepository.findById(userEnrolProgram).get(), EnrolProgramDto.class).get();
        String nodeName = enrolDto.getNodeName();
        PropertyUtil.setProperty(node, "name", nodeName);
        return node;
    }

    private Node generateName4UserAttitudeAssessment(Node node) throws RepositoryException {
        var userAttitudeResult = PropertyUtil.getString(node, "userAttitudeResult");
        var assessment = PropertyUtil.getString(node, "assessment");
        var userAttitudeResultDto = MapperUtils.nodeToObject(userAttitudeResultRepository.findById(userAttitudeResult).get(), UserAttitudeResultDto.class).get();
        var assessmentCriteriaDto = MapperUtils.nodeToObject(assessmentCriteriaRepository.findById(assessment).get(), AssessmentCriteriaDto.class).get();
        String nodeName = String.format("%s-%s",
                userAttitudeResultDto.getNodeName(),
                assessmentCriteriaDto.getNodeName());
        PropertyUtil.setProperty(node, "name", nodeName);
        return node;
    }

    private Node generateName4CourseResult(Node node) throws RepositoryException {
        var userEnrolProgram = PropertyUtil.getString(node, "program");
        var enrolDto = MapperUtils.nodeToObject(enrolProgramRepository.findById(userEnrolProgram).get(), EnrolProgramDto.class).get();
        var course = PropertyUtil.getString(node, "course");
        var courseDto = MapperUtils.nodeToObject(courseRepository.findById(course).get(), CourseDto.class).get();
        var quizNum = eventChangeService.findNumberQuizzesByCourseId(course, userEnrolProgram);
        PropertyUtil.setProperty(node, "numQuiz", quizNum);

        String nodeName = String.format("%s-%s",
                enrolDto.getNodeName(),
                courseDto.getNodeName());
        PropertyUtil.setProperty(node, "name", nodeName);
        return node;
    }

    private Node generateName4UserEvaluation(Node node) throws RepositoryException {
        var userEnrolProgram = PropertyUtil.getString(node, "userProgram");
        var semester = PropertyUtil.getString(node, "semester");
        var enrolDto = MapperUtils.nodeToObject(enrolProgramRepository.findById(userEnrolProgram).get(), EnrolProgramDto.class).get();
        String nodeName = enrolDto.getNodeName() + semester;
        PropertyUtil.setProperty(node, "name", nodeName);
        return node;
    }

    private Node generateName4OjtProject(Node node) throws RepositoryException {
        var programId = PropertyUtil.getString(node, "program");
        var programDto = MapperUtils.nodeToObject(programRepository.findById(programId).get(), ProgramDto.class).get();
        var projectName = PropertyUtil.getString(node, "projectName");
        String nodeName = String.format("%s-%s", programDto.getNodeName(), projectName);
        PropertyUtil.setProperty(node, "name", nodeName);
        PropertyUtil.setProperty(node, "uriName", projectName.trim().replace("/","-").replace(" ", "-"));
        return node;
    }

    //endregion

    //region Check unique data
    private void getCondition4EnrolProgram(Map<String, List<String>> filtersCondition, JcrNodeAdapter item) {
        filtersCondition.put("user", List.of(item.getItemProperty("user").getValue().toString()));
        filtersCondition.put("program", List.of(item.getItemProperty("program").getValue().toString()));
    }

    private void getCondition4CourseResult(Map<String, List<String>> filtersCondition, JcrNodeAdapter item) {
        filtersCondition.put("program", List.of(item.getItemProperty("program").getValue().toString()));
        filtersCondition.put("course", List.of(item.getItemProperty("course").getValue().toString()));
    }

    private void getCondition4OjtUser(Map<String, List<String>> filtersCondition, JcrNodeAdapter item) {
        filtersCondition.put("userEnrolProgram", List.of(item.getItemProperty("userEnrolProgram").getValue().toString()));
        filtersCondition.put("ojtProject", List.of(item.getItemProperty("ojtProject").getValue().toString()));
    }

    private void getCondition4OjtUserAssessment(Map<String, List<String>> filtersCondition, JcrNodeAdapter item) {
        filtersCondition.put("ojtUserResult", List.of(item.getItemProperty("ojtUserResult").getValue().toString()));
        filtersCondition.put("assessment", List.of(item.getItemProperty("assessment").getValue().toString()));
    }

    private void getCondition4UserAttitude(Map<String, List<String>> filtersCondition, JcrNodeAdapter item) {
        filtersCondition.put("userEnrolProgram", List.of(item.getItemProperty("userEnrolProgram").getValue().toString()));
    }

    private void getCondition4UserAttitudeAssessment(Map<String, List<String>> filtersCondition, JcrNodeAdapter item) {
        filtersCondition.put("userAttitudeResult", List.of(item.getItemProperty("userAttitudeResult").getValue().toString()));
        filtersCondition.put("assessment", List.of(item.getItemProperty("assessment").getValue().toString()));
    }

    private void getCondition4UserEvaluation(Map<String, List<String>> filtersCondition, JcrNodeAdapter item) {
        filtersCondition.put("userProgram", List.of(item.getItemProperty("userProgram").getValue().toString()));
        filtersCondition.put("userProgram", List.of(item.getItemProperty("semester").getValue().toString()));
    }

    //endregion

    private Node setListUser4Attendance(Node node) {
        Map<String, Object> map = new HashMap<>();
        map.put("comboboxValue", PropertyUtil.getString(node, "course"));
        map.put("dateValue", PropertyUtil.getDate(node, "date").getTime());
        var options = eventChangeService.getUsersByCourse(map);
        var list = options.stream().map(SelectFieldOptionDefinition::getValue).collect(Collectors.toList());
        try {
            PropertyUtil.setProperty(node, "fullUser", list);
        } catch (RepositoryException e) {
            log.warn("Can't set property because {}", e.getMessage());
        }
        return node;
    }

    //region Save notification
    private Node createNotificationNode(String category, String initialPath, String title, String description, Map<String, Object> map, String relatedId) {
        if (NotificationRepository.CATEGORY_NEWS.equals(category)) {
            var result = notificationRepository.findByCategory(category);
            var node = result.isPresent() ? result.get() : notificationRepository.getOrAddNode(initialPath).get();
            return createOrUpdateNotification(node, category, title, description, map, relatedId);
        }
        var node = notificationRepository.getOrAddNode(initialPath).get();
        return createOrUpdateNotification(node, category, title, description, map, relatedId);
    }

    private Node createOrUpdateNotification(Node node, String category, String title, String description, Map<String, Object> map, String relatedId) {
        try {
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_TITLE, title);
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_DESCRIPTION, description);
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_PARTICIPANT_LINK, map.get("participantLink"));
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_TRAINER_LINK, map.get("trainerLink"));
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_SUPERVISOR_LINK, map.get("supervisorLink"));
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_PARTICIPANTS, map.get("participants"));
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_TRAINERS, map.get("trainers"));
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_SUPERVISORS, map.get("supervisors"));
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_NOTIFICATION_DATE, LocalDate.now().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
            PropertyUtil.setProperty(node, NotificationRepository.PROPERTY_RELATED, relatedId);
            PropertyUtil.setProperty(node, NotificationRepository.CATEGORY, category);
            PropertyUtil.setProperty(node, "name", relatedId);
        } catch (RepositoryException e) {
            log.warn("Can't create new notification because {}", e.getMessage());
        }
        return node;
    }


    private void save4Notification(Node node) {
        String id = PropertyUtil.getString(node, JcrConstants.JCR_UUID);
        String courseId = PropertyUtil.getString(node, "course");
        var sessionDate = TimeUtils.toLocalDateTime(PropertyUtil.getDate(node, "sessionDate"));
        String desc = sessionDate.format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
        String link = String.format("%s?selectedMonth=%s", "/calendar", TimeUtils.toString(sessionDate.toLocalDate()));
        var date = Date.from(sessionDate.atZone(ZoneId.systemDefault()).toInstant());
        var participants = programCourseRepository.findProgramIdBySessionDate(date)
                .stream()
                .map(itm -> enrolProgramRepository.findAllApprovedUsersByProgram(itm).stream()
                        .filter(enrolNode -> PropertyUtil.getBoolean(enrolNode, "isParticipant", false))
                            .map(enrolNode -> PropertyUtil.getString(enrolNode, "user"))
                            .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        var courseNodes = programCourseRepository.findAllCourseIdBySessionDate(date)
                .stream()
                .map(itm -> NodeUtils.getSubNodes(programCourseRepository.findByCourseId(itm).get()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var sessionNodes = courseNodes.stream()
                .filter(itm -> courseId.equals(PropertyUtil.getString(itm, "courseName")))
                .map(NodeUtils::getSubNodes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var trainers = sessionNodes.stream()
                .map(itm -> PropertyUtil.getString(itm, "trainer"))
                .distinct()
                .collect(Collectors.toList());
        var supervisors = courseNodes.stream()
                .map(NodeUtils::getSubNodes)
                .flatMap(Collection::stream)
                .map(itm -> PropertyUtil.getString(itm, "supervisor"))
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("participants", participants);
        map.put("trainers", trainers);
        map.put("supervisors", supervisors);
        map.put("participantLink", link);
        map.put("trainerLink", link);
        map.put("supervisorLink", link);
        var notificationNode = createNotificationNode(NotificationRepository.CATEGORY_SCHEDULE, id, "New schedule updated: ", desc, map, id);
        try {
            PropertyUtil.setProperty(notificationNode, "sessionDate", PropertyUtil.getDate(node, "sessionDate"));
            PropertyUtil.setProperty(notificationNode, "course", PropertyUtil.getString(node, "course"));
            PropertyUtil.setProperty(notificationNode, "name", notificationNode.getIdentifier());
            notificationRepository.save(notificationNode);
        } catch (RepositoryException e) {
            log.warn("Can't set property because {}", e.getMessage());
        }

        publishingService.publish(List.of(notificationNode));
    }

    private void saveNotification4News(Node newsNode) {
        String id = PropertyUtil.getString(newsNode, JcrConstants.JCR_UUID);
        List<String> users = userRepository.findAll().stream()
                .map(itm -> PropertyUtil.getString(itm, JcrConstants.JCR_UUID))
                .collect(Collectors.toList());
        String link = certificateServicesModule.getNewsUrl();
        Map<String, Object> map = new HashMap<>();
        map.put("participants", users);
        map.put("trainers", users);
        map.put("supervisors", users);
        map.put("participantLink", link);
        map.put("trainerLink", link);
        map.put("supervisorLink", link);
        Node notificationNode = createNotificationNode(NotificationRepository.CATEGORY_NEWS, id, "More <strong>news</strong> awaits! ", " ", map, id);
        notificationRepository.save(notificationNode);
        publishingService.publish(List.of(notificationNode));
    }

    private void saveNotification4OJTProject(Node ojtProjectNode) {
        String id = PropertyUtil.getString(ojtProjectNode, JcrConstants.JCR_UUID);
        String projectName = PropertyUtil.getString(ojtProjectNode, "projectName");
        String programId = PropertyUtil.getString(ojtProjectNode, "program");
        String desc = String.format("%s(%s)", NotificationRepository.OJT_PROJECT_TITLE, projectName);
        String projectNameLink = projectName.replace("/","-").replace(" ", "-");
        String link = String.format("%s%s/%s", certificateServicesModule.getMyProgressUrl(), certificateServicesModule.getOnJobTrainingUrl(), projectNameLink);
        List<Node> ojtProjectResults = ojtUserResultRepository.findByOJTProjectID(PropertyUtil.getString(ojtProjectNode, JcrConstants.JCR_UUID));

        var participants = ojtProjectResults.stream()
                .map(itm -> MapperUtils.nodeToObject(itm, OJTUserResultDto.class).get())
                .map(itm -> itm.getUserEnrolProgram().getUser().getUuid())
                .collect(Collectors.toList());
        var programCourseNode = programCourseRepository.findByProgramId(programId).get();
        var courseNodes = NodeUtils.getSubNodes(programCourseNode);
        var sessionNodes = courseNodes.stream()
                .map(NodeUtils::getSubNodes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var supervisors = sessionNodes.stream()
                .map(itm -> PropertyUtil.getString(itm, "supervisor"))
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("participants", participants);
        map.put("trainers", new ArrayList<String>());
        map.put("supervisors", supervisors);
        map.put("participantLink", link);
        map.put("trainerLink", "none");
        map.put("supervisorLink", certificateServicesModule.getProgramStatusUrl());

        Node notificationNode = createNotificationNode(NotificationRepository.CATEGORY_OJT_PROJECT, id,"New information for: ", desc ,map, id);
        notificationRepository.save(notificationNode);
        publishingService.publish(List.of(notificationNode));
    }

    private void saveNotification4CourseResult(Node courseResultNode) {
        String id = PropertyUtil.getString(courseResultNode, JcrConstants.JCR_UUID);
        var enrolProgramNode = enrolProgramRepository.findById(PropertyUtil.getString(courseResultNode, "program")).get();
        var participants = Arrays.asList(PropertyUtil.getString(enrolProgramNode, "user"));
        var courseNode = courseRepository.findById(PropertyUtil.getString(courseResultNode, "course")).get();
        String courseId = PropertyUtil.getString(courseNode, JcrConstants.JCR_UUID);
        String courseName = PropertyUtil.getString(courseNode, Constants.PROPERTY_NAME);
        var programCourseNode = programCourseRepository.findByCourseId(courseId).get();
        var courseNodes = NodeUtils.getSubNodes(programCourseNode);
        var sessionNodes = courseNodes.stream()
                .filter(itm -> courseId.equals(PropertyUtil.getString(itm, "courseName")))
                .map(NodeUtils::getSubNodes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var trainers = sessionNodes.stream()
                .map(itm -> PropertyUtil.getString(itm, "trainer"))
                .distinct()
                .collect(Collectors.toList());
        var supervisors = courseNodes.stream()
                .map(NodeUtils::getSubNodes)
                .flatMap(Collection::stream)
                .map(itm -> PropertyUtil.getString(itm, "supervisor"))
                .distinct()
                .collect(Collectors.toList());


        Map<String, Object> map = new HashMap<>();
        map.put("participants", participants);
        map.put("trainers", trainers);
        map.put("supervisors", supervisors);
        map.put("participantLink", certificateServicesModule.getMyProgressUrl());
        map.put("trainerLink", certificateServicesModule.getCourseStatusUrl());
        map.put("supervisorLink", String.format("%s%s", certificateServicesModule.getProgramStatusUrl(), certificateServicesModule.getCourseStatusUrl()));

        Node notificationNode = createNotificationNode(NotificationRepository.CATEGORY_COURSE_RESULT, id, "New score updated: ", courseName, map, id);
        notificationRepository.save(notificationNode);
        publishingService.publish(List.of(notificationNode));
    }

    private void saveNotification4Course(Node courseNode) {
        String courseId = PropertyUtil.getString(courseNode, JcrConstants.JCR_UUID);
        String courseName = PropertyUtil.getString(courseNode, Constants.PROPERTY_NAME);
        String courseLink = courseName.replace("/","-").replace(" ", "-");

        String link = String.format("%s%s/%s", certificateServicesModule.getCoursesUrl(), certificateServicesModule.getCourseDetailUrl(), courseLink);
//        String supervisorLink = "/calendar" + link;
        String trainerLink = "/calendar" + String.format("%s/%s", certificateServicesModule.getCourseDetailUrl(), courseLink);;

        Node programCourseNode = programCourseRepository.findByCourseId(courseId).get();

        String programId = PropertyUtil.getString(programCourseNode, "program");

        var participants = enrolProgramRepository.findAllApprovedUsersByProgram(programId).stream()
                .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get())
                .filter(EnrolProgramDto::getIsParticipant)
                .map(itm -> itm.getUser().getUuid())
                .collect(Collectors.toList());
        var courseNodes = NodeUtils.getSubNodes(programCourseNode);
        var sessionNodes = courseNodes.stream()
                .filter(itm -> courseId.equals(PropertyUtil.getString(itm, "courseName")))
                .map(NodeUtils::getSubNodes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var trainers = sessionNodes.stream()
                .map(itm -> PropertyUtil.getString(itm, "trainer"))
                .distinct()
                .collect(Collectors.toList());
        var supervisors = courseNodes.stream()
                .map(NodeUtils::getSubNodes)
                .flatMap(Collection::stream)
                .map(itm -> PropertyUtil.getString(itm, "supervisor"))
                .distinct()
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("participants", participants);
        map.put("trainers", trainers);
        map.put("supervisors", supervisors);
        map.put("participantLink", link);
        map.put("trainerLink", trainerLink);
        map.put("supervisorLink", trainerLink);

        Node notificationNode = createNotificationNode(NotificationRepository.CATEGORY_COURSE, courseId, "New information for: ", courseName, map, courseId);
        notificationRepository.save(notificationNode);
        publishingService.publish(List.of(notificationNode));
    }
    //endregion
}
