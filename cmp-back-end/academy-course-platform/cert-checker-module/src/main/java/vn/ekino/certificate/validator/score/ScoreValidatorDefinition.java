package vn.ekino.certificate.validator.score;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class ScoreValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public ScoreValidatorDefinition() {
        setFactoryClass(ScoreValidatorFactory.class);
    }
}
