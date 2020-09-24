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
public class UserAttitudeResultRepository extends NodeRepository {

    public static final String USER_ATTITUDE_RESULT_WORKSPACE = "userAttitudeResult";
    public static final String USER_ATTITUDE_RESULT_TYPE = "mgnl:userAttitudeResult";
    private static final String USER_ENROL_PROGRAM_PROPERTY = "userEnrolProgram";

    public UserAttitudeResultRepository() {
        this(USER_ATTITUDE_RESULT_WORKSPACE, USER_ATTITUDE_RESULT_TYPE);
    }

    public UserAttitudeResultRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findByUserEnrolProgram(String userEnrolProgramId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(USER_ENROL_PROGRAM_PROPERTY, List.of(userEnrolProgramId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("cannot find node because {}.", e.getMessage());
        }
        return Optional.empty();
    }
}