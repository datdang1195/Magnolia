package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

public class SaveFormActionDefinition extends ConfiguredActionDefinition {
    public SaveFormActionDefinition() {
        setImplementationClass(SaveFormAction.class);
    }
}
