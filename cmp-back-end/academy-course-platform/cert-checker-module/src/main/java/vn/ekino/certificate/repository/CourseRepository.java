package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class CourseRepository extends NodeRepository {

    public static final String COURSE_WORKSPACE = "courses";
    private static final String COURSE_NODE_TYPE = "mgnl:course";

    public CourseRepository() {
        this(COURSE_WORKSPACE, COURSE_NODE_TYPE);
    }

    public CourseRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findCourseNodeByUuidAndCategory(String uuid, String category) {
        try {
            Map<String, List<String>> filtering = new HashMap<>();
            filtering.put("@jcr:uuid", List.of(uuid));
            filtering.put("category", List.of(category));

            Query query = buildQuery(workspace, List.of(nodeType), filtering);
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find all node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public List<Node> findSessionsNodeByCoursePath(String coursePath) {
        return findNodesByParentNodePath(coursePath, "sessions");
    }

    public List<Node> findTrainingMaterialsNodeByCoursePath(String coursePath) {
        return findNodesByParentNodePath(coursePath, "trainingMaterials");
    }

    private List<Node> findNodesByParentNodePath(String parentNodePath, String childNodePath) {
        try {
            Session session = MgnlContext.getJCRSession(COURSE_WORKSPACE);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query query = qm.createQuery("SELECT * FROM [mgnl:contentNode] AS NODE "
                            + "WHERE ISDESCENDANTNODE (NODE, '" + parentNodePath + "/" + childNodePath + "')",
                    Query.JCR_SQL2);
            NodeIterator iterator = query.execute().getNodes();

            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> findByCourseId(String courseId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("courseId", List.of(courseId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }


    public List<Node> findByIdsAndCategory(List<String> ids, String categoryId) {
        return findByIdIn(ids)
                .stream()
                .filter(node -> categoryId.equals(PropertyUtil.getString(node, "category")))
                .collect(Collectors.toList());

    }

    public Optional<Node> getCourseByPath(String path) {
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

    public Optional<Node> findByName(String name) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("name", List.of(name)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }
}
