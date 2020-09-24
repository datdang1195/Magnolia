package vn.ekino.certificate.model;

import com.google.gson.Gson;
import info.magnolia.context.WebContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.dto.SemesterDto;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.model.data.Participant;
import vn.ekino.certificate.model.data.Program;
import vn.ekino.certificate.repository.*;
import vn.ekino.certificate.service.ParticipantService;
import vn.ekino.certificate.service.RedisService;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ParticipantStatusModel extends BaseModel {
    private final Provider<WebContext> webContextProvider;
    private final ProgramRepository programRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final SemesterRepository semesterRepository;
    private final RedisService redisService;
    boolean isOnlyEnrollSemester2 = false, isFullProgram = false, isOnlyEnrollSemester1 = false;

    @Inject
    public ParticipantStatusModel(Node content, ConfiguredTemplateDefinition definition
            , RenderingModel<?> parent
            , CertificateServicesModule servicesModule
            , Provider<WebContext> webContextProvider
            , ProgramRepository programRepository
            , EnrolProgramRepository enrolProgramRepository
            , UserRepository userRepository
            , ParticipantService participantService
            , SemesterRepository semesterRepository, RedisService redisService) {
        super(content, definition, parent, servicesModule);
        this.webContextProvider = webContextProvider;
        this.programRepository = programRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.userRepository = userRepository;
        this.participantService = participantService;
        this.semesterRepository = semesterRepository;
        this.redisService = redisService;
    }

    public String getListYear() {
        var cacheData = redisService.getCache(RedisService.YEARS, RedisService.YEARS, String.class);
        return cacheData.orElseGet(participantService::getListYear);
    }

    public Map<String, Object> getParticipantStatusInformation() {
        log.info("start getParticipantStatusInformation");
        Constants.totalQuery = 0;
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        String yearParam = webContextProvider.get().getParameter("year");
        String phaseParam = webContextProvider.get().getParameter("phase");
        String programParam = webContextProvider.get().getParameter("program");
        String userParam = webContextProvider.get().getParameter("user");
        final LocalDateTime currentDate = LocalDateTime.now();
        final int year = StringUtils.isEmpty(yearParam) ? currentDate.getYear() : Integer.parseInt(yearParam);
        var listPhase = participantService.getListPhaseByYear(year);
        map.put("phases", new Gson().toJson(listPhase));
        map.put("listPhase", listPhase);
        if (StringUtils.isNotEmpty(phaseParam)) {
            var programs = programRepository.findProgramByPhase(phaseParam)
                    .stream()
                    .map(itm -> MapperUtils.nodeToObject(itm, ProgramDto.class).get())
                    .map(this::mapProgram)
                    .collect(Collectors.toList());
            map.put("programs", new Gson().toJson(programs));
            map.put("listProgram", programs);
            List semesterList = new ArrayList();
            programs.forEach(program -> {
                String programId = program.getId();
                var semestersOfProgram = semesterRepository.findByProgram(programId).stream()
                        .map(itm -> MapperUtils.nodeToObject(itm, SemesterDto.class).get())
                        .sorted(Comparator.comparing(SemesterDto::getTitle))
                        .collect(Collectors.toList());
                semesterList.addAll(semestersOfProgram);
            });
            map.put("semestersOfProgram", new Gson().toJson(semesterList));
            map.put("semesterList", semesterList);
            if (StringUtils.isNotEmpty(programParam)) {
                map.put("programInfo", new ProgramDto());
                var programDto = programRepository.findById(programParam).flatMap(itm -> MapperUtils.nodeToObject(itm, ProgramDto.class));
                programDto.ifPresent(itm -> map.put("programInfo", itm));
                if (StringUtils.isNotEmpty(userParam)) {
                    var enrolProgramDto = enrolProgramRepository.findByProgramUser(userParam, programParam)
                            .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get())
                            .orElse(null);
                    map.put("enrolProgramDto", new Gson().toJson(enrolProgramDto));
                    map.put("listEnrolProgramDto", enrolProgramDto);
                    map.put("user", new UserDto());
                    var userDto = userRepository.findById(userParam).flatMap(itm -> MapperUtils.nodeToObject(itm, UserDto.class));
                    userDto.ifPresent(itm -> {
                        if (enrolProgramDto != null) {
                            itm.setParticipantStatus(enrolProgramDto.getParticipantStatus());
                        }
                        map.put("user", itm);
                    });
                    String enrollType = enrolProgramDto.getEnrollType();
                    if ((Constants.Semester.SEMESTER_1.equals(enrollType))) {
                        isOnlyEnrollSemester1 = true;
                    } else if ((Constants.Semester.SEMESTER_2.equals(enrollType))) {
                        isOnlyEnrollSemester2 = true;
                    } else {
                        isFullProgram = true;
                    }
                    String key = String.format("%s-%s", userParam, programParam);
                    var cacheData = redisService.getCache(RedisService.PARTICIPANT_STATUS, key, Map.class);
                    if (cacheData.isPresent()) {
                        map.putAll(cacheData.get());
                    } else {
                        participantService.buildInformation(map, enrolProgramDto);
                    }
                }
            }
        }
        long elapsedTimeMillis = System.currentTimeMillis()-start;
        float elapsedTimeSec = elapsedTimeMillis/1000F;
        log.info("end getCoursesStatus");
        log.info("total: {} second, {} query", elapsedTimeSec, Constants.totalQuery);
        return map;
    }

    public boolean isOnlyEnrollSemester2() {
        return this.isOnlyEnrollSemester2;
    }

    public boolean isOnlyEnrollSemester1() {
        return this.isOnlyEnrollSemester1;
    }

    public boolean isFullProgram() {
        return this.isFullProgram;
    }

    private Program mapProgram(ProgramDto dto) {
        String time = String.format("%s - %s", dto.getPhase().getStartDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL)
                , dto.getPhase().getEndDate().format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL));
        return Program.builder()
                .id(dto.getUuid())
                .name(dto.getGroup().getDisplayName())
                .time(time)
                .participants(enrolProgramRepository.findAllUserByProgram(dto.getUuid())
                        .stream()
                        .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get())
                        .filter(itm -> itm.getUser().getRoles().contains(Constants.PARTICIPANT_ROLE))
                        .map(enrol -> Participant.builder().id(enrol.getUser().getUuid())
                                .name(enrol.getUser().getFullName()).build())
                        .collect(Collectors.toList()))
                .build();
    }
}
