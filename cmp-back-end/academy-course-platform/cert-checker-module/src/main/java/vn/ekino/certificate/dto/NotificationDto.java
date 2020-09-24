package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.ekino.certificate.config.mapper.NodeMapping;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDto extends NodeItem implements Serializable {
    String title;
    String description;
    String link;
    String notificationDate;
    LocalDateTime sessionDate;
    String course;
    List<String> participants = new ArrayList<>();
    List<String> trainers = new ArrayList<>();
    List<String> supervisors = new ArrayList<>();
    String participantLink;
    String trainerLink;
    String supervisorLink;

    @NodeMapping(propertyName = "mgnl:lastModified")
    LocalDateTime lastModified;
}
