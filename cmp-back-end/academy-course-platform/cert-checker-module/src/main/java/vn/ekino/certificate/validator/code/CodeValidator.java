package vn.ekino.certificate.validator.code;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.validator.AbstractStringValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.repository.GeneratedCertificateRepository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeValidator extends AbstractStringValidator {
    private final GeneratedCertificateRepository generatedCertificateRepository;
    private final Item itm;

    @Inject
    public CodeValidator(String errorMessage, GeneratedCertificateRepository generatedCertificateRepository, Item itm) {
        super(errorMessage);
        this.generatedCertificateRepository = generatedCertificateRepository;
        this.itm = itm;
    }

    @Override
    protected boolean isValidValue(String code) {
        return (StringUtils.isNotEmpty(code)) && !codeIsExist(code);
    }

    private boolean codeIsExist(String code){
        Map<String, List<String>> filtersCondition = new HashMap<>();
        filtersCondition.put("enrolProgram", List.of(itm.getItemProperty("enrolProgram").getValue().toString()));
        var unique =generatedCertificateRepository.findByMultiValue(GeneratedCertificateRepository.WORKSPACE, GeneratedCertificateRepository.NODE_TYPE, filtersCondition);
        if (unique.isPresent()) {
            return true;
        }
        return CollectionUtils.isNotEmpty(generatedCertificateRepository.findAllNodeByCode(code));
    }
}
