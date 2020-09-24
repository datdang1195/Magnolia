package vn.ekino.certificate.service;

import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.dto.OJTUserResultDto;
import vn.ekino.certificate.repository.OJTUserResultRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class OJTUserResultService {
    private final OJTUserResultRepository ojtUserResultRepository;

    @Inject
    public OJTUserResultService(OJTUserResultRepository ojtUserResultRepository) {
        this.ojtUserResultRepository = ojtUserResultRepository;
    }

    public List<OJTUserResultDto> findByOJTProjectID(String ojtProjectId) {
        return ojtUserResultRepository.findByOJTProjectID(ojtProjectId)
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, OJTUserResultDto.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
