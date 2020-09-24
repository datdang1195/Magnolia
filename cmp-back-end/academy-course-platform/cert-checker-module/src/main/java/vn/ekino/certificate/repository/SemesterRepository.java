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
public class SemesterRepository extends NodeRepository {
    public static final String WORKSPACE = "semester";
    private static final String NODE_TYPE = "mgnl:semester";

    public SemesterRepository() {this(WORKSPACE, NODE_TYPE);}

    public SemesterRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> findByProgram(String programId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("program", List.of(programId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", WORKSPACE, NODE_TYPE, e.getMessage());
        }
        return Collections.emptyList();
    }



}
