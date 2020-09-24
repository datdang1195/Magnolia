package vn.ekino.certificate.validator.rate;

import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import javax.inject.Inject;

public class RateValidatorFactory extends AbstractFieldValidatorFactory<RateValidatorDefinition> {

    @Inject
    public RateValidatorFactory(RateValidatorDefinition definition) {
        super(definition);
    }

    @Override
    public Validator createValidator() {
        return new RateValidator(getI18nErrorMessage());
    }
}
