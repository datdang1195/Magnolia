package vn.ekino.certificate.validator.quizName;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class QuizNameValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public QuizNameValidatorDefinition() {
        setFactoryClass(QuizNameValidatorFactory.class);
    }
}
