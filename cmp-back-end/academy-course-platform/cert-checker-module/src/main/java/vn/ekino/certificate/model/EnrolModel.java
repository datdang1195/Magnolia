package vn.ekino.certificate.model;

import com.google.gson.Gson;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.PhaseDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EnrolModel extends BaseModel {

    private final PhaseRepository phaseRepository;
    private final ProgramRepository programRepository;

    @Inject
    public EnrolModel(Node content, ConfiguredTemplateDefinition definition, RenderingModel<?> parent, CertificateServicesModule servicesModule, PhaseRepository phaseRepository, ProgramRepository programRepository) {
        super(content, definition, parent, servicesModule);
        this.phaseRepository = phaseRepository;
        this.programRepository = programRepository;
    }

    public List<String>getYears() {
        List<String> list = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        list.add(String.valueOf(currentYear));
        for (int i = 1; i < 11; i++) {
            list.add(String.valueOf(currentYear + i));
        }
        return list;
    }

    public String getAllPhase() {
        return new Gson().toJson(phaseRepository.findAll()
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, PhaseDto.class).get())
                .collect(Collectors.toList()));
    }

    public String getAllProgram() {
        String result = new Gson().toJson(programRepository.findAll()
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, ProgramDto.class).get())
                .collect(Collectors.toList()));
        return result;
    }
}