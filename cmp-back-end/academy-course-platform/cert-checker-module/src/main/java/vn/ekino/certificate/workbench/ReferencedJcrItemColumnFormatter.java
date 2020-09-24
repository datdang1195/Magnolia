package vn.ekino.certificate.workbench;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemId;
import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.jcr.RepositoryException;
import java.util.Optional;

/**
 * A custom {@link info.magnolia.ui.workbench.column.definition.ColumnFormatter} to render a property of a referenced item.
 * (For instance an tem in content app1 has a reference to an item in content app app2.<br/>
 * This column formatter is used in content app1 to render a property of the referenced item from content app2.)
 */
@Slf4j
public class ReferencedJcrItemColumnFormatter extends AbstractColumnFormatter<ReferencedJcrItemColumnDefinition> {

    public ReferencedJcrItemColumnFormatter(ReferencedJcrItemColumnDefinition definition) {
        super(definition);
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {

        StringBuilder res = new StringBuilder();
        res.append("<span>");

        if (itemId instanceof JcrItemId) {
            Item item = source.getItem(itemId);

            if (StringUtils.isNotBlank(definition.getReferencedItemWorkspace()) &&
                    StringUtils.isNotBlank(definition.getReferencedItemPropertyName()) &&
                    StringUtils.isNotBlank(definition.getRefItemPropertyName())) {

                Optional.ofNullable(item.getItemProperty(definition.getRefItemPropertyName()))
                        .map(Property::getValue)
                        .map(String::valueOf)
                        .map(refItemUuid -> {
                            try {
                                return NodeUtil.getNodeByIdentifier(definition.getReferencedItemWorkspace(), refItemUuid);
                            } catch (RepositoryException e) {
                                log.error("Failed to fetch referenced node for ws={} and uuid={}", e,
                                        definition.getReferencedItemWorkspace(), refItemUuid);
                                return null;
                            }
                        })
                        .map(referencedNode -> PropertyUtil.getString(
                                referencedNode, definition.getReferencedItemPropertyName()))
                        .ifPresent(res::append);
            }
        } else {
            res.append("Cannot render this column label.");
            log.warn("Cannot render this column label.");
        }
        res.append("</span>");
        return res;
    }
}
