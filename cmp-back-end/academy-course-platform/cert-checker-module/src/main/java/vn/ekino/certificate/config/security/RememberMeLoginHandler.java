package vn.ekino.certificate.config.security;

import info.magnolia.cms.security.SecurityUtil;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.auth.login.LoginHandlerBase;
import info.magnolia.cms.security.auth.login.LoginResult;
import info.magnolia.context.MgnlContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.service.AuthenticateService;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Stream;

import static vn.ekino.certificate.util.Constants.*;

/**
 * This class handle login if the request has remember me cookie, then login to system automatically.
 * Token inside remember me cookie will include:
 * username + ":" + expirationTime + ":" + md5Hex(username + ":" + expirationTime + ":" password + ":" + key)
 */
@Slf4j
public class RememberMeLoginHandler extends LoginHandlerBase {

    private final AuthenticateService authenticateService;
    private final CertificateServicesModule certificateServicesModule;

    @Inject
    public RememberMeLoginHandler(AuthenticateService authenticateService,
                                  CertificateServicesModule certificateServicesModule) {
        this.authenticateService = authenticateService;
        this.certificateServicesModule = certificateServicesModule;
    }

    @Override
    public LoginResult handle(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> cookieAuth =
                Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                        .filter(cookie -> COOKIE_AUTH_NAME.equals(cookie.getName()))
                        .findFirst();

        if (cookieAuth.isPresent()) {
            log.debug("login via remember me cookie...");
            String[] credentials = getDecodedCredentials(cookieAuth.get().getValue()).split(":");
            String username = credentials[0];
            String expirationTime = credentials[1];

            String decryptedMessage = SecurityUtil.decrypt(credentials[2]);
            String[] messages = decryptedMessage.split(":");

            if (!username.equals(messages[0]) ||
                    !expirationTime.equals(messages[1]) ||
                    !PRIVATE_KEY.equals(messages[3]) ||
                    System.currentTimeMillis() - Long.parseLong(expirationTime) > 0) {
                cookieAuth.get().setValue(null);
                cookieAuth.get().setMaxAge(0);
                cookieAuth.get().setPath("/");
                cookieAuth.get().setDomain(certificateServicesModule.getDomainName());
                response.addCookie(cookieAuth.get());

                return LoginResult.NOT_HANDLED;
            }

            String password = messages[2];
            Optional<User> existingUser = MgnlContext.doInSystemContext(
                    () -> authenticateService.getExistingUser(username));
            if (existingUser.isPresent() && password.equals(existingUser.map(User::getPassword).orElse(""))) {
                return new LoginResult(
                        LoginResult.STATUS_SUCCEEDED,
                        authenticateService.initializeSubject(existingUser.get()));
            }
        }

        return LoginResult.NOT_HANDLED;
    }

    private static String getDecodedCredentials(String credentials) {
        return (new String(Base64.decodeBase64(credentials.getBytes())));
    }

}
