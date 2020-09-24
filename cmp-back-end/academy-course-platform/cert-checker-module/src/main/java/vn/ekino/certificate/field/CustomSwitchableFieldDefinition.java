package vn.ekino.certificate.field;

import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.Field;
import info.magnolia.ui.form.field.definition.SwitchableFieldDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomSwitchableFieldDefinition extends SwitchableFieldDefinition {
    boolean checkProgramFinish = false;
    Field<PropertysetItem> field;
}
