package vn.ekino.certificate.validator.attendance;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;
import vn.ekino.certificate.repository.AttendanceRepository;

import javax.inject.Inject;

public class AttendanceValidatorFactory extends AbstractFieldValidatorFactory<AttendanceValidatorDefinition> {

    private final Item itm;
    private final AttendanceRepository attendanceRepository;

    @Inject
    public AttendanceValidatorFactory(AttendanceValidatorDefinition definition, Item itm, AttendanceRepository attendanceRepository) {
        super(definition);
        this.itm = itm;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public Validator createValidator() {
        return new AttendanceValidator(getI18nErrorMessage(), itm, attendanceRepository);
    }
}
