package vn.ekino.certificate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is optional and represents the configuration for the cert-checker-module module.
 * By exposing simple getter/setter/adder methods, this bean can be configured via content2bean
 * using the properties and node from <tt>config:/modules/cert-checker-module</tt>.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 * See https://documentation.magnolia-cms.com/display/DOCS/Module+configuration for information about module configuration.
 */
@Getter
@Setter
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateServicesModule {
    /* you can optionally implement info.magnolia.module.ModuleLifecycle */
    String serverPath = "http://localhost:8080";
    String adminEmail = "test.academy.ekino@gmail.com";
    String authorPath = "http://localhost:8080";
    String loginPath = "/login";
    int courseListPageSize = 10;
    String domainName = "localhost";
    String emailReplyTo = "ekino.academy@gmail.com";
    String homePage = "/general";
    String participantRole = "participant";
    String accessDeniedPage = "/access-denied";
    String notFoundPage = "/not-found";
    String unauthorizedAccessPage = "/unauthorized-access";
    String programsUrl = "/general";
    String programDetailUrl = "/program-detail";
    String programStatusUrl = "/program-status";
    String coursesUrl = "/my-course";
    String courseDetailUrl = "/course-detail";
    String courseStatusUrl = "/course-status";
    String newsUrl = "/news";
    String newsDetailUrl = "/news-detail";
    String onJobTrainingUrl = "/On-Job-Training";
    String enrolUrl = "/enrol";
    String myProgressUrl = "/my-progress";
    String senderName = "ekino academy";
    String sendGridApiKey = "";
    String redisServer = "redis://127.0.0.1:6379";

}
