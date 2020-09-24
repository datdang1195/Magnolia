package vn.ekino.certificate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.CourseRepository;
import vn.ekino.certificate.repository.CourseResultRepository;
import vn.ekino.certificate.repository.EnrolProgramRepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResultDto extends NodeItem implements Serializable {

    @JsonIgnore
    @NodeMapping(reference = EnrolProgramRepository.class)
    EnrolProgramDto program;

    @NodeMapping(reference = CourseRepository.class)
    CourseDto course;

    BigDecimal homework;
    BigDecimal score;
    Integer numberOfQuizzes;
    String semester;
    String courseName;

    @NodeMapping(propertyName = "mgnl:lastModified")
    LocalDateTime updatedDate;

    List<QuizDto> quizzes;

    public String getCourseName() {
        return course.getNodeName();
    }

}
