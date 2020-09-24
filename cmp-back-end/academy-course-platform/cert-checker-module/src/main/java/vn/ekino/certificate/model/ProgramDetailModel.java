package vn.ekino.certificate.model;

import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.service.EnrolProgramService;
import vn.ekino.certificate.service.ProgramService;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.time.LocalDate;
import java.util.Optional;

public class ProgramDetailModel<RD extends ConfiguredTemplateDefinition>
        extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    private final ProgramService programService;
    private final EnrolProgramService enrolProgramService;
    private final Provider<WebContext> webContextProvider;
    private final NodeNameHelper nodeNameHelper;

    @Inject
    public ProgramDetailModel(Node content,
                              ConfiguredTemplateDefinition definition,
                              RenderingModel<?> parent,
                              ProgramService programService,
                              EnrolProgramService enrolProgramService,
                              Provider<WebContext> webContextProvider,
                              NodeNameHelper nodeNameHelper) {
        super(content, definition, parent);
        this.programService = programService;
        this.enrolProgramService = enrolProgramService;
        this.webContextProvider = webContextProvider;
        this.nodeNameHelper = nodeNameHelper;
    }

    public User getCurrentUser() {
        return MgnlContext.getUser();
    }

    public String getModifiedDateOfDetailProgram() {
        return Optional.ofNullable(getDetailProgram()).map(p -> {
            LocalDate modifiedStartDate = p.getStartDate().toLocalDate();
            LocalDate modifiedEndDate = p.getEndDate().toLocalDate();

            return modifiedStartDate.format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL) + " - " +
                    modifiedEndDate.format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
        }).orElse("");
    }

    public ProgramDto getDetailProgram() {
        if (webContextProvider.get().getParameter("selectedProgram") != null) {
            return programService.getProgramById(webContextProvider.get().getParameter("selectedProgram")).orElse(null);
        }
        return null;
    }

    public boolean haveEnrolButton() {
        return checkCurrentDateIsValidForEnrol() &&
                (isAnonymous() || !(checkUserHaveEnrolledProgram(getCurrentUser().getIdentifier())));
    }

    public String getEnrolButtonUrl() {
        String enrolLink = PropertyUtil.getString(content, "enrolPage", "/enrol");
        return (getDetailProgram() != null) ?
                enrolLink.concat("/").concat(nodeNameHelper.getValidatedName(getDetailProgram().getNodeName()))
                :
                enrolLink;
    }

    private boolean checkCurrentDateIsValidForEnrol() {
        return Optional.ofNullable(getDetailProgram()).map(detailProgram -> {
            LocalDate currentDate = LocalDate.now();
            LocalDate startEnrolProgramDate = detailProgram.getStartDate().toLocalDate();
            LocalDate endEnrolProgramDate = detailProgram.getEndDate().toLocalDate();
            return (currentDate.isAfter(startEnrolProgramDate) || currentDate.isEqual(startEnrolProgramDate))
                    && (currentDate.isBefore(endEnrolProgramDate) || currentDate.isEqual(endEnrolProgramDate));
        }).orElse(false);
    }

    private boolean isAnonymous() {
        return getCurrentUser().getName().equals("anonymous");
    }

    private boolean checkUserHaveEnrolledProgram(String userId) {
        return enrolProgramService.getAllEnrolProgramByUserId(userId)
                .stream()
                .map(enrolProgramDto -> enrolProgramDto.getProgram())
                .anyMatch(program -> program.getUuid()
                        .equals(webContextProvider.get().getParameter("selectedProgram")));
    }
}

