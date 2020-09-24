package vn.ekino.certificate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import info.magnolia.context.MgnlContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AssetDto extends NodeItem implements Serializable {
    String assetKey;
    String link;

    @JsonIgnore
    String path;

    @JsonIgnore
    public String getAssetKey() {
        return this.assetKey = String.format("%s:%s", "jcr", getUuid());
    }

    public String getLink() {
        final String contextPath = MgnlContext.getContextPath();
        return this.link = contextPath + path;
    }
}
