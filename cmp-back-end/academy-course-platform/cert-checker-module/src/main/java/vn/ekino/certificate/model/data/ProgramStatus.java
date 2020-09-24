package vn.ekino.certificate.model.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ProgramStatus implements Serializable {
    int year;
    List<Phase> phases;
}
