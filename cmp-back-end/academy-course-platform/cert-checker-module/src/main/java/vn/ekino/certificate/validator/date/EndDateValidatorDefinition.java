package vn.ekino.certificate.validator.date;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class EndDateValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public EndDateValidatorDefinition() {
        setFactoryClass(EndDateValidatorFactory.class);
    }
}
