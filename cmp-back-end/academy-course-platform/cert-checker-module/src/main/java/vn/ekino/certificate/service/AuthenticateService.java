package vn.ekino.certificate.service;

import info.magnolia.cms.security.ACLImpl;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.auth.ACL;
import info.magnolia.cms.security.auth.GroupList;
import info.magnolia.cms.security.auth.PrincipalCollection;
import info.magnolia.cms.security.auth.PrincipalCollectionImpl;
import info.magnolia.cms.security.auth.RoleList;
import info.magnolia.jaas.principal.GroupListImpl;
import info.magnolia.jaas.principal.RoleListImpl;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static info.magnolia.cms.security.Realm.REALM_ADMIN;

@Singleton
@Slf4j
public class AuthenticateService {

    private final UserRepository userRepository;
    private final Provider<SecuritySupport> securitySupportProvider;

    @Inject
    public AuthenticateService(UserRepository userRepository, Provider<SecuritySupport> securitySupportProvider) {
        this.userRepository = userRepository;
        this.securitySupportProvider = securitySupportProvider;
    }

    public Optional<User> getExistingUser(String userName) {
        Optional<Node> userNode = userRepository.findByName(userName);
        try {
            String userId = userNode.get().getIdentifier();
            return Optional.ofNullable(
                    securitySupportProvider.get().getUserManager("admin").getUserById(userId));
        } catch (RepositoryException e) {
            log.warn("can't get identifier of user node", e);
        }

        return Optional.empty();
    }

    public Optional<User> getExistingUserById(String userId) {
        return Optional.ofNullable(
                securitySupportProvider.get().getUserManager("admin").getUserById(userId));
    }

    public Subject initializeSubject(User user) {
        Subject subject = new Subject();
        subject.getPrincipals().add(user);
        subject.getPrincipals().add(REALM_ADMIN);

        setACL(subject, user);

        return subject;
    }

    private void setACL(Subject subject, User user) {
        String[] roles = user.getAllRoles().toArray(new String[user.getAllRoles().size()]);
        String[] groups = user.getAllGroups().toArray(new String[user.getAllGroups().size()]);

        log.debug("Roles: {}", Arrays.toString(roles));
        log.debug("Groups: {}", Arrays.toString(groups));

        subject.getPrincipals().add(getRoles(roles));
        subject.getPrincipals().add(getGroups(groups));

        PrincipalCollection principalList = new PrincipalCollectionImpl();
        setACLForRoles(roles, principalList);
        setACLForGroups(groups, principalList);

        subject.getPrincipals().add(principalList);
    }

    private RoleList getRoles(String[] roles) {
        RoleList roleList = new RoleListImpl();
        for (String role : roles) {
            roleList.add(role);
        }

        return roleList;
    }

    private GroupList getGroups(String[] groups) {
        GroupList groupList = new GroupListImpl();
        for (String group : groups) {
            groupList.add(group);
        }
        return groupList;
    }

    private void setACLForRoles(String[] roles, PrincipalCollection principalList) {
        SecuritySupport securitySupport = securitySupportProvider.get();
        for (String role : roles) {
            mergePrincipals(principalList, securitySupport.getRoleManager().getACLs(role).values());
        }
    }

    private void setACLForGroups(String[] groups, PrincipalCollection principalList) {
        SecuritySupport securitySupport = securitySupportProvider.get();

        for (String group : groups) {
            mergePrincipals(principalList, securitySupport.getGroupManager().getACLs(group).values());
        }
    }

    private void mergePrincipals(PrincipalCollection principalList, Collection<ACL> principals) {
        for (ACL princ : principals) {
            if (principalList.contains(princ.getName())) {
                ACL oldACL = (ACL) principalList.get(princ.getName());
                Collection<Permission> permissions = new HashSet<Permission>(oldACL.getList());
                permissions.addAll(princ.getList());
                principalList.remove(oldACL);
                princ = new ACLImpl(princ.getName(), new ArrayList<Permission>(permissions));
            }
            principalList.add(princ);
        }
    }

}
