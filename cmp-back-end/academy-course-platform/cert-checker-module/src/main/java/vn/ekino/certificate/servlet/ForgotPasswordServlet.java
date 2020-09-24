package vn.ekino.certificate.servlet;

import com.google.gson.Gson;
import info.magnolia.cms.util.RequestDispatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.dto.enumeration.CMPMessage;
import vn.ekino.certificate.model.data.UserProfile;
import vn.ekino.certificate.repository.UserRepository;
import vn.ekino.certificate.service.MailService;
import vn.ekino.certificate.util.MapperUtils;

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
public class ForgotPasswordServlet extends HttpServlet {
    private final CertificateServicesModule certificateServicesModule;
    public static final String EMPTY_STRING = "";
    private final UserRepository userRepository;
    private final MailService mailService;
    private static final String ROLE = "academy-user-role";

    @Inject
    public ForgotPasswordServlet(CertificateServicesModule certificateServicesModule, UserRepository userRepository,
                                 MailService mailService) {
        this.certificateServicesModule = certificateServicesModule;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        String email = Optional.ofNullable(request.getParameter("emailReset")).orElse(EMPTY_STRING);
        userRepository.findByName(email).ifPresentOrElse(userNode -> {
                    HttpURLConnection conn = null;
                    try {
                        URL url = new URL(certificateServicesModule.getAuthorPath() + "/.rest/certChecker/v1/forgotPassword");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("PUT");
                        conn.setRequestProperty("Content-Type", "application/json; utf-8");
                        conn.setRequestProperty("Accept", "application/json");

                        String code = generateCode();
                        UserProfile model = UserProfile.builder()
                                .uuid(userNode.getIdentifier())
                                .code(code)
                                .build();

                        String input = new Gson().toJson(model);

                        OutputStream os = conn.getOutputStream();
                        os.write(input.getBytes());
                        os.flush();
                        conn.getInputStream();
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            var userDto = MapperUtils.nodeToObject(userNode, UserDto.class).get();
                            mailService.sendForgotPasswordMail(userDto, code, request.getParameter("resetPasswordPage"));
                            RequestDispatchUtil.dispatch(String.format("redirect:%s?code=%s&forgotMode=true",
                                    request.getParameter("successPageForForgot"),
                                    CMPMessage.FORGOT_PASSWORD_SUCCESS.getCode()), request, response);
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
                () -> RequestDispatchUtil.dispatch(String.format("redirect:%s?forgotPasswordErrorCode=%s&usernameInForgotFrm=%s",
                        request.getParameter("currentPageForForgot"),
                        CMPMessage.FORGOT_PASSWORD_FAIL.getCode(), email), request, response));

    }

    private String generateCode() {
        return RandomStringUtils.random(6, true, true);
    }
}
