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

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssessmentCriteriaDto extends NodeItem implements Serializable {

    @NodeMapping(reference = ProgramRepository.class)
    ProgramDto program;

    @NodeMapping(reference = CategoryRepository.class)
    CategoryDto assessmentType;

    @NodeMapping(reference = CategoryRepository.class)
    CategoryDto assessmentGroup;
}
