package vn.ekino.certificate.repository;

import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UserAttitudeAssessmentRepository extends NodeRepository {

    public static final String USER_ATTITUDE_ASSESSMENT_WORKSPACE = "user-attitude-assessments";
    public static final String USER_ATTITUDE_ASSESSMENT_TYPE = "mgnl:userAttitudeAssessment";

    public UserAttitudeAssessmentRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public UserAttitudeAssessmentRepository() {
        this(USER_ATTITUDE_ASSESSMENT_WORKSPACE, USER_ATTITUDE_ASSESSMENT_TYPE);
    }

    public Optional<Node> findByUserAttitudeResultAndAssessment(String userAttitudeResultId, String assessmentId) {
        try {

            Query query = buildQuery(workspace,
                    List.of(nodeType),
                    Map.of("userAttitudeResult", List.of(userAttitudeResultId),
                            "assessment", List.of(assessmentId)));

            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }
}
