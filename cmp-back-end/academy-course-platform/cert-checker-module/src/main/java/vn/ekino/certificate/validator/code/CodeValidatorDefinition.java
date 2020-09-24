package vn.ekino.certificate.validator.code;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class CodeValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public CodeValidatorDefinition() {
        setFactoryClass(CodeValidatorFactory.class);
    }
}
