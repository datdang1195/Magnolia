package vn.ekino.certificate.service;

import com.google.gson.Gson;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.UserManager;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.SemesterDto;
import vn.ekino.certificate.dto.enumeration.ProgramSemesterDto;
import vn.ekino.certificate.model.data.Phase;
import vn.ekino.certificate.model.data.Program;
import vn.ekino.certificate.model.data.ProgramStatus;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.SemesterRepository;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class RedisService {
    public static final String PHASES = "phases";
    public static final String YEARS = "years";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String PROGRAM_SEMESTER = "programSemester";
    public static final String PROGRAM_STATUS = "programStatus";
    public static final String MY_PROGRESS = "myProgress";
    public static final String MY_COURSE = "myCourse";
    public static final String PARTICIPANT_STATUS = "participantStatus";
    public static final String CALENDAR = "calendar";

    public static final String CALENDAR_PARTICIPANT = "calendarParticipant";
    public static final String CALENDAR_SCHEDULE = "dataSchedule";

    private final CertificateServicesModule module;
    private final ParticipantService participantService;
    private final MyProgressService myProgressService;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final ProgramCourseService programCourseService;
    private final NodeNameHelper nodeNameHelper;
    private final CategoryService categoryService;
    private final ProgramRepository programRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    private final CalendarService calendarService;

    private RedissonClient redis;

    @Inject
    public RedisService(CertificateServicesModule module
            , ParticipantService participantService
            , MyProgressService myProgressService
            , UserRepository userRepository
            , SemesterRepository semesterRepository
            , ProgramCourseService programCourseService
            , NodeNameHelper nodeNameHelper
            , CategoryService categoryService
            , ProgramRepository programRepository
            , EnrolProgramRepository enrolProgramRepository
            , CalendarService calendarService) {
        this.calendarService = calendarService;
        initCache();
        this.module = module;
        this.participantService = participantService;
        this.myProgressService = myProgressService;
        this.userRepository = userRepository;
        this.semesterRepository = semesterRepository;
        this.programCourseService = programCourseService;
        this.nodeNameHelper = nodeNameHelper;
        this.categoryService = categoryService;
        this.programRepository = programRepository;
        this.enrolProgramRepository = enrolProgramRepository;
    }

    public <T> Optional<T> getCache(String catalog, String key, Class<T> target) {
        try {
            T targetObj;
            initCache();
            var map = redis.getMap(catalog);
            targetObj = target.cast(map.get(key));
            return Optional.ofNullable(targetObj);
        } catch (Exception e) {
            log.warn("Fail to get cache because {}", e.getMessage());
        }
        return Optional.empty();
    }

    public void cacheData() {
        var currentYear = LocalDate.now().getYear();
        try {
            initCache();
            cacheAvailableYears();
//            cacheCalendar();
            for (int year = 2019; year <= currentYear; year++) {
                cachePhase(year);
                cacheCourseStatus(year);
                cacheProgramStatus(year);
                cacheMyProgressAndParticipantStatus(year);
                cacheMyCourse(year);
            }
        } catch (Exception e) {
            log.warn("Scheduler fail to cache data because {}", e.getMessage());
        }
    }

    private void cacheAvailableYears() {
        try {
            RMap<String, String> map = redis.getMap(YEARS);
            map.put(YEARS, participantService.getListYear());
        } catch (Exception e) {
            log.warn("Fail to cacheAvailableYears because {}", e.getMessage());
        }
    }

    private void cachePhase(int year) {
        try {
            var listPhase = participantService.getListPhaseByYear(year);
            RMap<String, List<Phase>> map = redis.getMap(PHASES);
            map.put(String.valueOf(year), listPhase);
        } catch (Exception e) {
            log.warn("Fail to cachePhase because {}", e.getMessage());
        }
    }

    private void cacheCourseStatus(int year) {
        try {
            List<Phase> listPhase = (List<Phase>) redis.getMap(PHASES).get(String.valueOf(year));
            List<Phase> listProgramByPhase = new ArrayList<>(listPhase);
            RMap<String, ProgramStatus> map = redis.getMap(COURSE_STATUS);
            var users = findAllByRoles(List.of(Constants.SUPERVISOR_ROLE, Constants.TRAINER_ROLE));
            for (User itm : users) {
                var userId = itm.getIdentifier();
                participantService.setCurrentUser(itm);
                listProgramByPhase = participantService.buildData4CourseStatus(listProgramByPhase);
                var data = ProgramStatus.builder().year(year)
                        .phases(listProgramByPhase).build();
                map.put(String.format("%s-%s", year, userId), data);
            }
            List<ProgramSemesterDto> programSemesterList = new ArrayList<>();
            listProgramByPhase.forEach(itm -> {
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
            RMap<String, String> rMap = redis.getMap(PROGRAM_SEMESTER);
            rMap.put(String.valueOf(year), Constants.OBJECT_MAPPER.writeValueAsString(programSemesterList));
            participantService.setCurrentUser(null);
        } catch (Exception e) {
            log.warn("Fail to cache Course Status because {}", e.getMessage());
        }
    }

    private void cacheProgramStatus(int year) {
        try {
            List<Phase> listPhase = (List<Phase>) redis.getMap(PHASES).get(String.valueOf(year));
            List<Phase> listProgramByPhase = new ArrayList<>(listPhase);
            RMap<String, String> map = redis.getMap(PROGRAM_STATUS);
            listProgramByPhase = participantService.buildData4ProgramStatus(listProgramByPhase);
            var result = ProgramStatus.builder().year(year)
                    .phases(listProgramByPhase).build();
            map.put(String.valueOf(year), new Gson().toJson(result));
        } catch (Exception e) {
            log.warn("Fail to cacheProgramStatus because {}", e.getMessage());
        }

    }

    private void cacheMyProgressAndParticipantStatus(int year) {
        try {
            var programs = programRepository.findAll();
            var users = findAllByRoles(List.of(Constants.PARTICIPANT_ROLE));
            RMap<String, Map<String, Object>> mapMyProgress = redis.getMap(MY_PROGRESS);
            RMap<String, Map<String, Object>> mapParticipant = redis.getMap(PARTICIPANT_STATUS);
            for (User user : users) {
                myProgressService.setCurrentUser(user);
                Map<String, Object> data = myProgressService.getMyProgressInformation(String.valueOf(year));
                mapMyProgress.put(String.format("%s-%s", year, user.getIdentifier()), data);

                participantService.setCurrentUser(user);
                programs.forEach(itm -> {
                    String programId = PropertyUtil.getString(itm, "jcr:uuid");
                    var optionalNode = enrolProgramRepository.findByProgramUser(user.getIdentifier(), programId);
                    if (optionalNode.isPresent()) {
                        var enrolProgramDto = MapperUtils.nodeToObject(optionalNode.get(), EnrolProgramDto.class).get();
                        Map<String, Object> map = new HashMap<>();
                        participantService.buildInformation(map, enrolProgramDto);
                        mapParticipant.put(String.format("%s-%s", user.getIdentifier(), programId), map);
                    }
                });
            }
            myProgressService.setCurrentUser(null);
            participantService.setCurrentUser(null);
        } catch (Exception e) {
            log.warn("Fail to cacheMyProgressAndParticipantStatus because {}", e.getMessage());
        }
    }

    private void cacheMyCourse(int year) {
        try {
            var users = findAllByRoles(List.of(Constants.PARTICIPANT_ROLE, Constants.TRAINER_ROLE, Constants.SUPERVISOR_ROLE));
            RMap<String, String> map = redis.getMap(MY_COURSE);
            for (User user : users) {
                programCourseService.setCurrentUser(user);
                var result = programCourseService.getCurrentCoursesOfUser()
                        .stream().map(itm -> {
                            itm.setReadMoreLink(nodeNameHelper.getValidatedName(itm.getCourseDetail().getNodeName()));
                            itm.setHours(programCourseService.getHoursOfCourse(itm.getCourseId()));
                            return itm;
                        }).collect(Collectors.toList());
                map.put(String.format("%s-%s", year, user.getIdentifier()), Constants.OBJECT_MAPPER.writeValueAsString(result));
            }
            var allCourse = categoryService.findAllCategoriesOfCourse();
            map.put(String.format("%s-AllCourse", year), Constants.OBJECT_MAPPER.writeValueAsString(allCourse));
            programCourseService.setCurrentUser(null);
        } catch (Exception e) {
            log.warn("Fail to cacheMyCourse because {}", e.getMessage());
        }
    }

    private void cacheCalendar() {
        try {
            var users = findAllByRoles(List.of(Constants.PARTICIPANT_ROLE, Constants.TRAINER_ROLE, Constants.SUPERVISOR_ROLE));
            RMap<String, Object> map = redis.getMap(CALENDAR);
            for (User user : users) {
                calendarService.setCurrentUser(user);
                var calendarParticipant = calendarService.getCalendar4Participant();
                map.put(String.format("%s-%s", user.getIdentifier(), CALENDAR_PARTICIPANT), calendarParticipant);
                var dataSchedule = calendarService.getDataSchedule();
                map.put(String.format("%s-%s", user.getIdentifier(), CALENDAR_SCHEDULE), dataSchedule);
            }
            calendarService.setCurrentUser(null);
        } catch (Exception e) {
            log.warn("Fail to cacheCalendar because {}", e.getMessage());
        }
    }

    private List<User> findAllByRoles(List<String> roles) {
        return userRepository.findAll()
                .stream()
                .map(this::getUserByName)
                .filter(Objects::nonNull)
                .filter(itm -> itm.getAllRoles().stream().anyMatch(roles::contains))
                .collect(Collectors.toList());
    }

    private User getUserByName(Node node) {
        UserManager userManager = SecuritySupport.Factory.getInstance().getUserManager();
        try {
            return userManager.getUser(node.getName());
        } catch (RepositoryException e) {
            log.warn("Fail to get user because {}", e.getMessage());
        }
        return null;
    }


    private void initCache() {
        if (this.redis != null) {
            if (!this.module.getRedisServer().equals(this.redis.getConfig().useSingleServer().getAddress())) {
                this.redis.shutdown();
                connect();
            }
        } else {
            connect();
        }
    }

    private void connect() {
        try {
            if (this.redis != null) {
                if (!this.module.getRedisServer().equals(this.redis.getConfig().useSingleServer().getAddress())) {
                    this.redis.shutdown();
                }
            }
            Config config = new Config();
            config.useSingleServer().setAddress(this.module.getRedisServer());
            this.redis = Redisson.create(config);
        } catch (Exception e) {
            this.redis = null;
            log.warn("Fail to connect to redis because {}", e.getMessage());
        }
    }
}
