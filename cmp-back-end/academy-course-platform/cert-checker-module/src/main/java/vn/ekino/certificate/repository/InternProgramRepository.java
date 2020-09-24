package vn.ekino.certificate.repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InternProgramRepository extends NodeRepository {
    private static final String INTERN_PROGRAM_WORKSPACE = "interns";
    private static final String INTERN_PROGRAM_NODE_TYPE = "mgnl:intern";

    public InternProgramRepository() {
        this(INTERN_PROGRAM_WORKSPACE, INTERN_PROGRAM_NODE_TYPE);
    }

    public InternProgramRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }
}
