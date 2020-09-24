package vn.ekino.certificate.validator.homework;

import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;
import com.vaadin.v7.data.Item;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.inject.Inject;

public class HomeworkValidatorFactory extends AbstractFieldValidatorFactory<HomeworkValidatorDefinition> {

    private final Item item;
    private final JcrNodeAdapter jcrNodeAdapter;

    @Inject
    public HomeworkValidatorFactory(HomeworkValidatorDefinition definition, Item item, JcrNodeAdapter jcrNodeAdapter) {
        super(definition);
        this.item = item;
        this.jcrNodeAdapter = jcrNodeAdapter;
    }

    @Override
    public Validator createValidator() {
        return new HomeworkValidator(getI18nErrorMessage(), item, jcrNodeAdapter);
    }
}
