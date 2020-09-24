package vn.ekino.certificate.validator.score;

import com.vaadin.v7.data.validator.AbstractValidator;

import javax.inject.Inject;
import java.math.BigDecimal;

public class ScoreValidator extends AbstractValidator<BigDecimal> {
    @Inject
    public ScoreValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

    @Override
    protected boolean isValidValue(BigDecimal value) {
        return value == null || isValidScore(value);
    }

    private boolean isValidScore(BigDecimal score) {
        try {
            return score.doubleValue() >= 0.0 && score.doubleValue() <= 100.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
