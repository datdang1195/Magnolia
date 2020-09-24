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
public class OJTUserAssessmentRepository extends NodeRepository {
    public static final String WORKSPACE = "OJT-User-Assessment";
    private static final String NODE_TYPE = "mgnl:ojtUserAssessment";

    public OJTUserAssessmentRepository() {this(WORKSPACE, NODE_TYPE);}

    public OJTUserAssessmentRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> findByOjtUserResult(String id) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("ojtUserResult", List.of(id)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }
}