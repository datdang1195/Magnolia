package vn.ekino.certificate.config.permission;

import info.magnolia.cms.filters.AbstractMgnlFilter;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.auth.ACL;
import info.magnolia.cms.util.RequestDispatchUtil;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.repository.*;
import vn.ekino.certificate.service.PhaseService;
import vn.ekino.certificate.service.WebsiteService;
import vn.ekino.certificate.util.Constants;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AcademyPermissionFilter extends AbstractMgnlFilter {

    private static final String MAGNOLIA_ADMINCENTRAL_HEARTBEAT = "/.magnolia/admincentral/HEARTBEAT/";
    private static final String HTML = ".html";
    private static final String URI = "uri";
    private static final String SLASH = "/";
    private static final String REGEX = "^[\\/][^\\/?]*";

    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    private final NewsRepository newsRepository;
    private final OJTProjectRepository ojtProjectRepository;
    private final ProgramCourseRepository programCourseRepository;
    private final PhaseService phaseService;
    private final WebsiteService websiteService;

    private final MgnlRoleManager mgnlRoleManager;
    private final CertificateServicesModule certificateServicesModule;

    @Inject
    public AcademyPermissionFilter(ProgramRepository programRepository,
                                   CourseRepository courseRepository,
                                   NewsRepository newsRepository,
                                   OJTProjectRepository ojtProjectRepository,
                                   ProgramCourseRepository programCourseRepository,
                                   PhaseService phaseService,
                                   MgnlRoleManager mgnlRoleManager,
                                   WebsiteService websiteService,
                                   CertificateServicesModule certificateServicesModule) {
        this.programRepository = programRepository;
        this.courseRepository = courseRepository;
        this.newsRepository = newsRepository;
        this.ojtProjectRepository = ojtProjectRepository;
        this.programCourseRepository = programCourseRepository;
        this.phaseService = phaseService;

        this.mgnlRoleManager = mgnlRoleManager;
        this.websiteService = websiteService;
        this.certificateServicesModule = certificateServicesModule;
    }

    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (!MAGNOLIA_ADMINCENTRAL_HEARTBEAT.equals(request.getRequestURI())) {
            User currentUser = MgnlContext.getUser();

            Set<String> deniedUrls = getDenyUrls(currentUser.getAllRoles());

            String requestUri = request.getQueryString() == null
                    ? request.getRequestURI().replace(HTML, StringUtils.EMPTY)
                    : request.getRequestURI().replace(HTML, StringUtils.EMPTY).concat("?" + request.getQueryString());

            ArrayList<String> sectionsOfUri = new ArrayList(Arrays.asList(StringUtils.split(request.getRequestURI(), SLASH)));
            if (sectionsOfUri.size() > 1 && certificateServicesModule.getAccessDeniedPage().equals(sectionsOfUri.get(sectionsOfUri.size() - 1))){
                requestUri = SLASH.concat(certificateServicesModule.getAccessDeniedPage());
            }

            String firstSectionOfUri = sectionsOfUri.size() == 0 ? SLASH : SLASH.concat(sectionsOfUri.get(0));

            if (deniedUrls.contains(firstSectionOfUri)) {
                String redirectPage = Constants.ANONYMOUS_ROLE.equals(currentUser.getName())
                        ? certificateServicesModule.getLoginPath()
                        : certificateServicesModule.getAccessDeniedPage();

                request.getSession().setAttribute("referer", requestUri);

                response.sendRedirect(redirectPage);

                // If url is long (like: 'http://10.161.207.218:8080/course-detail/All-kinds-of-Session', ...)
                if (!deniedUrls.contains(requestUri)) {
                    // handle redirect to previous page after login successfully for long url
                    handleLongRequest(request, response);
                }
            } else {
                // handle for long urls (like http://10.161.207.218:8080/course-detail/All-kinds-of-Session) when a user
                // has already logged in.
                handleLongRequest(request, response);
            }
        }
        chain.doFilter(request, response);
    }

    private void handleLongRequest(HttpServletRequest request, HttpServletResponse response) {
        if (certificateServicesModule.getLoginPath().equals(request.getRequestURI())) {
            request.getSession().setAttribute("referer", request.getSession().getAttribute("referer"));
        }

        ArrayList<String> uris = new ArrayList(Arrays.asList(StringUtils.split(request.getRequestURI(), "/")));
        List<String> websiteUrls = websiteService.findAllWebsiteUrl();

        if (uris.size() > 1 && request.getQueryString() == null &&
                !MAGNOLIA_ADMINCENTRAL_HEARTBEAT.equals(request.getRequestURI()) &&
                websiteUrls.contains(getModifiedCurrentURL(request))) {
            String redirectUrl = StringUtils.EMPTY;
            // handle for urls like: http://10.161.207.218:8080/course-detail/All-kinds-of-Session
            try {
                if (uris.size() == 2 && !websiteUrls.contains(SLASH.concat(uris.get(1)))) {
                    if (certificateServicesModule.getProgramsUrl().equals(SLASH.concat(uris.get(0)))) {
                        redirectUrl = programDetailLink(uris.get(1) != null ? uris.get(1) : StringUtils.EMPTY);
                    } else if (certificateServicesModule.getCoursesUrl().equals(SLASH.concat(uris.get(0)))) {
                        redirectUrl = courseDetailLink(uris.get(1) != null ? uris.get(1) : StringUtils.EMPTY);
                    } else if (certificateServicesModule.getNewsUrl().equals(SLASH.concat(uris.get(0)))) {
                        redirectUrl = newsDetailLink(uris.get(1) != null ? uris.get(1) : StringUtils.EMPTY);
                    } else if (certificateServicesModule.getEnrolUrl().equals(SLASH.concat(uris.get(0)))) {
                        redirectUrl = enrolLink(uris.get(1) != null ? uris.get(1) : StringUtils.EMPTY);
                    } else {
                        redirectUrl = request.getRequestURI();
                    }
                } else if (uris.size() > 2 && !websiteUrls.contains(SLASH.concat(uris.get(uris.size() - 1)))) { //if (uris.size() == 3) {
                    // handle for urls like: http://10.161.207.218:8080/my-progress/onJobTraining/Charity-web-development-2019
                    if (certificateServicesModule.getOnJobTrainingUrl().equals(SLASH.concat(uris.get(1)))) {
                        redirectUrl = OJTProjectLink(uris);
                    }
                    //  handle for urls like: http://10.161.207.218:8080/course-status/course-detail/basic-of-http-protocol
                    else if (certificateServicesModule.getCourseDetailUrl().equals(SLASH.concat(uris.get(uris.size() - 2)))) {
                        redirectUrl = subCourseDetailLink(uris);
                    } else {
                        redirectUrl = request.getRequestURI();
                    }
                } else {
                    redirectUrl = request.getRequestURI();
                }
            } catch (Exception e) {
                log.error("Error in {} method cause {}: ", e.getStackTrace()[0].getMethodName(), e.getMessage());
                RequestDispatchUtil.dispatch(String.format("forward:/%s", certificateServicesModule.getNotFoundPage()), request, response);
            }

            if (StringUtils.isNotEmpty(redirectUrl)) {
                RequestDispatchUtil.dispatch(redirectUrl, request, response);
            } else {
                RequestDispatchUtil.dispatch(String.format("forward:%s", certificateServicesModule.getNotFoundPage()), request, response);
            }
        }
        request.getSession().setAttribute("referer", request.getSession().getAttribute("referer"));
    }


    private Set<String> getDenyUrls(Collection<String> roles) {
        Set<String> denyUrls = new HashSet<>();
        Set<String> permitUrls = new HashSet<>();

        List<String> allWebsiteUrl = websiteService.findAllWebsiteUrl();

        roles.forEach(roleName -> {
            ACL aclUrl = mgnlRoleManager.getACLs(roleName).get(URI);
            if (Objects.nonNull(aclUrl)) {
                for (Permission permission : aclUrl.getList()) {
                    if (allWebsiteUrl.contains(permission.getPattern().getPatternString())) {
                        if (permission.getPermissions() == 0) {
                            denyUrls.add(permission.getPattern().getPatternString());
                        } else {
                            permitUrls.add(permission.getPattern().getPatternString());
                        }
                    }
                }
            }
        });
        return denyUrls.stream().filter(url -> !permitUrls.contains(url)).collect(Collectors.toSet());
    }

    private String getModifiedCurrentURL(HttpServletRequest request) {
        List<String> cur = new ArrayList(Arrays.asList(StringUtils.split(request.getRequestURI(), "/")));
        if (cur.size() > 1) {
            cur.remove(cur.size() - 1);
            return SLASH.concat(cur.get(0));
        }
        return cur.size() > 0 ? SLASH.concat(cur.get(0)) : StringUtils.EMPTY;
    }

    private String programDetailLink(String programPath) throws RepositoryException {
        Optional<Node> node = programRepository.getProgramByPath(programPath);
        if (node.isPresent()) {
            return String.format("forward:%s?selectedProgram=%s",
                    certificateServicesModule.getProgramsUrl().concat(certificateServicesModule.getProgramDetailUrl()), node.get().getIdentifier());
        }
        return StringUtils.EMPTY;
    }

    private String courseDetailLink(String coursePath) throws RepositoryException {
        coursePath = coursePath.trim().replace("-", " ");
        return setURLByCourse(courseRepository.findByName(coursePath),
                Collections.singletonList(certificateServicesModule.getCoursesUrl().concat(SLASH)
                        .concat(certificateServicesModule.getCourseDetailUrl())), false);
    }

    private String newsDetailLink(String newsPath) throws RepositoryException {
        Optional<Node> node = newsRepository.findByPath(newsPath);
        if (node.isPresent()) {
            return String.format("forward:%s?uuid=%s", certificateServicesModule.getNewsUrl()
                    .concat(certificateServicesModule.getNewsDetailUrl()), node.get().getIdentifier());
        }
        return StringUtils.EMPTY;
    }

    private String enrolLink(String programPath) throws RepositoryException {
        Optional<Node> node = programRepository.getProgramByPath(programPath);
        if (node.isPresent()) {
            String phaseId = node.get().getProperty("phase").getString();
            return String.format("forward:%s?year=%s&phaseId=%s&programId=%s", certificateServicesModule.getEnrolUrl(),
                    phaseService.getPhaseById(phaseId).getStartDate().getYear(),
                    phaseId,
                    node.get().getIdentifier());
        }
        return StringUtils.EMPTY;
    }

    private String OJTProjectLink(List<String> uris) throws RepositoryException {
        String OJTPProjectName = uris.get(uris.size() - 1);
        Optional<Node> node = ojtProjectRepository.findByURIName(OJTPProjectName);
        if (node.isPresent()) {
            return String.format("forward:%s?uuid=%s", modifyRedirectURI(uris), node.get().getIdentifier());
        }
        return StringUtils.EMPTY;
    }

    private String subCourseDetailLink(List<String> uris) throws RepositoryException {
        String courseName = uris.get(uris.size() - 1);
        Optional<Node> node = courseRepository.findByPath(courseName);
        if(!node.isPresent()){
            courseName = courseName.trim().replace("-", " ");
            node = courseRepository.findByPath(courseName);
        }
        return setURLByCourse(node, uris, true);
    }

    private String setURLByCourse(Optional<Node> courseNode, List<String> uris, boolean hasMore2Params) throws RepositoryException {
        if (courseNode.isPresent()) {
            var courseInProgram = programCourseRepository.findCourseCompulsoryNodeByCourseName(courseNode.get().getIdentifier(), StringUtils.EMPTY);
            if (courseInProgram.isPresent()) {
                return hasMore2Params ? String.format("forward:%s?uuid=%s", modifyRedirectURI(uris), courseInProgram.get().getIdentifier()) :
                        String.format("forward:%s?uuid=%s", certificateServicesModule.getCoursesUrl().concat(SLASH)
                                .concat(certificateServicesModule.getCourseDetailUrl()), courseInProgram.get().getIdentifier());
            }
            return StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    private StringBuilder modifyRedirectURI(List<String> uris) {
        uris.remove(uris.size() - 1);
        StringBuilder redirectURI = new StringBuilder();
        for (String uri : uris) {
            redirectURI.append(SLASH.concat(uri));
        }
        return redirectURI;
    }

}
