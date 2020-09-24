package vn.ekino.certificate.repository;

import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.commons.JcrUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DamRepository extends NodeRepository {
    private static final String DAM_WORKSPACE = "dam";
    private static final String DAM_NODE_TYPE = "mgnl:asset";
    private final DamTemplatingFunctions damFunctions;
    private static Map<String,String> renditions = new HashMap<>();

    public static final String PROPERTY_META_DATA = "metadataStandard";

    @Inject
    public DamRepository(DamTemplatingFunctions damFunctions) {
        this(DAM_WORKSPACE, DAM_NODE_TYPE, damFunctions);
    }

    public DamRepository(String workspace, String nodeType, DamTemplatingFunctions damFunctions) {
        super(workspace, nodeType);
        this.damFunctions = damFunctions;
//        renditions.put(Constants.Rendition.THUMBNAIL, Constants.Rendition.THUMBNAIL);
    }

    @Override
    public Optional<Node> findById(String id) {
        return super.findById(id.substring(id.indexOf(":") + 1));
    }

    public Optional<Node> findById(String id, String propertyName) {
        var asset = findById(id);
        if (asset.isPresent()) {
            Node node = asset.get();
            try {
                id = id.startsWith("jcr:") ? id : "jcr:" + id;
                String path = damFunctions.getAssetLink(id, renditions.get(propertyName));
                PropertyUtil.setProperty(node, "path", path);
                return Optional.of(node);
            } catch (RepositoryException e) {
                log.warn("Can't set property because {}", e.getMessage());
            }
        }
        return asset;
    }

    public List<Node> findByIdIn(List<String> ids, String propertyName) {
        return ids.stream()
                .map(id -> findById(id, propertyName))
                .filter(node -> node.isPresent())
                .map(node -> node.get())
                .collect(Collectors.toList());
    }

    public Node getOrAddFolder(Node parent, String name) {
        Node node = null;
        try {
            node = JcrUtils.getOrAddFolder(parent, name);
            node.setPrimaryType("mgnl:folder");
        } catch (RepositoryException e) {
            log.warn("Can't get folder because {}", e.getMessage());
        }
        return node;
    }
}
