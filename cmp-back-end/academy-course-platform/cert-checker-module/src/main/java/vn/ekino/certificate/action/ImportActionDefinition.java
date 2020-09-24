package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.CommandActionDefinition;

public class ImportActionDefinition extends CommandActionDefinition {
    public ImportActionDefinition() {
        setImplementationClass(ImportAction.class);
    }
}
