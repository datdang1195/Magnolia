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
public class OJTUserResultRepository extends NodeRepository {

    public static final String OJT_USER_RESULT_WORKSPACE = "OJT-User-Result";
    public static final String OJT_USER_RESULT_TYPE = "mgnl:ojtUserResult";

    public OJTUserResultRepository() {
        this(OJT_USER_RESULT_WORKSPACE, OJT_USER_RESULT_TYPE);
    }

    public OJTUserResultRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> findByOJTProjectID(String ojtProjectId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("ojtProject", List.of(ojtProjectId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("cannot find node with OJTProjectId {}, because {}.", e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> findByUserEnrolProgram(String userEnrolProgramId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("userEnrolProgram", List.of(userEnrolProgramId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("cannot find node with OJTProjectId {}, because {}.", e.getMessage());
        }
        return Optional.empty();
    }
}
