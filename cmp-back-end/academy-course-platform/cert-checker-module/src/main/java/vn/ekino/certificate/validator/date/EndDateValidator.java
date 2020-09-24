package vn.ekino.certificate.validator.date;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.validator.AbstractValidator;

import javax.inject.Inject;
import java.util.Date;

public class EndDateValidator extends AbstractValidator<Date> {

    private final Item itm;

    @Inject
    public EndDateValidator(String errorMessage, Item itm) {
        super(errorMessage);
        this.itm = itm;
    }

    @Override
    protected boolean isValidValue(Date endDate) {
        Date startDate = (Date) itm.getItemProperty("startDate").getValue();
        return endDate.compareTo(startDate) >= 0;
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }
}
