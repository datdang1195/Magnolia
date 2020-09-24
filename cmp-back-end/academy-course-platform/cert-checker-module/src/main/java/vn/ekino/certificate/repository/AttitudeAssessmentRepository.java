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

@Slf4j
public class AttitudeAssessmentRepository extends NodeRepository {

    private static final String WORKSPACE = "user-attitude-assessments";
    private static final String NODE_TYPE = "mgnl:userAttitudeAssessment";

    public AttitudeAssessmentRepository() {this(WORKSPACE, NODE_TYPE);}

    public AttitudeAssessmentRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> findByAttitudeUserResult(String id) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("userAttitudeResult", List.of(id)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }
}
