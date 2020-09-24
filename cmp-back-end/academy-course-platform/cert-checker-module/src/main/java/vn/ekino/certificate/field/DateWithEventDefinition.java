package vn.ekino.certificate.field;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Field;
import info.magnolia.ui.form.field.definition.DateFieldDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.function.Consumer;

@Setter
@Getter
public class DateWithEventDefinition extends DateFieldDefinition {

    private Consumer<Object> valueChanged;
    private Field<Date> field;
    private String comboboxName;

    public void changeValue(Property.ValueChangeEvent event) {
        if (valueChanged != null) {
            valueChanged.accept(event.getProperty().getValue());
        }
    }
}
