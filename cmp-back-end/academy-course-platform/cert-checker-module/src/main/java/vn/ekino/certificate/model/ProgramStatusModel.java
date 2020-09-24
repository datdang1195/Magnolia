package vn.ekino.certificate.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import info.magnolia.context.WebContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProgramStatusModel extends BaseModel {
    private final Provider<WebContext> webContextProvider;
    private final ParticipantService participantService;
    private final SemesterRepository semesterRepository;
    private final RedisService redisService;

    @Inject
    public ProgramStatusModel(Node content, ConfiguredTemplateDefinition definition
            , RenderingModel<?> parent
            , CertificateServicesModule servicesModule
            , Provider<WebContext> webContextProvider
            , ParticipantService participantService
            , SemesterRepository semesterRepository
            , RedisService redisService) {
        super(content, definition, parent, servicesModule);
        this.webContextProvider = webContextProvider;
        this.participantService = participantService;
        this.semesterRepository = semesterRepository;
        this.redisService = redisService;
    }

    public String getListYear() {
        var cacheData = redisService.getCache(RedisService.YEARS, RedisService.YEARS, String.class);
        return cacheData.orElseGet(participantService::getListYear);
    }

    public Map<String, Object> getProgramStatusInformation() throws JsonProcessingException {
        log.info("start getProgramStatusInformation");
        Constants.totalQuery = 0;
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        String yearParam = webContextProvider.get().getParameter("year");
        final LocalDateTime currentDate = LocalDateTime.now();
        final int year = StringUtils.isEmpty(yearParam) ? currentDate.getYear() : Integer.parseInt(yearParam);
        var cacheData = redisService.getCache(RedisService.PHASES, String.valueOf(year), List.class);
        List<Phase> listPhase = cacheData.orElseGet(() -> participantService.getListPhaseByYear(year));
        map.put("phases", new Gson().toJson(listPhase));
        map.put("listPhase", listPhase);

        var cacheDataProgramStatus = redisService.getCache(RedisService.PROGRAM_STATUS, String.valueOf(year), String.class);
        if (cacheDataProgramStatus.isPresent()) {
            map.put("data", cacheDataProgramStatus.get());
        } else {
            List<Phase> listProgramByPhase = new ArrayList<>(listPhase);
            listProgramByPhase = participantService.buildData4ProgramStatus(listProgramByPhase);
            var data = ProgramStatus.builder().year(year)
                    .phases(listProgramByPhase).build();
            map.put("data", new Gson().toJson(data));
        }

        var cacheDataSemesterProgram = redisService.getCache(RedisService.PROGRAM_SEMESTER, String.valueOf(year), String.class);
        if (cacheDataSemesterProgram.isPresent()) {
            map.put("semestersOfProgram", cacheDataSemesterProgram.get());
            return map;
        }
        List<ProgramSemesterDto> programSemesterList = new ArrayList<>();
        listPhase.forEach(phase -> {
            List<Program> programDtoList = phase.getPrograms();
            if (programDtoList != null) {
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
            }
        });
        map.put("semestersOfProgram", Constants.OBJECT_MAPPER.writeValueAsString(programSemesterList));
        long elapsedTimeMillis = System.currentTimeMillis()-start;
        float elapsedTimeSec = elapsedTimeMillis/1000F;
        log.info("end getCoursesStatus");
        log.info("total: {} second, {} query", elapsedTimeSec, Constants.totalQuery);
        return map;
    }

}
