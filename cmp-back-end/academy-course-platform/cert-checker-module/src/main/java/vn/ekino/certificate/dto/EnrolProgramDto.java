package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.UserRepository;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrolProgramDto extends NodeItem implements Serializable {
    @NodeMapping(reference = UserRepository.class)
    UserDto user;
    @NodeMapping(reference = ProgramRepository.class)
    ProgramDto program;
    String enrollStatus;
    @NodeMapping(propertyName = JcrConstants.JCR_CREATED)
    LocalDateTime createdDate;

    LocalDateTime enrollDate;

    @NodeMapping(propertyName = "isTrainer")
    Boolean isTrainer = false;

    @NodeMapping(propertyName = "isParticipant")
    Boolean isParticipant = false;

    String participantStatus;

    String enrollType;

    LocalDateTime cancelDate;

    public LocalDateTime getEnrollDate() {
        if (enrollDate == null) {
            return createdDate;
        }
        return enrollDate;
    }
}
