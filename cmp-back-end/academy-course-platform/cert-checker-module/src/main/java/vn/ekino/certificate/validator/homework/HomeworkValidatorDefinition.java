package vn.ekino.certificate.validator.homework;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class HomeworkValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public HomeworkValidatorDefinition() {
        setFactoryClass(HomeworkValidatorFactory.class);
    }
}
