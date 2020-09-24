package vn.ekino.certificate.validator.duration;

import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import javax.inject.Inject;

public class DurationValidatorFactory extends AbstractFieldValidatorFactory<DurationValidatorDefinition> {

    @Inject
    public DurationValidatorFactory(DurationValidatorDefinition definition) {
        super(definition);
    }

    @Override
    public Validator createValidator() {
        return new DurationValidator(getI18nErrorMessage());
    }
}
