package vn.ekino.certificate.field;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.Layout;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.OptionGroupFieldFactory;

import java.util.List;

public class MultiChoiceFactory extends OptionGroupFieldFactory<MultiChoiceDefinition> {

    public MultiChoiceFactory(MultiChoiceDefinition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport, ComponentProvider componentProvider) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport, componentProvider);
    }

    @Override
    protected AbstractSelect createFieldComponent() {
        select = createSelectionField();
        select.setContainerDataSource(buildOptions(getOptions()));
        select.setNullSelectionAllowed(false);
        select.setMultiSelect(false);
        select.setNewItemsAllowed(false);
        if (select instanceof ComboBox) {
            ((ComboBox) select).setFilteringMode(definition.getFilteringMode());
            ((ComboBox) select).setTextInputAllowed(definition.isTextInputAllowed());
            ((ComboBox) select).setPageLength(definition.getPageLength());
        }
        select.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        select.setItemCaptionPropertyId("label");

        select.setMultiSelect(getFieldDefinition().isMultiselect());
        select.setNullSelectionAllowed(true);
        if (definition.getLayout() == Layout.horizontal) {
            select.addStyleName("horizontal");
        }

        select.addListener(this.getFieldDefinition()::changeValue);
        this.definition.setComboBox(select);
        return select;
    }

    private IndexedContainer buildOptions(List<SelectFieldOptionDefinition> list) {
        IndexedContainer optionContainer = new IndexedContainer();

        if (!list.isEmpty()) {
            Class<?> fieldType = String.class;
            optionContainer.addContainerProperty("value", fieldType, null);
            optionContainer.addContainerProperty("label", String.class, null);

            for (SelectFieldOptionDefinition option : list) {
                Object value = createTypedValue(option.getValue(), String.class);
                Item item = optionContainer.addItem(value);
                if (item != null) {
                    item.getItemProperty("value").setValue(value);
                    item.getItemProperty("label").setValue(option.getLabel());
                }

            }
        }
        return optionContainer;
    }
}
