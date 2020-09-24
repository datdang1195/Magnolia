package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.UserRepository;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OJTProjectDto extends NodeItem implements Serializable {

    @NodeMapping(reference = ProgramRepository.class)
    ProgramDto program;

    @NodeMapping(reference = UserRepository.class)
    UserDto projectLead;

    @NodeMapping(reference = CategoryRepository.class, propertyName = "status")
    CategoryDto projectStatus;

    String projectName;

    String description;

    LocalDateTime startDate;

    LocalDateTime endDate;

    @NodeMapping(propertyName = "uriName")
    String uriName;
}
