package vn.ekino.certificate.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ekino.certificate.dto.QuizDto;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant implements Serializable {
    String id;
    String name;
    String team;
    String role;
    String profile;
    String enrollDate;
    String link;
    String fullName;
    String email;
    String attendant;
    String quiz;
    String numberOfQuizzes;
    List<QuizDto> quizzes;
    String homework;
    String score;
    String status;
    String semester;
}
