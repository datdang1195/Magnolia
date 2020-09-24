package vn.ekino.certificate.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.AvailableYearDto;
import vn.ekino.certificate.dto.CategoryDto;
import vn.ekino.certificate.dto.PhaseDto;
import vn.ekino.certificate.dto.SimplePhaseDto;
import vn.ekino.certificate.dto.SimpleProgramDto;
import vn.ekino.certificate.service.CategoryService;
import vn.ekino.certificate.service.PhaseService;
import vn.ekino.certificate.service.ProgramService;
import vn.ekino.certificate.util.Constants;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgramListModel<RD extends ConfiguredTemplateDefinition> extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    private final ProgramService programService;
    private final PhaseService phaseService;
    private final CategoryService categoryService;
    private final CertificateServicesModule certificateServicesModule;
    private final NodeNameHelper nodeNameHelper;

    private String PROGRAM_DETAIL_PAGE_URL = StringUtils.EMPTY;

    @Inject
    public ProgramListModel(Node content,
                            ConfiguredTemplateDefinition definition,
                            RenderingModel<?> parent,
                            ProgramService programService,
                            PhaseService phaseService,
                            CategoryService categoryService,
                            CertificateServicesModule certificateServicesModule,
                            NodeNameHelper nodeNameHelper) {
        super(content, definition, parent);
        this.programService = programService;
        this.phaseService = phaseService;
        this.categoryService = categoryService;
        this.certificateServicesModule = certificateServicesModule;
        this.nodeNameHelper = nodeNameHelper;
    }

    /**
     *
     * @return the list of available phases,likes FOUNDATION, PROFESSION, EXPERT, MASTER
     */
    public List<CategoryDto> getAvailablePhases() {
        return categoryService.findAllCourseCategories();
    }

    /**
     *
     * @return the info of defined phases (such as the id, title, programs list of it...) that group by available years
     * @throws JsonProcessingException
     */
    public String getListPhasesInAllYears() throws JsonProcessingException {
        Set<Integer> availableYrs = getAvailableYears();
        return Constants.OBJECT_MAPPER.writeValueAsString(
                availableYrs.stream().map(this::mapToAvailableYearDto).collect(Collectors.toList()));
    }

    /**
     * Use to get the list of defined phases by the available year,
     * and convert to the AvailableYearDto
     * @param year
     * @return the mapped list of defined phases in the available year
     */
    private AvailableYearDto mapToAvailableYearDto(Integer year) {

        List<PhaseDto> phaseDtoList = phaseService.getPhasesBySelectedYear(year);
        phaseDtoList.stream().forEach(phaseDto ->
                phaseDto.setPrograms(programService.getProgramsBySelectedPhase(phaseDto.getUuid())));

        List<SimplePhaseDto> simplePhaseDtoList = new ArrayList<>();
        for (PhaseDto phaseDto : phaseDtoList) {
            SimplePhaseDto simplePhaseDto = SimplePhaseDto.builder()
                    .id(phaseDto.getUuid())
                    .title(phaseDto.getPhase().getDisplayName())
                    .definition(phaseDto.getDescription())
                    .build();

            List<SimpleProgramDto> simpleProgramDtoList = new ArrayList<>();
            phaseDto.getPrograms().stream().forEach(
                    programDto -> {
                        SimpleProgramDto simpleProgramDto = SimpleProgramDto.builder()
                                .id(programDto.getUuid())
                                .title(programDto.getGroup().getDisplayName())
                                .url(certificateServicesModule.getProgramsUrl().concat("/").concat(nodeNameHelper.getValidatedName(programDto.getNodeName())))
                                .build();
                        simpleProgramDtoList.add(simpleProgramDto);
                    }
            );
            simplePhaseDto.setPrograms(simpleProgramDtoList);
            simplePhaseDtoList.add(simplePhaseDto);
        }
        return AvailableYearDto.builder()
                .year(year)
                .phases(simplePhaseDtoList)
                .build();
    }

    /**
     *
     * @return the list of years that defined phases
     */
    public Set<Integer> getAvailableYears() {
        List<PhaseDto> phaseDtoList = phaseService.getAll();

        phaseDtoList.forEach(phaseDto -> {
            phaseDto.setPrograms(programService.getProgramsBySelectedPhase(phaseDto.getUuid()));
        });

        List<PhaseDto> definedPhases = phaseDtoList.stream()
                .filter(phaseDto -> !phaseDto.getPrograms().isEmpty())
                .collect(Collectors.toList());

        Set<Integer> availableYear = new HashSet<>();
        availableYear.addAll(definedPhases.stream()
                .map(phaseDto -> phaseDto.getStartDate().getYear())
                .collect(Collectors.toList()));

        return availableYear;
    }

}
