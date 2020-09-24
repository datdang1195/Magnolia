package vn.ekino.certificate.dto.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum AssessmentType implements Serializable {
    ATTITUDE("Attitude"),
    ON_JOB_TRAINING("On Job Training");

    @Getter
    String type;
}
