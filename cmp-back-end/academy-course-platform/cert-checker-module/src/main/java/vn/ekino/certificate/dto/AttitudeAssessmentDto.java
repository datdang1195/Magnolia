package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.AssessmentCriteriaRepository;
import vn.ekino.certificate.repository.UserAttitudeResultRepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttitudeAssessmentDto extends NodeItem implements Serializable {

    @NodeMapping(reference = UserAttitudeResultRepository.class)
    UserAttitudeResultDto userAttitudeResult;

    @NodeMapping(reference = AssessmentCriteriaRepository.class)
    AssessmentCriteriaDto assessment;

    @NodeMapping(propertyName = "assessmentScore")
    BigDecimal score;

    @NodeMapping(propertyName = "assessmentComment")
    String comment;

    public String getAssessmentGroupName() {
        return Optional.of(assessment)
                .map(AssessmentCriteriaDto::getAssessmentGroup)
                .map(CategoryDto::getDisplayName)
                .orElse(StringUtils.EMPTY);
    }

}
