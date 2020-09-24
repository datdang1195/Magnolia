package vn.ekino.certificate.field;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.PopupDateField;
import info.magnolia.cms.security.MgnlUserManager;
import info.magnolia.context.Context;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.FormTab;
import info.magnolia.ui.form.definition.TabDefinition;
import info.magnolia.ui.form.field.definition.DateFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.ekino.certificate.service.ProgramCourseService;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static info.magnolia.ui.form.field.definition.DateFieldDefinition.NOW;

@Slf4j
public class DateWithEventDefinitionFactory extends AbstractFieldFactory<DateWithEventDefinition, Date> {

    /**
     * @deprecated not used anymore in timezone options, though some user profiles might still have it.
     */
    private static final String BROWSER_TIMEZONE = "browser";

    private final SimpleTranslator i18n;
    private final Context context;

    private final ProgramCourseService programCourseService;
    private ComboBoxFieldDefinition combobox;

    @Inject
    public DateWithEventDefinitionFactory(DateWithEventDefinition definition,
                                          Item relatedFieldItem,
                                          UiContext uiContext,
                                          I18NAuthoringSupport i18NAuthoringSupport,
                                          SimpleTranslator i18n,
                                          Context context, ProgramCourseService programCourseService) {
        super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
        this.i18n = i18n;
        this.context = context;
        this.programCourseService = programCourseService;
    }

    @Override
    public Field<Date> createField() {
        Field<Date> field = super.createField();
        field.setWidthUndefined();

        field.addValueChangeListener(this.getFieldDefinition()::changeValue);
        this.definition.setField(field);
        this.definition.setValueChanged(this::dateChanged);
        if (combobox == null) {
            setCombobox();
        }
        combobox.setOptions(new ArrayList<>());
        Date date = field.getValue();
        if (date != null && combobox != null) {
            dateChanged(date);
        }
        return field;
    }

    private void setCombobox() {
        try {
            var parent = (FormTab) this.getParent();
            var field = parent.getClass().getDeclaredField("definition");
            field.setAccessible(true);
            var tabDefinition = (TabDefinition) field.get(parent);
            combobox = (ComboBoxFieldDefinition) tabDefinition.getFields()
                    .stream()
                    .filter(itm -> this.definition.getComboboxName().equals(itm.getName()))
                    .findFirst().orElse(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Can't get or set value to combobox because {}", e.getMessage());
        }
    }

    private void dateChanged(Object value) {
        Date selectedDate = (Date) value;
        var comboboxField = combobox.getComboBox();
        List<SelectFieldOptionDefinition> optionDefinitions = programCourseService.getCourseSelectOptions(selectedDate);
        if (comboboxField != null) {
            comboboxField.setContainerDataSource(new IndexedContainer());
            comboboxField.setContainerDataSource(buildOptions(optionDefinitions));
            combobox.setOptions(optionDefinitions);
            comboboxField.getContainerDataSource().getItemIds()
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(comboboxField::setValue,
                            () -> combobox.getValueChanged().accept(StringUtils.EMPTY));
        } else {
            combobox.setOptions(optionDefinitions);
        }

    }

    private IndexedContainer buildOptions(List<SelectFieldOptionDefinition> list) {
        IndexedContainer optionContainer = new IndexedContainer();

        if (!list.isEmpty()) {
            Class<?> fieldType =  String.class;
            optionContainer.addContainerProperty("value", fieldType, null);
            optionContainer.addContainerProperty("label", String.class, null);

            for (SelectFieldOptionDefinition option : list) {
                Object value = createTypedValue(option.getValue(), String.class);
                Item item = optionContainer.addItem(value);
                item.getItemProperty("value").setValue(value);
                item.getItemProperty("label").setValue(option.getLabel());
            }
        }
        return optionContainer;
    }

    @Override
    protected Field<Date> createFieldComponent() {
        DateFieldDefinition definition = getFieldDefinition();
        PopupDateField popupDateField = new PopupDateField();
        setTimeZone(popupDateField);
        String dateFormat;

        // set Resolution
        if (definition.isTime()) {
            popupDateField.setResolution(Resolution.MINUTE);
            dateFormat = definition.getDateFormat() + " " + definition.getTimeFormat();
        } else {
            popupDateField.setResolution(Resolution.DAY);
            dateFormat = definition.getDateFormat();
        }
        popupDateField.setDateFormat(dateFormat);
        return popupDateField;
    }

    @Override
    protected Class<?> getDefaultFieldType() {
        return Date.class;
    }

    /**
     * Sets the field's time zone according to user preference if set, or to system default otherwise.
     * User is informed of the input field's expected time zone through the field description and input prompt.
     *
     * Do mind however, that when value is null, the client-side time selector always shows in "local" time,
     * thus the default selected time might be off from the current instant. TODO: we might patch that from Vaadin/GWT
     */
    protected void setTimeZone(PopupDateField popupDateField) {

        final String timeZoneId = context.getUser().getProperty(MgnlUserManager.PROPERTY_TIMEZONE);
        final TimeZone timeZone;

        if (timeZoneId == null || timeZoneId.isEmpty() || timeZoneId.equals(BROWSER_TIMEZONE)) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = TimeZone.getTimeZone(timeZoneId);
        }
        if (timeZone != null) {
            popupDateField.setTimeZone(timeZone);

            // Show resolved TZ info in the description tooltip & input-prompt
            popupDateField.setDescription(i18n.translate("ui-admincentral.dateField.timeZone.description", timeZone.getDisplayName(false, TimeZone.LONG, context.getLocale()), timeZone.getRawOffset() / 3600000));
            popupDateField.setInputPrompt(i18n.translate("ui-admincentral.dateField.timeZone.description", timeZone.getDisplayName(false, TimeZone.SHORT, context.getLocale()), timeZone.getRawOffset() / 3600000));
        }
    }

    @Override
    protected Object getConfiguredDefaultValue() {
        if (NOW.equalsIgnoreCase(definition.getDefaultValue())) {
            return new Date();
        }
        if (definition.isTime() && StringUtils.isNotEmpty(definition.getDefaultValue())) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(definition.getDateFormat() + " " + definition.getTimeFormat());
            try {
                return dateFormatter.parse(definition.getDefaultValue());
            } catch (ParseException e) {
            }
        }
        return super.getConfiguredDefaultValue();
    }
}
