package vn.ekino.certificate.validator.assessmentType;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import vn.ekino.certificate.repository.CategoryRepository;

import javax.inject.Inject;

public class AssessmentGroupValidatorFactory extends AbstractFieldValidatorFactory<AssessmentGroupValidatorDefinition> {

    private final Item item;
    private final JcrNodeAdapter jcrNodeAdapter;
    private final CategoryRepository categoryRepository;

    @Inject
    public AssessmentGroupValidatorFactory(AssessmentGroupValidatorDefinition definition,
                                           Item item,
                                           JcrNodeAdapter jcrNodeAdapter,
                                           CategoryRepository categoryRepository) {
        super(definition);
        this.item = item;
        this.jcrNodeAdapter = jcrNodeAdapter;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Validator createValidator() {
        return new AssessmentGroupValidator(getI18nErrorMessage(), item, jcrNodeAdapter, categoryRepository);
    }
}
