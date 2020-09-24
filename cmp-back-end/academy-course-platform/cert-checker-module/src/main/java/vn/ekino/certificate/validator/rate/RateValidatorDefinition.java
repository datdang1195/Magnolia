package vn.ekino.certificate.validator.rate;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class RateValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public RateValidatorDefinition() {
        setFactoryClass(RateValidatorFactory.class);
    }
}
