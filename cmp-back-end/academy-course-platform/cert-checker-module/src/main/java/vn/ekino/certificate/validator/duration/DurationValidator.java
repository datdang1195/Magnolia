package vn.ekino.certificate.validator.duration;

import com.vaadin.v7.data.validator.AbstractStringValidator;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;

public class DurationValidator extends AbstractStringValidator {

    @Inject
    public DurationValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    protected boolean isValidValue(String value) {
        return (StringUtils.isNotEmpty(value)) && durationIsValid(value);
    }

    private boolean durationIsValid(String value) {
        try {
            int duration = Integer.parseInt(value);
            return duration >= 1 && duration <= 24;
        }catch (NumberFormatException e){
            return false;
        }
    }
}
