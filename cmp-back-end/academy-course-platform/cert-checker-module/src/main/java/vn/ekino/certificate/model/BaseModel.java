package vn.ekino.certificate.model;

import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import vn.ekino.certificate.CertificateServicesModule;

import javax.jcr.Node;
import java.util.ArrayList;
import java.util.List;

public class BaseModel extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    private final CertificateServicesModule servicesModule;

    public BaseModel(Node content, ConfiguredTemplateDefinition definition, RenderingModel<?> parent, CertificateServicesModule servicesModule) {
        super(content, definition, parent);
        this.servicesModule = servicesModule;
    }

    public String getAuthorPath() {
        return servicesModule.getAuthorPath();
    }

    public User getCurrentUser(){
        return MgnlContext.getUser();
    }


    public List<String> getCurrentUserRoles(){
        return new ArrayList<>(getCurrentUser().getAllRoles());
    }
}
