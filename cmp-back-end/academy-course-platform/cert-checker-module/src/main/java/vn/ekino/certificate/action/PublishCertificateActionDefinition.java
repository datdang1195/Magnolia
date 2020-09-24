package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

public class PublishCertificateActionDefinition extends ConfiguredActionDefinition {

    public PublishCertificateActionDefinition() {
        this.setImplementationClass(PublishCertificateAction.class);
    }
}
