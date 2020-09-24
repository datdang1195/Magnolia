package vn.ekino.certificate.dto.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum CMPMessage implements Serializable {
    ENROL_SUCCESS("CMP_001", "Confirmation!", "Thank you for your enrollment to %s. A confirmation email will be sent to you shortly. Stay tuned!"),
    ENROL_ERROR("CMP_002", "OOPS! Error \uD83D\uDE1E", "Something wrong happened! Please contact Admin to check your enrollment."),
    PROGRAM_OUTSIDE_REGISTRATION("CMP_003", "", "Thank for your interesting in our programs. For the moment, enrollment is closed. Please come back later or contact admin."),
    ENROL_REJECT("CMP_004", "Enrol failed", "You have already enrolled for this program."),
    FORGOT_PASSWORD_SUCCESS("CMP_005", "", "A message has been sent to your email address. Please follow the url in the email to reset your password."),
    FORGOT_PASSWORD_FAIL("CMP_006", "", ""),
    RESET_PASSWORD_SUCCESS("CMP_007", "", "Your password has been reset successfully!"),
    RESET_PASSWORD_FAIL("CMP_008", "", ""),
    CHANGE_PASSWORD_SUCCESS("CMP_009", "", "Your password has been changed successfully!");

    public static final String SEPARATOR = ", ";
    @Getter
    String code;
    @Getter
    String title;
    String message;


    public String getMessage() {
        return getMessage(Collections.emptyList());
    }

    public String getMessage(Collection<String> errorValues) {
        return String.format(message, String.join(SEPARATOR, errorValues));
    }

    public String getMessage(String... args) {
        return String.format(message, (Object[]) args);
    }

    public static CMPMessage getCMPMessageByCode(String code) {
        return Arrays.stream(CMPMessage.values())
                .filter(itm -> code.equals(itm.getCode()))
                .findFirst()
                .orElse(CMPMessage.ENROL_ERROR);
    }
}
