package vn.ekino.certificate.dto.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum EnrollProgramStatusEnum implements Serializable {
    ENROLLED("enrolled"), APPROVED("approved"), REFUSED("refused");

    @Getter
    String status;
}
