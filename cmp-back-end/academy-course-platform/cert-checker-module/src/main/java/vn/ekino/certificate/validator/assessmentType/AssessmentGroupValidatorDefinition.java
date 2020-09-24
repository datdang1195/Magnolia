package vn.ekino.certificate.validator.assessmentType;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

public class AssessmentGroupValidatorDefinition extends ConfiguredFieldValidatorDefinition {
    public AssessmentGroupValidatorDefinition() {
        setFactoryClass(AssessmentGroupValidatorFactory.class);
    }
}
