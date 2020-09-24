package vn.ekino.certificate.service;

import com.google.common.collect.Lists;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.dto.DescriptionDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.repository.DamRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class ProgramService {

    private final DamRepository damRepository;
    private final DamTemplatingFunctions damFunctions;
    private final ProgramRepository programRepository;

    @Inject
    public ProgramService(DamRepository damRepository,
                          DamTemplatingFunctions damFunctions,
                          ProgramRepository programRepository) {
        this.damRepository = damRepository;
        this.damFunctions = damFunctions;
        this.programRepository = programRepository;
    }

    public List<ProgramDto> getProgramsBySelectedPhase(String selectedPhase) {
        List<Node> programNodes = programRepository.findProgramByPhase(selectedPhase);

        return programNodes.stream()
                .map(programNode -> MapperUtils.nodeToObject(programNode, ProgramDto.class).get())
                .collect(Collectors.toList());
    }

    public Optional<ProgramDto> getProgramById(String programId) {
        Optional<Node> nodeOptional = programRepository.findById(programId);
        if (nodeOptional.isPresent()) {
            ProgramDto program = MapperUtils.nodeToObject(nodeOptional.get(), ProgramDto.class).get();
            Node programNode = nodeOptional.get();
            program.setDescriptionList(getDescriptionsByProgramNode(programNode));
            return Optional.of(program);
        }
        return Optional.empty();
    }

    public List<DescriptionDto> getDescriptionsByProgramNode(Node programNode) {
        List<Node> descriptionsNode = new ArrayList<>();
        try {
            descriptionsNode = Lists.newArrayList(programNode.getNode("descriptionList").getNodes());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return descriptionsNode.stream()
                .map(descNode -> MapperUtils.nodeToObject(descNode, DescriptionDto.class).get())
                .collect(Collectors.toList());
    }
}
