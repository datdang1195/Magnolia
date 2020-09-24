package vn.ekino.certificate.validator.quizScore;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class QuizScoreValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public QuizScoreValidatorDefinition() {
        setFactoryClass(QuizScoreValidatorFactory.class);
    }
}
