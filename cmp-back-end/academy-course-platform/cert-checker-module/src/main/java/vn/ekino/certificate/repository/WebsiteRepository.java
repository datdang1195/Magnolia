package vn.ekino.certificate.repository;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

@Slf4j
public class WebsiteRepository extends NodeRepository {
    public static final String WEBSITE_WORKSPACE = "website";
    private static final String WEBSITE_NODE_TYPE = "mgnl:page";

    public static final String PROPERTY_TEMPLATE = "mgnl:template";
    public static final String PROPERTY_TITLE = "title";
    public static final String PROPERTY_FULL_NAME = "fullName";
    public static final String PROPERTY_EMAIL = "email";
    public static final String PROPERTY_CERTIFICATE_IMAGE = "certificateImage";
    public static final String PROPERTY_CERTIFICATE_URL = "certificateUrl";

    public WebsiteRepository() {
        this(WEBSITE_WORKSPACE, WEBSITE_NODE_TYPE);
    }

    public WebsiteRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> getReferenceNode(Node node) {
        try {
            String type = PropertyUtil.getString(node, "type");
            String reference = PropertyUtil.getString(node, "referenceLink");
            String referencePath = String.format("%s/%s", reference, type);
            String sql = "SELECT * from [nt:base] AS t WHERE ISCHILDNODE([" + referencePath + "]) and name(t) = '0'";
            NodeIterator iterator = QueryUtil.search("website", sql);
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("Problem while getting the referenced node because {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Node> getWebsiteByPath(String path) {
        try {
            Session session = MgnlContext.getJCRSession(workspace);
            Node rootNode = session.getRootNode();
            if (rootNode.hasNode(path)) {
                return  Optional.of(rootNode.getNode(path));
            }
        } catch (RepositoryException e) {
            log.warn("can't get node because: {}", e.getMessage());
        }
        return Optional.empty();
    }

}
