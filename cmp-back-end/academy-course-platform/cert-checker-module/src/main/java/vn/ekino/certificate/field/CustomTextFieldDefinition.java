package vn.ekino.certificate.field;

import com.vaadin.v7.ui.Field;
import info.magnolia.ui.form.field.definition.TextFieldDefinition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomTextFieldDefinition extends TextFieldDefinition {
    private Field<String> field;
}
