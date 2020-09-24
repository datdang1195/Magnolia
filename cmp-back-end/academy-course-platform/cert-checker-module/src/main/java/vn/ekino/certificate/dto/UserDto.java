package vn.ekino.certificate.dto;

import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.UserManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.CategoryRepository;
import vn.ekino.certificate.repository.DamRepository;
import vn.ekino.certificate.repository.ProgramRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto extends NodeItem implements Serializable {
    @NodeMapping(propertyName = "title")
    String fullName;
    String email;
    Boolean active = false;
    Boolean enabled = false;
    @NodeMapping(reference = ProgramRepository.class)
    ProgramDto program;
    String phone;

    @NodeMapping(reference = CategoryRepository.class)
    CategoryDto profile;

    @NodeMapping(reference = CategoryRepository.class)
    CategoryDto department;

    @NodeMapping(reference = DamRepository.class)
    ImageDto image;

    @NodeMapping(reference = DamRepository.class)
    ImageDto headerThumbnail;

    @NodeMapping(reference = DamRepository.class)
    ImageDto userProfileThumbnail;

    String participantStatus;

    public List<String> getRoles() {
        UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
        User user = userManager.getUser(getNodeName());
        return new ArrayList<>(user.getAllRoles());
    }

}
