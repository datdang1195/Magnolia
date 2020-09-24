package vn.ekino.certificate.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrolDto extends NodeItem implements Serializable {
    @NotNull
    String email;
    @NotNull
    String username;
    @NotNull
    String password;
    @NotNull
    String program;
    @NotNull
    String programId;
}
