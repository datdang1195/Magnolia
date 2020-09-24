package vn.ekino.certificate.workbench;

import info.magnolia.ui.workbench.column.definition.AbstractColumnDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * The definition class for {@link ReferencedJcrItemColumnFormatter}
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReferencedJcrItemColumnDefinition extends AbstractColumnDefinition {

    /**
     * The name of the workspace of the referenced item.
     */
    String referencedItemWorkspace;
    /**
     * The name of the property of the referenced item.
     */
    String referencedItemPropertyName;
    /**
     * The name of the property which holds the reference to the referenced item.
     */
    String refItemPropertyName;
}
