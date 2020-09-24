package vn.ekino.certificate.model.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OjtTraining implements Serializable {
    String id;
    String projectName;
    String status;
    String role;
    String mentor;
    BigDecimal score;
    String comment;
    String uriName;
}
