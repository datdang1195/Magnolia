package vn.ekino.certificate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsDto  extends NodeItem implements Serializable {

    String author;

    String content;

    LocalDateTime date;

    String image;

    String subtitle;

    Boolean displayImage;

    String title;
}
