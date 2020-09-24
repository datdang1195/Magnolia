package vn.ekino.certificate.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class NodeUtils {
    public static Optional<Node> getChildNode(Node node, String nodeName) {
        try {
            return Optional.ofNullable(node.getNode(nodeName));
        } catch (RepositoryException e) {
            log.warn("Cannot get child node because: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public static List<Node> getSubNodes(Node node) {
        try {
            return Lists.newArrayList(node.getNodes());
        } catch (RepositoryException e) {
            log.warn("Cannot get sub nodes because {}: ", e.getMessage());
        }
        return Collections.emptyList();
    }

    public static Optional<Node> getParentNode(Node node) {
        try {
            return Optional.ofNullable(node.getParent());
        } catch (RepositoryException e) {
            log.warn("Cannot get parent node because: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public static String getNodePath(Node node){
        try {
            return node.getPath();
        } catch (RepositoryException e) {
            log.warn("Cannot get parent node because: {}", e.getMessage());
        }
        return StringUtils.EMPTY;
    }

    public static Optional<Node> addNode(Node node, String nodeName, String nodeType) {
        try {
            return Optional.of(node.addNode(nodeName, nodeType));
        } catch (RepositoryException e) {
            log.warn("Cannot add node because: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
