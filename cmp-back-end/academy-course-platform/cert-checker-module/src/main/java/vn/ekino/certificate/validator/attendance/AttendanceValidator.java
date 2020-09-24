package vn.ekino.certificate.validator.attendance;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.validator.AbstractValidator;
import vn.ekino.certificate.repository.AttendanceRepository;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AttendanceValidator extends AbstractValidator<Date> {

    private final Item itm;
    private final AttendanceRepository attendanceRepository;

    @Inject
    public AttendanceValidator(String errorMessage, Item itm, AttendanceRepository attendanceRepository) {
        super(errorMessage);
        this.itm = itm;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    protected boolean isValidValue(Date date) {
        String courseId = itm.getItemProperty("course").getValue().toString();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        var result = attendanceRepository.findUserAttendance(TimeUtils.toString(localDate), courseId);
        return result.isEmpty();
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }
}
