package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.DamRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratedCertificateDto extends NodeItem implements Serializable {
    LocalDateTime issueDate;
    @NodeMapping(reference = DamRepository.class)
    List<AssetDto> generatedFiles;
    @NodeMapping(reference = EnrolProgramRepository.class)
    EnrolProgramDto enrolProgram;
    String code;
}
