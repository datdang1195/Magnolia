package vn.ekino.certificate.validator.duration;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class DurationValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public DurationValidatorDefinition() {
        setFactoryClass(DurationValidatorFactory.class);
    }
}
