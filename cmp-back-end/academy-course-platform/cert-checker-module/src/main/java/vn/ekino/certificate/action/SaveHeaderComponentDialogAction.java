package vn.ekino.certificate.action;

import com.vaadin.v7.data.Item;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.admincentral.dialog.action.SaveDialogAction;
import info.magnolia.ui.admincentral.dialog.action.SaveDialogActionDefinition;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import vn.ekino.certificate.repository.UserRolesRepository;
import vn.ekino.certificate.repository.WebsiteRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.NodeUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class SaveHeaderComponentDialogAction extends SaveDialogAction {

    private final WebsiteRepository websiteRepository;
    private final UserRolesRepository userRolesRepository;
    @Inject
    public SaveHeaderComponentDialogAction(SaveDialogActionDefinition definition,
                                           Item item,
                                           EditorValidator validator,
                                           EditorCallback callback,
                                           WebsiteRepository websiteRepository,
                                           UserRolesRepository userRolesRepository) {
        super(definition, item, validator, callback);
        this.websiteRepository = websiteRepository;
        this.userRolesRepository = userRolesRepository;
    }

    @Override
    public void execute() {
        validator.showValidation(true);
        if (validator.isValid()) {
            final JcrNodeAdapter itemChanged = (JcrNodeAdapter) item;
            try {
                final Node node = itemChanged.applyChanges();
                setNodeName(node, itemChanged);
                var result = websiteRepository.getReferenceNode(node);
                if (result.isPresent()) {
                    grantPermissionForRoleInNav(result.get());
                }
                node.getSession().save();
            } catch (final RepositoryException e) {
                log.warn("Error in get navigation node: {} ", e.getMessage());
            }
            callback.onSuccess(getDefinition().getName());
        } else {
            log.warn("Validation error(s) occurred. No save performed.");
        }
    }

    private void grantPermissionForRoleInNav(Node navNode) {
        Map<String, List<String>> denyPages = new HashMap<>();
        denyPages.put(Constants.SUPERVISOR_ROLE, new ArrayList<>());
        denyPages.put(Constants.TRAINER_ROLE, new ArrayList<>());
        denyPages.put(Constants.PARTICIPANT_ROLE, new ArrayList<>());

        List<Node> headerNavNodes = NodeUtils.getSubNodes(navNode);

        for (Node headerNavNode : headerNavNodes) {
            List<Node> subNavNodes = NodeUtils.getSubNodes(headerNavNode);
            if (CollectionUtils.isEmpty(subNavNodes)) {
                setDeniedPagesForRole(denyPages, headerNavNode, "grantedRoles", "link");
            } else {
                for (Node subNavNode : subNavNodes) {
                    setDeniedPagesForRole(denyPages, subNavNode, "subGrantedRoles", "linkSub");
                }
            }
        }
        for (Map.Entry<String, List<String>> entry : denyPages.entrySet()) {
            String role = entry.getKey();
            List<String> deniedPages = entry.getValue();
            Node userRoleNode = userRolesRepository.getUserRoleByPath(role).get();
            NodeUtils.getChildNode(userRoleNode, "acl_uri").ifPresent(userRoleChildNode -> {
                userRolesRepository.deleteNode(userRoleChildNode);
            });
            addDeniedUrl(deniedPages, userRoleNode);
        }
    }

    private void addDeniedUrl(List<String> deniedPages, Node userRoleNode) {
        try {
            Node deniedPageNodes = userRoleNode.addNode("acl_uri", Constants.CONTENT_NODE);
            for (int i = 0; i < deniedPages.size(); i++) {
                Node deniedPageNode = deniedPageNodes.addNode(String.valueOf(i), Constants.CONTENT_NODE);
                PropertyUtil.setProperty(deniedPageNode, "path", deniedPages.get(i));
                PropertyUtil.setProperty(deniedPageNode, "permissions", 0);
            }
            userRolesRepository.save();
        } catch (RepositoryException e) {
            log.warn("Error in add child node of acl_uri node: {} ", e.getMessage());
        }
    }

    private void setDeniedPagesForRole(Map<String, List<String>> denyPages, Node navNode, String grantedRoles, String link) {
        List<String> roles = (LinkedList) PropertyUtil.getPropertyValueObject(navNode, grantedRoles);
        for (String role : denyPages.keySet()) {
            if (!roles.contains(role)) {
                List<String> currentPagesOfRole = denyPages.get(role);
                currentPagesOfRole.add(PropertyUtil.getString(navNode, link));
                denyPages.put(role, currentPagesOfRole);
            }
        }
    }

}