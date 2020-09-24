package vn.ekino.certificate.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Year implements Serializable {
    int year;
}
