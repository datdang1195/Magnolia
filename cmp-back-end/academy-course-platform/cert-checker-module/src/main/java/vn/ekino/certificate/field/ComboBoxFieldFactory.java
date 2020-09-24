package vn.ekino.certificate.field;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.FormTab;
import info.magnolia.ui.form.definition.TabDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.SelectFieldFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.dto.enumeration.FieldTarget;
import vn.ekino.certificate.service.EventChangeService;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboBoxFieldFactory extends SelectFieldFactory<ComboBoxFieldDefinition> {
    BaseSelectFieldDefinition targetField;
    DateWithEventDefinition dateField;
    CustomSwitchableFieldDefinition switchableField;
    private final EventChangeService eventChangeService;
    String selectedValue;

    ComboBoxFieldDefinition comboxCourse;
    CustomTextFieldDefinition targetFieldText;

    @Inject
    public ComboBoxFieldFactory(ComboBoxFieldDefinition definition,
                                Item relatedFieldItem,
                                UiContext uiContext,
                                I18NAuthoringSupport i18nAuthoringSupport, EventChangeService eventChangeService) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport);
        this.eventChangeService = eventChangeService;
    }

    @Override
    public Field<Object> createField() {
        Field<Object> field = super.createField();
        field.addValueChangeListener(this.getFieldDefinition()::changeValue);
        this.definition.setComboBox(select);
        this.definition.setValueChanged(this::comboboxChanged);

        selectedValue = (String) field.getValue();
        String initMethod = this.definition.getInitialMethod();
        if (StringUtils.isNotEmpty(initMethod)) {
            initDataSource(this.definition.getInitialParam());
        }

        if (StringUtils.isNotEmpty(this.definition.getTargetFieldName())) {
            FieldTarget fieldTarget = FieldTarget.getFieldTargetByType(this.definition.getTargetFieldType());
            if (switchableField == null || targetFieldText == null || targetField != null) {
                InitField(fieldTarget);
                if (targetField != null) {
                    targetField.setOptions(new ArrayList<>());
                    targetField.getOptions().clear();
                }
            }
            if (selectedValue != null) {
                this.definition.getValueChanged().accept(selectedValue);
            }
        }
        return field;
    }

    private void initDataSource(String param) {
        try {
            List<SelectFieldOptionDefinition> options;
            Method method;
            if (StringUtils.isEmpty(param)) {
                method = eventChangeService.getClass().getDeclaredMethod(this.definition.getInitialMethod());
                method.setAccessible(true);
                options = (List<SelectFieldOptionDefinition>) method.invoke(eventChangeService);
            } else {
                method = eventChangeService.getClass().getDeclaredMethod(this.definition.getInitialMethod(), String.class);
                method.setAccessible(true);
                options = (List<SelectFieldOptionDefinition>) method.invoke(eventChangeService, param);
            }
            this.definition.setOptions(new ArrayList<>());
            this.definition.getOptions().clear();
            var comboboxField = this.definition.getComboBox();
            if (comboboxField != null) {
                comboboxField.setContainerDataSource(new IndexedContainer());
                comboboxField.setContainerDataSource(buildOptions(options));
                comboboxField.getContainerDataSource().getItemIds()
                        .stream()
                        .findFirst()
                        .ifPresentOrElse(itm -> {
                                    var value = selectedValue == null ? itm : selectedValue;
                                    comboboxField.setValue(value);
                                    this.definition.getValueChanged().accept(value);
                                },
                                () -> this.definition.getValueChanged().accept(StringUtils.EMPTY));
            }
            this.definition.setOptions(options);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.warn("Can't get or set value to combobox because {}", e.getMessage());
        }
    }

    private void InitField(FieldTarget fieldTarget) {
        try {
            var parent = (FormTab) this.getParent();
            var field = parent.getClass().getDeclaredField("definition");
            field.setAccessible(true);
            var tabDefinition = (TabDefinition) field.get(parent);
            var result = tabDefinition.getFields()
                    .stream()
                    .filter(itm -> this.definition.getTargetFieldName().equals(itm.getName()))
                    .findFirst().orElse(null);
            if (fieldTarget.getFieldType().equals(FieldTarget.SWITCHABLE.getFieldType())) {
                switchableField = CustomSwitchableFieldDefinition.class.cast(result);
            } else if (fieldTarget.getFieldType().equals(FieldTarget.TEXTFIELD.getFieldType())) {
                targetFieldText = fieldTarget.getFieldDefinition().asSubclass(CustomTextFieldDefinition.class).cast(result);
                if (StringUtils.isNotEmpty(definition.getComboboxFieldName())) {
                    var fieldMatched = tabDefinition.getFields()
                            .stream()
                            .filter(itm -> this.definition.getComboboxFieldName().equals(itm.getName()))
                            .findFirst().orElse(null);
                    comboxCourse = (ComboBoxFieldDefinition) fieldMatched;
                }
            } else {
                targetField = fieldTarget.getFieldDefinition().asSubclass(BaseSelectFieldDefinition.class).cast(result);
                if (StringUtils.isNotEmpty(definition.getDateFieldName())) {
                    var fieldMatched = tabDefinition.getFields()
                            .stream()
                            .filter(itm -> this.definition.getDateFieldName().equals(itm.getName()))
                            .findFirst().orElse(null);
                    dateField = (DateWithEventDefinition) fieldMatched;
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Can't get or set value to combobox because {}", e.getMessage());
        }
    }

    private void comboboxChanged(Object value) {
        if (StringUtils.isEmpty(this.definition.getTargetFieldName()) || (targetField == null && switchableField == null && targetFieldText == null)) {
            return;
        }
        try {
            String methodName = this.definition.getImplementationMethod();
            if (switchableField != null) {
                var method = eventChangeService.getClass().getDeclaredMethod(methodName, String.class);
                method.setAccessible(true);
                boolean programFinish = (boolean) method.invoke(eventChangeService, value);
                if (switchableField.getField() != null) {
                    switchableField.getField().setEnabled(!programFinish);
                }
            } else if (targetFieldText != null) {
                var method = eventChangeService.getClass().getDeclaredMethod(methodName, String.class, String.class);
                method.setAccessible(true);
                String program = (String) comboxCourse.getComboBox().getValue();
                var result = method.invoke(eventChangeService, value, program);
                if (targetFieldText.getField() != null) {
                    targetFieldText.getField().setReadOnly(false);
                    targetFieldText.getField().setValue(result.toString());
                    targetFieldText.getField().setReadOnly(true);
                }
            } else {
                List<SelectFieldOptionDefinition> options;
                if (dateField != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("comboboxValue", value);
                    map.put("dateValue", dateField.getField().getValue());
                    var method = eventChangeService.getClass().getDeclaredMethod(methodName, Map.class);
                    method.setAccessible(true);
                    options = (List<SelectFieldOptionDefinition>) method.invoke(eventChangeService, map);
                } else {
                    var method = eventChangeService.getClass().getDeclaredMethod(methodName, String.class);
                    method.setAccessible(true);
                    options = (List<SelectFieldOptionDefinition>) method.invoke(eventChangeService, value);
                }

                var comboboxField = targetField.getComboBox();
                if (comboboxField != null) {
                    comboboxField.setContainerDataSource(new IndexedContainer());
                    comboboxField.setContainerDataSource(buildOptions(options));
                    comboboxField.getContainerDataSource().getItemIds()
                            .stream()
                            .findFirst()
                            .ifPresentOrElse(itm -> {
                                        if (targetField instanceof ComboBoxFieldDefinition && targetField.getValueChanged() != null) {
                                            comboboxField.setValue(itm);
                                            targetField.getValueChanged().accept(itm);
                                        }
                                    },
                                    () -> {
                                        if (targetField instanceof ComboBoxFieldDefinition && targetField.getValueChanged() != null) {
                                            targetField.getValueChanged().accept(StringUtils.EMPTY);
                                        }
                                    });
                }
                targetField.setOptions(options);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.warn("Can't get or set value to combobox because {}", e.getMessage());
        }
    }

    private IndexedContainer buildOptions(List<SelectFieldOptionDefinition> list) {
        IndexedContainer optionContainer = new IndexedContainer();

        if (!list.isEmpty()) {

            Class<?> fieldType = String.class;
            optionContainer.addContainerProperty("value", fieldType, null);
            optionContainer.addContainerProperty("label", fieldType, null);

            for (SelectFieldOptionDefinition option : list) {
                Object value = createTypedValue(option.getValue(), fieldType);
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
