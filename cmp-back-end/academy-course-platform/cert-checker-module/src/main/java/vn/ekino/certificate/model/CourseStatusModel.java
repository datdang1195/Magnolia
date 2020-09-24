package vn.ekino.certificate.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.dto.SemesterDto;
import vn.ekino.certificate.dto.enumeration.ProgramSemesterDto;
import vn.ekino.certificate.model.data.Phase;
import vn.ekino.certificate.model.data.Program;
import vn.ekino.certificate.model.data.ProgramStatus;
import vn.ekino.certificate.repository.SemesterRepository;
import vn.ekino.certificate.service.ParticipantService;
import vn.ekino.certificate.service.RedisService;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CourseStatusModel<RD extends ConfiguredTemplateDefinition> extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    private static final String SELECTED_YEAR = "year";
    private final Provider<WebContext> webContextProvider;
    private final ParticipantService participantService;
    private final SemesterRepository semesterRepository;
    private final RedisService redisService;


    @Inject
    public CourseStatusModel(Node content,
                             ConfiguredTemplateDefinition definition,
                             RenderingModel<?> parent,
                             Provider<WebContext> webContextProvider,
                             ParticipantService participantService, SemesterRepository semesterRepository, RedisService redisService) {
        super(content, definition, parent);
        this.webContextProvider = webContextProvider;
        this.participantService = participantService;
        this.semesterRepository = semesterRepository;
        this.redisService = redisService;
    }

    public User getCurrentUser() {
        return MgnlContext.getUser();
    }

    public String getRoleAccount() {
        return new Gson().toJson(orderRole(getCurrentUser().getAllRoles()));
    }

    public String getSelectedYear() {
        return Optional.ofNullable(webContextProvider.get()
                .getParameter(SELECTED_YEAR))
                .orElse(String.valueOf(LocalDate.now().getYear()));
    }

    /**
     * @return
     * @throws JsonProcessingException
     */
    public String getSemestersOfPrograms() throws JsonProcessingException {
        var currentYear = getSelectedYear();
        var cacheData = redisService.getCache(RedisService.PROGRAM_SEMESTER, currentYear, String.class);
        if (cacheData.isPresent()) {
            return cacheData.get();
        }
        var listPhase = getPhaseOfCourseStatus(Integer.parseInt(currentYear));
        List<ProgramSemesterDto> programSemesterList = new ArrayList<>();
        if (listPhase != null){
            listPhase.forEach(itm -> {
                List<Program> programDtoList = itm.getPrograms();
                programDtoList.forEach(program -> {
                    List<Node> semesters = semesterRepository.findByProgram(program.getId());
                    List<SemesterDto> semesterDtos = semesters.stream().map(semester -> MapperUtils.nodeToObject(semester, SemesterDto.class).get()).collect(Collectors.toList());
                    List<String> semesterDates = new ArrayList<>();
                    semesterDtos
                            .forEach(semesterDto -> semesterDates.add(semesterDto.getTitle() + ";" + semesterDto.getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL)
                                    + ";"
                                    + semesterDto.getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL)));
                    programSemesterList.add(new ProgramSemesterDto(program.getId(), semesterDates));
                });
            });
        }
        return Constants.OBJECT_MAPPER.writeValueAsString(programSemesterList);
    }

    public String getCoursesStatus() throws JsonProcessingException {
        log.info("start getCoursesStatus");
        Constants.totalQuery = 0;
        long start = System.currentTimeMillis();
        var currentYear = getSelectedYear();
        var userId = getCurrentUser().getIdentifier();
        String key = String.format("%s-%s", currentYear, userId);
        var cacheData = redisService.getCache(RedisService.COURSE_STATUS, key, ProgramStatus.class);
        if (cacheData.isPresent()) {
            var data = cacheData.get();
            return Constants.OBJECT_MAPPER.writeValueAsString(data);
        }
        var listProgramByPhase = getPhaseOfCourseStatus(Integer.parseInt(currentYear));
        var data = ProgramStatus.builder().year(Integer.parseInt(currentYear))
                .phases(listProgramByPhase).build();
        var result = Constants.OBJECT_MAPPER.writeValueAsString(data);
        long elapsedTimeMillis = System.currentTimeMillis()-start;
        float elapsedTimeSec = elapsedTimeMillis/1000F;
        log.info("end getCoursesStatus");
        log.info("total: {} second, {} query", elapsedTimeSec, Constants.totalQuery);
        return result;
    }

    public List<Phase> getPhases() {
        var currentYear = getSelectedYear();
        var cacheData = redisService.getCache(RedisService.PHASES, currentYear, List.class);
        return cacheData.orElseGet(() -> participantService.getListPhaseByYear(Integer.parseInt(getSelectedYear())));
    }

    public String getListYear() {
        var cacheData = redisService.getCache(RedisService.YEARS, RedisService.YEARS, String.class);
        return cacheData.orElseGet(participantService::getListYear);
    }

    private List<Phase> getPhaseOfCourseStatus(int year) {
        long start = System.currentTimeMillis();
        var listPhase = participantService.getListPhaseByYear(year);
        float elapsedTimeSec = (System.currentTimeMillis()-start)/1000F;
        log.info("end getListPhaseByYear");
        log.info("total: {} second", elapsedTimeSec);
        List<Phase> listProgramByPhase = new ArrayList<>(listPhase);
        start = System.currentTimeMillis();
        listProgramByPhase = participantService.buildData4CourseStatus(listProgramByPhase);
        elapsedTimeSec = (System.currentTimeMillis()-start)/1000F;
        log.info("end buildData4CourseStatus");
        log.info("total: {} second", elapsedTimeSec);
        return listProgramByPhase;
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
        return new ArrayList<>(result);
    }

}
