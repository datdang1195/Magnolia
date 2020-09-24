package vn.ekino.certificate.repository;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class NewsRepository extends NodeRepository {
    public static final String NEWS_WORKSPACE = "news";
    private static final String NEWS_NODE_TYPE = "mgnl:news";
    private static final String DATE_DESC = "date desc";

    public NewsRepository() {this(NEWS_WORKSPACE, NEWS_NODE_TYPE);}

    public NewsRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> getLatestNews(int quantityOfNews){
        return getAllNews(0, quantityOfNews);
    }

    public List<Node> getAllNews(int offset, int limit){
        try {
            Query query = buildQuery(
                    workspace, StringUtils.EMPTY, Arrays.asList(nodeType),
                    Map.of(),
                    Arrays.asList(DATE_DESC),
                    offset, limit);
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find news in workspace {} because: {}", workspace, e.getMessage());
        }
        return Collections.emptyList();
    }
}

