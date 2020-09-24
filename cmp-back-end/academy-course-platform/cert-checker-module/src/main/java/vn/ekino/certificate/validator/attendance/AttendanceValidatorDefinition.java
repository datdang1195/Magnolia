package vn.ekino.certificate.validator.attendance;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class AttendanceValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public AttendanceValidatorDefinition() {
        setFactoryClass(AttendanceValidatorFactory.class);
    }
}
