package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import info.magnolia.cms.util.PathUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.iterator.FilteringNodeIterator;
import info.magnolia.rest.delivery.jcr.filter.NodeTypesPredicate;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.util.NodeUtils;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Singleton
public class CategoryRepository extends NodeRepository {
    private static final String CATEGORY_WORKSPACE = "category";
    private static final String CATEGORY_NODE_TYPE = "mgnl:category";
    private static final String CATEGORY_COURSE_TYPES_ROOT_PATH = "course-categories";
    private static final String CATEGORY_PHASE_ROOT_PATH = "phases";
    public static final String ASSESSMENT_TYPE = "assessment-type";

    public CategoryRepository() {
        this(CATEGORY_WORKSPACE, CATEGORY_NODE_TYPE);
    }

    public CategoryRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> findAllPhaseCategories() {
        return this.getNodeWithNaturalOrder(CATEGORY_PHASE_ROOT_PATH);
    }

    /**
     * find all category's of course
     *
     * @return
     */
    public List<Node> findAllCourseCategories() {
        return this.getNodeWithNaturalOrder(CATEGORY_COURSE_TYPES_ROOT_PATH);
    }

    public Optional<Node> findByDisplayName(String displayName) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("displayName", List.of(displayName)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public List<Node> findAllAssessmentType() {
        return getNodeWithNaturalOrder(ASSESSMENT_TYPE);
    }
    public List<Node> findAllByPath(String path) {
        return getNodeWithNaturalOrder(path);
    }

    public List<Node> getRelatedCategoryById(String uuid){
        Optional<Node> nodeOptional = findById(uuid);
        return nodeOptional
                .map(node -> NodeUtils.getChildNode(node, "relatedUUID")
                .map(NodeUtils::getSubNodes).orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());

    }

    public Optional<Node> findAssessmentGroupByDisplayName(String displayName) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("displayName", List.of(displayName)));
            NodeIterator iterator = query.execute().getNodes();
            while (iterator.hasNext()) {
                Node node = iterator.nextNode();
                if (node.getPath().contains("assessment-group")) {
                    return Optional.of(node);
                }
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    private List<Node> getNodeWithNaturalOrder(String path) {
        try {
            String nodePath = PathUtil.createPath("/", path);
            Node parent = MgnlContext.getJCRSession(workspace).getNode(nodePath);
            NodeIterator nodeIterator = new FilteringNodeIterator(parent.getNodes(),
                    new NodeTypesPredicate(List.of(CATEGORY_NODE_TYPE), false));
            return Lists.newArrayList(nodeIterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes in workspace {} with path [{}] because: {}", workspace, path, e.getMessage());
        }
        return Collections.emptyList();
    }


}
