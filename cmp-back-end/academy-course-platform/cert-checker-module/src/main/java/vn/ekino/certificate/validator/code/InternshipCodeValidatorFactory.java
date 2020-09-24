package vn.ekino.certificate.validator.code;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;
import vn.ekino.certificate.repository.GeneratedInternshipCertificateRepository;

import javax.inject.Inject;

public class InternshipCodeValidatorFactory extends AbstractFieldValidatorFactory<InternshipCodeValidatorDefinition> {

    private final GeneratedInternshipCertificateRepository generatedInternshipCertificateRepository;
    private final Item itm;

    @Inject
    public InternshipCodeValidatorFactory(InternshipCodeValidatorDefinition definition,
                                          GeneratedInternshipCertificateRepository generatedInternshipCertificateRepository, Item itm) {
        super(definition);
        this.generatedInternshipCertificateRepository = generatedInternshipCertificateRepository;
        this.itm = itm;
    }

    @Override
    public Validator createValidator() {
        return new IntershipCodeValidator(getI18nErrorMessage(), generatedInternshipCertificateRepository, itm);
    }
}
