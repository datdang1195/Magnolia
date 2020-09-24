package vn.ekino.certificate.service;

import com.google.common.collect.Lists;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.UserManager;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.dialog.DialogPresenter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import vn.ekino.certificate.dto.AttitudeDto;
import vn.ekino.certificate.dto.enumeration.CourseStatus;
import vn.ekino.certificate.repository.AssessmentCriteriaRepository;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.CourseRepository;
import vn.ekino.certificate.repository.CourseResultRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.OJTProjectRepository;
import vn.ekino.certificate.repository.OJTUserAssessmentRepository;
import vn.ekino.certificate.repository.OJTUserResultRepository;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.UserAttitudeAssessmentRepository;
import vn.ekino.certificate.repository.UserAttitudeResultRepository;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.NodeUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ExcelService {
    public static final String EVALUATION = "Evaluation";
    public static final String RELATED_UUID = "relatedUUID";
    public static final String JCR_UUID = "jcr:uuid";
    private static final String PROGRAM_NOT_EXIST = "Program does not exist, please create this new program";
    private static final String USER_NOT_EXIST = "User does not exist, please create new one";
    private static final String ENROL_PROGRAM_NOT_EXIST = "Enrol program does not exist, please create new one";
    private static final String COURSE_NOT_EXIST = "Course does not exist, please create new one";
    private static final String INVALID_QUIZ_SCORE = "Quiz score is invalid, please check again";
    private static final String INVALID_QUIZ_RATE = "Quiz rate is invalid, please check again";
    private static final String INVALID_HOMEWORK_SCORE = "Homework score is invalid, please check again";
    private static final String LENGTH_NOT_EQUAL = "Length of Quiz Score array and Quiz Rate array is not equal, please check again";
    private static final String QUIZZES_NODE_NAME = "quizzes";
    private static final String QUIZZES_NODE_TYPE = "mgnl:contentNode";
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final ProgramRepository programRepository;
    private final ProgramCourseRepository programCourseRepository;
    private final PhaseRepository phaseRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    private final OJTProjectRepository ojtProjectRepository;
    private final OJTUserResultRepository ojtUserResultRepository;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final OJTUserAssessmentRepository ojtUserAssessmentRepository;
    private final UserAttitudeResultRepository userAttitudeResultRepository;
    private final UserAttitudeAssessmentRepository userAttitudeAssessmentRepository;
    private final NodeNameHelper nodeNameHelper;
    private final UserRepository userRepository;
    private final CourseResultRepository courseResultRepository;
    private HashSet<String> listError = new HashSet<>();

    @Inject
    public ExcelService(CourseRepository courseRepository,
                        CategoryRepository categoryRepository,
                        ProgramRepository programRepository,
                        ProgramCourseRepository programCourseRepository,
                        PhaseRepository phaseRepository,
                        EnrolProgramRepository enrolProgramRepository,
                        OJTProjectRepository ojtProjectRepository,
                        OJTUserResultRepository ojtUserResultRepository,
                        AssessmentCriteriaRepository assessmentCriteriaRepository,
                        OJTUserAssessmentRepository ojtUserAssessmentRepository,
                        UserAttitudeResultRepository userAttitudeResultRepository,
                        UserAttitudeAssessmentRepository userAttitudeAssessmentRepository,
                        NodeNameHelper nodeNameHelper, UserRepository userRepository,
                        CourseResultRepository courseResultRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.programRepository = programRepository;
        this.programCourseRepository = programCourseRepository;
        this.phaseRepository = phaseRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.ojtProjectRepository = ojtProjectRepository;
        this.ojtUserResultRepository = ojtUserResultRepository;
        this.assessmentCriteriaRepository = assessmentCriteriaRepository;
        this.ojtUserAssessmentRepository = ojtUserAssessmentRepository;
        this.userAttitudeResultRepository = userAttitudeResultRepository;
        this.userAttitudeAssessmentRepository = userAttitudeAssessmentRepository;
        this.nodeNameHelper = nodeNameHelper;
        this.userRepository = userRepository;
        this.courseResultRepository = courseResultRepository;
    }

    public void importExcelToWorkspace(Workbook workbook, SubAppContext uiContext, DialogPresenter dialogPresenter) throws ActionExecutionException, RepositoryException {
        int numberOfSheet = workbook.getNumberOfSheets();

        Sheet sheet = workbook.getSheetAt(0);
        String sheetName = sheet.getSheetName().trim().replace(' ', '-');
        String programName;
        if (!sheetName.contains("_Sessions")) {
            programName = sheetName;
            Node nodeProgram = programRepository.getProgramByPath(programName)
                    .orElseThrow(() -> new ActionExecutionException(PROGRAM_NOT_EXIST));
            String programId = nodeProgram.getIdentifier();
            Optional<Node> programCourse = programCourseRepository.findByProgramId(programId);
            boolean hasProgramCourse = programCourse.isPresent();
            Node nodeProgramCourse = hasProgramCourse ? programCourse.get() : programCourseRepository.createNode(generateNodeName(programId)).get();
            importCourse(sheet, programId, nodeProgramCourse, hasProgramCourse);
        } else {
            programName = sheetName.replace("_Sessions", "");
            importExcelSession(sheet, programName);
        }
        if (numberOfSheet > 1) {
            sheet = workbook.getSheetAt(1);
            programName = workbook.getSheetName(1).trim()
                    .replace(' ', '-')
                    .replace("_Sessions", "");
            importExcelSession(sheet, programName);
        }
        String result = String.join(System.lineSeparator(), new ArrayList<>(listError));
        if (StringUtils.isNotEmpty(result)) {
            dialogPresenter.closeDialog();
            throw new ActionExecutionException(result);
        }
        uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Import successful");
    }

    public void importExcelToCourseResultWorkspace(Workbook workbook, SubAppContext uiContext, DialogPresenter dialogPresenter)
            throws ActionExecutionException {
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() >= 1) {
                try {
                    if (currentRow.getCell(0) == null) {
                        break;
                    }
                    String programName = currentRow.getCell(0).getStringCellValue().trim().replace(' ', '-');
                    String courseId = currentRow.getCell(1).getStringCellValue() != null ? currentRow.getCell(1).getStringCellValue() : "";
                    String courseName = currentRow.getCell(2).getStringCellValue() != null ? currentRow.getCell(2).getStringCellValue() : "";
                    String userMail = currentRow.getCell(3).getStringCellValue() != null ? currentRow.getCell(3).getStringCellValue() : "";
                    String quizzesScore = currentRow.getCell(4).getStringCellValue() != null ? currentRow.getCell(4).getStringCellValue() : "";
                    String quizzesRate = currentRow.getCell(6).getStringCellValue() != null ? currentRow.getCell(6).getStringCellValue() : "";
                    String[] quizzesScoreArr = quizzesScore.split(",");

                    String[] quizzesRateArr = quizzesRate.split(",");

                    int numberOfQuizzes = 0;
                    String quizNo = "";
                    String quizScore = "";
                    String conditionalRate = "";

                    if (quizzesScore.split(",").length == quizzesRate.split(",").length) {
                        numberOfQuizzes = quizzesScore.split(",").length;
                    } else {
                        throw new ActionExecutionException(LENGTH_NOT_EQUAL);
                    }

                    Node programNode = programRepository.getProgramByPath(programName)
                            .orElseThrow(() -> new ActionExecutionException(PROGRAM_NOT_EXIST));
                    String programId = programNode.getIdentifier();

                    Node userNode = userRepository.findByName(userMail)
                            .orElseThrow(() -> new ActionExecutionException(USER_NOT_EXIST));
                    String userId = userNode.getIdentifier();

                    Node enrolProgramNode = enrolProgramRepository.findByProgramUser(userId, programId)
                            .orElseThrow(() -> new ActionExecutionException(ENROL_PROGRAM_NOT_EXIST));
                    String enrolProgramId = enrolProgramNode.getIdentifier();
                    String enrolProgramName = enrolProgramNode.getName();

                    Node courseNode = courseRepository.findByCourseId(courseId)
                            .orElseThrow(() -> new ActionExecutionException(COURSE_NOT_EXIST));
                    String courseUUId = courseNode.getIdentifier();

                    Node courseResultNode;
                    Node quizzesNode;

                    var courseResult = courseResultRepository.findByCourseAndEnrolProgram(enrolProgramId, courseUUId);
                    if (courseResult.isPresent()) {
                        courseResultNode = courseResult.get();
                        quizzesNode = NodeUtils.getSubNodes(courseResult.get()).stream().findFirst().get();
                    } else {
                        courseResultNode = courseResultRepository.createNode(courseName.replace("/", "-")).get();
                        NodeUtils.addNode(courseResultNode, QUIZZES_NODE_NAME, QUIZZES_NODE_TYPE);
                        quizzesNode = NodeUtils.getSubNodes(courseResultNode).stream().findFirst().get();
                    }

                    PropertyUtil.setProperty(courseResultNode, "name", enrolProgramName + "-" + courseName);
                    PropertyUtil.setProperty(courseResultNode, "course", courseUUId);
                    PropertyUtil.setProperty(courseResultNode, "program", enrolProgramId);


                    if (!scoreIsBlank(currentRow.getCell(4))) {
                        for (int i = 0; i < numberOfQuizzes; i++) {
                            Node quizNode = null;
                            if (!quizzesScoreArr[i].equals("-")) {
                                if (!isValidQuizScore(quizzesScoreArr[i])) {
                                    throw new ActionExecutionException(INVALID_QUIZ_SCORE);
                                }

                                if (NodeUtils.getChildNode(quizzesNode, "0".concat(String.valueOf(i))).isEmpty()){
                                    NodeUtils.addNode(quizzesNode, "0".concat(String.valueOf(i)), QUIZZES_NODE_TYPE);
                                }
                                quizNode = NodeUtils.getChildNode(quizzesNode, "0".concat(String.valueOf(i))).get();
                                PropertyUtil.setProperty(quizNode, "quizNo", String.valueOf(i + 1));
                                PropertyUtil.setProperty(quizNode, "quizScore", quizzesScoreArr[i]);
                                PropertyUtil.setProperty(quizNode, "conditionalRate", null);

                                if (!scoreIsBlank(currentRow.getCell(6))) {
                                    if (!quizzesRateArr[i].equals("-")) {
                                        if (!isValidQuizRate(quizzesRateArr[i])) {
                                            throw new ActionExecutionException(INVALID_QUIZ_RATE);
                                        }
                                        quizNode = NodeUtils.getChildNode(quizzesNode, "0".concat(String.valueOf(i))).get();
                                        PropertyUtil.setProperty(quizNode, "conditionalRate", String.valueOf(quizzesRateArr[i]));
                                    }
                                } else {
                                    PropertyUtil.setProperty(quizNode, "conditionalRate", null);
                                }
                            }
                        }
                    }

                    if (!scoreIsBlank(currentRow.getCell(5))) {
                        double homeWork = currentRow.getCell(5).getNumericCellValue();
                        if (!isValidQuizScore(String.valueOf(homeWork))) {
                            throw new ActionExecutionException(INVALID_HOMEWORK_SCORE);
                        }
                        PropertyUtil.setProperty(courseResultNode, "homework", BigDecimal.valueOf(homeWork));
                    } else {
                        PropertyUtil.setProperty(courseResultNode, "homework", null);
                    }

                    if (!scoreIsBlank(currentRow.getCell(7))) {
                        double score = currentRow.getCell(7).getNumericCellValue();
                        if (!isValidQuizScore(String.valueOf(score))) {
                            throw new ActionExecutionException(INVALID_QUIZ_SCORE);
                        }
                        PropertyUtil.setProperty(courseResultNode, "score", BigDecimal.valueOf(score));
                    } else {
                        PropertyUtil.setProperty(courseResultNode, "score", null);
                    }

                    courseResultRepository.save();
                } catch (RepositoryException e) {
                    log.warn("Can't get or set property because {}", e.getMessage());
                } catch (Exception e) {
                    log.warn("Fail to import at row {} because {}", currentRow.getRowNum(), e.getMessage());
                }
            }
        }

        String result = String.join(System.lineSeparator(), new ArrayList<>(listError));
        if (StringUtils.isNotEmpty(result)) {
            dialogPresenter.closeDialog();
            throw new ActionExecutionException(result);
        }
        uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Import successful");
    }

    private boolean scoreIsBlank(Cell scoreCell) {
        return (CellType.BLANK).equals(scoreCell.getCellType());
    }

    private boolean isValidQuizScore(String score) {
        try {
            double value = Double.parseDouble(score);
            return value >= 0.0 && value <= 100.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidQuizRate(String rate) {
        try {
            double value = Double.parseDouble(rate);
            return value >= 0.0 && value <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void importExcelSession(Sheet sheet, String programName) throws ActionExecutionException, RepositoryException {
        Node nodeProgram = programRepository.getProgramByPath(programName)
                .orElseThrow(() -> new ActionExecutionException(PROGRAM_NOT_EXIST));
        String programId = nodeProgram.getIdentifier();
        Optional<Node> programCourse = programCourseRepository.findByProgramId(programId);
        if (programCourse.isPresent()) {
            Node nodeProgramCourse = programCourse.get();
            importSession(sheet, nodeProgramCourse.getIdentifier());
        } else {
            listError.add(String.format("Program [%s] in sheet Session dose not exist", programName));
        }
    }

    private void importCourse(Sheet sheet, String programId, Node nodeProgramCourse, boolean hasProgramCourse) throws ActionExecutionException {
        listError = new HashSet<>();
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() > 1) {
                try {
                    if (currentRow.getCell(0) == null) {
                        break;
                    }
                    String courseId = currentRow.getCell(0).getStringCellValue();
                    String courseName = currentRow.getCell(1) != null ? currentRow.getCell(1).getStringCellValue() : "";
                    String description = currentRow.getCell(2) != null ? currentRow.getCell(2).getStringCellValue() : "";
                    String outline = currentRow.getCell(3) != null ? currentRow.getCell(3).getStringCellValue() : "";
                    boolean isOnline = "online".equals(currentRow.getCell(4) != null ? currentRow.getCell(4).getStringCellValue().toLowerCase() : "offline");
                    String prerequisites = currentRow.getCell(5) != null ? currentRow.getCell(5).getStringCellValue() : "";
                    String groupName = currentRow.getCell(6) != null ? currentRow.getCell(6).getStringCellValue() : "Common";
                    String group = getIdByDisplayName(groupName);
                    boolean compulsory = "Y".equals(currentRow.getCell(7) != null ? currentRow.getCell(7).getStringCellValue().toUpperCase() : "N");
                    String courseStatus = currentRow.getCell(8) != null ? currentRow.getCell(8).getStringCellValue() : CourseStatus.OPEN.getDisplayName();
                    String semester = currentRow.getCell(9) != null ? currentRow.getCell(9).getStringCellValue() : "";
                    String categoryName = currentRow.getCell(11).getStringCellValue();
                    String category = getIdByDisplayName(categoryName);

                    Node nodeCourse;
                    var course = courseRepository.findByCourseId(courseId);
                    if (course.isPresent()) {
                        nodeCourse = course.get();
                    } else {
                        nodeCourse = courseRepository.createNode(nodeNameHelper.getValidatedName(courseName)).get();
                    }
                    PropertyUtil.setProperty(nodeCourse, "name", courseName);
                    PropertyUtil.setProperty(nodeCourse, "courseId", courseId);
                    PropertyUtil.setProperty(nodeCourse, "description", description);
                    PropertyUtil.setProperty(nodeCourse, "outline", outline);
                    PropertyUtil.setProperty(nodeCourse, "online", isOnline);

                    if (StringUtils.isEmpty(group)) {
                        listError.add(String.format("Group [%S] dose not exist", groupName));
                    } else {
                        PropertyUtil.setProperty(nodeCourse, "group", group);
                    }
                    if (StringUtils.isEmpty(category)) {
                        listError.add(String.format("Category [%S] dose not exist", categoryName));
                    } else {
                        PropertyUtil.setProperty(nodeCourse, "category", category);
                    }
                    if (StringUtils.isNotEmpty(prerequisites)) {
                        List<String> listPrerequisites = new ArrayList<>();
                        var list = prerequisites.split(",");
                        for (String itm : list) {
                            var prerequisite = courseRepository.findByCourseId(itm);
                            if (prerequisite.isEmpty()) {
                                listError.add(String.format("Prerequisites [%S] dose not exist", itm));
                            } else {
                                listPrerequisites.add(prerequisite.get().getIdentifier());
                            }
                        }
                        if (!listPrerequisites.isEmpty()) {
                            PropertyUtil.setProperty(nodeCourse, "prerequisites", listPrerequisites);
                        }
                    }

                    if (hasProgramCourse) {
                        var courseInProgram = programCourseRepository.findCourseCompulsoryNodeByCourseName(nodeCourse.getIdentifier(), StringUtils.EMPTY);
                        if (courseInProgram.isPresent()) {
                            var courseItem = courseInProgram.get();
                            PropertyUtil.setProperty(courseItem, "isCompulsory", compulsory);
                        } else {
                            String courseNameId = nodeCourse.getIdentifier();
                            saveProgramCourseCompulsory(nodeProgramCourse, compulsory, courseNameId, courseStatus, semester);
                        }
                    } else {
                        String courseNameId = nodeCourse.getIdentifier();
                        PropertyUtil.setProperty(nodeProgramCourse, "program", programId);
                        saveProgramCourseCompulsory(nodeProgramCourse, compulsory, courseNameId, courseStatus, semester);
                    }
                    courseRepository.save();
                } catch (RepositoryException e) {
                    log.warn("Can't get or set property because {}", e.getMessage());
                } catch (Exception e) {
                    log.warn("Fail to import at row {} because {}", currentRow.getRowNum(), e.getMessage());
                    continue;
                }
            }
        }
        programCourseRepository.save();
    }

    private void importSession(Sheet sheet, String programCourseId) {
        Iterator<Row> iterator = sheet.iterator();
        String nextCourseId = StringUtils.EMPTY;
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() > 0) {
                try {
                    if (currentRow.getCell(0) == null || currentRow.getCell(1) == null) {
                        break;
                    }
                    UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
                    Date date = currentRow.getCell(0).getDateCellValue();
                    String courseId = currentRow.getCell(1).getStringCellValue();
                    String sessionName = currentRow.getCell(2) != null ? currentRow.getCell(2).getStringCellValue() : "";
                    String duration = currentRow.getCell(3) != null ? String.valueOf((int) currentRow.getCell(3).getNumericCellValue()) : "0";
                    String trainer = StringUtils.EMPTY, supervisor = StringUtils.EMPTY;
                    if (currentRow.getCell(4) != null) {
                        User user = userManager.getUser(currentRow.getCell(4).getStringCellValue());
                        if (user != null) {
                            trainer = user.getIdentifier();
                        }
                    }
                    if (currentRow.getCell(5) != null) {
                        User user = userManager.getUser(currentRow.getCell(5).getStringCellValue());
                        if (user != null) {
                            supervisor = user.getIdentifier();
                        }
                    }
                    var course = courseRepository.findByCourseId(courseId);
                    if (course.isPresent()) {
                        Node nodeCourse = course.get();
                        var courseInProgram = programCourseRepository.findCourseCompulsoryNodeByCourseName(nodeCourse.getIdentifier(), programCourseId);
                        if (courseInProgram.isPresent()) {
                            var courseItem = courseInProgram.get();
                            String currentCourseId = courseItem.getIdentifier();
                            if (!currentCourseId.equals(nextCourseId)) {
                                var listSession = courseItem.getNodes();
                                while (listSession.hasNext()) {
                                    try {
                                        listSession.nextNode().remove();
                                    } catch (Exception ex) {
                                        continue;
                                    }
                                }
                                nextCourseId = currentCourseId;
                            }

                            Node nodeSession = courseItem.addNode(String.format("sessions%s", courseItem.getNodes().getSize()), Constants.CONTENT_NODE);
                            PropertyUtil.setProperty(nodeSession, "date", date);
                            PropertyUtil.setProperty(nodeSession, "duration", duration);
                            PropertyUtil.setProperty(nodeSession, "supervisor", supervisor);
                            PropertyUtil.setProperty(nodeSession, "title", sessionName);
                            PropertyUtil.setProperty(nodeSession, "trainer", trainer);
                            programCourseRepository.save();
                        } else {
                            listError.add(String.format("Program-Course not contain Course [%s] in sheet Session", courseId));
                        }
                    } else {
                        listError.add(String.format("Course [%s] in sheet Session dose not exist", courseId));
                    }
                } catch (RepositoryException e) {
                    log.warn("Can't get or set property because {}", e.getMessage());
                } catch (Exception e) {
                    log.warn("Fail to import at row {} because {}", currentRow.getRowNum(), e.getMessage());
                    continue;
                }
            }
        }
    }

    private void saveProgramCourseCompulsory(Node nodeProgramCourse, boolean compulsory, String courseNameId, String courseStatus, String semester) throws RepositoryException {
        List<Node> list = Lists.newArrayList(nodeProgramCourse.getNodes());
        Node courseItem = list.stream().filter(itm -> courseNameId.equals(PropertyUtil.getString(itm, "courseName"))).findFirst()
                .orElse(JcrUtils.getOrAddNode(nodeProgramCourse, String.format("courses%s", nodeProgramCourse.getNodes().getSize()), Constants.CONTENT_NODE));
        PropertyUtil.setProperty(courseItem, "isCompulsory", compulsory);
        PropertyUtil.setProperty(courseItem, "courseName", courseNameId);
        PropertyUtil.setProperty(courseItem, "status", getIdByDisplayName(courseStatus));
        PropertyUtil.setProperty(courseItem, "semester", semester);
    }

    private String getIdByDisplayName(String displayName) {
        var result = categoryRepository.findByDisplayName(displayName);
        if (result.isPresent()) {
            try {
                return result.get().getIdentifier();
            } catch (RepositoryException e) {
                log.warn("Can't get property because {}", e.getMessage());
            }
        }
        return StringUtils.EMPTY;
    }

    private String generateNodeName(String programId) {
        List<String> names = new ArrayList<>();
        programRepository.findById(programId).ifPresent(progeamNode -> {
            names.add(PropertyUtil.getString(progeamNode, "name", StringUtils.EMPTY));
            String phaseId = PropertyUtil.getString(progeamNode, "phase", StringUtils.EMPTY);
            phaseRepository.findById(phaseId).ifPresent(phaseNode -> {
                names.add(PropertyUtil.getString(phaseNode, "name", StringUtils.EMPTY));

                Optional.of(TimeUtils.toLocalDate(
                        PropertyUtil.getDate(phaseNode, "startDate", null)))
                        .ifPresent(startDate -> names.add(TimeUtils.toString(startDate)));
            });
        });
        String generatedName = String.join("-", names);
        return generatedName;
    }

    public boolean importOJTUserResult(Workbook workbook, SubAppContext uiContext, DialogPresenter dialogPresenter) throws ActionExecutionException, RepositoryException {
        Sheet sheet = workbook.getSheetAt(2);
        Iterator<Row> iterator = sheet.iterator();
        String ojtUserResultId = StringUtils.EMPTY;
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() > 0) {
                if (currentRow.getCell(0) == null) {
                    break;
                }
                UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
                String programName = currentRow.getCell(0).getStringCellValue().replace(' ', '-');
                String ojtProjectName = currentRow.getCell(1) != null ? currentRow.getCell(1).getStringCellValue() : "";
                String userName = currentRow.getCell(2) != null ? currentRow.getCell(2).getStringCellValue() : "";
                String roleName = currentRow.getCell(3) != null ? currentRow.getCell(3).getStringCellValue() : "";
                String mentorName = currentRow.getCell(4) != null ? currentRow.getCell(4).getStringCellValue() : "";
                String criteriaName = currentRow.getCell(5) != null ? currentRow.getCell(5).getStringCellValue() : "";
                double score = currentRow.getCell(6) != null ? currentRow.getCell(6).getNumericCellValue() : 0.0;
                String comment = currentRow.getCell(7) != null ? currentRow.getCell(7).getStringCellValue() : "";
                String note = currentRow.getCell(8) != null ? currentRow.getCell(8).getStringCellValue() : "";

                String programId = programRepository.getProgramByPath(programName)
                        .orElseThrow(() -> new ActionExecutionException(PROGRAM_NOT_EXIST))
                        .getIdentifier();

                String userId = StringUtils.EMPTY, mentorId = StringUtils.EMPTY, ojtProjectId = StringUtils.EMPTY, criteriaId = StringUtils.EMPTY;
                String roleId = getIdByDisplayName(roleName);
                if (StringUtils.isNotEmpty(userName)) {
                    User user = userManager.getUser(userName);
                    if (user != null) {
                        userId = user.getIdentifier();
                    }
                }

                if (StringUtils.isNotEmpty(mentorName)) {
                    User user = userManager.getUser(mentorName);
                    if (user != null) {
                        mentorId = user.getIdentifier();
                    }
                }

                String enrolProgramId = enrolProgramRepository.findByProgramUser(userId, programId)
                        .orElseThrow(() -> new ActionExecutionException(String.format("User [%s] not enrol program [%s]!", userName, programName)))
                        .getIdentifier();

                if (StringUtils.isNotEmpty(ojtProjectName)) {
                    ojtProjectId = ojtProjectRepository.findByProjectName(ojtProjectName)
                            .orElseThrow(() -> new ActionExecutionException(String.format("OJT Project [%s] not found!", ojtProjectName)))
                            .getIdentifier();
                }

                if (StringUtils.isNotEmpty(criteriaName) && !"Evaluation".equals(criteriaName)) {
                    criteriaId = assessmentCriteriaRepository.findByName(criteriaName)
                            .orElseThrow(() -> new ActionExecutionException(String.format("Criteria [%s] not found!", criteriaName)))
                            .getIdentifier();
                }

                if ("Evaluation".equals(criteriaName)) {
                    String nodeName = String.format("%s-%s-%s", programName, ojtProjectName, userName);
                    var node = ojtUserResultRepository.getOrAddNode(nodeName).get();
                    ojtUserResultId = node.getIdentifier();
                    var list = ojtUserAssessmentRepository.findByOjtUserResult(ojtUserResultId);
                    ojtUserAssessmentRepository.save();
                    ojtUserAssessmentRepository.deleteNodes(list);
                    PropertyUtil.setProperty(node, "name", nodeName);
                    PropertyUtil.setProperty(node, "userEnrolProgram", enrolProgramId);
                    PropertyUtil.setProperty(node, "ojtProject", ojtProjectId);
                    PropertyUtil.setProperty(node, "role", roleId);
                    PropertyUtil.setProperty(node, "mentor", mentorId);
                    PropertyUtil.setProperty(node, "comment", comment);
                    PropertyUtil.setProperty(node, "note", note);
                    node.setProperty("ojtEvaluation", BigDecimal.valueOf(score));
                    ojtUserResultRepository.save();
                } else if (StringUtils.isNotEmpty(ojtUserResultId)) {
                    String nodeName = String.format("%s-%s-%s-%s", programName, ojtProjectName, userName, criteriaName);
                    var node = ojtUserAssessmentRepository.createNode(nodeName).get();
                    PropertyUtil.setProperty(node, "name", nodeName);
                    PropertyUtil.setProperty(node, "ojtUserResult", ojtUserResultId);
                    PropertyUtil.setProperty(node, "assessment", criteriaId);
                    PropertyUtil.setProperty(node, "assessmentComment", comment);
                    node.setProperty("assessmentScore", BigDecimal.valueOf(score));
                    ojtUserAssessmentRepository.save();
                }
            }
        }
        uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Import successful");
        return true;
    }

    public boolean importAttitudeAssessmentsResult(Workbook workbook,
                                                   SubAppContext uiContext,
                                                   DialogPresenter dialogPresenter) throws ActionExecutionException, RepositoryException {

        List<AttitudeDto> attitudes = getAttitudeAssessmentsResultData(workbook);
        for (AttitudeDto attitude : attitudes) {
            if (EVALUATION.equals(attitude.getAssessmentCriteriaName())) {
                userAttitudeResultRepository
                        .findByUserEnrolProgram(attitude.getUserProgramId())
                        .ifPresentOrElse(node -> {
                            Map<String, Object> properties = new HashMap<>();
                            properties.put("attitudeEvaluation", new BigDecimal(attitude.getScore()));
                            properties.put("generalComment", attitude.getComment());
                            updateNode(node, properties);

                            userAttitudeResultRepository.save(node);
                        }, () -> {
                            String nodeName = String.format("%s-%s",
                                    attitude.getProgramName().replace(" ", "-"),
                                    attitude.getUserName());

                            userAttitudeResultRepository.createNode(nodeName).ifPresent(node -> {
                                Map<String, Object> properties = new HashMap<>();

                                properties.put("name", nodeName);
                                properties.put("userEnrolProgram", attitude.getUserProgramId());
                                properties.put("attitudeEvaluation", new BigDecimal(attitude.getScore()));
                                properties.put("generalComment", attitude.getComment());
                                updateNode(node, properties);
                                userAttitudeResultRepository.save(node);
                            });
                        });
            } else {

                String attitudeResultId = userAttitudeResultRepository
                        .findByUserEnrolProgram(attitude.getUserProgramId())
                        .map(node -> PropertyUtil.getString(node, JCR_UUID, StringUtils.EMPTY))
                        .orElse(StringUtils.EMPTY);

                Optional<Node> assessmentCriteriaNodeOptional = assessmentCriteriaRepository
                        .findByName(attitude.getAssessmentCriteriaName());

                String assessmentCriteriaId = assessmentCriteriaNodeOptional
                        .map(node -> PropertyUtil.getString(node, JCR_UUID, StringUtils.EMPTY))
                        .orElse(StringUtils.EMPTY);

                Optional<Node> userAttitudeAssessmentOptional = userAttitudeAssessmentRepository
                        .findByUserAttitudeResultAndAssessment(attitudeResultId, assessmentCriteriaId);

                if (userAttitudeAssessmentOptional.isPresent()) {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("assessmentScore", new BigDecimal(attitude.getScore()));
                    properties.put("assessmentComment", attitude.getComment());

                    updateNode(userAttitudeAssessmentOptional.get(), properties);
                    userAttitudeAssessmentRepository.save(userAttitudeAssessmentOptional.get());
                } else {
                    String nodeName = String.format("%s-%s-%s",
                            attitude.getProgramName().replace(" ", "-"),
                            attitude.getUserName(),
                            attitude.getAssessmentCriteriaName().replace(" ", "-"));

                    Node newNode = userAttitudeAssessmentRepository.createNode(nodeName).get();

                    Map<String, Object> properties = new HashMap<>();
                    properties.put("name", nodeName);
                    properties.put("userAttitudeResult", attitudeResultId);
                    properties.put("assessment", assessmentCriteriaId);
                    properties.put("assessmentScore", new BigDecimal(attitude.getScore()));
                    properties.put("assessmentComment", attitude.getComment());

                    updateNode(newNode, properties);
                    userAttitudeAssessmentRepository.save(newNode);
                }
            }
        }
        uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Import user attitude assessment successful");
        return true;
    }

    public boolean importAssessment(Workbook workbook, SubAppContext uiContext) throws ActionExecutionException, RepositoryException {
        Sheet sheet = workbook.getSheetAt(3);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() > 0) {
                if (currentRow.getCell(0) == null) {
                    break;
                }
                String programName = Optional.ofNullable(currentRow.getCell(0))
                        .map(cell -> cell.getStringCellValue().replace(' ', '-'))
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Program name null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String typeName = Optional.ofNullable(currentRow.getCell(1))
                        .map(Cell::getStringCellValue)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Assessment type null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String groupName = Optional.ofNullable(currentRow.getCell(2))
                        .map(Cell::getStringCellValue)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Assessment group null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String assessmentId = Optional.ofNullable(currentRow.getCell(3))
                        .map(Cell::getStringCellValue)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Assessment ID null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String assessmentName = Optional.ofNullable(currentRow.getCell(4))
                        .map(Cell::getStringCellValue)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Assessment name null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String programId = programRepository.getProgramByPath(programName)
                        .orElseThrow(() -> new ActionExecutionException(PROGRAM_NOT_EXIST))
                        .getIdentifier();

                String typeId = categoryRepository.findByDisplayName(typeName)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Assessment type not exist at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName()))).getIdentifier();
                String groupId = categoryRepository.findAssessmentGroupByDisplayName(groupName)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Assessment group not exist at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName()))).getIdentifier();

                var nodeAssessment = assessmentCriteriaRepository.findByAssessmentId(assessmentId);
                if (nodeAssessment.isPresent()) {
                    var node = nodeAssessment.get();
                    PropertyUtil.setProperty(node, "name", assessmentName);
                    PropertyUtil.setProperty(node, "program", programId);
                    PropertyUtil.setProperty(node, "assessmentType", typeId);
                    PropertyUtil.setProperty(node, "assessmentGroup", groupId);
                } else {
                    var node = assessmentCriteriaRepository.createNode(assessmentName).get();
                    PropertyUtil.setProperty(node, "code", assessmentId);
                    PropertyUtil.setProperty(node, "name", assessmentName);
                    PropertyUtil.setProperty(node, "program", programId);
                    PropertyUtil.setProperty(node, "assessmentType", typeId);
                    PropertyUtil.setProperty(node, "assessmentGroup", groupId);
                }
                assessmentCriteriaRepository.save();
            }
        }
        uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Import successful");
        return true;
    }

    private List<AttitudeDto> getAttitudeAssessmentsResultData(Workbook workbook) throws ActionExecutionException, RepositoryException {
        Sheet sheet = workbook.getSheetAt(1);
        Iterator<Row> rowIterator = sheet.rowIterator();
        List<AttitudeDto> attitudes = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();
            if (currentRow.getRowNum() > 0) {
                if (currentRow.getCell(0) == null) {
                    break;
                }
                String programName = Optional.ofNullable(currentRow.getCell(0))
                        .map(cell -> cell.getStringCellValue().replace(' ', '-'))
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Program name null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String userName = Optional.ofNullable(currentRow.getCell(1))
                        .map(Cell::getStringCellValue)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("User name null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String competency = Optional.ofNullable(currentRow.getCell(2))
                        .map(Cell::getStringCellValue)
                        .orElse(StringUtils.EMPTY);

                String behavior = Optional.ofNullable(currentRow.getCell(3))
                        .map(Cell::getStringCellValue)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Behavior null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                if (StringUtils.isEmpty(competency) && !EVALUATION.equals(behavior)) {
                    throw new ActionExecutionException(String.format("Behavior at row [%s] , at sheet [%s] must be [Evaluation]. Please check excel file again",
                            currentRow.getRowNum(), sheet.getSheetName()));
                }

                double score = Optional.ofNullable(currentRow.getCell(4))
                        .map(Cell::getNumericCellValue)
                        .orElseThrow(() ->
                                new ActionExecutionException(String.format("Score null at row [%s] , at sheet [%s]. Please check excel file again",
                                        currentRow.getRowNum(), sheet.getSheetName())));

                String comment = Optional.ofNullable(currentRow.getCell(5))
                        .map(Cell::getStringCellValue)
                        .orElse(StringUtils.EMPTY);

                AttitudeDto attitude = new AttitudeDto();

                String programId = programRepository.getProgramByPath(programName)
                        .orElseThrow(() -> new ActionExecutionException(String.format("Program [%s] at line [%s] do not exist. Please check excel file again",
                                programName, currentRow.getRowNum())))
                        .getIdentifier();

                String userId = userRepository.findByName(userName)
                        .orElseThrow(() -> new ActionExecutionException(String.format("User [%s] at line [%s] don't exist. Please check excel file again",
                                userName, currentRow.getRowNum())))
                        .getIdentifier();

                String userProgramId = enrolProgramRepository.findByProgramUser(userId, programId)
                        .orElseThrow(() -> new ActionExecutionException(String.format("User [%s] at line [%s]not enrol program [%s]!", userName, currentRow.getRowNum(), programName)))
                        .getIdentifier();

                attitude.setUserProgramId(userProgramId);
                attitude.setProgramName(programName);
                attitude.setUserId(userId);
                attitude.setUserName(userName);
                attitude.setProgramId(programId);
                attitude.setScore(score);
                attitude.setComment(comment);
                attitude.setAssessmentCriteriaId(StringUtils.EMPTY);
                attitude.setAssessmentCriteriaName(EVALUATION);

                if (!EVALUATION.equals(behavior)) {
                    String assessmentGroupId = categoryRepository
                            .findByDisplayName(competency)
                            .map(node -> PropertyUtil.getString(node, JCR_UUID, ""))
                            .orElse("");

                    Node assessmentTypeNode = categoryRepository.findAllAssessmentType().stream()
                            .filter(node -> CollectionUtils.isNotEmpty(NodeUtils.getSubNodes(node)))
                            .filter(node -> {

                                Optional<Node> relatedUUIDParentNodeOptional = NodeUtils.getChildNode(node, RELATED_UUID);
                                List<String> relatedUUIDs = new ArrayList<>();
                                if (relatedUUIDParentNodeOptional.isPresent()) {
                                    relatedUUIDs = NodeUtils
                                            .getSubNodes(relatedUUIDParentNodeOptional.get())
                                            .stream()
                                            .map(relatedUUIDNode -> PropertyUtil.getString(relatedUUIDNode, RELATED_UUID, null))
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList());
                                }
                                return relatedUUIDs.contains(assessmentGroupId);
                            })
                            .findFirst()
                            .orElseThrow(() -> new ActionExecutionException(String.format("Can not found assessment type of [%s] group at line [%s].", competency, currentRow.getRowNum())));

                    String assessmentTypeId = assessmentTypeNode.getIdentifier();

                    String assessmentCriteriaId = assessmentCriteriaRepository
                            .findByNameAndAssessmentGroupId(behavior, assessmentGroupId)
                            .map(node -> PropertyUtil.getString(node, JCR_UUID, StringUtils.EMPTY))
                            .orElseThrow(() -> new ActionExecutionException(String.format("Can not found assessment criteria [%s] at line [%s]", behavior, currentRow.getRowNum())));

                    attitude.setAssessmentCriteriaId(assessmentCriteriaId);
                    attitude.setAssessmentCriteriaName(behavior);
                    attitude.setAssessmentGroupId(assessmentGroupId);
                    attitude.setAssessmentTypeId(assessmentTypeId);

                }

                attitudes.add(attitude);
            }
        }
        return attitudes;
    }

    private void updateNode(Node node, Map<String, Object> values) {
        try {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                PropertyUtil.setProperty(node, entry.getKey(), entry.getValue());
            }
        } catch (RepositoryException e) {
            log.warn("can not update property for user tatitude assessment because: {}", e.getMessage());
        }
    }
}