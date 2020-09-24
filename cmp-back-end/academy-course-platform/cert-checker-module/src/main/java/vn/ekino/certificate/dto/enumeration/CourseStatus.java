package vn.ekino.certificate.dto.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum CourseStatus implements Serializable {
    OPEN("Open"),
    CLASS("Class"),
    QUIZ("Quiz"),
    HOMEWORK("Homework"),
    FINISHED("Finished"),
    NONE("None");

    @Getter
    String displayName;
}
