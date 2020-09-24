package vn.ekino.certificate.model.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OjtProject implements Serializable {
    String id;
    String projectName;
    String projectLead;
    String projectStatus;
    int participants;
    String startDate;
    String endDate;
    String uriName;
}
