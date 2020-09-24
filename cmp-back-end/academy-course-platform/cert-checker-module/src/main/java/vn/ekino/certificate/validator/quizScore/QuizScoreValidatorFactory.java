package vn.ekino.certificate.validator.quizScore;

import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import javax.inject.Inject;

public class QuizScoreValidatorFactory extends AbstractFieldValidatorFactory<QuizScoreValidatorDefinition> {


    @Inject
    public QuizScoreValidatorFactory(QuizScoreValidatorDefinition definition) {
        super(definition);
    }

    @Override
    public Validator createValidator() {
        return new QuizScoreValidator(getI18nErrorMessage());
    }
}
