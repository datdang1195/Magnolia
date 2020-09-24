package vn.ekino.certificate.service;

import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.enumeration.AssessmentType;
import vn.ekino.certificate.dto.enumeration.EnrolStatus;
import vn.ekino.certificate.repository.AssessmentCriteriaRepository;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.OJTProjectRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class EventChangeService {

    private final ProgramCourseRepository programCourseRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    private final OJTProjectRepository ojtProjectRepository;
    private final CategoryRepository categoryRepository;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final CertificateServicesModule servicesModule;
    private final ProgramRepository programRepository;

    @Inject
    public EventChangeService(ProgramCourseRepository programCourseRepository
            , EnrolProgramRepository enrolProgramRepository
            , OJTProjectRepository ojtProjectRepository
            , CategoryRepository categoryRepository
            , AssessmentCriteriaRepository assessmentCriteriaRepository
            , CertificateServicesModule servicesModule
            , ProgramRepository programRepository) {
        this.programCourseRepository = programCourseRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.ojtProjectRepository = ojtProjectRepository;
        this.categoryRepository = categoryRepository;
        this.assessmentCriteriaRepository = assessmentCriteriaRepository;
        this.servicesModule = servicesModule;
        this.programRepository = programRepository;
    }

    public List<SelectFieldOptionDefinition> getUsersByCourse(Map<String, Object> map) {
        String courseId = (String) map.get("comboboxValue");
        Date date = (Date) map.get("dateValue");
        if (StringUtils.isEmpty(courseId)) {
            return Collections.emptyList();
        }
        var node = programCourseRepository.findByCourseAndDate(courseId, date);
        if (node.isPresent()) {
            String programId = PropertyUtil.getString(node.get(), "program", StringUtils.EMPTY);
            if (StringUtils.isNotEmpty(programId)) {
                List<SelectFieldOptionDefinition> list = enrolProgramRepository.findAllUserByProgram(programId)
                        .stream()
                        .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get())
                        .filter(itm -> filterParticipant(itm, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
                        .map(itm -> buildOptionDefinition(itm.getUser().getUuid(), itm.getUser().getFullName()))
                        .collect(Collectors.toList());
                return list;
            }
        }
        return Collections.emptyList();
    }


    public List<SelectFieldOptionDefinition> getEnrolProgramApproved() {
        return enrolProgramRepository.findAllByStatus(EnrolStatus.APPROVED.getStatus())
                .stream()
                .map(itm -> {
                    try {
                        String value = itm.getIdentifier();
                        String label = itm.getName();
                        return buildOptionDefinition(value, label);
                    } catch (RepositoryException e) {
                        log.warn("Can't get or set Properties because {}", e.getMessage());
                    }
                    return new SelectFieldOptionDefinition();
                })
                .collect(Collectors.toList());
    }

    public List<SelectFieldOptionDefinition> getSubCategories(String path) {
        List<SelectFieldOptionDefinition> result = new ArrayList<>();
        var listCategories = categoryRepository.findAllByPath(path);
        if(!listCategories.isEmpty()) {
            listCategories.forEach(node -> {
                try {
                    categoryRepository.getRelatedCategoryById(node.getIdentifier()).forEach(node1 -> {
                        try {
                            var relatedNode = categoryRepository.findById(PropertyUtil.getString(node1, "relatedUUID")).get();
                            String value = relatedNode.getIdentifier();
                            String label = PropertyUtil.getString(relatedNode, "displayName");
                            result.add(buildOptionDefinition(value, label));
                        } catch (RepositoryException e) {
                            log.warn("Can't get or set Properties because {}", e.getMessage());
                        }
                    });
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }

    public List<SelectFieldOptionDefinition> getOJTProjectByUserEnrol(String userEnrolId) {
        if (StringUtils.isEmpty(userEnrolId)) {
            return Collections.emptyList();
        }
        var node = enrolProgramRepository.findById(userEnrolId);
        if (node.isPresent()) {
            String programId = PropertyUtil.getString(node.get(), "program", StringUtils.EMPTY);
            if (StringUtils.isNotEmpty(programId)) {
                List<SelectFieldOptionDefinition> list = ojtProjectRepository.findAllByProgram(programId)
                        .stream()
                        .map(itm -> {
                            String value = PropertyUtil.getString(itm, JcrConstants.JCR_UUID);
                            String label = PropertyUtil.getString(itm, "projectName");
                            return buildOptionDefinition(value, label);
                        })
                        .collect(Collectors.toList());
                return list;
            }
        }
        return Collections.emptyList();
    }

    public List<SelectFieldOptionDefinition> getAssessmentOJT() {
        return getAssessmentFor(AssessmentType.ON_JOB_TRAINING.getType());
    }

    public List<SelectFieldOptionDefinition> getAssessmentAttitude() {
        return getAssessmentFor(AssessmentType.ATTITUDE.getType());
    }

    private List<SelectFieldOptionDefinition> getAssessmentFor(String assessmentType) {
        var nodeType = categoryRepository.findByDisplayName(assessmentType);
        if (nodeType.isPresent()) {
            String typeId = PropertyUtil.getString(nodeType.get(), JcrConstants.JCR_UUID);
            return assessmentCriteriaRepository.findByAssessmentType(typeId)
                    .stream()
                    .map(itm -> {
                        try {
                            String value = itm.getIdentifier();
                            String label = itm.getName();
                            return buildOptionDefinition(value, label);
                        } catch (RepositoryException e) {
                            log.warn("Can't get or set Properties because {}", e.getMessage());
                        }
                        return new SelectFieldOptionDefinition();
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private SelectFieldOptionDefinition buildOptionDefinition(String value, String label) {
        SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
        optionDefinition.setValue(value);
        optionDefinition.setLabel(label);
        return optionDefinition;
    }

    public List<SelectFieldOptionDefinition> getAllAssessmentGroupByType(String assessmentTypeUuid) {
        List<Node> assessmentGroups = categoryRepository.getRelatedCategoryById(assessmentTypeUuid);

        return assessmentGroups.stream().map(relatedCategoryNode -> {
            String value = PropertyUtil.getString(relatedCategoryNode, "relatedUUID", StringUtils.EMPTY);
            String label = categoryRepository.findById(value)
                    .map(node -> PropertyUtil.getString(node, "displayName", StringUtils.EMPTY))
                    .orElse(StringUtils.EMPTY);
            return buildOptionDefinition(value, label);
        }).collect(Collectors.toList());
    }

    public List<SelectFieldOptionDefinition> getAllAssessmentType() {
        return categoryRepository.findAllAssessmentType().stream().map(node ->
                buildOptionDefinition(PropertyUtil.getString(node, JcrConstants.JCR_UUID, StringUtils.EMPTY),
                        PropertyUtil.getString(node, "name", StringUtils.EMPTY))
        ).collect(Collectors.toList());
    }

    private boolean filterParticipant(EnrolProgramDto dto, LocalDate sessionDate) {
        var userDto = dto.getUser();
        if (StringUtils.isEmpty(userDto.getNodeName())) {
            return false;
        }
        return userDto.getRoles().contains(Constants.PARTICIPANT_ROLE)
                && (dto.getCancelDate() == null || dto.getCancelDate().toLocalDate().compareTo(sessionDate) >= 0);
    }

    public boolean checkProgramFinish(String programId) {
        var programNode = programRepository.findById(programId);
        if (programNode.isPresent()) {
            var dto = MapperUtils.nodeToObject(programNode.get(), ProgramDto.class).get();
            return "Completed".equals(dto.getStatus().getDisplayName());
        }
        return false;
    }

    public String findNumberQuizzesByCourseId(String courseId, String programCoursesId) {
        Optional<Node> programs = enrolProgramRepository.findById(programCoursesId);
        Optional<String> numberOfQuizzes = Optional.empty();
        if (programs.isPresent()) {
            String programId = PropertyUtil.getString(programs.get(), "program");
            numberOfQuizzes = programCourseRepository.findNumberQuizzesByCourseId(courseId, programId);
        }
        return numberOfQuizzes.orElseGet(()->"0");
    }
}
