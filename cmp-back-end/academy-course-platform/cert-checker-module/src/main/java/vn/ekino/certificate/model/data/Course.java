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
public class Course implements Serializable {
    String courseId;
    String courseName;
    int totalCourse;
    int completed;
    int inProgress;
    int todo;
    int totalHours;
    int attended;
    int absent;
    int inProgressHours;
    int open;
    String status;
    boolean compulsory;
    String supervisor;
    String trainer;
    String duration;
    String desc;
    String sessionNo;
    List<Participant> participants;
    String uriName;
    boolean myCourse;
    String semester;
}
