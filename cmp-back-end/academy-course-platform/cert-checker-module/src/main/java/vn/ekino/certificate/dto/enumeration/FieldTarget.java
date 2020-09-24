package vn.ekino.certificate.dto.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.field.ComboBoxFieldDefinition;
import vn.ekino.certificate.field.CustomSwitchableFieldDefinition;
import vn.ekino.certificate.field.CustomTextFieldDefinition;
import vn.ekino.certificate.field.MultiChoiceDefinition;

import java.io.Serializable;
import java.util.Arrays;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum  FieldTarget implements Serializable {
    COMBOBOX("combobox", ComboBoxFieldDefinition.class),
    MULTICHOICE("multiChoice", MultiChoiceDefinition.class),
    SWITCHABLE("switchable", CustomSwitchableFieldDefinition.class),
    TEXTFIELD("textfield", CustomTextFieldDefinition.class);


    @Getter
    String fieldType;

    @Getter
    Class<?> fieldDefinition;

    public static FieldTarget getFieldTargetByType(String fieldType) {
        return Arrays.stream(FieldTarget.values())
                .filter(itm -> itm.getFieldType().equals(fieldType))
                .findFirst()
                .orElse(FieldTarget.COMBOBOX);
    }
}
