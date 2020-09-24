package vn.ekino.certificate.service;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import vn.ekino.certificate.dto.SessionDto;
import vn.ekino.certificate.dto.TrainingMaterialDto;
import vn.ekino.certificate.repository.CourseRepository;
import vn.ekino.certificate.repository.CourseResultRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class CourseService {
    private long totalCourse;
    private List<String> courseInfomation = new ArrayList<>();

    private final ProgramCourseService programCourseService;

    private final EnrolProgramService enrolProgramService;
    private final CourseRepository courseRepository;
    private final CourseResultRepository courseResultRepository;
    private final PublishingService publishingService;
    private final EventChangeService eventChangeService;

    @Inject
    public CourseService(ProgramCourseService programCourseService,
                         EnrolProgramService enrolProgramService,
                         CourseRepository courseRepository, CourseResultRepository courseResultRepository, PublishingService publishingService, EventChangeService eventChangeService) {

        this.programCourseService = programCourseService;
        this.enrolProgramService = enrolProgramService;
        this.courseRepository = courseRepository;
        this.courseResultRepository = courseResultRepository;
        this.publishingService = publishingService;
        this.eventChangeService = eventChangeService;
    }

    public List<SessionDto> getListSessionOfCourse(String courseId) {
        Optional<Node> courseOptional = courseRepository.findById(courseId);

        if (courseOptional.isPresent()) {
            List<Node> sessionNodes = courseRepository.findSessionsNodeByCoursePath(NodeUtil.getNodePathIfPossible(courseOptional.get()));
            if (CollectionUtils.isNotEmpty(sessionNodes)) {
                return sessionNodes
                        .stream()
                        .map(sessionNode -> MapperUtils.nodeToObject(sessionNode, SessionDto.class).get())
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    public List<TrainingMaterialDto> getListTrainingMaterialOfCourse(String courseId) {
        Optional<Node> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            List<Node> trainingMaterialNodes = courseRepository
                    .findTrainingMaterialsNodeByCoursePath(NodeUtil.getNodePathIfPossible(courseOptional.get()));
            if (CollectionUtils.isNotEmpty(trainingMaterialNodes)) {
                return trainingMaterialNodes
                        .stream()
                        .map(trainingMaterialNode
                                -> MapperUtils.nodeToObject(trainingMaterialNode, TrainingMaterialDto.class).get())
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public void dataMigration() {
        List<String> allowUsers = List.of("superuser", "superuser1");
        if (!allowUsers.contains(MgnlContext.getUser().getName())) {
            return;
        }
        final String nodeQuiz = "quizzes";
        final String quizChild = "00";
        var list = courseResultRepository.findAll();
            for (Node itm : list) {
                try {
                    Node node = itm.hasNode(nodeQuiz) ? itm.getNode(nodeQuiz) : itm.addNode(nodeQuiz, Constants.CONTENT_NODE);
                    Node child = node.hasNode(quizChild) ? node.getNode(quizChild) : node.addNode(quizChild, Constants.CONTENT_NODE);
                    BigDecimal quiz = itm.getProperty("quiz").getDecimal();
                    BigDecimal conditionalRate = itm.getProperty("conditionalRate").getDecimal();
                    String course = itm.getProperty("course").getString();
                    String userEnrolProgram = itm.getProperty("program").getString();
                    String numQuiz = eventChangeService.findNumberQuizzesByCourseId(course, userEnrolProgram);

                    PropertyUtil.setProperty(child, "quizNo", String.valueOf(1));
                    PropertyUtil.setProperty(child, "quizScore", quiz.toString());
                    PropertyUtil.setProperty(child, "conditionalRate", conditionalRate.toString());
                    PropertyUtil.setProperty(itm, "numQuiz", numQuiz);

                    courseResultRepository.save();
                } catch (RepositoryException e) {
                    log.warn("data migration fail because {}", e.getMessage());
                }
            }
        publishingService.publish(courseResultRepository.findAll());
    }
}
