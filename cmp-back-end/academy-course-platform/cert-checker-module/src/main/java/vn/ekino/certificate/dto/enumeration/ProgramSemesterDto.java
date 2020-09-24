package vn.ekino.certificate.dto.enumeration;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgramSemesterDto implements Serializable {
    String programId;
    List<String> semesterDates;
}
