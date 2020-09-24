package vn.ekino.certificate.repository;

import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UserRepository extends NodeRepository {

    private static final String USER_WORKSPACE = "users";
    private static final String USER_NODE_TYPE = "mgnl:user";

    public UserRepository() {
        this(USER_WORKSPACE, USER_NODE_TYPE);
    }

    public UserRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findByName(String userName) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("name", List.of(userName)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Node> findByResetPasswordCode(String code) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("verifiedCodeForForgotPass", List.of(code)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public void removeVerifiedCodePropertyByNode(Node userNode) {
        try {
            PropertyUtil.getProperty(userNode, "verifiedCodeForForgotPass").remove();
            userNode.getSession().save();
        } catch (RepositoryException e) {
            log.warn("can't remove property of {} node in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
    }
}
