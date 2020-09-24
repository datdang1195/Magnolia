package vn.ekino.certificate.validator.quizScore;

import com.vaadin.v7.data.validator.AbstractValidator;

import javax.inject.Inject;

public class QuizScoreValidator extends AbstractValidator<String> {
    @Inject
    public QuizScoreValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    protected boolean isValidValue(String value) {
        return value == null || isValidScore(value);
    }

    private boolean isValidScore(String value) {
        try {
            double score = Double.valueOf(value);
            return score >= 0.0 && score <= 100.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
