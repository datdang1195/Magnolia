package vn.ekino.certificate.model;

import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.service.CalendarService;
import vn.ekino.certificate.service.ProgramCourseService;
import vn.ekino.certificate.service.RedisService;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class CalendarModel extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    private final Provider<WebContext> webContextProvider;
    private final ProgramCourseService programCourseService;
    private final CalendarService calendarService;
    private final RedisService redisService;

    @Inject
    public CalendarModel(Node content,
                         ConfiguredTemplateDefinition definition,
                         RenderingModel<?> parent,
                         Provider<WebContext> webContextProvider,
                         ProgramCourseService programCourseService,
                         CalendarService calendarService,
                         RedisService redisService) {
        super(content, definition, parent);
        this.webContextProvider = webContextProvider;
        this.programCourseService = programCourseService;
        this.calendarService = calendarService;
        this.redisService = redisService;
    }

    public User getCurrentUser() {
        return MgnlContext.getUser();
    }



    public List<String> getProgramInformationByDate() {
        LocalDate selectedDate = TimeUtils.toLocalDate(getSelectedDate());
        Optional<ProgramDto> programOptional = getListProgramOfCurrentUser()
                .stream()
                .filter(program ->
                        (program.getPhase().getStartDate().toLocalDate().isBefore(selectedDate)
                                || program.getPhase().getStartDate().toLocalDate().isEqual(selectedDate))
                                && (program.getPhase().getEndDate().toLocalDate().isAfter(selectedDate)
                                || program.getPhase().getEndDate().toLocalDate().isEqual(selectedDate)))
                .findFirst();

        List<String> result = new ArrayList<>();

        programOptional.ifPresent(p -> {
                    result.add(p.getPhase().getNodeName());
                    result.add(p.getGroup().getDisplayName());
                    result.add(p.getPhase().getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL) +
                            " - " + p.getPhase().getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
                }
        );

        return result;
    }

    public String getCalendar() {
        String key = String.format("%s-%s", getCurrentUser().getIdentifier(), RedisService.CALENDAR_PARTICIPANT);
        var cacheData = redisService.getCache(RedisService.CALENDAR, key, String.class);
        if (cacheData.isPresent()) {
            return cacheData.get();
        }
        calendarService.setCurrentUser(getCurrentUser());
        return calendarService.getCalendar4Participant();
    }

    public boolean showFilter() {
        List<String> roles = List.of(Constants.SUPERVISOR_ROLE, Constants.TRAINER_ROLE);
        return getCurrentUser().getRoles().stream().anyMatch(roles::contains);
    }

    public String getRoleAccount() {
        return new Gson().toJson(orderRole(getCurrentUser().getAllRoles()));
    }

    public List<String> getRole4Toggle() {
        var roles = getCurrentUser().getAllRoles();
        List<String> result = new ArrayList<>();
        if (roles.contains(Constants.SUPERVISOR_ROLE)) {
            result.add(Constants.SUPERVISOR_ROLE);
        }
        if (roles.contains(Constants.TRAINER_ROLE)) {
            result.add(Constants.TRAINER_ROLE);
        }
        if (roles.contains(Constants.PARTICIPANT_ROLE)) {
            result.add(Constants.PARTICIPANT_ROLE);
        }
        return result;
    }

    public Map<String, Object> getDataSchedule() {
        String key = String.format("%s-%s", getCurrentUser().getIdentifier(), RedisService.CALENDAR_SCHEDULE);
        var cacheData = redisService.getCache(RedisService.CALENDAR, key, Map.class);
        if (cacheData.isPresent()) {
            return cacheData.get();
        }
        calendarService.setCurrentUser(getCurrentUser());
        return calendarService.getDataSchedule();
    }

    private List<String> orderRole(Collection<String> roles) {
        Set<String> result = new LinkedHashSet<>();
        if (roles.contains(Constants.SUPERVISOR_ROLE)) {
            result.add(Constants.SUPERVISOR_ROLE);
        }
        if (roles.contains(Constants.TRAINER_ROLE)) {
            result.add(Constants.TRAINER_ROLE);
        }
        if (roles.contains(Constants.PARTICIPANT_ROLE)) {
            result.add(Constants.PARTICIPANT_ROLE);
        }
        result.addAll(roles);
        return new ArrayList<>(result);
    }

    private List<ProgramDto> getListProgramOfCurrentUser() {
        return programCourseService.getListProgramOfCurrentUser();
    }

    private String getSelectedDate() {
        HttpServletRequest request = webContextProvider.get().getRequest();
        return Optional.ofNullable(request.getParameter("selectedMonth"))
                .orElse(LocalDate.now().toString());
    }


}
