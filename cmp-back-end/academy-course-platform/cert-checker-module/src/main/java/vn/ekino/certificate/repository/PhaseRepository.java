package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class PhaseRepository extends NodeRepository {
    private static final String PHASE_WORKSPACE = "phases";
    private static final String PHASE_NODE_TYPE = "mgnl:phase";

    public PhaseRepository() {
        this(PHASE_WORKSPACE, PHASE_NODE_TYPE);
    }

    public PhaseRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> getPhases() {
        try {
            Session session = MgnlContext.getJCRSession(PHASE_WORKSPACE);
            QueryManager qm = session.getWorkspace().getQueryManager();

            Query query = qm.createQuery("SELECT * FROM [nt:base] "
                    + "WHERE ([jcr:primaryType] = 'mgnl:phase')", Query.JCR_SQL2);

            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", PHASE_NODE_TYPE, PHASE_WORKSPACE, e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Node> findAllByPhaseCategory(String categoryId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("phase", List.of(categoryId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find all node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Collections.emptyList();
    }

}