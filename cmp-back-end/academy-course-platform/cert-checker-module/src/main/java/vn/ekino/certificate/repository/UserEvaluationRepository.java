package vn.ekino.certificate.repository;

import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UserEvaluationRepository extends NodeRepository {

    public static final String WORKSPACE = "userEvaluations";
    private static final String NODE_TYPE = "mgnl:userEvaluation";

    public UserEvaluationRepository() {
        this(WORKSPACE, NODE_TYPE);
    }

    public UserEvaluationRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findByUserEnrolProgram(String userEnrolProgramId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("userProgram", List.of(userEnrolProgramId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("cannot find node with OJTProjectId {}, because {}.", e.getMessage());
        }
        return Optional.empty();
    }
    
    public List<Node> findListByUserEnrolProgram(String userEnrolProgramId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("userProgram", List.of(userEnrolProgramId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("cannot find node with OJTProjectId {}, because {}.", e.getMessage());
        }
        return Collections.emptyList();
    }
}
