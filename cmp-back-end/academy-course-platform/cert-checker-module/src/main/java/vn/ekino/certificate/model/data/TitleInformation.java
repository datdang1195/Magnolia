package vn.ekino.certificate.model.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class TitleInformation implements Serializable {
    String phaseName;
    String programName;
    String dateRange;
    String status;
}
