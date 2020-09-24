package vn.ekino.certificate.service;

import info.magnolia.cms.security.User;
import info.magnolia.commands.CommandsManager;
import info.magnolia.commands.chain.Command;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Slf4j
public class PublishingService {

    private final CommandsManager commandsManager;

    private static final String CATALOG_PUBLISH = "versioned";
    private static final String COMMAND_PUBLISH = "activate";
    private static final String PROPERTY_MODIFIED_ONLY = "modifiedOnly";

    @Inject
    public PublishingService(CommandsManager commandsManager) {
        this.commandsManager = commandsManager;
    }


    public boolean publish(List<Node> nodeList) {
        Command command = commandsManager.getCommand(CATALOG_PUBLISH, COMMAND_PUBLISH);
        User user = MgnlContext.getUser();
        try {
            for (Node node: nodeList) {
                Map<String, Object> params = new HashMap<>();
                final String path = node.getPath();
                final String workspace = node.getSession().getWorkspace().getName();
                final String identifier = node.getIdentifier();

                params.put(Context.ATTRIBUTE_REPOSITORY, workspace);
                params.put(Context.ATTRIBUTE_UUID, identifier);
                params.put(Context.ATTRIBUTE_PATH, path);
                params.put(Context.ATTRIBUTE_USERNAME, user.getName());
                params.put(Context.ATTRIBUTE_REQUESTOR, user.getName());
                params.put(Context.ATTRIBUTE_RECURSIVE, true);
                params.put(PROPERTY_MODIFIED_ONLY, false);
                commandsManager.executeCommand(command, params);
            }
        } catch (Exception e) {
            log.warn("can't execute command because {}", e.getMessage());
            return false;
        }
        return true;
    }
}
