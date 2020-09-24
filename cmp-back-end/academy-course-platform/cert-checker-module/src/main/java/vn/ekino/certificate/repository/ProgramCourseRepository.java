package vn.ekino.certificate.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.NodeUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
public class ProgramCourseRepository extends NodeRepository {
    private static final String PROGRAM_COURSE_WORKSPACE = "programCourses";
    private static final String PROGRAM_COURSE_NODE_TYPE = "mgnl:programCourse";
    private final CourseRepository courseRepository;
    private static final String JCR_UUID = JcrConstants.JCR_UUID;


    @Inject
    public ProgramCourseRepository(CourseRepository courseRepository) {
        this(PROGRAM_COURSE_WORKSPACE, PROGRAM_COURSE_NODE_TYPE, courseRepository);
    }

    public ProgramCourseRepository(String workspace, String nodeType, CourseRepository courseRepository) {
        super(workspace, nodeType);
        this.courseRepository = courseRepository;
    }

    /**
     * find all node by name
     *
     * @param name
     * @return
     */
    public List<Node> findAllNodeByName(String name) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("name", List.of(name)));
            NodeIterator iterator = query.execute().getNodes();
            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> findByProgramId(String programId) {
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("program", List.of(programId)));
            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public List<Node> findCoursesNodeByProgramCoursePath(String programCoursePath) {
        return findCoursesNodeByProgramCoursePath(programCoursePath, 0, -1);
    }

    public List<Node> findCoursesNodeByProgramCoursePath(String programCoursePath, int offset, int limit) {
        try {
            Session session = MgnlContext.getJCRSession(PROGRAM_COURSE_WORKSPACE);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query query = qm.createQuery("SELECT * FROM [mgnl:contentNode] AS NODE "
                            + "WHERE ISDESCENDANTNODE (NODE, '" + programCoursePath + "/courseList')",
                    Query.JCR_SQL2);

            if (limit != -1) {
                query.setOffset(offset);
                query.setLimit(limit);
            }

            NodeIterator iterator = query.execute().getNodes();

            return Lists.newArrayList(iterator);
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Node> findCourseCompulsoryNodeById(String uuid) {
        try {
            Session session = MgnlContext.getJCRSession(PROGRAM_COURSE_WORKSPACE);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query query = qm.createQuery("SELECT * FROM [mgnl:contentNode] AS NODE "
                            + "WHERE [jcr:uuid] = '" + uuid + "'",
                    Query.JCR_SQL2);

            NodeIterator iterator = query.execute().getNodes();
            if (iterator.hasNext()) {
                return Optional.of(iterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Node> findCourseCompulsoryNodeByCourseName(String courseNameId, String parentId) {
        try {
            Query query = buildQuery(workspace, List.of(Constants.CONTENT_NODE), Map.of("courseName", List.of(courseNameId)));
            NodeIterator iterator = query.execute().getNodes();
            while (iterator.hasNext()) {
                Node node = iterator.nextNode();
                if (StringUtils.EMPTY.equals(parentId) || parentId.equals(node.getParent().getIdentifier())) {
                    return Optional.of(node);
                }
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public List<String> findAllCourseIdBySessionDate(Date selectedDate) {
        List<String> courseIds = Lists.newArrayList();
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of());
            NodeIterator programs = query.execute().getNodes();
            while (programs.hasNext()) {
                Node program = programs.nextNode();
                List<Node> courses = NodeUtils.getSubNodes(program);
                for (Node course : courses) {
                    List<Node> sessions = NodeUtils.getSubNodes(course);
                    for (Node session : sessions) {
                        Calendar calendar = PropertyUtil.getDate(session, "date");
                        if (calendar != null) {
                            Date date = calendar.getTime();
                            if (DateUtils.isSameDay(selectedDate, date)) {
                                courseIds.add(PropertyUtil.getString(course, "courseName"));
                            }
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return courseIds;
    }

    public Map<String, String> findAllCourseAndTypeBySessionDate(Date selectedDate) {
        Map<String, String> map = Maps.newHashMap();
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of());
            NodeIterator programs = query.execute().getNodes();
            while (programs.hasNext()) {
                Node program = programs.nextNode();
                var programName = PropertyUtil.getString(program, "name", StringUtils.EMPTY);
                if (!programName.isEmpty())
                    programName = (programName.split("-"))[0].trim();

                List<Node> courses = NodeUtils.getSubNodes(program);
                for (Node course : courses) {
                    List<Node> sessions = NodeUtils.getSubNodes(course);
                    for (Node session : sessions) {
                        Calendar calendar = PropertyUtil.getDate(session, "date");
                        if (calendar != null) {
                            Date date = calendar.getTime();
                            if (DateUtils.isSameDay(selectedDate, date)) {
                                List<String> lst = Lists.newArrayList();
                                var courseName = PropertyUtil.getString(course, "courseName");
                                Optional<Node> courseNode = courseRepository.findById(courseName);
                                if (courseNode.isPresent()) {
                                    var type = PropertyUtil.getString(session, "type", StringUtils.EMPTY);
                                    String id = PropertyUtil.getString(courseNode.get(), JCR_UUID);
                                    String name = PropertyUtil.getString(courseNode.get(), "name");
                                    map.put(id, programName + " - " + name + " - " + type);
                                }
                            }
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return map;
    }

    public List<String> findProgramIdBySessionDate(Date selectedDate) {
        List<String> programIds = Lists.newArrayList();
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of());
            NodeIterator programs = query.execute().getNodes();
            while (programs.hasNext()) {
                Node program = programs.nextNode();
                List<Node> courses = NodeUtils.getSubNodes(program);
                for (Node course : courses) {
                    List<Node> sessions = NodeUtils.getSubNodes(course);
                    for (Node session : sessions) {
                        Date date = PropertyUtil.getDate(session, "date").getTime();
                        if (DateUtils.isSameDay(selectedDate, date)) {
                            programIds.add(PropertyUtil.getString(program, "program"));
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }
        return programIds;
    }

    public Map<String, String> findAllSessionByCourse(Date selectedDate, String courseUUID) {
        Map<String, String> programCourses = Maps.newHashMap();
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of());
            NodeIterator programs = query.execute().getNodes();
            while (programs.hasNext()) {
                Node program = programs.nextNode();
                List<Node> courses = NodeUtils.getSubNodes(program);
                for (Node course : courses) {
                    List<Node> sessions = NodeUtils.getSubNodes(course);
                    for (Node session : sessions) {
                        Date date = PropertyUtil.getDate(session, "date").getTime();
                        String courseName = PropertyUtil.getString(course, "courseName");
                        if (selectedDate.compareTo(date) == 0 && courseName.equals(courseUUID)) {
                            programCourses.put(
                                    session.getIdentifier(),
                                    PropertyUtil.getString(session, "title"));
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.warn("can't find a node {} in workspace {} because: {}", nodeType, workspace, e.getMessage());
        }

        return programCourses;
    }

    public Optional<Node> findByCourseAndDate(String courseId, Date date) {
        try {
            Query query = buildQuery(workspace, List.of(Constants.CONTENT_NODE), Map.of("courseName", List.of(courseId)));
            NodeIterator iterator = query.execute().getNodes();
            while (iterator.hasNext()) {
                var course = iterator.nextNode();
                List<Node> sessions = NodeUtils.getSubNodes(course);
                for (Node session : sessions) {
                    Date sessionDate = PropertyUtil.getDate(session, "date").getTime();
                    String type = PropertyUtil.getString(session, "type");
                    if (DateUtils.isSameDay(sessionDate, date) && "in-class".equals(type)) {
                        return Optional.of(course.getParent());
                    }
                }
            }
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", Constants.CONTENT_NODE, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Node> findByCourseId(String courseId) {
        try {
            Query query = buildQuery(workspace, List.of(Constants.CONTENT_NODE), Map.of("courseName", List.of(courseId)));
            NodeIterator iterator = query.execute().getNodes();
            while (iterator.hasNext()) {
                var course = iterator.nextNode();
                return Optional.of(course.getParent());
            }
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", Constants.CONTENT_NODE, workspace, e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<String> findNumberQuizzesByCourseId(String courseId, String programId) {
        String numberOfQuizzes = null;
        try {
            Query query = buildQuery(workspace, List.of(nodeType), Map.of("program", List.of(programId)));
            NodeIterator iterator = query.execute().getNodes();
            while (iterator.hasNext()) {
                var program = iterator.nextNode();
                List<Node> courses = NodeUtils.getSubNodes(program);

                Optional<Node> course = courses.stream().filter(tmp -> PropertyUtil.getString(tmp, "courseName").equals(courseId)).findFirst();
                if (course.isPresent()) {
                    numberOfQuizzes = PropertyUtil.getString(course.get(), "numberOfQuizzes");
                }
            }
        } catch (PathNotFoundException ex) {
            log.warn("PathNotFoundException in workspace {} because: {}", workspace, ex.getMessage());
        } catch (RepositoryException e) {
            log.warn("can't find nodes {} in workspace {} because: {}", Constants.CONTENT_NODE, workspace, e.getMessage());
        }
        return Optional.ofNullable(numberOfQuizzes);
    }
}
