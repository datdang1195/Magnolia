package vn.ekino.certificate.field;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;
import info.magnolia.ui.form.field.definition.OptionGroupFieldDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.function.Consumer;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseSelectFieldDefinition extends OptionGroupFieldDefinition {
    Consumer<Object> valueChanged;
    AbstractSelect comboBox;

    public void changeValue(Property.ValueChangeEvent event) {
        if (valueChanged != null) {
            valueChanged.accept(event.getProperty().getValue());
        }
    }
}
