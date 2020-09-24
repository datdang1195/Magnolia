package vn.ekino.certificate.field;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import info.magnolia.objectfactory.Components;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.vaadin.extension.MaxLengthIndicator;

import javax.inject.Inject;

public class CustomTextFieldFactory extends AbstractFieldFactory<CustomTextFieldDefinition, String> {

    private AbstractTextField field;

    @Inject
    public CustomTextFieldFactory(CustomTextFieldDefinition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport);
    }


    @Deprecated
    public CustomTextFieldFactory(CustomTextFieldDefinition definition, Item relatedFieldItem) {
        this(definition, relatedFieldItem, null, Components.getComponent(I18NAuthoringSupport.class));
    }

    @Override
    protected Field<String> createFieldComponent() {
        // Create a TextArea if the rows > 1
        if (definition.getRows() > 1) {
            TextArea textArea = new TextArea();
            textArea.setRows(definition.getRows());
            field = textArea;
        } else {
            field = new TextField();
        }
        field.setNullRepresentation("");
        field.setNullSettingAllowed(true);
        if (definition.getMaxLength() != -1) {
            field.setMaxLength(definition.getMaxLength());
            MaxLengthIndicator.extend(field);
        }

        String placeholder = definition.getPlaceholder();
        if (placeholder != null && !isMessageKey(placeholder)) {
            field.setInputPrompt(placeholder);
        }

        return field;
    }

    @Override
    public Field<String> createField() {
        if (this.definition.getField() == null) {
            var field = super.createField();
            this.definition.setField(field);
        }
        return this.definition.getField();
    }
}