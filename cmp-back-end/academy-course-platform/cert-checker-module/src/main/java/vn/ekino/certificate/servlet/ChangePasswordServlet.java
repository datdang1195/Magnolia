package vn.ekino.certificate.servlet;

import com.google.gson.Gson;
import info.magnolia.cms.security.User;
import info.magnolia.cms.util.RequestDispatchUtil;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.enumeration.CMPMessage;
import vn.ekino.certificate.model.data.UserProfile;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class ChangePasswordServlet extends HttpServlet {
    private final CertificateServicesModule certificateServicesModule;

    @Inject
    public ChangePasswordServlet(CertificateServicesModule certificateServicesModule) {
        this.certificateServicesModule = certificateServicesModule;
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        var currentPassword = request.getParameter("currentPassword");
        var newPassword = request.getParameter("password");
        String currentPage = request.getParameter("currentPage");
        String targetPage = request.getParameter("targetPage");
        User user = MgnlContext.getUser();
        String pwd = user.getPassword();
        if (!BCrypt.checkpw(currentPassword, pwd)) {
            RequestDispatchUtil.dispatch(String.format("redirect:%s?error", currentPage), request, response);
        } else {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(certificateServicesModule.getAuthorPath() + "/.rest/certChecker/v1/changePassword");
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");

                UserProfile model = UserProfile.builder()
                        .uuid(MgnlContext.getUser().getIdentifier())
                        .pwd(newPassword)
                        .build();

                String input = new Gson().toJson(model);

                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                conn.getInputStream();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    RequestDispatchUtil.dispatch(String.format("redirect:%s?code=%s&changePassMode=true",
                            targetPage,
                            CMPMessage.CHANGE_PASSWORD_SUCCESS.getCode()), request, response);
                }
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
}
