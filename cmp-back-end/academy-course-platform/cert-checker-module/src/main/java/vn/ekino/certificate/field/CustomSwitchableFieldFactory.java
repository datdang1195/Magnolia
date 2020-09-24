package vn.ekino.certificate.field;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.Field;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.SwitchableField;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.definition.Layout;
import info.magnolia.ui.form.field.definition.OptionGroupFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.StaticFieldDefinition;
import info.magnolia.ui.form.field.definition.SwitchableFieldDefinition;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;
import info.magnolia.ui.form.field.factory.SwitchableFieldFactory;
import info.magnolia.ui.form.field.transformer.Transformer;
import info.magnolia.ui.form.field.transformer.composite.DelegatingCompositeFieldTransformer;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CustomSwitchableFieldFactory <D extends CustomSwitchableFieldDefinition> extends AbstractFieldFactory<D, PropertysetItem> {

    private static final Logger log = LoggerFactory.getLogger(SwitchableFieldFactory.class);

    private final FieldFactoryFactory fieldFactoryFactory;
    private final ComponentProvider componentProvider;
    private final I18NAuthoringSupport i18nAuthoringSupport;
    private final ProgramRepository programRepository;

    @Inject
    public CustomSwitchableFieldFactory(D definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, ProgramRepository programRepository) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport);
        this.fieldFactoryFactory = fieldFactoryFactory;
        this.componentProvider = componentProvider;
        this.i18nAuthoringSupport = i18nAuthoringSupport;
        this.programRepository = programRepository;
    }

    @Deprecated
    public CustomSwitchableFieldFactory(D definition, Item relatedFieldItem, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, I18NAuthoringSupport i18nAuthoringSupport, ProgramRepository programRepository) {
        this(definition, relatedFieldItem, null, i18nAuthoringSupport, fieldFactoryFactory, componentProvider, programRepository);

    }

    /**
     * @deprecated since 5.3.5 removing i18nContentSupport dependency (actually unused way before that). Besides, fields should use i18nAuthoringSupport for internationalization.
     */
    @Deprecated
    public CustomSwitchableFieldFactory(D definition, Item relatedFieldItem, FieldFactoryFactory fieldFactoryFactory, I18nContentSupport i18nContentSupport, ComponentProvider componentProvider, ProgramRepository programRepository) {
        this(definition, relatedFieldItem, null, componentProvider.getComponent(I18NAuthoringSupport.class), fieldFactoryFactory, componentProvider, programRepository);
    }

    @SneakyThrows
    @Override
    public Field<PropertysetItem> createField() {
        var field = super.createField();
        if (definition.isCheckProgramFinish()) {
            field.setReadOnly(true);
            JcrNodeAdapter item = (JcrNodeAdapter) field.getValue().getItemProperty("enrollStatus").getValue();
            String programId = (String) item.getItemProperty("program").getValue();
            programRepository.findById(programId).ifPresent(itm -> {
                var dto = MapperUtils.nodeToObject(itm, ProgramDto.class).get();
                if ("Completed".equals(dto.getStatus().getDisplayName())) {
                    field.setEnabled(false);
                }
            });
        }
        definition.setField(field);
        return  field;
    }

    @Override
    protected Field<PropertysetItem> createFieldComponent() {
        // FIXME change i18n setting : MGNLUI-1548
        Messages messages = getMessages();
        if (messages != null) {
            definition.setI18nBasename(messages.getBasename());
        }

        // create the select field definition
        if (!containsSelectFieldDefinition()) {
            definition.addField(createSelectFieldDefinition());
        }

        return new SwitchableField(definition, fieldFactoryFactory, componentProvider, item, i18nAuthoringSupport);
    }

    /**
     * Create a new Instance of {@link Transformer}.
     */
    @Override
    protected Transformer<?> initializeTransformer(Class<? extends Transformer<?>> transformerClass) {
        // fieldNames list is unmodifiable, ensure safe usage in transformers (e.g. MailSecurityTransformer)
        List<String> propertyNames = new ArrayList<>(definition.getFieldNames());
        if (!propertyNames.contains(definition.getName())) {
            propertyNames.add(definition.getName());
        }
        final Transformer<?> transformer = this.componentProvider.newInstance(transformerClass, item, definition, PropertysetItem.class, propertyNames, i18nAuthoringSupport);
        transformer.setLocale(getLocale());
        return transformer;
    }

    /**
     * @return true if the select field definition was already initialized.
     */
    private boolean containsSelectFieldDefinition() {
        for (ConfiguredFieldDefinition fieldDefinition : definition.getFields()) {
            if (StringUtils.equals(fieldDefinition.getName(), definition.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@link SelectFieldDefinition} initialized based on the {@link SwitchableFieldDefinition#getOptions()} and relevant options. <br>
     * In case of exception, return a {@link StaticFieldDefinition} containing a warn message.
     */
    protected ConfiguredFieldDefinition createSelectFieldDefinition() {
        try {
            SelectFieldDefinition selectDefinition;
            // Create the correct definition class
            String layout = "horizontal";
            if (definition.getSelectionType().equals("radio")) {
                selectDefinition = new OptionGroupFieldDefinition();
                if (definition.getLayout() == Layout.vertical) {
                    layout = "vertical";
                }
            } else {
                selectDefinition = new SelectFieldDefinition();
            }
            // Copy options to the newly created select definition. definition
            selectDefinition.setOptions(definition.getOptions());
            selectDefinition.setTransformerClass(null);
            selectDefinition.setRequired(false);
            selectDefinition.setSortOptions(false);
            selectDefinition.setStyleName(layout);
            selectDefinition.setName(definition.getName());
            if (definition.isI18n() && definition.getTransformerClass().isAssignableFrom(DelegatingCompositeFieldTransformer.class)) {
                selectDefinition.setI18n(definition.isI18n());
            }
            return selectDefinition;
        } catch (Exception e) {
            log.warn("Couldn't create the select field.", e.getMessage());
            StaticFieldDefinition definition = new StaticFieldDefinition();
            definition.setName(this.definition.getName());
            definition.setValue("Select definition not correctly initialised. Please check your field configuration");
            return definition;
        }
    }
}
