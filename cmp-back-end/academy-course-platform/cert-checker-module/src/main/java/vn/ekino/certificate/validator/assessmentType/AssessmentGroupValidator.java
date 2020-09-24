package vn.ekino.certificate.validator.assessmentType;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.validator.AbstractStringValidator;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.repository.CategoryRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AssessmentGroupValidator extends AbstractStringValidator  {

    private static final String ASSESSMENT_TYPE = "assessmentType";
    private final Item item;
    private final JcrNodeAdapter jcrNodeAdapter;
    private final CategoryRepository categoryRepository;

    @Inject
    public AssessmentGroupValidator(String errorMessage,
                                    Item item,
                                    JcrNodeAdapter jcrNodeAdapter,
                                    CategoryRepository categoryRepository) {
        super(errorMessage);
        this.item = item;
        this.jcrNodeAdapter = jcrNodeAdapter;
        this.categoryRepository = categoryRepository;
    }



    @Override
    protected boolean isValidValue(String value) {
        String assessmentGroupId = value;
        String assessmentTypeId = Optional
                .ofNullable((String) item.getItemProperty(ASSESSMENT_TYPE).getValue())
                .orElse(StringUtils.EMPTY);
        if (StringUtils.isNotEmpty(assessmentGroupId) && StringUtils.isNotEmpty(assessmentTypeId)) {
            List<String> relatedCategories = categoryRepository
                    .getRelatedCategoryById(assessmentTypeId)
                    .stream()
                    .map(node -> PropertyUtil.getString(node, "relatedUUID"))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());


            return relatedCategories.contains(assessmentGroupId);
        }
        return false;
    }
}
