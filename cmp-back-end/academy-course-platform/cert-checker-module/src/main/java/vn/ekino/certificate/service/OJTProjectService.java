package vn.ekino.certificate.service;

import vn.ekino.certificate.dto.OJTProjectDto;
import vn.ekino.certificate.repository.OJTProjectRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import java.util.Optional;

public class OJTProjectService {
    private final OJTProjectRepository ojtProjectRepository;

    @Inject
    public OJTProjectService(OJTProjectRepository ojtProjectRepository) {
        this.ojtProjectRepository = ojtProjectRepository;
    }

    public Optional<OJTProjectDto> findOJTProjectById(String uuid) {
        return ojtProjectRepository
                .findById(uuid)
                .flatMap(node
                        -> MapperUtils.nodeToObject(node, OJTProjectDto.class));

    }



}
