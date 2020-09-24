package vn.ekino.certificate.action;

import info.magnolia.cms.core.Path;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.ModelConstants;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.service.SaveFormService;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

@Slf4j
public class SaveAndSendMailAction extends AbstractAction<SaveFormActionDefinition> {

    private final JcrNodeAdapter item;
    private final EditorCallback callback;
    private final EditorValidator validator;
    private final SaveFormService saveFormService;

    @Inject
    public SaveAndSendMailAction(SaveFormActionDefinition definition,
                                 JcrNodeAdapter item,
                                 EditorCallback callback,
                                 EditorValidator validator,
                                 SaveFormService saveFormService) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
        this.saveFormService = saveFormService;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (validateForm()) {
            try {
                saveFormService.checkDuplicateData(item);
                Node node = item.applyChanges();
                node = saveFormService.execute(node, true, false);
                setNodeName(node, item);
                node.getSession().save();
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

    private void setNodeName(Node node, JcrNodeAdapter item) throws RepositoryException {
        String propertyName = "name";
        if (node.hasProperty(propertyName) && !node.hasProperty(ModelConstants.JCR_NAME)) {
            Property property = node.getProperty(propertyName);
            String newNodeName = property.getString();
            if (!node.getName().equals(Path.getValidatedLabel(newNodeName))) {
                newNodeName = Path.getUniqueLabel(node.getSession(), node.getParent().getPath(), Path.getValidatedLabel(newNodeName));
                item.setNodeName(newNodeName);
                NodeUtil.renameNode(node, newNodeName);
            }
        }
    }
}
