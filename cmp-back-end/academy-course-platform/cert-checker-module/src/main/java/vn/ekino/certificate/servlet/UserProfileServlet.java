package vn.ekino.certificate.servlet;

import com.google.gson.Gson;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.model.data.UserProfile;
import vn.ekino.certificate.service.UserProfileService;
import vn.ekino.certificate.util.Constants;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserProfileServlet extends HttpServlet {
    private final UserProfileService userProfileService;
    private final CertificateServicesModule certificateServicesModule;


    @Inject
    public UserProfileServlet(UserProfileService userProfileService, CertificateServicesModule certificateServicesModule) {
        this.userProfileService = userProfileService;
        this.certificateServicesModule = certificateServicesModule;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", MgnlContext.getUser().getIdentifier());
        map.put("file", MapUtils.isNotEmpty(MgnlContext.getPostedForm().getDocuments()) ? MgnlContext.getPostedForm().getDocument("image").getFile() : null);
        HttpURLConnection conn = null;
        try {
            Map<String, String> imageIdMap = userProfileService.saveAvatar(map);
            URL url = new URL(certificateServicesModule.getAuthorPath() + "/.rest/certChecker/v1/profile");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            UserProfile model = UserProfile.builder()
                    .uuid(MgnlContext.getUser().getIdentifier())
                    .name(request.getParameter("name"))
                    .phone(request.getParameter("phone"))
                    .headerThumbnail(imageIdMap.get(Constants.HEADER_THUMBNAIL))
                    .userProfileThumbnail(imageIdMap.get(Constants.USER_PROFILE_THUMBNAIL))
                    .departmentId(request.getParameter("department"))
                    .profileId(request.getParameter("profile"))
                    .build();

//            userProfileService.saveUserProfile(model);

            String input = new Gson().toJson(model);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            conn.getInputStream();
        } catch (IOException e) {
            log.warn("Can't save user profile because {}", e.getMessage());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }


    }
}
