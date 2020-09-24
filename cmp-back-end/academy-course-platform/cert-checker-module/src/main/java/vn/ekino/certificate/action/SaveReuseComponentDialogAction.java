package vn.ekino.certificate.action;

import com.vaadin.v7.data.Item;
import info.magnolia.ui.admincentral.dialog.action.SaveDialogAction;
import info.magnolia.ui.admincentral.dialog.action.SaveDialogActionDefinition;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.repository.WebsiteRepository;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Slf4j
public class SaveReuseComponentDialogAction extends SaveDialogAction {

    private final WebsiteRepository websiteRepository;

    @Inject
    public SaveReuseComponentDialogAction(SaveDialogActionDefinition definition, Item item, EditorValidator validator, EditorCallback callback, WebsiteRepository websiteRepository) {
        super(definition, item, validator, callback);
        this.websiteRepository = websiteRepository;
    }

    @Override
    public void execute() throws ActionExecutionException {
        validator.showValidation(true);
        if (validator.isValid()) {
            final JcrNodeAdapter itemChanged = (JcrNodeAdapter) item;
            try {
                final Node node = itemChanged.applyChanges();
                setNodeName(node, itemChanged);
                setReferenceProperties(node);
                node.getSession().save();
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            callback.onSuccess(getDefinition().getName());
        } else {
            log.warn("Validation error(s) occurred. No save performed.");
        }
    }

    private void setReferenceProperties(Node node) {
        var result = websiteRepository.getReferenceNode(node);
        if (result.isPresent()) {
            try {
                Node referenceNode = result.get();
                node.setProperty("referencePath", referenceNode.getPath());
                node.setProperty("referenceId", referenceNode.getIdentifier());

                if (referenceNode.hasProperty("mgnl:template")) {
                    node.setProperty("referenceTemplate", referenceNode.getProperty("mgnl:template").getString());
                }
            } catch (RepositoryException e) {
                log.warn("Problem while setting reference properties because {}", e.getMessage());
            }
        }
    }
}