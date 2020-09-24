package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AttitudeDto extends NodeItem implements Serializable {

    String programId;
    String programName;
    String userId;
    String userName;
    String userProgramId;
    String assessmentGroupId;
    String assessmentTypeId;
    String assessmentCriteriaId;
    String assessmentCriteriaName;
    double score;
    String comment;

}
