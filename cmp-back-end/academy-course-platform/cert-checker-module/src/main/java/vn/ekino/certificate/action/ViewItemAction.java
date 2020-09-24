package vn.ekino.certificate.action;

import com.vaadin.v7.data.Item;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.location.LocationController;
import info.magnolia.ui.contentapp.detail.DetailLocation;
import info.magnolia.ui.contentapp.detail.DetailView;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViewItemAction extends AbstractAction<ViewItemActionDefinition> {

    private final Item nodeItemToEdit;
    private final LocationController locationController;
    private final ContentConnector contentConnector;

    public ViewItemAction(ViewItemActionDefinition definition, Item nodeItemToEdit, LocationController locationController, ContentConnector contentConnector) {
        super(definition);
        this.nodeItemToEdit = nodeItemToEdit;
        this.locationController = locationController;
        this.contentConnector = contentConnector;
    }

    @Override
    public void execute() throws ActionExecutionException {
        try {
            Object itemId = contentConnector.getItemId(nodeItemToEdit);
            if (!contentConnector.canHandleItem(itemId)) {
                log.warn("EditItemAction requested for a node type definition {}. Current node type is {}. No action will be performed.", getDefinition(), String.valueOf(itemId));
                return;
            }

            final String path = contentConnector.getItemUrlFragment(itemId);
            DetailLocation location = new DetailLocation(getDefinition().getAppName(), getDefinition().getSubAppId(), DetailView.ViewType.VIEW, path, "");
            locationController.goTo(location);

        } catch (Exception e) {
            throw new ActionExecutionException("Could not execute EditItemAction: ", e);
        }
    }
}
