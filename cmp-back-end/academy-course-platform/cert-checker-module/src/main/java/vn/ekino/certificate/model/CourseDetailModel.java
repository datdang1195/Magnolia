package vn.ekino.certificate.model;

import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.dto.CommentDto;
import vn.ekino.certificate.dto.CourseCompulsoryDto;
import vn.ekino.certificate.dto.ImageDto;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.model.data.UserProfile;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.service.CommentService;
import vn.ekino.certificate.service.ProgramCourseService;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class CourseDetailModel<RD extends ConfiguredTemplateDefinition> extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    private final ProgramCourseService programCourseService;
    private final Provider<WebContext> webContextProvider;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final NodeNameHelper nodeNameHelper;

    @Inject
    public CourseDetailModel(Node content,
                             ConfiguredTemplateDefinition definition,
                             RenderingModel<?> parent,
                             ProgramCourseService programCourseService,
                             Provider<WebContext> webContextProvider,
                             UserRepository userRepository, CommentService commentService,
                             NodeNameHelper nodeNameHelper) {
        super(content, definition, parent);
        this.programCourseService = programCourseService;
        this.webContextProvider = webContextProvider;
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.nodeNameHelper = nodeNameHelper;
    }

    public User getCurrentUser() {
        return MgnlContext.getUser();
    }

    public List<String> getCourseTitleDetails() {

        return programCourseService.getProgramInformation(getCourseCompulsoryId())
                .map(p -> List.of(p.getPhase().getNodeName(), p.getGroup().getDisplayName()))
                .orElse(Collections.emptyList());
    }

    public List<CourseCompulsoryDto> getListPrerequisiteClassesOfCourseCompulsory() {
        return programCourseService.getListPrerequisiteClassesByCourseCompulsoryId(getCourseCompulsoryId()).stream().map(itm -> {itm.setReadMoreLink(nodeNameHelper.getValidatedName(itm.getCourseDetail().getNodeName()));return itm; }).collect(Collectors.toList());
    }


    public CourseCompulsoryDto getCurrentCourseCompulsory() {
        String courseId = getCourseCompulsoryId();

        return programCourseService.findCourseCompulsoryById(courseId).orElse(null);
    }

    public UserProfile getUserProfile() {
        var currentUser = MgnlContext.getUser();
        return userRepository.findById(currentUser.getIdentifier())
                .map(itm -> MapperUtils.nodeToObject(itm, UserDto.class)).get()
                .map(this::mapUserProfile)
                .orElse(new UserProfile());
    }

    public List<CommentDto> getComments() {
        return commentService.getListCommentByCourse(getCourseCompulsoryId());
    }

    public int getTotalComment(){
        return commentService.getTotalComment(getCourseCompulsoryId());
    }

    public String getCommentList() {
        return new Gson().toJson(commentService.getListCommentByCourse(getCourseCompulsoryId()));
    }

    private String getCourseCompulsoryId() {
        return Optional.ofNullable(webContextProvider.get().getParameter("uuid")).orElse("");
    }

    private UserProfile mapUserProfile(UserDto dto) {
        return UserProfile.builder()
                .uuid(dto.getUuid())
                .name(dto.getFullName())
                .image(Optional.ofNullable(dto.getImage()).map(ImageDto::getLink).orElse(StringUtils.EMPTY))
                .headerThumbnail(Optional.ofNullable(dto.getHeaderThumbnail()).map(ImageDto::getLink).orElse(StringUtils.EMPTY))
                .userProfileThumbnail(Optional.ofNullable(dto.getUserProfileThumbnail()).map(ImageDto::getLink).orElse(StringUtils.EMPTY))
                .build();
    }

}
