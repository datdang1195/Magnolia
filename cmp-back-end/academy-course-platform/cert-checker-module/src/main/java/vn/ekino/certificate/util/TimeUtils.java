package vn.ekino.certificate.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@UtilityClass
public class TimeUtils {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL =
            DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_OF_PROGRAM_DETAIL);
    public static final DateTimeFormatter DATE_TIME_PATTERN_OF_PROGRAM_STATUS =
            DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_OF_PROGRAM_STATUS);

    public static final DateTimeFormatter DATE_TIME_PATTERN_OF_PROGRAM_COURSE_NOTIFICATION =
            DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_OF_PROGRAM_COURSE_NOTIFICATION);

    public LocalDate toLocalDate(Calendar calendar) {
        return Optional.ofNullable(calendar)
                .map(cal -> toLocalDateTime(cal).toLocalDate())
                .orElse(null);
    }

    public LocalTime toLocalTime(Calendar calendar) {
        return Optional.ofNullable(calendar)
                .map(cal -> toLocalDateTime(calendar).toLocalTime())
                .orElse(null);
    }

    public LocalDateTime toLocalDateTime(Calendar calendar) {
        TimeZone timeZone = calendar.getTimeZone();
        ZoneId zoneId = timeZone == null ? ZoneId.systemDefault() : timeZone.toZoneId();

        return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
    }

    public Calendar toCalendar(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        return calendar;
    }

    public LocalDateTime toLocalDateTime(String s) {
        return LocalDateTime.parse(s, DATE_TIME_FORMATTER);
    }

    public LocalDate toLocalDate(String s) {
        return LocalDate.parse(s, DATE_FORMATTER);
    }

    public LocalDate toLocalDate4Notification(String s) {
        return LocalDate.parse(s, DATE_TIME_PATTERN_OF_PROGRAM_COURSE_NOTIFICATION);
    }

    public String toString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }
    public String toString(LocalDateTime localDateTime, String format) {
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }
    public String toString(LocalDate localDate) {
        return localDate.format(DATE_FORMATTER);
    }

    public Date toDate(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
