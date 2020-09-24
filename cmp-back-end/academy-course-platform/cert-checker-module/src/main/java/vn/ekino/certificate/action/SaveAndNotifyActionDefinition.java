package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

public class SaveAndNotifyActionDefinition extends ConfiguredActionDefinition {
    public SaveAndNotifyActionDefinition() {
        setImplementationClass(SaveAndNotifyAction.class);
    }
}
