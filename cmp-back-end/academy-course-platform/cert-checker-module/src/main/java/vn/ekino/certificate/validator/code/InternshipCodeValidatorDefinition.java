package vn.ekino.certificate.validator.code;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class InternshipCodeValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public InternshipCodeValidatorDefinition() {
        setFactoryClass(InternshipCodeValidatorFactory.class);
    }
}
