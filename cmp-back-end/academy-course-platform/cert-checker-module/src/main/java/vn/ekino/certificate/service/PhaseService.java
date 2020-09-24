package vn.ekino.certificate.service;

import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.dto.PhaseDto;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class PhaseService {

    private final PhaseRepository phaseRepository;

    @Inject
    public PhaseService(PhaseRepository phaseRepository) {
        this.phaseRepository = phaseRepository;
    }

    public List<PhaseDto> getPhasesBySelectedYear(int selectedYear) {

        List<Node> phasesNode = phaseRepository.getPhases();

        return Optional.of(
                phasesNode
                        .stream()
                        .map(phaseNode -> MapperUtils.nodeToObject(phaseNode, PhaseDto.class).get())
                        .filter(phaseDto -> phaseDto.getStartDate().getYear() == selectedYear)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

    }

    public PhaseDto getPhaseById(String phaseId) {
        return phaseRepository.findById(phaseId)
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, PhaseDto.class).get())
                .findFirst().get();
    }

    public List<PhaseDto> getAll() {
        return phaseRepository.findAll()
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, PhaseDto.class).get())
                .collect(Collectors.toList());
    }
}
