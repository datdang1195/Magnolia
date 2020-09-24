package vn.ekino.certificate.dto.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum  ParticipantStatus implements Serializable {
    ON_GOING("On-going"),
    FINISHED("Finished"),
    CANCELLED("Cancelled"),
    PENDING("Pending");

    String status;
}
