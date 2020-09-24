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
public class AssessmentCriteriaRepository extends NodeRepository {
    public static final String WORKSPACE = "assessments";
    private static final String NODE_TYPE = "mgnl:assessment";

    public AssessmentCriteriaRepository() {
        this(WORKSPACE, NODE_TYPE);
    }

    public AssessmentCriteriaRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findByName(String name) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("name", List.of(name)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Node> findByNameAndAssessmentGroupId(String name, String assessmentGroupId) {
        try {
            Query query = buildQuery(workspace,
                    List.of(nodeType),
                    Map.of("name", List.of(name), "assessmentGroup", List.of(assessmentGroupId)));

            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public List<Node> findByAssessmentType(String typeId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("assessmentType", List.of(typeId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", NODE_TYPE, WORKSPACE, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> findByAssessmentId(String assessmentId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("code", List.of(assessmentId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", NODE_TYPE, WORKSPACE, e.getMessage());
        }
        return Optional.empty();
    }
}