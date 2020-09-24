package vn.ekino.certificate.validator.rate;

import com.vaadin.v7.data.validator.AbstractValidator;

import javax.inject.Inject;

public class RateValidator extends AbstractValidator<String> {
    @Inject
    public RateValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    protected boolean isValidValue(String value) {
        return value == null || isValidRate(value);
    }

    private boolean isValidRate(String value) {
        try {
            var rate = Double.parseDouble(value);
            return rate >= 0.0 && rate <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
