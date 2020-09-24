package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.AssessmentCriteriaRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OJTUserAssessmentDto extends NodeItem implements Serializable {
    @NodeMapping(reference = EnrolProgramRepository.class)
    OJTUserResultDto ojtUserResult;

    @NodeMapping(reference = AssessmentCriteriaRepository.class)
    AssessmentCriteriaDto assessment;

    BigDecimal assessmentScore;
    String assessmentComment;
}
