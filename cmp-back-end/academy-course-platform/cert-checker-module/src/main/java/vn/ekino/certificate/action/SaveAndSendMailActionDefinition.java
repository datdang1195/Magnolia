package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

public class SaveAndSendMailActionDefinition extends ConfiguredActionDefinition {
    public SaveAndSendMailActionDefinition() {
        setImplementationClass(SaveAndSendMailAction.class);
    }
}
