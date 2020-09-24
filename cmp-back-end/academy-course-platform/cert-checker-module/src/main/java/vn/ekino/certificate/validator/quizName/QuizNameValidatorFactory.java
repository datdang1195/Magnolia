package vn.ekino.certificate.validator.quizName;

import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import javax.inject.Inject;

public class QuizNameValidatorFactory extends AbstractFieldValidatorFactory<QuizNameValidatorDefinition> {


    @Inject
    public QuizNameValidatorFactory(QuizNameValidatorDefinition definition) {
        super(definition);
    }

    @Override
    public Validator createValidator() {
        return new QuizNameValidator(getI18nErrorMessage());
    }
}
