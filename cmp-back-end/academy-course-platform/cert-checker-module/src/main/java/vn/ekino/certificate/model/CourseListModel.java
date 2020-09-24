package vn.ekino.certificate.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.dto.CourseCompulsoryDto;
import vn.ekino.certificate.dto.EnrolProgramDto;
import vn.ekino.certificate.dto.OJTUserResultDto;
import vn.ekino.certificate.repository.EnrolProgramRepository;
import vn.ekino.certificate.repository.OJTUserResultRepository;
import vn.ekino.certificate.service.CategoryService;
import vn.ekino.certificate.service.CourseService;
import vn.ekino.certificate.service.ProgramCourseService;
import vn.ekino.certificate.service.RedisService;
import vn.ekino.certificate.util.Constants;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CourseListModel<RD extends ConfiguredTemplateDefinition> extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    //    private static final String PAGE_NUMBER = "pageNumber";
//    private static final String CATEGORY = "category";
    private final CategoryService categoryService;
    private final CourseService courseService;
    private final ProgramCourseService programCourseService;
    private final OJTUserResultRepository ojtUserResultRepository;
    private final EnrolProgramRepository enrolProgramRepository;

    private final NodeNameHelper nodeNameHelper;
    private final RedisService redisService;

    //    private long numberOfPage;
    private List<String> courseInformation;

    @Inject
    public CourseListModel(Node content,
                           ConfiguredTemplateDefinition definition,
                           RenderingModel<?> parent,
                           CategoryService categoryService,
                           CourseService courseService,
                           ProgramCourseService programCourseService,
                           OJTUserResultRepository ojtUserResultRepository,
                           EnrolProgramRepository enrolProgramRepository,
                           NodeNameHelper nodeNameHelper,
                           RedisService redisService) {
        super(content, definition, parent);

        this.categoryService = categoryService;
        this.courseService = courseService;
        this.programCourseService = programCourseService;
        this.ojtUserResultRepository = ojtUserResultRepository;
        this.enrolProgramRepository = enrolProgramRepository;
        this.nodeNameHelper = nodeNameHelper;
        this.redisService = redisService;
    }

    public User getCurrentUser() {
        return MgnlContext.getUser();
    }

    public String findAllCategoriesOfCourse() {
        var currentYear = LocalDate.now().getYear();
        String key = String.format("%s-AllCourse", currentYear);
        var cacheData = redisService.getCache(RedisService.MY_COURSE, key, String.class);
        return cacheData.orElseGet(() -> new Gson().toJson(categoryService.findAllCategoriesOfCourse()));
    }

    /**
     * Get all course of current user
     *
     * @return list courses of current user in page number
     */
    public String getCurrentCoursesOfUser() throws JsonProcessingException {
        var currentYear = LocalDate.now().getYear();
        String key = String.format("%s-%s", currentYear, getCurrentUser().getIdentifier());
        var cacheData = redisService.getCache(RedisService.MY_COURSE, key, String.class);
        if (cacheData.isPresent()) {
            return cacheData.get();
        }
        List<CourseCompulsoryDto> result = programCourseService.getCurrentCoursesOfUser();
        result.stream().map(itm -> {
            itm.setReadMoreLink(nodeNameHelper.getValidatedName(itm.getCourseDetail().getNodeName()));
            itm.setHours(programCourseService.getHoursOfCourse(itm.getCourseId()));
            return itm;
        }).collect(Collectors.toList());
        return Constants.OBJECT_MAPPER.writeValueAsString(result);
    }

    public String getOJTProjectName() {
        String ojtProjectName = StringUtils.EMPTY;

        var enrolProgramDto = enrolProgramRepository.getAllByUserApproved(getCurrentUser().getIdentifier())
                .stream()
                .map(itm -> MapperUtils.nodeToObject(itm, EnrolProgramDto.class).get())
                .filter(itm -> LocalDateTime.now().getYear() == itm.getProgram().getPhase().getStartDate().getYear()
                        || LocalDateTime.now().getYear() == itm.getProgram().getPhase().getEndDate().getYear())
                .sorted(Comparator.comparingInt(t -> t.getProgram().getStartDate().getYear()))
                .findFirst().orElse(null);

        if (enrolProgramDto != null) {
            var nodeOjtUserResult = ojtUserResultRepository.findByUserEnrolProgram(enrolProgramDto.getUuid());
            if (nodeOjtUserResult.isPresent()) {
                var ojtUserResultDto = MapperUtils.nodeToObject(nodeOjtUserResult.get(), OJTUserResultDto.class).get();
                ojtProjectName = ojtUserResultDto.getOjtProject() != null ? ojtUserResultDto.getOjtProject().getUriName() : ojtProjectName;
            }
        }
        return ojtProjectName;
    }

    /**
     * get information about Course page title
     *
     * @return list string contains information about Course page title
     */
    public List<String> getCourseInformation() {
        return programCourseService.getCourseInformation();
    }

//    /**
//     * Get all courses of current user and filter by category with pagination
//     * <p>
//     * //     * @param categoryId
//     *
//     * @return list courses of current user in page number
//     */
//    public String getCurrentCoursesOfUserByCategory(String categoryId) {
//
//        List<CourseCompulsoryDto> result = programCourseService
//                .getCurrentCourseCompulsoriesOfUserByCategory(categoryId, (getPageNum() - 1) * getPageSize(), getPageSize());
//        setNumberOfPage(programCourseService.getTotalCourseOfCategory());
//        result.forEach(courseCompulsoryDto -> courseCompulsoryDto.setReadMoreLink(nodeNameHelper.getValidatedName(
//                courseCompulsoryDto.getCourseDetail().getNodeName())));
//        return new Gson().toJson(result);
//    }
//
//    public int getPageSize() {
//        return certificateServicesModule.getCourseListPageSize();
//    }
//
//    public int getPageNum() {
//        String pageNum = Optional.ofNullable(webContextProvider.get().getParameter(PAGE_NUMBER)).orElse("1");
//        return Integer.parseInt(pageNum);
//    }
//    public CategoryDto getCategory() {
//        String category = getCategoryId();
//        if (StringUtils.isNotEmpty(category)) {
//            return categoryService.findCategoryById(category).orElse(null);
//        }
//        return null;
//    }
//
//    public String getCategoryId() {
//        return Optional.ofNullable(webContextProvider.get().getParameter(CATEGORY)).orElse("");
//    }



//    public void setCourseInformation(List<String> courseInformation) {
//        this.courseInformation = courseInformation;
//    }

//    public long getNumberOfPage() {
//        return numberOfPage;
//    }


//    public String getTotalInfo() {
//        return new Gson().toJson(programCourseService.getTotalInfo());
//    }

//    public long getTotalCourse() {
//        return programCourseService.getTotalCoursesOfProgram();
//    }
//
//    public long getTotalHours() {
//        return programCourseService.getTotalHoursOfProgram();

//    }

//    public void setNumberOfPage(long coursesSize) {
//        this.numberOfPage = coursesSize % getPageSize() == 0 ? coursesSize / getPageSize() : coursesSize / getPageSize() + 1;
//    }

//    public int getHoursOfCourse(String courseId) throws RepositoryException {
//        return programCourseService.getHoursOfCourse(courseId);
//    }

}
