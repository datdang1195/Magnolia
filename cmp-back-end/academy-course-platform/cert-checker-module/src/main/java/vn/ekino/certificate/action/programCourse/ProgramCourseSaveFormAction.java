package vn.ekino.certificate.action.programCourse;

import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import org.apache.commons.collections.CollectionUtils;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgramCourseSaveFormAction extends AbstractAction<ProgramCourseSaveFormActionDefinition> {
    private static final String NAME_PROPERTY = "name";
    private static final String JCR_NAME_PROPERTY = "jcrName";
    public static final String EMPTY_STRING = "";

    private final JcrNodeAdapter item;
    private final EditorCallback callback;
    private final EditorValidator validator;
    private final NodeNameHelper nodeNameHelper;
    private final ProgramRepository programRepository;
    private final PhaseRepository phaseRepository;
    private final ProgramCourseRepository programCourseRepository;

    @Inject
    public ProgramCourseSaveFormAction(ProgramCourseSaveFormActionDefinition definition,
                                       JcrNodeAdapter item,
                                       EditorCallback callback,
                                       EditorValidator validator,
                                       NodeNameHelper nodeNameHelper,
                                       ProgramRepository programRepository,
                                       PhaseRepository phaseRepository,
                                       ProgramCourseRepository programCourseRepository) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
        this.nodeNameHelper = nodeNameHelper;
        this.programRepository = programRepository;
        this.phaseRepository = phaseRepository;
        this.programCourseRepository = programCourseRepository;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (validator.isValid()) {
            try {
                final Node node = item.applyChanges();
                node.setProperty(NAME_PROPERTY, generateNodeName(node));
                setNodeName(node, item);
                node.getSession().save();
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
        }
        callback.onSuccess(getDefinition().getName());
    }

    /**
     * Generate node name with pattern [program]-[phase]-[start date]
     * @param node
     * @return node name
     * @throws ActionExecutionException
     */
    private String generateNodeName(Node node) throws ActionExecutionException{
        List<String> names = new ArrayList<>();
        String programId = PropertyUtil.getString(node, "program", EMPTY_STRING);
        programRepository.findById(programId).ifPresent(progeamNode -> {
            names.add(PropertyUtil.getString(progeamNode, "name", EMPTY_STRING));
            String phaseId = PropertyUtil.getString(progeamNode, "phase", EMPTY_STRING);
            phaseRepository.findById(phaseId).ifPresent(phaseNode -> {
                names.add(PropertyUtil.getString(phaseNode, "name", EMPTY_STRING));

                Optional.of(TimeUtils.toLocalDate(
                        PropertyUtil.getDate(phaseNode, "startDate", null)))
                        .ifPresent(startDate -> names.add(TimeUtils.toString(startDate)));
            });
        });
        String generatedName = String.join("-", names);
        return generatedName;
    }

    /**
     * Set the node Name.
     * Node name is set to: <br>
     * the value of the property 'name' if it is present.
     */
    private void setNodeName(Node node, JcrNodeAdapter item) throws RepositoryException {
        String propertyName = "name";
        if (node.hasProperty(propertyName) && !node.hasProperty(JCR_NAME_PROPERTY)) {
            Property property = node.getProperty(propertyName);
            String newNodeName = property.getString();
            if (!node.getName().equals(nodeNameHelper.getValidatedName(newNodeName))) {
                newNodeName = nodeNameHelper.getUniqueName(node.getSession(), node.getParent().getPath(), nodeNameHelper.getValidatedName(newNodeName));
                item.setNodeName(newNodeName);
                NodeUtil.renameNode(node, newNodeName);
            }
        }
    }
}

