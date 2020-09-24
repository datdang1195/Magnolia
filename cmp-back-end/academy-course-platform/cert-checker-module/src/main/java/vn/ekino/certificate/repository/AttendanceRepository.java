package vn.ekino.certificate.repository;


import lombok.extern.slf4j.Slf4j;
import com.google.common.collect.Lists;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class AttendanceRepository extends NodeRepository {
    public static final String ATTENDANCE_WORKSPACE = "attendance";
    private static final String ATTENDANCE_NODE_TYPE = "mgnl:attendance";

    public AttendanceRepository() {this(ATTENDANCE_WORKSPACE, ATTENDANCE_NODE_TYPE);}

    public AttendanceRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public Optional<Node> findUserAttendance(String date, String courseId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("course", List.of(courseId)
                    , "date[in]", List.of(date))
            );
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public List<Node> findUserAttendanceByCourseId(String courseId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("course", List.of(courseId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }
}

