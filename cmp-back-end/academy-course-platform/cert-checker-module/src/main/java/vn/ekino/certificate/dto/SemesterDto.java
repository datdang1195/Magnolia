package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.util.TimeUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterDto extends NodeItem implements Serializable {

    String title;

    @NodeMapping(reference = ProgramRepository.class)
    ProgramDto program;

    @NodeMapping(propertyName = "startDate")
    LocalDateTime startDate;

    @NodeMapping(propertyName = "endDate")
    LocalDateTime endDate;

    Boolean isFullProgram = false;

    public String getSemesterStartDate() {

        return startDate.format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
    }

    public String getSemesterEndDate() {

        return endDate.format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL);
    }

}
