package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import vn.ekino.certificate.dto.enumeration.EnrolStatus;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class EnrolProgramRepository extends NodeRepository {
    public static final String ENROL_PROGRAM_WORKSPACE = "enrolledProgram";
    private static final String ENROL_PROGRAM_NODE_TYPE = "mgnl:enrolledProgram";
    public static final String PROPERTY_USER = "user";
    public static final String PROPERTY_PROGRAM = "program";
    public static final String ENROL_STATUS = "enrollStatus";

    public EnrolProgramRepository() {
        this(ENROL_PROGRAM_WORKSPACE, ENROL_PROGRAM_NODE_TYPE);
    }

    public EnrolProgramRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findByProgramUser(String userId, String programId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(PROPERTY_USER, List.of(userId), PROPERTY_PROGRAM, List.of(programId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Node> findByProgramAndUserAndParticipantStatus(String userId, String programId, String participantStatus) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(PROPERTY_USER, List.of(userId)
                    , PROPERTY_PROGRAM, List.of(programId)
                    , "participantStatus", List.of(participantStatus)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public List<Node> getAllByUserApproved(String userId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(PROPERTY_USER, List.of(userId), ENROL_STATUS, List.of(EnrolStatus.APPROVED.getStatus())));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", ENROL_PROGRAM_WORKSPACE, ENROL_PROGRAM_NODE_TYPE, e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Node> getAllNodeByUserId(String userId) {
        return findAllByCondition(userId, PROPERTY_USER);
    }

    public List<Node> findAllUserByProgram(String programId) {
        return findAllByCondition(programId, PROPERTY_PROGRAM);
    }

    public List<Node> findAllApprovedUsersByProgram(String programId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(PROPERTY_PROGRAM, List.of(programId), ENROL_STATUS, List.of(EnrolStatus.APPROVED.getStatus())));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", ENROL_PROGRAM_WORKSPACE, ENROL_PROGRAM_NODE_TYPE, e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Node> findAllByStatus(String status) {
        return findAllByCondition(status, ENROL_STATUS);
    }

    @NotNull
    private List<Node> findAllByCondition(String programId, String propertyProgram) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(propertyProgram, List.of(programId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", ENROL_PROGRAM_WORKSPACE, ENROL_PROGRAM_NODE_TYPE, e.getMessage());
        }
        return Collections.emptyList();
    }
}
