package vn.ekino.certificate.repository;

import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

@Slf4j
public class UserRolesRepository extends NodeRepository {
    private static final String USER_ROLE_WORKSPACE = "userroles";
    private static final String USER_ROLE_NODE_TYPE = "mgnl:role";

    public UserRolesRepository() {
        this(USER_ROLE_WORKSPACE, USER_ROLE_NODE_TYPE);
    }

    public UserRolesRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> getUserRoleByPath(String path) {
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
