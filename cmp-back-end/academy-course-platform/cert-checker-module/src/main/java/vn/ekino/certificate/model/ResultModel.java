package vn.ekino.certificate.model;

import info.magnolia.context.WebContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.enumeration.CMPMessage;

import javax.inject.Provider;
import javax.jcr.Node;

public class ResultModel extends BaseModel {

    private final Provider<WebContext> webContextProvider;

    public ResultModel(Node content, ConfiguredTemplateDefinition definition, RenderingModel<?> parent, CertificateServicesModule servicesModule, Provider<WebContext> webContextProvider) {
        super(content, definition, parent, servicesModule);
        this.webContextProvider = webContextProvider;
    }

    public CMPMessage getCMPMessage() {
        String code = webContextProvider.get().getParameter("code");
        return code!= null ? CMPMessage.getCMPMessageByCode(code) : CMPMessage.ENROL_ERROR;
    }


}
