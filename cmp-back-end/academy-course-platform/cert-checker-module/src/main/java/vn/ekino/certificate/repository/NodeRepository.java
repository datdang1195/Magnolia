package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import info.magnolia.cms.util.ExclusiveWrite;
import info.magnolia.context.MgnlContext;
import info.magnolia.rest.delivery.jcr.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.ekino.certificate.util.Constants;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public abstract class NodeRepository implements Repository<Node, String> {

    protected String workspace;
    protected String nodeType;

    public NodeRepository(String workspace, String nodeType) {
        this.workspace = workspace;
        this.nodeType = nodeType;
    }

    /**
     * find node by uuid
     * @param id
     * @return
     */
    @Override
    public Optional<Node> findById(String id) {
        return findById(workspace, nodeType, id);
    }

    public List<Node> findByIdIn(List<String> ids) {
        return ids.stream()
                .map(id -> findById(id))
                .filter(node -> node.isPresent())
                .map(node -> node.get())
                .collect(Collectors.toList());
    }

    /**
     * find all node in workspace
     * @return
     */
    @Override
    public List<Node> findAll() {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of());
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find all node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Collections.emptyList();
    }

    @Override
    public void save(Node node) {
        try {
            Session session = MgnlContext.getJCRSession(workspace);

            synchronized (ExclusiveWrite.getInstance()) {
                session.save();
            }
        } catch (RepositoryException e) {
            log.warn("can't save a node because: {}", e.getMessage());
        }
    }

    public void save() {
        try {
            MgnlContext.getJCRSession(workspace).save();
        } catch (RepositoryException e) {
            log.warn("can't save because: {}", e.getMessage());
        }
    }

    /**
     * create a new node and add it root
     * @return
     */
    public Optional<Node> createNode(String path) {
        try {
            Session session = MgnlContext.getJCRSession(workspace);
            return Optional.of(session.getRootNode().addNode(path, nodeType));
        } catch (RepositoryException e) {
            log.warn("can't get root node because: {}", e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Node> getOrAddNode(String path) {
        try {
            Session session = MgnlContext.getJCRSession(workspace);
            Node rootNode = session.getRootNode();
            if (!rootNode.hasNode(path)) {
                return Optional.of(rootNode.addNode(path, nodeType));
            }
            return Optional.of(rootNode.getNode(path));
        } catch (RepositoryException e) {
            log.warn("can't get node because: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public void deleteNodes(List<Node> nodeList) {
        nodeList.forEach(this::deleteNode);
    }

    public void deleteNode(Node node) {
        try {
            node.remove();
            save(node);
        } catch (RepositoryException e) {
            log.warn("can't delete node from workspace because {}", e.getMessage());
        }
    }

    public Optional<Node> findByMultiValue(Map<String, List<String>> filtersCondition) {
        return findByMultiValue(this.workspace, this.nodeType, filtersCondition);
    }

    public Optional<Node> findByMultiValue(String workspace, String nodeType, Map<String, List<String>> filtersCondition) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), filtersCondition);
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Node> findById(String workspace, String nodeType, String id) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("@jcr:uuid", List.of(id)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Node> findByPath(String path) {
        try {
            Session session = MgnlContext.getJCRSession(workspace);
            Node rootNode = session.getRootNode();
            if (rootNode.hasNode(path)) {
                return  Optional.of(rootNode.getNode(path));
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    protected Query buildQuery(final String workspace,
                               final List<String> nodeTypes,
                               final Map<String, List<String>> filtersCondition) throws RepositoryException {
        return buildQuery(workspace, StringUtils.EMPTY, nodeTypes, filtersCondition, Collections.emptyList(), 0L, -1L);
    }

    protected Query buildQuery(final String workspace,
                               final String rootPath,
                               final List<String> nodeTypes,
                               final Map<String, List<String>> filtersCondition,
                               final List<String> propertiesToOrderBy,
                               long offset,
                               long limit) throws RepositoryException {
        final Session session = MgnlContext.getJCRSession(workspace);
        Constants.totalQuery ++;

        return QueryBuilder.inWorkspace(session.getWorkspace())
                .rootPath(rootPath)
                .nodeTypes(nodeTypes)
                .conditions(filtersCondition)
                .orderBy(propertiesToOrderBy)
                .offset(offset)
                .limit(limit)
                .build();
    }

}
