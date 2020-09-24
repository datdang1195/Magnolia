package vn.ekino.certificate.action;

import com.vaadin.v7.data.Item;
import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.commands.CommandsManager;
import info.magnolia.event.EventBus;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.event.ContentChangedEvent;
import info.magnolia.ui.dialog.DialogPresenter;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeItemId;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.command.ImportCommand;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ImportAction extends AbstractAction<ImportActionDefinition> {

    private final Item item;
    private final CommandsManager commandsManager;
    private final EditorValidator validator;
    private final EditorCallback callback;
    private final EventBus eventBus;
    private final SubAppContext uiContext;
    private final DialogPresenter dialogPresenter;

    protected ImportAction(ImportActionDefinition definition, Item item, CommandsManager commandsManager, EditorValidator validator, EditorCallback callback, EventBus eventBus, SubAppContext uiContext, DialogPresenter dialogPresenter) {
        super(definition);
        this.item = item;
        this.commandsManager = commandsManager;
        this.validator = validator;
        this.callback = callback;
        this.eventBus = eventBus;
        this.uiContext = uiContext;
        this.dialogPresenter = dialogPresenter;
    }

    public ImportAction(ImportActionDefinition definition, Item item, CommandsManager commandsManager, EditorValidator validator, EditorCallback callback, SubAppContext uiContext, DialogPresenter dialogPresenter) {
        this(definition, item, commandsManager, validator, callback, null, uiContext, dialogPresenter);
    }

    @Override
    public void execute() throws ActionExecutionException {
        // First Validate
        validator.showValidation(true);
        if (validator.isValid()) {
            try {
                JcrNodeAdapter parent = (JcrNodeAdapter) item;
                JcrNodeAdapter importXml = (JcrNodeAdapter) parent.getChild("import");
                if (importXml == null) {
                    throw new IllegalArgumentException("Nothing to import, given item does not contain any child named 'import'.");
                }

                if (eventBus != null) {
                    List<Node> nodesBeforeImport = NodeUtil.asList(NodeUtil.asIterable(parent.getJcrItem().getNodes()));
                    executeCommand(parent, dialogPresenter);

                    List<Node> nodesAfterImport = NodeUtil.asList(NodeUtil.asIterable(parent.getJcrItem().getNodes()));
                    Set<JcrNodeItemId> importedNodeItemIds = getImportedNodeItemIds(nodesBeforeImport, nodesAfterImport);

                    eventBus.fireEvent(new ContentChangedEvent(importedNodeItemIds));
                    callback.onCancel();

                } else {
                    executeCommand(parent, dialogPresenter);
                    callback.onSuccess(getDefinition().getName());
                }

            } catch (RepositoryException e) {
                throw new ActionExecutionException(e);
            }
        } else {
            log.info("Validation error(s) occurred. No Import performed.");
        }
    }

    private Set<JcrNodeItemId> getImportedNodeItemIds(List<Node> nodesBeforeImport, List<Node> nodesAfterImport) throws RepositoryException {
        Set<JcrNodeItemId> importedNodeItemIds = new HashSet<>();
        for (Node nodeAfterImport : nodesAfterImport) {
            boolean existedBeforeImport = false;
            for (Node nodeBeforeImport : nodesBeforeImport) {
                if (NodeUtil.isSame(nodeAfterImport, nodeBeforeImport)) {
                    existedBeforeImport = true;
                    break;
                }
            }
            if (!existedBeforeImport) {
                importedNodeItemIds.add(new JcrNodeItemId(nodeAfterImport.getIdentifier(), nodeAfterImport.getSession().getWorkspace().getName()));
            }
        }
        return importedNodeItemIds;
    }

    public void executeCommand(JcrNodeAdapter itemChanged, DialogPresenter dialogPresenter) throws ActionExecutionException {

        String commandName = getDefinition().getCommand();
        String catalog = getDefinition().getCatalog();
        long start = System.currentTimeMillis();
        try {
            ImportCommand command = (ImportCommand) this.commandsManager.getCommand(catalog, commandName);
            if (command == null) {
                throw new ActionExecutionException(String.format("Could not find command [%s] in any catalog", commandName));
            }
            final JcrNodeAdapter node = (JcrNodeAdapter) itemChanged.getChild("import");
            command.setStream(((Binary)node.getItemProperty(JcrConstants.JCR_DATA).getValue()).getStream());
            command.setFileName((String) node.getItemProperty(FileProperties.PROPERTY_FILENAME).getValue());
            command.setRepository(itemChanged.getWorkspace());
            command.setPath(itemChanged.getJcrItem().getPath());
            log.debug("Executing command [{}] from catalog [{}] with the following parameters [{}]...", new Object[]{commandName, catalog});
            commandsManager.executeCommand(command, Map.of("uiContext", uiContext, "dialogPresenter", dialogPresenter));
            log.debug("Command executed successfully in {} ms ", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.debug("Command execution failed after {} ms ", System.currentTimeMillis() - start);
            throw new ActionExecutionException(e);
        }
    }

    protected CommandsManager getCommandsManager() {
        return commandsManager;
    }
}
