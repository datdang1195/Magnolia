package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimplePhaseDto extends NodeItem implements Serializable {
    String id;
    String title;
    String definition;
    List<SimpleProgramDto> programs;
}
