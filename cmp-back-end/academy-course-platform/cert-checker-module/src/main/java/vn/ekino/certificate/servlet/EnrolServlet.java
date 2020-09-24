package vn.ekino.certificate.servlet;

import com.google.gson.Gson;
import info.magnolia.cms.util.RequestDispatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.EnrolDto;
import vn.ekino.certificate.dto.ProgramDto;
import vn.ekino.certificate.repository.ProgramRepository;
import vn.ekino.certificate.service.MailService;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.dto.enumeration.CMPMessage;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class EnrolServlet extends HttpServlet {

    private final CertificateServicesModule certificateServicesModule;
    private final ProgramRepository programRepository;
    private final MailService mailService;

    @Inject
    public EnrolServlet(CertificateServicesModule certificateServicesModule,
                        ProgramRepository programRepository,
                        MailService mailService) {
        this.certificateServicesModule = certificateServicesModule;
        this.programRepository = programRepository;
        this.mailService = mailService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String targetPage = request.getParameter("successPage");
        HttpURLConnection conn = null;
        try {
            URL url = new URL(certificateServicesModule.getAuthorPath() + "/.rest/certChecker/v1/enrol");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            String programName = request.getParameter("programName");
            String programId = request.getParameter("program");
            EnrolDto model = EnrolDto.builder()
                    .email(request.getParameter("email"))
                    .username(request.getParameter("username"))
                    .password(request.getParameter("password"))
                    .program(programName)
                    .programId(programId)
                    .build();
            ProgramDto dto = MapperUtils.nodeToObject(programRepository.findById(programId).get(), ProgramDto.class).get();

            String input = new Gson().toJson(model);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            conn.getInputStream();
            if (conn.getResponseCode() == HttpStatus.SC_CREATED) {
                String name = String.format("%s-%s", dto.getPhase().getNodeName(), dto.getGroup().getDisplayName());
                mailService.sendNotificationMailToAdmin(model.getEmail(), name);
                RequestDispatchUtil.dispatch(String.format("redirect:%s?code=%s&programName=%s", targetPage, CMPMessage.ENROL_SUCCESS.getCode(), dto.getPhase().getNodeName() + " - " + dto.getGroup().getNodeName()), request, response);
            } else if (conn.getResponseCode() == HttpStatus.SC_ACCEPTED){
                RequestDispatchUtil.dispatch(String.format("redirect:%s?code=%s", targetPage, CMPMessage.ENROL_REJECT.getCode()), request, response);
            } else {
                RequestDispatchUtil.dispatch(String.format("redirect:%s?code=%s", targetPage, CMPMessage.ENROL_ERROR.getCode()), request, response);
            }

        } catch (IOException e) {
            log.warn("Can't enrol because {}", e.getMessage());
            RequestDispatchUtil.dispatch(String.format("redirect:%s", targetPage), request, response);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
