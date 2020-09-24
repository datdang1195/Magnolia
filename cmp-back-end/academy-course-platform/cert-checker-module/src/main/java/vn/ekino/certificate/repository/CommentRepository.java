package vn.ekino.certificate.repository;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommentRepository extends NodeRepository {
    public static final String WORKSPACE = "comment";
    public static final String NODE_TYPE = "mgnl:comment";

    public CommentRepository() {this(WORKSPACE, NODE_TYPE);}

    public CommentRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> findByCourse(String courseId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("courseId", List.of(courseId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", NODE_TYPE, WORKSPACE, e.getMessage());
        }
        return Collections.emptyList();
    }
}

