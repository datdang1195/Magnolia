package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.CourseRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceDto extends NodeItem implements Serializable {
    LocalDateTime date;
    @NodeMapping(reference = CourseRepository.class)
    CourseDto course;

    List<String> users = new ArrayList<>();

//    @NodeMapping(reference = UserRepository.class)
//    List<UserDto> fullUser = new ArrayList<>();
}
