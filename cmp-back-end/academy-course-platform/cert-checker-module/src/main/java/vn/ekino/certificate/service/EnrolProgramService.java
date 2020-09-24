package vn.ekino.certificate.service;

import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.enumeration.EnrollProgramStatusEnum;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class EnrolProgramService {

    private final EnrolProgramRepository enrolProgramRepository;

    @Inject
    public EnrolProgramService(EnrolProgramRepository enrolProgramRepository) {
        this.enrolProgramRepository = enrolProgramRepository;
    }

    public List<EnrolProgramDto> getAllEnrolProgramByUserId(String userId) {
        return enrolProgramRepository
                .getAllNodeByUserId(userId)
                .stream()
                .map(node -> MapperUtils.nodeToObject(node, EnrolProgramDto.class).get())
                .collect(Collectors.toList());
    }

    private List<ProgramDto> getListFilteredEnrolProgramByUserId(String userId, Predicate<EnrolProgramDto> p) {
        return getAllEnrolProgramByUserId(userId)
                .stream()
                .filter(p::test)
                .map(EnrolProgramDto::getProgram)
                .collect(Collectors.toList());
    }

    public List<ProgramDto> getListApprovedEnrolProgramByUserId(String userId) {
        return getListFilteredEnrolProgramByUserId(userId,
                enrolProgramDto -> EnrollProgramStatusEnum.APPROVED.getStatus().equals(enrolProgramDto.getEnrollStatus()));
    }

    public List<ProgramDto> getListEnrolProgramsOfParticipantByUserId(String userId) {
        return getListFilteredEnrolProgramByUserId(userId,
                enrolProgramDto -> enrolProgramDto.getIsParticipant());
    }

    public Optional<EnrolProgramDto> findById(String uuid) {
        return enrolProgramRepository
                .findById(uuid)
                .flatMap(node
                        -> MapperUtils.nodeToObject(node, EnrolProgramDto.class));
    }
}
