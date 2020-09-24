package vn.ekino.certificate.repository;

import javax.jcr.Node;
import java.util.List;
import java.util.Optional;

public final class NotSupportNodeRepository extends NodeRepository {
    public NotSupportNodeRepository() {
        this("", "");
    }

    public NotSupportNodeRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    @Override
    public Optional<Node> findById(String id) {
        throw new UnsupportedOperationException("findById is not supported");
    }

    @Override
    public List<Node> findAll() {
        throw new UnsupportedOperationException("findAll is not supported");
    }

    @Override
    public void save(Node node) {
        throw new UnsupportedOperationException("save is not supported");
    }
}
