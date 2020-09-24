package vn.ekino.certificate.action;

import com.vaadin.v7.data.Item;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SilentSessionOp;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.UserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.security.app.util.UsersWorkspaceUtil;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.dialog.action.SaveDialogAction;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeItemId;
import info.magnolia.ui.vaadin.integration.jcr.ModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.service.MailService;
import vn.ekino.certificate.service.PublishingService;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static info.magnolia.cms.security.MgnlUserManager.PROPERTY_PASSWORD;
import static info.magnolia.cms.security.SecurityConstants.NODE_GROUPS;
import static info.magnolia.cms.security.SecurityConstants.NODE_ROLES;

@Slf4j
public class SaveUserAction extends SaveDialogAction<SaveUserActionDefinition> {
    private final MailService mailService;
    private final UserRepository userRepository;
    private final PublishingService publishingService;
//    private static final String VALIDATE_STATUS = "validated";

    private SecuritySupport securitySupport;
    private final List<String> protectedProperties = Arrays.asList(PROPERTY_PASSWORD, "name", NODE_GROUPS, NODE_ROLES);

    @Inject
    public SaveUserAction(SaveUserActionDefinition definition, Item item, EditorValidator validator, EditorCallback callback, MailService mailService, UserRepository userRepository, PublishingService publishingService, SecuritySupport securitySupport) {
        super(definition, item, validator, callback);
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.publishingService = publishingService;
        this.securitySupport = securitySupport;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (validateForm()) {
            final JcrNodeAdapter nodeAdapter = (JcrNodeAdapter) item;
            createOrUpdateUser(nodeAdapter);
            callback.onSuccess(getDefinition().getName());
        }
    }

    private void createOrUpdateUser(final JcrNodeAdapter userItem) throws ActionExecutionException {
        try {
            String userManagerRealm = getDefinition().getUserManagerRealm();
            if (StringUtils.isBlank(userManagerRealm)){
                log.debug("userManagerRealm property is not defined -> will try to get realm from node path");
                userManagerRealm = resolveUserManagerRealm(userItem);
            }
            UserManager userManager = securitySupport.getUserManager(userManagerRealm);
            if (userManager == null){
                throw new ActionExecutionException("User cannot be created. No user manager with realm name " + userManagerRealm + " is defined.");
            }

            String newUserName = (String) userItem.getItemProperty(ModelConstants.JCR_NAME).getValue();
            String newPassword = (String) userItem.getItemProperty(PROPERTY_PASSWORD).getValue();

            User user;
            Session session = userItem.getJcrItem().getSession();
            final Node userNode;
            if (userItem instanceof JcrNewNodeAdapter) {

                // JcrNewNodeAdapter returns the parent JCR item here
                Node parentNode = userItem.getJcrItem();
                String parentPath = parentNode.getPath();

                if ("/".equals(parentPath)) {
                    throw new ActionExecutionException("Users cannot be created directly under root");
                }

                // Make sure this user is allowed to add a user here, the user manager would happily do it and then we'd fail to read the node
                parentNode.getSession().checkPermission(parentNode.getPath(), Session.ACTION_ADD_NODE);

                user = userManager.createUser(parentPath, newUserName, newPassword);
                userNode = session.getNodeByIdentifier(user.getIdentifier());
                // workaround that updates item id of the userItem so we can use it in OpenCreateDialogAction to fire ContentChangedEvent
                try {
                    Field f = userItem.getClass().getDeclaredField("appliedChanges");
                    f.setAccessible(true);
                    f.setBoolean(userItem, true);
                    f.setAccessible(false);
                    userItem.setItemId(new JcrNodeItemId(userNode.getIdentifier(), RepositoryConstants.USERS));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    log.warn("Unable to set new JcrItemId for adapter {}", userItem, e);
                }
            } else {
                userNode = userItem.getJcrItem();
                String existingUserName = userNode.getName();
                user = userManager.getUser(existingUserName);

                if (!StringUtils.equals(existingUserName, newUserName)) {
                    String pathBefore = userNode.getPath();
                    NodeUtil.renameNode(userNode, newUserName);
                    userNode.setProperty("name", newUserName);
                    UsersWorkspaceUtil.updateAcls(userNode, pathBefore);
                }

                if (StringUtils.isNotBlank(newPassword)) {
                    userManager.setProperty(user, PROPERTY_PASSWORD, newPassword);
                }
            }

            final Collection<String> groups = resolveItemsNamesFromIdentifiers((Collection<String>) userItem.getItemProperty(NODE_GROUPS).getValue(), RepositoryConstants.USER_GROUPS);
            log.debug("Assigning user the following groups [{}]", groups);
            storeGroupsCollection(userManager, user, groups);

            final Collection<String> roles = resolveItemsNamesFromIdentifiers((Collection<String>) userItem.getItemProperty(NODE_ROLES).getValue(), RepositoryConstants.USER_ROLES);
            log.debug("Assigning user the following roles [{}]", roles);
            storeRolesCollection(userManager, user, roles);

            Collection<?> userProperties = userItem.getItemPropertyIds();
            ValueFactory valueFactory = session.getValueFactory();
            for (Object propertyName : userProperties) {
                if (!protectedProperties.contains(propertyName)) {
                    Value propertyValue = PropertyUtil.createValue(userItem.getItemProperty(propertyName).getValue(), valueFactory);
                    userManager.setProperty(user, propertyName.toString(), propertyValue);
                }
            }

            userItem.updateChildren(userNode);
            UserDto userDto = MapperUtils.nodeToObject(userRepository.findById(userNode.getIdentifier()).get(), UserDto.class).get();
            if (!userItem.isNew() && !userDto.getActive() && userDto.getEnabled()) {
//                mailService.sendMail(userDto, VALIDATE_STATUS);
                PropertyUtil.setProperty(userNode, "active", true);
            }
            session.save();
            publishingService.publish(List.of(userNode));

        } catch (final RepositoryException e) {
            throw new ActionExecutionException(e);
        }
    }

    private String resolveUserManagerRealm(final JcrNodeAdapter userItem) throws RepositoryException{
        String userPath = userItem.getJcrItem().getPath();
        if (userItem instanceof JcrNewNodeAdapter && !"/".equals(userPath)) {
            //parent JCR item is returned so we need enclose path with "/" to handle correctly in case when user is placed directly under realm root
            userPath += "/";
        }
        return StringUtils.substringBetween(userPath, "/");
    }

    private void storeGroupsCollection(UserManager userManager, User user, Collection<String> newGroups){
        Collection<String> oldGroups = new ArrayList<String>();
        for (String group : user.getGroups()) {
            oldGroups.add(group);
        }
        for(String newGroup : newGroups) {
            userManager.addGroup(user, newGroup);
            oldGroups.remove(newGroup);
        }
        for(String oldGroup : oldGroups) {
            userManager.removeGroup(user, oldGroup);
        }
    }

    private void storeRolesCollection(UserManager userManager, User user, Collection<String> newRoles){
        Collection<String> oldRoles = new ArrayList<String>();
        for (String role : user.getRoles()) {
            oldRoles.add(role);
        }
        for(String newRole : newRoles) {
            userManager.addRole(user, newRole);
            oldRoles.remove(newRole);
        }
        for(String oldRole : oldRoles) {
            userManager.removeRole(user, oldRole);
        }
    }

    private Collection<String> resolveItemsNamesFromIdentifiers(Collection<String> itemsIdentifiers, String repository){
        final Collection<String> itemsNames = new ArrayList<String>();
        for (final String itemIdentifier : itemsIdentifiers) {
            MgnlContext.doInSystemContext(new SilentSessionOp<Void>(repository) {

                @Override
                public Void doExec(Session session) {
                    try {
                        final String itemName =  session.getNodeByIdentifier(itemIdentifier).getName();
                        itemsNames.add(itemName);
                    } catch (RepositoryException e) {
                        log.error("Can't resolve group/role with uuid: " + itemIdentifier);
                        log.debug(e.getMessage());
                    }
                    return null;
                }
            });
        }
        return itemsNames;
    }
}
