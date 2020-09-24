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
public class Program implements Serializable {
    String id;
    String name;
    String title;
    String time;
    List<Participant> participants;
    List<OjtProject> onJobTraining;
    Course course;
    String startDate;
    String endDate;
    List<Course> courses;
    String className;
    String groupProgram;
    List<Session> listSession;
}
