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
import java.util.Optional;

@Slf4j
public class NotificationRepository extends NodeRepository {
    public static final String NOTIFICATION_WORKSPACE = "notification";
    private static final String NOTIFICATION_NODE_TYPE = "mgnl:notification";

    public static final String PROPERTY_TITLE = "title";
    public static final String PROPERTY_PARTICIPANT_LINK = "participantLink";
    public static final String PROPERTY_TRAINER_LINK = "trainerLink";
    public static final String PROPERTY_SUPERVISOR_LINK = "supervisorLink";
    public static final String PROPERTY_PARTICIPANTS = "participants";
    public static final String PROPERTY_SUPERVISORS = "supervisors";
    public static final String PROPERTY_TRAINERS = "trainers";
    public static final String PROPERTY_DESCRIPTION= "description";
    public static final String PROPERTY_NOTIFICATION_DATE= "notificationDate";
    public static final String PROPERTY_RELATED= "relatedId";
    public static final String CATEGORY= "category";
    public static final String CATEGORY_NEWS= "news";
    public static final String CATEGORY_OJT_PROJECT= "OJT Project";
    public static final String CATEGORY_COURSE_RESULT= "Course result";
    public static final String CATEGORY_COURSE= "Course";
    public static final String CATEGORY_SCHEDULE= "Schedule";

    public static final String NEWS_TITLE = "news";
    public static final String OJT_PROJECT_TITLE = "On-the-job-training";

    public NotificationRepository() {this(NOTIFICATION_WORKSPACE, NOTIFICATION_NODE_TYPE);}

    public NotificationRepository(String workspace, String nodeType) {
        super(workspace, nodeType);
    }

    public List<Node> getAllNotificationsByUserId(String userId, String propertyName) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(propertyName, List.of(userId)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> findByCategory(String category) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of(CATEGORY, List.of(category)));
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

