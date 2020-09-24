package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.OJTProjectRepository;
import vn.ekino.certificate.repository.UserRepository;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OJTUserResultDto extends NodeItem implements Serializable {
    @NodeMapping(reference = EnrolProgramRepository.class)
    EnrolProgramDto userEnrolProgram;

    @NodeMapping(reference = OJTProjectRepository.class)
    OJTProjectDto ojtProject;

    @NodeMapping(reference = CategoryRepository.class)
    CategoryDto role;

    @NodeMapping(reference = UserRepository.class)
    UserDto mentor;

    BigDecimal ojtEvaluation;

    String comment;

    String note;
}
