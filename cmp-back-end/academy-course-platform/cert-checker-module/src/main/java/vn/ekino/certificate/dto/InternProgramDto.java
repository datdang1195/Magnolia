package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.DamRepository;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.util.TimeUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InternProgramDto extends NodeItem implements Serializable {

    @NodeMapping(reference = ProgramRepository.class, propertyName = "descriptionList")
    List<DescriptionDto> descriptionList = new ArrayList<>();

    @NodeMapping(reference = DamRepository.class)
    AssetDto certificateTemplateImage;

    @NodeMapping(propertyName = "startDate")
    LocalDateTime startDate;

    @NodeMapping(propertyName = "endDate")
    LocalDateTime endDate;

    public String getInternProgramStartDate() {

        return startDate.format(TimeUtils.DATE_TIME_PATTERN_OF_PROGRAM_STATUS);
    }

    public String getInternProgramEndDate() {
        return endDate.format(TimeUtils.DATE_TIME_PATTERN_OF_PROGRAM_STATUS);
    }
}
