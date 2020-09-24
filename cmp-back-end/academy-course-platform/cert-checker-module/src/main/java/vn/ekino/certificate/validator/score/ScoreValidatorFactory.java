package vn.ekino.certificate.validator.score;

import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import javax.inject.Inject;

public class ScoreValidatorFactory extends AbstractFieldValidatorFactory<ScoreValidatorDefinition> {


    @Inject
    public ScoreValidatorFactory(ScoreValidatorDefinition definition) {
        super(definition);
    }

    @Override
    public Validator createValidator() {
        return new ScoreValidator(getI18nErrorMessage());
    }
}
