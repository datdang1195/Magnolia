package vn.ekino.certificate.validator.code;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.validator.AbstractStringValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.repository.GeneratedInternshipCertificateRepository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntershipCodeValidator extends AbstractStringValidator {
    private final GeneratedInternshipCertificateRepository generatedInternshipCertificateRepository;
    private final Item itm;

    @Inject
    public IntershipCodeValidator(String errorMessage, GeneratedInternshipCertificateRepository generatedInternshipCertificateRepository, Item itm) {
        super(errorMessage);
        this.generatedInternshipCertificateRepository = generatedInternshipCertificateRepository;
        this.itm = itm;
    }

    @Override
    protected boolean isValidValue(String code) {
        return (StringUtils.isNotEmpty(code)) && !codeIsExist(code);
    }

    private boolean codeIsExist(String code){
        Map<String, List<String>> filtersCondition = new HashMap<>();
        filtersCondition.put("internship", List.of(itm.getItemProperty("internship").getValue().toString()));
        var unique = generatedInternshipCertificateRepository.findByMultiValue(GeneratedInternshipCertificateRepository.WORKSPACE, GeneratedInternshipCertificateRepository.NODE_TYPE, filtersCondition);
        if (unique.isPresent()) {
            return true;
        }
        return CollectionUtils.isNotEmpty(generatedInternshipCertificateRepository.findAllNodeByCode(code));
    }
}
