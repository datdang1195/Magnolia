package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OnJobTrainingDto extends NodeItem implements Serializable {

    ProgramDto program;
    String projectName;
    CategoryDto projectStatus;
    UserDto projectLead;
    String startDate;
    String endDate;
    String description;
    List<ParticipantDto> participants;
}
