package vn.ekino.certificate.action;

import info.magnolia.ui.dialog.action.SaveDialogActionDefinition;

public class SaveUserActionDefinition extends SaveDialogActionDefinition {
    private String userManagerRealm;

    public SaveUserActionDefinition() {
        setImplementationClass(SaveUserAction.class);
    }

    public String getUserManagerRealm() {
        return userManagerRealm;
    }

    public void setUserManagerRealm(String userManagerRealm) {
        this.userManagerRealm = userManagerRealm;
    }
}
