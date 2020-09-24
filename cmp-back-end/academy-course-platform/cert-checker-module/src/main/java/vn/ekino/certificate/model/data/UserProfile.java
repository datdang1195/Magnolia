package vn.ekino.certificate.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {
    String uuid;
    String name;
    String email;
    String phone;
    String image;
    String headerThumbnail;
    String userProfileThumbnail;
    List<SelectField> department;
    List<SelectField> profile;
    String departmentId;
    String profileId;
    String pwd;
    String code;
}
