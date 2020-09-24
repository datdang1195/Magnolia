package vn.ekino.certificate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String TIME_STAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_PATTERN_OF_PROGRAM_DETAIL = "MMM dd, yyyy";
    public static final String DATE_TIME_PATTERN_OF_PROGRAM_STATUS = "MMM - dd - yyyy";
    public static final String DATE_TIME_PATTERN_OF_PROGRAM_COURSE_NOTIFICATION = "dd - MMM - yyyy";
    public static final String CONTENT_NODE = "mgnl:contentNode";
    public static final String PROPERTY_NAME = "name";

    public static final String COOKIE_AUTH_NAME = "academy_auth";
    public static final long COOKIE_AUTH_EXPIRATION_TIME = 604800000;
    public static final String PRIVATE_KEY = "ekino";

    public static final String SUPERVISOR_ROLE = "supervisor";
    public static final String TRAINER_ROLE = "trainer";
    public static final String PARTICIPANT_ROLE = "participant";
    public static final String ANONYMOUS_ROLE = "anonymous";

    public static final String HEADER_THUMBNAIL = "header-thumbnail";
    public static final String USER_PROFILE_THUMBNAIL = "user-profile-thumbnail";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static class Rendition {
        public static String THUMBNAIL = "thumbnail";
    }

    public static class Semester {
        public static String SEMESTER_1 = "Semester 1";
        public static String SEMESTER_2 = "Semester 2";
        public static String FULL_PROGRAM = "Full Program";
    }

    public static int totalQuery = 0;
}
