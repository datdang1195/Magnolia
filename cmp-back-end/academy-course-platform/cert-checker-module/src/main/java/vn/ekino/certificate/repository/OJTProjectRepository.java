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
public class OJTProjectRepository extends NodeRepository {
    public static final String OJT_PROJECT_WORKSPACE = "OJT-Project";
    private static final String OJT_PROJECT_NODE_TYPE = "mgnl:ojtProject";

    public OJTProjectRepository() {this(OJT_PROJECT_WORKSPACE, OJT_PROJECT_NODE_TYPE);}

    public OJTProjectRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findByProjectName(String projectName) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("projectName", List.of(projectName)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Node> findByURIName(String uriName) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("uriName", List.of(uriName)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public List<Node> findAllByProgram(String programId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("program", List.of(programId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }

}

