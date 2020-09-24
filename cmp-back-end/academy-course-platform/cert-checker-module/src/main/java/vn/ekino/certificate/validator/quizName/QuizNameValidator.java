package vn.ekino.certificate.validator.quizName;

import com.vaadin.v7.data.validator.AbstractValidator;

import javax.inject.Inject;

public class QuizNameValidator extends AbstractValidator<String> {

    @Inject
    public QuizNameValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    protected boolean isValidValue(String value) {
        return value == null || isValidQuizName(value);
    }

    private boolean isValidQuizName(String value) {
        try {
            var quizName = Double.parseDouble(value);
            return quizName > 0.0 && isIntegerValue(quizName);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isIntegerValue(double number) {
        return number == (int) number;
    }
}
