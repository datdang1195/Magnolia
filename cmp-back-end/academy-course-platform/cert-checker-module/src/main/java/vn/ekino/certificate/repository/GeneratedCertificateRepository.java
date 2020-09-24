package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GeneratedCertificateRepository extends NodeRepository {

    public static final String WORKSPACE = "generatedCertificate";
    public static final String NODE_TYPE = "mgnl:generatedCertificate";
    public static final String PROPERTY_GENERATED_FILES = "generatedFiles";
    public static final String PROPERTY_CODE = "generatedFiles";
    public static final String PROPERTY_PATH = "path";
    public static final String PROPERTY_FULL_PAGE_PATH = "fullPagePath";

    public GeneratedCertificateRepository() {
        this(WORKSPACE, NODE_TYPE);
    }

    public GeneratedCertificateRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    /**
     * find node by code
     *
     * @param code
     * @return
     */
    public List<Node> findAllNodeByCode(String code) {
        try {
            Session session = MgnlContext.getJCRSession(WORKSPACE);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query query = qm.createQuery("SELECT * FROM [nt:base] AS t "
                    + "WHERE ([jcr:primaryType] = 'mgnl:generatedCertificate') "
                    + "AND ([code] = '" + code + "')", Query.JCR_SQL2);

            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> findByEnrolProgram(String id) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("enrolProgram", List.of(id)));
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
