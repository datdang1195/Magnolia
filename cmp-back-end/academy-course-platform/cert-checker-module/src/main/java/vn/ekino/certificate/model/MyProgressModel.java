package vn.ekino.certificate.model;

import info.magnolia.context.WebContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.PhaseRepository;
import vn.ekino.certificate.repository.ProgramCourseRepository;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.repository.SemesterRepository;
import vn.ekino.certificate.service.MyProgressService;
import vn.ekino.certificate.service.ParticipantService;
import vn.ekino.certificate.service.RedisService;
import vn.ekino.certificate.util.Constants;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Slf4j
public class MyProgressModel extends BaseModel {
    private final Provider<WebContext> webContextProvider;
    private final ProgramCourseRepository programCourseRepository;
    private final PhaseRepository phaseRepository;
    private final EnrolProgramRepository enrolProgramRepository;
    private final ParticipantService participantService;
    private final ProgramRepository programRepository;
    private final SemesterRepository semesterRepository;
    private final MyProgressService myProgressService;
    private final RedisService redisService;

    boolean isOnlyEnrollSemester2 = false, isFullProgram = false, isOnlyEnrollSemester1 = false;

    @Inject
    public MyProgressModel(Node content, ConfiguredTemplateDefinition definition
            , RenderingModel<?> parent, CertificateServicesModule servicesModule
            , Provider<WebContext> webContextProvider
            , ProgramCourseRepository programCourseRepository
            , PhaseRepository phaseRepository
            , EnrolProgramRepository enrolProgramRepository
            , ParticipantService participantService
            , ProgramRepository programRepository
            , SemesterRepository semesterRepository
            , MyProgressService myProgressService
            , RedisService redisService) {
        super(content, definition, parent, servicesModule);
        this.webContextProvider = webContextProvider;
        this.programCourseRepository = programCourseRepository;
        this.phaseRepository = phaseRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.participantService = participantService;
        this.programRepository = programRepository;
        this.semesterRepository = semesterRepository;
        this.myProgressService = myProgressService;
        this.redisService = redisService;
    }

    public String getListYear() {
        var cacheData = redisService.getCache(RedisService.YEARS, RedisService.YEARS, String.class);
        return cacheData.orElseGet(participantService::getListYear);
    }

    public Map<String, Object> getMyProgressInformation() {
        log.info("start getMyProgressInformation");
        Constants.totalQuery = 0;
        long start = System.currentTimeMillis();
        String yearParam = webContextProvider.get().getParameter("year");
        final LocalDateTime currentDate = LocalDateTime.now();
        final String year = StringUtils.isEmpty(yearParam) ? String.valueOf(currentDate.getYear()) : yearParam;
        String key = String.format("%s-%s", year, getCurrentUser().getIdentifier());
        var cacheData = redisService.getCache(RedisService.MY_PROGRESS, key, Map.class);
        if (cacheData.isPresent()) {
            var data = cacheData.get();
            setSemester(data);
            return data;
        }
        myProgressService.setCurrentUser(getCurrentUser());
        var result = myProgressService.getMyProgressInformation(year);
        setSemester(result);
        long elapsedTimeMillis = System.currentTimeMillis()-start;
        float elapsedTimeSec = elapsedTimeMillis/1000F;
        log.info("end getCoursesStatus");
        log.info("total: {} second, {} query", elapsedTimeSec, Constants.totalQuery);
        return result;
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

    private void setSemester(Map<String, Object> map) {
        if (!map.isEmpty()) {
            this.isOnlyEnrollSemester1 = (boolean) map.get("isOnlyEnrollSemester1");
            this.isOnlyEnrollSemester2 = (boolean) map.get("isOnlyEnrollSemester2");
            this.isFullProgram = (boolean) map.get("isFullProgram");
        }
    }

}


