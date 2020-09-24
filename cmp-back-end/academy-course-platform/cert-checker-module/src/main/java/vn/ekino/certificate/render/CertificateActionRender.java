package vn.ekino.certificate.render;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.contentapp.detail.DetailPresenter;
import info.magnolia.ui.dialog.actionarea.ActionListener;
import info.magnolia.ui.dialog.actionarea.renderer.ActionRenderer;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.dialog.BaseDialog;
import vn.ekino.certificate.repository.GeneratedCertificateRepository;

import java.util.HashMap;
import java.util.List;

public class CertificateActionRender implements ActionRenderer {
    private static final List<String> DEFAULT_ACTION_NAMES = List.of(BaseDialog.COMMIT_ACTION_NAME, BaseDialog.CANCEL_ACTION_NAME);

    @Override
    public View start(ActionDefinition definition, ActionListener listener) {
        boolean isCreate = ((DetailPresenter) listener).getItem().getItemProperty(GeneratedCertificateRepository.PROPERTY_CODE) == null;
        return new DefaultActionView(definition.getLabel(), definition.getName(), listener, isCreate);
    }

    private static class DefaultActionView implements View {

        private Button button = null;

        private DefaultActionView(final String label, final String name, final ActionListener listener, boolean isCreate) {
            this.button = new Button(label, event -> buttonClick(event, name, listener));
            this.button.addStyleName(DEFAULT_ACTION_NAMES.contains(name) ? name : BaseDialog.COMMIT_ACTION_NAME);
            this.button.addStyleName("btn-dialog");
            this.button.addStyleName("webkit-fix");
            this.button.setDisableOnClick(true);
            if (!BaseDialog.CANCEL_ACTION_NAME.equals(name)) {
                this.button.setEnabled(isCreate);
            }
        }

        private void buttonClick(Button.ClickEvent event, String name, ActionListener listener) {
            // don't trigger validation for all fields when the action is 'cancel'
            // also check validity *before* firing the action, otherwise session can be saved and thus validators might be affected
            // TODO: well ideally, we shouldn't check validation at all here though (validation should be done only once in the save action)
            if (!BaseDialog.CANCEL_ACTION_NAME.equals(name) && listener instanceof EditorValidator && !((EditorValidator) listener).isValid()) {
                // validation will fail, so re-enable that button
                button.setEnabled(true);
            }

            listener.onActionFired(name, new HashMap<String, Object>());
        }

        @Override
        public Component asVaadinComponent() {
            return button;
        }
    }
}
