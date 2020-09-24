package vn.ekino.certificate.model.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SelectField implements Serializable {
    String value;
    String label;
    boolean selected;
}
