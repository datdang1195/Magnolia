package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.UserRepository;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionDto extends NodeItem implements Serializable {

    String duration;
    String title;
    @NodeMapping(reference = UserRepository.class)
    UserDto supervisor;
    @NodeMapping(reference = UserRepository.class)
    UserDto trainer;
    LocalDateTime date;
    String type;
}
