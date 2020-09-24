package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ProgramRepository extends NodeRepository {
    private static final String PROGRAM_WORKSPACE = "programs";
    private static final String PROGRAM_NODE_TYPE = "mgnl:program";

    public ProgramRepository() {
        this(PROGRAM_WORKSPACE, PROGRAM_NODE_TYPE);
    }

    public ProgramRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> findProgramByPhase(String phaseId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("phase", List.of(phaseId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find all node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> getProgramByPath(String path) {
        try {
            Session session = MgnlContext.getJCRSession(workspace);
            Node rootNode = session.getRootNode();
            if (rootNode.hasNode(path)) {
                return  Optional.of(rootNode.getNode(path));
            }
        } catch (RepositoryException e) {
            log.warn("can't get node because: {}", e.getMessage());
        }
        return Optional.empty();
    }


}
