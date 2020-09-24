package vn.ekino.certificate.render;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.dialog.actionarea.ActionListener;
import info.magnolia.ui.dialog.actionarea.renderer.ActionRenderer;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.dialog.BaseDialog;

import java.util.HashMap;
import java.util.List;

public class UniqueActionRender implements ActionRenderer {
    private static final List<String> DEFAULT_ACTION_NAMES = List.of(BaseDialog.COMMIT_ACTION_NAME, BaseDialog.CANCEL_ACTION_NAME);

    @Override
    public View start(ActionDefinition definition, ActionListener listener) {
        return new DefaultActionView(definition.getLabel(), definition.getName(), listener);
    }

    private static class DefaultActionView implements View {

        private Button button = null;

        private DefaultActionView(final String label, final String name, final ActionListener listener) {
            this.button = new Button(label, event -> buttonClick(event, name, listener));
            this.button.addStyleName(DEFAULT_ACTION_NAMES.contains(name) ? name : BaseDialog.COMMIT_ACTION_NAME);
            this.button.addStyleName("btn-dialog");
            this.button.addStyleName("webkit-fix");
            this.button.setDisableOnClick(true);
        }

        private void buttonClick(Button.ClickEvent event, String name, ActionListener listener) {
            try {
                if (!BaseDialog.CANCEL_ACTION_NAME.equals(name) && listener instanceof EditorValidator && !((EditorValidator) listener).isValid()) {
                    // validation will fail, so re-enable that button
                    button.setEnabled(true);
                }

                listener.onActionFired(name, new HashMap<String, Object>());
            } finally {
                button.setEnabled(true);
            }

        }

        @Override
        public Component asVaadinComponent() {
            return button;
        }
    }
}
