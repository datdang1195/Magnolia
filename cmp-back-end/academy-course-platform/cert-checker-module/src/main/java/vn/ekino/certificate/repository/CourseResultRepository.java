package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CourseResultRepository extends NodeRepository {
    public static final String COURSE_RESULT_WORKSPACE = "courseResults";
    public static final String COURSE_RESULT_NODE_TYPE = "mgnl:courseResult";
    public static final String PROPERTY_COURSE = "course";
    public static final String PROPERTY_PROGRAM = "program";

    public CourseResultRepository() {
        this(COURSE_RESULT_WORKSPACE, COURSE_RESULT_NODE_TYPE);
    }

    public CourseResultRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findByCourseAndEnrolProgram(String enrolProgramId, String courseId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType),
                    Map.of(PROPERTY_COURSE, List.of(courseId), PROPERTY_PROGRAM, List.of(enrolProgramId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public List<Node> findByUserEnrolProgram(String userEnrolProgramId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(PROPERTY_PROGRAM, List.of(userEnrolProgramId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", COURSE_RESULT_NODE_TYPE, COURSE_RESULT_WORKSPACE, e.getMessage());
        }
        return Collections.emptyList();
    }
}
