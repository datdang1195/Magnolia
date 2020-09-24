package vn.ekino.certificate.model;

import info.magnolia.cms.security.User;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.dto.OJTProjectDto;
import vn.ekino.certificate.dto.OJTUserResultDto;
import vn.ekino.certificate.dto.OnJobTrainingDto;
import vn.ekino.certificate.dto.ParticipantDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.enumeration.EnrollProgramStatusEnum;
import vn.ekino.certificate.service.EnrolProgramService;
import vn.ekino.certificate.service.OJTProjectService;
import vn.ekino.certificate.service.OJTUserResultService;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OnJobTrainingModel extends RenderingModelImpl<ConfiguredTemplateDefinition> {
    private final OJTUserResultService ojtUserResultService;
    private final OJTProjectService ojtProjectService;
    private final EnrolProgramService enrolProgramService;
    private final Provider<WebContext> webContextProvider;

    @Inject
    public OnJobTrainingModel(Node content,
                              ConfiguredTemplateDefinition definition,
                              RenderingModel<?> parent,
                              OJTUserResultService ojtUserResultService,
                              OJTProjectService ojtProjectService,
                              EnrolProgramService enrolProgramService,
                              Provider<WebContext> webContextProvider) {
        super(content, definition, parent);
        this.ojtUserResultService = ojtUserResultService;
        this.ojtProjectService = ojtProjectService;
        this.enrolProgramService = enrolProgramService;
        this.webContextProvider = webContextProvider;
    }

    public User getCurrentUser() {
        return MgnlContext.getUser();
    }



    public OnJobTrainingDto getOnJobTrainingData() {

        Optional<OJTProjectDto> optional = ojtProjectService.findOJTProjectById(getUuidParam());
        if (optional.isPresent()) {
            OJTProjectDto ojtProject = optional.get();
            List<OJTUserResultDto> ojtUserResults = ojtUserResultService.findByOJTProjectID(ojtProject.getUuid());
            return buildOnJobTraining(getCurrentUser(), ojtProject, ojtUserResults, content);
        }
        return null;
    }

    public boolean isOJTParticipant() {
        Optional<OJTProjectDto> optional = ojtProjectService.findOJTProjectById(getUuidParam());
        if (optional.isPresent()) {
            OJTProjectDto ojtProject = optional.get();
            List<OJTUserResultDto> ojtUserResults = ojtUserResultService.findByOJTProjectID(ojtProject.getUuid());
            return ojtUserResults
                    .stream()
                    .anyMatch(
                            ojtUserResultDto -> ojtUserResultDto.getUserEnrolProgram().getUser().getUuid().equals(getCurrentUser().getIdentifier())
                    );
        }
        return false;
    }

    public boolean isAnonymousRole(){
        return Constants.ANONYMOUS_ROLE.equals(MgnlContext.getUser().getName());
    }

    public List<String> getTitle(ProgramDto program) {
        List<String> titles = new ArrayList<>();

        Optional.ofNullable(program).ifPresent(programDto -> {
            Optional.ofNullable(programDto.getPhase())
                    .ifPresent(phaseDto -> titles.add(phaseDto.getNodeName()));

            Optional.ofNullable(programDto.getGroup())
                    .ifPresent(categoryDto -> titles.add(categoryDto.getDisplayName()));
        });
        return titles;
    }

    private OnJobTrainingDto buildOnJobTraining(User user, OJTProjectDto ojtProject, List<OJTUserResultDto> ojtUserResults, Node content) {

        String superviorRole = PropertyUtil.getString(content, "supervisorRole", "academy-supervisor-role");
        String participantRole = PropertyUtil.getString(content, "participantRole", "academy-user-role");

        OnJobTrainingDto onJobTraining = new OnJobTrainingDto();

        onJobTraining.setProgram(ojtProject.getProgram());
        onJobTraining.setProjectName(ojtProject.getProjectName());
        onJobTraining.setProjectStatus(ojtProject.getProjectStatus());
        onJobTraining.setProjectLead(ojtProject.getProjectLead());
        onJobTraining.setStartDate(ojtProject.getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
        onJobTraining.setEndDate(ojtProject.getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
        onJobTraining.setDescription(ojtProject.getDescription());

        List<ParticipantDto> participants = new ArrayList<>();

        ojtUserResults.forEach(ojtUserResult ->
                enrolProgramService
                        .findById(ojtUserResult.getUserEnrolProgram().getUuid())
                        .ifPresent(enrolProgram -> {
                            if (EnrollProgramStatusEnum.APPROVED.getStatus().equals(enrolProgram.getEnrollStatus())) {
                                ParticipantDto participant = new ParticipantDto();

                                participant.setName(enrolProgram.getUser().getFullName());

                                Optional.ofNullable(ojtUserResult.getRole())
                                        .ifPresent(role ->
                                                participant.setRole(role));

                                Optional.ofNullable(ojtUserResult.getMentor())
                                        .ifPresent(menter ->
                                                participant.setMentor(menter.getFullName())
                                        );

                                if (user.getAllRoles().contains(superviorRole)) {
                                    // set information for supervisor role
                                    Optional.ofNullable(ojtUserResult.getOjtEvaluation())
                                            .ifPresent(score -> participant.setScore(score.doubleValue()));
                                    participant.setComment(ojtUserResult.getComment());
                                    participants.add(participant);
                                } else if (user.getAllRoles().contains(participantRole)) {
                                    // set information for participant role
                                    participant.setNote(ojtUserResult.getNote());
                                    participants.add(participant);
                                }
                            }
                        }));

        onJobTraining.setParticipants(participants);
        return onJobTraining;
    }

    private String getUuidParam() {
        return Optional.ofNullable(webContextProvider.get().getParameter("uuid")).orElse("");
    }

}
