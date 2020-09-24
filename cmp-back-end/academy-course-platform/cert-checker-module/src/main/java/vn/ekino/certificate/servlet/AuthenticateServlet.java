package vn.ekino.certificate.servlet;

import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportBase;
import info.magnolia.cms.security.SecurityUtil;
import info.magnolia.cms.security.auth.callback.CredentialsCallbackHandler;
import info.magnolia.cms.security.auth.callback.PlainTextCallbackHandler;
import info.magnolia.cms.security.auth.login.LoginResult;
import info.magnolia.cms.util.RequestDispatchUtil;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.security.auth.Subject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Stream;

import static info.magnolia.cms.security.LogoutFilter.PARAMETER_LOGOUT;
import static vn.ekino.certificate.util.Constants.*;

@Slf4j
public class AuthenticateServlet extends HttpServlet {
    private static final String REALM = "admin";
    private static final String JAAS_CHAIN = SecuritySupportBase.DEFAULT_JAAS_LOGIN_CHAIN;
    private final Provider<SecuritySupport> securitySupportProvider;
    private final CertificateServicesModule certificateServicesModule;

    @Inject
    public AuthenticateServlet(Provider<SecuritySupport> securitySupportProvider,
                               CertificateServicesModule certificateServicesModule) {
        this.securitySupportProvider = securitySupportProvider;
        this.certificateServicesModule = certificateServicesModule;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("emailLogin");
        String password = request.getParameter("passwordLogin");
        String prePage = (String) request.getSession().getAttribute("referer");

        CredentialsCallbackHandler callbackHandler = new PlainTextCallbackHandler(username, password.toCharArray(), REALM);
        LoginResult loginResult = securitySupportProvider.get().authenticate(callbackHandler, JAAS_CHAIN);

        if (loginResult.getSubject() != null && (loginResult.getStatus() == LoginResult.STATUS_SUCCEEDED ||
                loginResult.getStatus() == LoginResult.STATUS_SUCCEEDED_REDIRECT_REQUIRED)) {
            Subject subject = loginResult.getSubject();
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }
            MgnlContext.login(subject);

            if ("on".equals(request.getParameter("rememberMe"))) {
                long expiringTime = System.currentTimeMillis() + COOKIE_AUTH_EXPIRATION_TIME;

                String encryptedMessage = SecurityUtil.encrypt(
                        String.format("%s:%s:%s:%s",
                                MgnlContext.getUser().getName(),
                                expiringTime,
                                MgnlContext.getUser().getPassword(),
                                PRIVATE_KEY));

                String messages = String.format("%s:%s:%s",
                        MgnlContext.getUser().getName(),
                        expiringTime,
                        encryptedMessage);

                Cookie authCookie = new Cookie(COOKIE_AUTH_NAME, Base64.encodeBase64String(messages.getBytes()));
                authCookie.setHttpOnly(true);
                authCookie.setMaxAge((int) (COOKIE_AUTH_EXPIRATION_TIME / 1000));
                authCookie.setPath("/");
                authCookie.setDomain(certificateServicesModule.getDomainName());

                response.addCookie(authCookie);
            }

            String targetPage = StringUtils.isEmpty(prePage) ? request.getParameter("successPage") : prePage;
            RequestDispatchUtil.dispatch(String.format("redirect:%s",targetPage), request, response);
        } else {
            log.error("Invalid login, because: {}", loginResult.getStatus(), loginResult.getLoginException());
            String targetPage = request.getParameter("currentPage");
            RequestDispatchUtil.dispatch(
                    String.format("redirect:%s?errorCode=%s&username=%s",
                            targetPage, LoginResult.STATUS_FAILED, username),
                    request, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        if (null != request.getParameter(PARAMETER_LOGOUT)) {
            Optional<Cookie> cookieAuth =
                    Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                            .filter(cookie -> COOKIE_AUTH_NAME.equals(cookie.getName()))
                            .findFirst();

            if (cookieAuth.isPresent()) {
                cookieAuth.get().setValue(null);
                cookieAuth.get().setMaxAge(0);
                cookieAuth.get().setPath("/");
                cookieAuth.get().setDomain(certificateServicesModule.getDomainName());
                response.addCookie(cookieAuth.get());
            }

            RequestDispatchUtil.dispatch("redirect:/?mgnlLogout", request, response);
        } else {
            RequestDispatchUtil.dispatch("redirect:/", request, response);
        }
    }
}