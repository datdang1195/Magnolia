package vn.ekino.certificate.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import info.magnolia.module.mail.MailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.UserDto;
import vn.ekino.certificate.dto.enumeration.EnrollProgramStatusEnum;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
public class MailService {
    private final CertificateServicesModule certificateServicesModule;
    private final CertificateServicesModule servicesModule;

    private static final String REPLY_TO = "replyTo";

    @Inject
    public MailService(CertificateServicesModule certificateServicesModule, CertificateServicesModule servicesModule) {
        this.certificateServicesModule = certificateServicesModule;
        this.servicesModule = servicesModule;
    }

    public void sendMail(UserDto userDto, String enrollProgramStatus) {
        try {
            Map<String, Object> params = initMailConfigs(userDto,
                    " [ekino.Academy] Your enrollment has been " + enrollProgramStatus);
            if (enrollProgramStatus.equals(EnrollProgramStatusEnum.REFUSED.getStatus())) {
                params.put(MailTemplate.MAIL_BODY, buildRefusedMailBody(userDto));
            } else {
                params.put(MailTemplate.MAIL_BODY, buildMailBody(userDto));
            }
            sendMailWithSendGrid(params);
        } catch (Exception e) {
            log.error("Unable to send email.", e);
        }
    }

    public void sendForgotPasswordMail(UserDto userDto, String code, String resetPasswordPageLink) {
        try {
            Map<String, Object> params = initMailConfigs(userDto, " [ekino.Academy] Forgot Your Password");
            params.put(MailTemplate.MAIL_BODY, buildForgotMailBody(userDto, code, resetPasswordPageLink));
            sendMailWithSendGrid(params);
        } catch (Exception e) {
            log.error("Unable to send email.", e);
        }
    }

    public void sendNotificationMailToAdmin(String userEnrolMail, String enrolProgramName) {
        try {
            Map<String, Object> configs = new HashMap<>();
            configs.put(MailTemplate.MAIL_TO, certificateServicesModule.getAdminEmail());
            configs.put(MailTemplate.MAIL_FROM, certificateServicesModule.getAdminEmail());
            configs.put("replyTo", certificateServicesModule.getEmailReplyTo());
            configs.put(MailTemplate.MAIL_CONTENT_TYPE, "text/html");
            configs.put(MailTemplate.MAIL_SUBJECT, "[ekino Academy] New enrollment");
            configs.put(MailTemplate.MAIL_BODY, buildNotificationMailBody(userEnrolMail, enrolProgramName));

            sendMailWithSendGrid(configs);
        } catch (Exception e) {
            log.error("Unable to send email.", e);
        }
    }

    private void sendMailWithSendGrid(Map<String, Object> map) {
        String mailFrom = MapUtils.getString(map, MailTemplate.MAIL_FROM);
        String senderName = certificateServicesModule.getSenderName();
        String mailTo = MapUtils.getString(map, MailTemplate.MAIL_TO);
        String replyTo = MapUtils.getString(map, REPLY_TO);
        String contentType = MapUtils.getString(map, MailTemplate.MAIL_CONTENT_TYPE);
        String mailSubject = MapUtils.getString(map, MailTemplate.MAIL_SUBJECT);
        String mailBody = MapUtils.getString(map, MailTemplate.MAIL_BODY);

        var from = new Email(mailFrom, senderName);
        var to = new Email(mailTo);
        Content content = new Content(contentType, mailBody);
        Mail mail = new Mail(from, mailSubject, to, content);
        if (replyTo != null) {
            mail.setReplyTo(new Email(replyTo));
        }
        SendGrid sg = new SendGrid(certificateServicesModule.getSendGridApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException e) {
            log.error("Unable to send email.", e);
        }
    }

    private Map<String, Object> initMailConfigs(UserDto userDto, String mailSubject) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(MailTemplate.MAIL_TO, userDto.getEmail());
        configs.put(MailTemplate.MAIL_FROM, certificateServicesModule.getAdminEmail());
        configs.put("replyTo", certificateServicesModule.getEmailReplyTo());
        configs.put(MailTemplate.MAIL_CONTENT_TYPE, "text/html");
        configs.put(MailTemplate.MAIL_SUBJECT, mailSubject);
        return configs;
    }

    private String buildMailBody(UserDto userDto) {
        String loginPath = servicesModule.getServerPath() + servicesModule.getLoginPath();
        StringBuilder mailBodyBuilder = new StringBuilder();
        return mailBodyBuilder
                .append("<html>")
                .append("<div style='width: 740px;'>")
                .append("<p>Hi ")
                .append(userDto.getFullName())
                .append(",</p>")
                .append("<p>Congratulations! Your enrollment to ")
                .append(getProgramInfoByUser(userDto))
                .append(" has been validated. You are now able to access the program's detail</p>")
                .append("<p>Welcome to ekino. Academy!</p>")
                .append("<form style='text-align: center;' action='")
                .append(loginPath)
                .append("' method=\"get\" target=\"_blank\">")
                .append("<button type='submit' style=\"border: 1px solid; width: 388px;height: 60px;width: 388px;height: 60px;border-radius: 30px;background-color: #3a5cac; color:#ffffff;font-size:20px;font-weight:700;letter-spacing:2.22px;text-transform:uppercase;\">login</button>")
                .append("</form>")
                .append("</html>")
                .toString();
    }

    private String buildRefusedMailBody(UserDto userDto) {
        StringBuilder mailBodyBuilder = new StringBuilder();
        return mailBodyBuilder
                .append("<html>")
                .append("<div style='width: 740px;'>")
                .append("<p>Hi ")
                .append(userDto.getFullName())
                .append(",</p>")
                .append("<p>Thank you for your enrollment to ekino Academy - ")
                .append(getProgramInfoByUser(userDto))
                .append(".</p>")
                .append("<p>Unfortunately, we regret to inform that you are not ready yet to participate in the program at this moment.</p>")
                .append("<p>We will have a face-to-face discussion with you to prepare for your readiness in the near future.</p>")
                .append("<p>Sincerely,</p>")
                .append("<p>Ekino Academy</p>")
                .append("</html>")
                .toString();
    }

    private String buildForgotMailBody(UserDto userDto, String code, String resetPasswordPageLink) {
        StringBuilder mailBodyBuilder = new StringBuilder();
        String resetPasswordUrl = servicesModule.getServerPath() + resetPasswordPageLink + "?key=" + code;
        return mailBodyBuilder
                .append("<html>")
                .append("<div style='width: 740px;'>")
                .append("<p>Dear ")
                .append(userDto.getFullName())
                .append(",</p>")
                .append("<p>Did you forget your password?</p>")
                .append("<p>Please click ")
                .append("<a href='")
                .append(resetPasswordUrl)
                .append("' title=\"Go to the reset password page\">here</a>")
                .append(" to change your password.</p>")
                .append("<p>If you don't want to change your password or didn't request this, please ignore and delete this message.</p>")
                .append("<p>Sincerely,</p>")
                .append("<p>Ekino Academy</p>")
                .append("</html>")
                .toString();
    }

    private String buildNotificationMailBody(String userEnrolMail, String enrolProgramName) {
        return new StringBuilder()
                .append("<html>")
                .append("<div style='width: 740px;'>")
                .append("<p>Hello Academy! ")
                .append("<p> ")
                .append(userEnrolMail)
                .append(" has enrolled to ")
                .append(enrolProgramName)
                .append(" program.</p>")
                .append("<p>The enrollment is waiting for your further validation.</p>")
                .append("<p>Sincerely,</p>")
                .append("<p>Ekino Academy</p>")
                .append("</html>")
                .toString();
    }

    private String getProgramInfoByUser(UserDto userDto) {
        String result = StringUtils.EMPTY;
        if (userDto.getProgram() != null) {
            result = userDto.getProgram().getPhase().getNodeName() + "-" + userDto.getProgram().getGroup().getDisplayName();
        }
        return result;
    }
}
