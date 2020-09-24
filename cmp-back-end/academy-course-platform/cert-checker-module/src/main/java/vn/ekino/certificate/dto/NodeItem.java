package vn.ekino.certificate.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.jackrabbit.JcrConstants;
import vn.ekino.certificate.config.mapper.NodeMapping;

@Getter
@Setter
public class NodeItem {

    @NodeMapping(propertyName = JcrConstants.JCR_UUID)
    private String uuid;
    @NodeMapping(propertyName = "name")
    private String nodeName;
}
