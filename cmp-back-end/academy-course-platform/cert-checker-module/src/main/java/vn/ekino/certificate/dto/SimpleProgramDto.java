package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleProgramDto extends NodeItem implements Serializable {
    String id;
    String title;
    String url;
}
