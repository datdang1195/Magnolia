package vn.ekino.certificate.validator.code;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;
import vn.ekino.certificate.repository.GeneratedCertificateRepository;

import javax.inject.Inject;

public class CodeValidatorFactory extends AbstractFieldValidatorFactory<CodeValidatorDefinition> {

    private final GeneratedCertificateRepository generatedCertificateRepository;
    private final Item itm;

    @Inject
    public CodeValidatorFactory(CodeValidatorDefinition definition,
                                GeneratedCertificateRepository generatedCertificateRepository, Item itm) {
        super(definition);
        this.generatedCertificateRepository = generatedCertificateRepository;
        this.itm = itm;
    }

    @Override
    public Validator createValidator() {
        return new CodeValidator(getI18nErrorMessage(), generatedCertificateRepository, itm);
    }
}
