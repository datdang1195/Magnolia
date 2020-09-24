package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.DamRepository;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto extends NodeItem implements Serializable {

    @NodeMapping
    String displayName;

    @NodeMapping(reference = DamRepository.class)
    AssetDto icon;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CategoryDto)) {
            return false;
        }
        CategoryDto that = (CategoryDto) obj;
        return Objects.equals(this.getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
