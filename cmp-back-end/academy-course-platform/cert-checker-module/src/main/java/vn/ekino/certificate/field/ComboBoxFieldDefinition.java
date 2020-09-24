package vn.ekino.certificate.field;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboBoxFieldDefinition extends BaseSelectFieldDefinition {
    String targetFieldName;
    String targetFieldType;
    String implementationMethod;
    String initialMethod;
    String initialParam;
    String dateFieldName;
    String comboboxFieldName;
}
