package vn.ekino.certificate.model;


import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.NotificationDto;
import vn.ekino.certificate.model.data.UserProfile;
import vn.ekino.certificate.repository.NotificationRepository;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.service.UserProfileService;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HeaderModel extends BaseModel {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Inject
    public HeaderModel(Node content, ConfiguredTemplateDefinition definition, RenderingModel<?> parent, CertificateServicesModule servicesModule, UserProfile userProfile, UserProfileService userProfileService, UserRepository userRepository, NotificationRepository notificationRepository) {
        super(content, definition, parent, servicesModule);
        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public String getUserFullName() {
        var userNode = userRepository.findById(getCurrentUser().getIdentifier()).get();
        return PropertyUtil.getString(userNode, "title", StringUtils.EMPTY);
    }

    public boolean isAnonymous(){
        return getCurrentUser().getName().equals("anonymous");
    }

    public boolean roleHasPermission(List<String> userRoles, List<String> navRoles){
        return navRoles.stream().anyMatch(userRoles::contains);
    }

    /**
     * check the current user just have 1 role of the ["Participant", "Trainer", "Supervisor"] list
     */
    public boolean userHasARole(List<String> userRoles) {
        List<String> specifiedRoles = Arrays.asList(Constants.SUPERVISOR_ROLE, Constants.TRAINER_ROLE, Constants.PARTICIPANT_ROLE);
        int roleCount = 0;
        for (String role : userRoles) {
            if (specifiedRoles.contains(role)) {
                roleCount++;
            }
        }
        return (roleCount == 1) ? true : false;
    }

    public List<NotificationDto> getNotifications() {
        var roles = MgnlContext.getUser().getAllRoles();
        String propertyName = NotificationRepository.PROPERTY_PARTICIPANTS;
        if (roles.contains(Constants.SUPERVISOR_ROLE)) {
            propertyName = NotificationRepository.PROPERTY_SUPERVISORS;
        } else if (roles.contains(Constants.TRAINER_ROLE) && !roles.contains(Constants.PARTICIPANT_ROLE)) {
            propertyName = NotificationRepository.PROPERTY_TRAINERS;
        }
        return notificationRepository.getAllNotificationsByUserId(getCurrentUser().getIdentifier(), propertyName)
                .stream()
                .map(itm -> MapperUtils.nodeToObject(itm, NotificationDto.class).get())
                .peek(itm -> {
                    if (roles.contains(Constants.SUPERVISOR_ROLE)) {
                        itm.setLink(itm.getSupervisorLink());
                    } else if (roles.contains(Constants.TRAINER_ROLE) && !roles.contains(Constants.PARTICIPANT_ROLE)) {
                        itm.setLink(itm.getTrainerLink());
                    } else {
                        itm.setLink(itm.getParticipantLink());
                    }
                })
                .sorted(Comparator.comparing(NotificationDto::getLastModified).reversed())
                .collect(Collectors.toList());
    }

    public String getUserAvatar() {
        return userProfileService.loadUserProfile().getHeaderThumbnail();
    }
}
