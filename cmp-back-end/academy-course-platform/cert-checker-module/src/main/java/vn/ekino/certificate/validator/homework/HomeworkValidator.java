package vn.ekino.certificate.validator.homework;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.validator.AbstractValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.inject.Inject;

public class HomeworkValidator extends AbstractValidator<Item> {

    public static final String HOMEWORK_TITLE = "homeworkTitle";
    public static final String HOMEWORK_DESCRIPTION = "homeworkDescription";
    private final Item item;
    private final JcrNodeAdapter jcrNodeAdapter;

    @Inject
    public HomeworkValidator(String errorMessage,
                             Item item,
                             JcrNodeAdapter jcrNodeAdapter) {
        super(errorMessage);
        this.item = item;
        this.jcrNodeAdapter = jcrNodeAdapter;
    }

    private boolean homeworkTitleNotEmpty() {
        return jcrNodeAdapter.getItemProperty(HOMEWORK_TITLE).getValue() != null;
    }

    private boolean homeworkDescriptionNotEmpty() {
        return jcrNodeAdapter.getItemProperty(HOMEWORK_DESCRIPTION).getValue() != null;
    }

    @Override
    protected boolean isValidValue(Item item) {
        if (item.getItemPropertyIds().size() > 0) {
            return homeworkTitleNotEmpty() && homeworkDescriptionNotEmpty();
        }
        return true;
    }

    @Override
    public Class<Item> getType() {
        return Item.class;
    }
}
