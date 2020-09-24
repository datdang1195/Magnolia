package vn.ekino.certificate.repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InternshipRepository extends NodeRepository {
    public static final String INTERNSHIP_WORKSPACE = "internships";
    private static final String INTERNSHIP_NODE_TYPE = "mgnl:internship";

    public InternshipRepository() {
        this(INTERNSHIP_WORKSPACE, INTERNSHIP_NODE_TYPE);
    }

    public InternshipRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }
}
