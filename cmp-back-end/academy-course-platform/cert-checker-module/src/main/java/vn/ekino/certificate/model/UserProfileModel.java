package vn.ekino.certificate.model;

import com.google.gson.Gson;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.service.UserProfileService;

import javax.inject.Inject;
import javax.jcr.Node;

public class UserProfileModel extends BaseModel {

    private final UserProfileService userProfileService;

    @Inject
    public UserProfileModel(Node content, ConfiguredTemplateDefinition definition, RenderingModel<?> parent, CertificateServicesModule servicesModule, UserProfileService userProfileService) {
        super(content, definition, parent, servicesModule);
        this.userProfileService = userProfileService;
    }

    public String loadUserProfile() {
        return new Gson().toJson(userProfileService.loadUserProfile());
    }
}
