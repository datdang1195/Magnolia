package vn.ekino.certificate.validator.date;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import javax.inject.Inject;

public class EndDateValidatorFactory extends AbstractFieldValidatorFactory<EndDateValidatorDefinition> {

    private final Item itm;

    @Inject
    public EndDateValidatorFactory(EndDateValidatorDefinition definition, Item itm) {
        super(definition);
        this.itm = itm;
    }

    @Override
    public Validator createValidator() {
        return new EndDateValidator(getI18nErrorMessage(), itm);
    }
}
