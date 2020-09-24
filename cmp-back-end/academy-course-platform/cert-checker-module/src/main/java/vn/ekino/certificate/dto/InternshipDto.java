package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.InternProgramRepository;
import vn.ekino.certificate.repository.UserRepository;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InternshipDto extends NodeItem implements Serializable {
    @NodeMapping(reference = UserRepository.class)
    UserDto user;
    @NodeMapping(reference = InternProgramRepository.class)
    InternProgramDto internProgram;
    @NodeMapping(propertyName = JcrConstants.JCR_CREATED)
    LocalDateTime createdDate;

    @NodeMapping(propertyName = "startDate")
    LocalDateTime startDate;

    @NodeMapping(propertyName = "endDate")
    LocalDateTime endDate;
}
