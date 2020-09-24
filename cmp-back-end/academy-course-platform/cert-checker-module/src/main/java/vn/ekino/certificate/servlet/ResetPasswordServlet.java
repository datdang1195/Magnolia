package vn.ekino.certificate.servlet;

import com.google.gson.Gson;
import info.magnolia.cms.util.RequestDispatchUtil;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.enumeration.CMPMessage;
import vn.ekino.certificate.model.data.UserProfile;
import vn.ekino.certificate.repository.UserRepository;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class ResetPasswordServlet extends HttpServlet {

    public static final String EMPTY_STRING = "";
    private final UserRepository userRepository;
    private static final String ROLE = "academy-user-role";
    private final CertificateServicesModule certificateServicesModule;

    @Inject
    public ResetPasswordServlet(UserRepository userRepository, CertificateServicesModule certificateServicesModule) {
        this.userRepository = userRepository;
        this.certificateServicesModule = certificateServicesModule;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String code = Optional.ofNullable(request.getParameter("key")).orElse(EMPTY_STRING);
        String passwordReset = Optional.ofNullable(request.getParameter("passwordReset")).orElse(EMPTY_STRING);
        userRepository.findByResetPasswordCode(code).ifPresentOrElse(userNode -> {
                    HttpURLConnection conn = null;
                    try {
                        URL url = new URL(certificateServicesModule.getAuthorPath() + "/.rest/certChecker/v1/resetPassword");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("PUT");
                        conn.setRequestProperty("Content-Type", "application/json; utf-8");
                        conn.setRequestProperty("Accept", "application/json");

                        UserProfile model = UserProfile.builder()
                                .uuid(userNode.getIdentifier())
                                .pwd(passwordReset)
                                .build();

                        String input = new Gson().toJson(model);

                        OutputStream os = conn.getOutputStream();
                        os.write(input.getBytes());
                        os.flush();
                        conn.getInputStream();
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            RequestDispatchUtil.dispatch(String.format("redirect:%s?code=%s&resetMode=true",
                                    request.getParameter("successPageForReset"),
                                    CMPMessage.RESET_PASSWORD_SUCCESS.getCode()), request, response);
                        }
                    } catch (IOException | RepositoryException e) {
                        log.warn("Can't send mail forgot password because {}", e.getMessage());
                    }
                    finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                },
                () -> RequestDispatchUtil.dispatch(String.format("redirect:%s?resetPasswordErrorCode=%s",
                        request.getParameter("currentPageForReset"),
                        CMPMessage.FORGOT_PASSWORD_FAIL.getCode()), request, response));
    }
}
