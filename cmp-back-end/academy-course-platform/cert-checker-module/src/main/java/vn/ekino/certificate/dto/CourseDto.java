package vn.ekino.certificate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CourseDto extends NodeItem implements Serializable {

    @JsonIgnore
    String description;
    @JsonIgnore
    String outline;

    @NodeMapping(reference = CourseRepository.class)
    List<CourseDto> prerequisites = new ArrayList<>();

    @NodeMapping(reference = CategoryRepository.class)
    CategoryDto category;

    @NodeMapping(reference = CategoryRepository.class)
    CategoryDto group;

    @NodeMapping
    String homeworkTitle;

    @NodeMapping
    @JsonIgnore
    String homeworkDescription;

    @NodeMapping
    List<HomeworkDto> homeworkLinks = new ArrayList<>();

    @NodeMapping
    String materialTitle;

    @NodeMapping
    Boolean online;

    @NodeMapping
    @JsonIgnore
    String materialDescription;

    @NodeMapping
    List<MaterialDto> materialLinks = new ArrayList<>();

}
