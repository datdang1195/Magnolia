package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.CourseRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCompulsoryDto extends NodeItem implements Serializable {

    @NodeMapping(reference = CourseRepository.class, propertyName = "courseName")
    CourseDto courseDetail;

    @NodeMapping(propertyName = "isCompulsory")
    Boolean compulsory = false;

    @NodeMapping(reference = CategoryRepository.class, propertyName = "status")
    CategoryDto courseStatus;

    @NodeMapping(reference = CourseRepository.class, propertyName = "sessions")
    List<SessionDto> sessions = new ArrayList<>();

    String readMoreLink;

    String categoryId;
    String semester;
    int hours;

    public String getCategoryId() {
        return courseDetail.getCategory().getUuid();
    }

    public String getCourseId(){
        return courseDetail.getUuid();
    }

}
