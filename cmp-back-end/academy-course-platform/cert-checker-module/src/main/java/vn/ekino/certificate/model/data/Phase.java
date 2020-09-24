package vn.ekino.certificate.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phase implements Serializable {
    String id;
    String name;
    String title;
    String fullName;
    boolean disabled = false;
    int index;
    String definition;
    String startDate;
    String endDate;
    List<Program> programs;
}
