package vn.ekino.certificate.action;

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.ModelConstants;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.repository.NewsRepository;
import vn.ekino.certificate.service.PublishingService;
import vn.ekino.certificate.service.SaveFormService;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.List;

@Slf4j
public class SaveAndNotifyAction extends AbstractAction<SaveFormActionDefinition> {

    private final JcrNodeAdapter item;
    private final EditorCallback callback;
    private final EditorValidator validator;
    private final SaveFormService saveFormService;
    private final PublishingService publishingService;

    @Inject
    public SaveAndNotifyAction(SaveFormActionDefinition definition,
                               JcrNodeAdapter item,
                               EditorCallback callback,
                               EditorValidator validator,
                               SaveFormService saveFormService,
                               PublishingService publishingService) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
        this.saveFormService = saveFormService;
        this.publishingService = publishingService;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (validateForm()) {
            try {
                saveFormService.checkDuplicateData(item);
                Node node = item.applyChanges();
                node = saveFormService.execute(node, false, true);
                if (!(NewsRepository.NEWS_WORKSPACE).equals(node.getSession().getWorkspace().getName())) {
                    setNodeName(node);
                }
                node.getSession().save();
                publishingService.publish(List.of(node));
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            callback.onSuccess(getDefinition().getName());
        }
    }

    private boolean validateForm() {
        boolean isValid = validator.isValid();
        validator.showValidation(!isValid);
        if (!isValid) {
            log.info("Validation error(s) occurred. No save performed.");
        }
        return isValid;
    }

    private void setNodeName(Node node) throws RepositoryException {
        String propertyName = "name";
        if (node.hasProperty(propertyName) && !node.hasProperty(ModelConstants.JCR_NAME)) {
            Property property = node.getProperty(propertyName);
            String newNodeName = property.getString();
            NodeUtil.renameNode(node, newNodeName);
        }
    }

}
