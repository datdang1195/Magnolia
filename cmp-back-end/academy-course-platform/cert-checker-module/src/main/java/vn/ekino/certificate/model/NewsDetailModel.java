package vn.ekino.certificate.model;

import info.magnolia.context.WebContext;
import info.magnolia.link.LinkException;
import info.magnolia.link.LinkUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.NewsDto;
import vn.ekino.certificate.service.NewsService;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class NewsDetailModel extends BaseModel {

    private final NewsService newsService;
    private final Provider<WebContext> webContextProvider;

    @Inject
    public NewsDetailModel(Node content,
                           ConfiguredTemplateDefinition definition,
                           RenderingModel<?> parent,
                           CertificateServicesModule servicesModule,
                           NewsService newsService,
                           Provider<WebContext> webContextProvider) {
        super(content, definition, parent, servicesModule);
        this.newsService = newsService;
        this.webContextProvider = webContextProvider;
    }

    public NewsDto getCurrentNews() {

        NewsDto result = getNewsByUuid(getUuidParam());

        Optional.ofNullable(result).ifPresent(news -> {
                    try {
                        news.setContent(LinkUtil.convertLinksFromUUIDPattern(news.getContent()));
                        news.setSubtitle(LinkUtil.convertLinksFromUUIDPattern(news.getSubtitle()));
                    } catch (LinkException e) {
                        log.warn("Error when reconvert link: {}", e.getMessage());
                    }
                });

        return result;

    }

    public String formatDate(LocalDateTime localDateTime){
        return Optional.ofNullable(localDateTime).map(time -> localDateTime.format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL)).orElse("");

    }

    private NewsDto getNewsByUuid(String uuid) {
        return newsService.getNewsByUuid(uuid).orElse(null);
    }

    private String getUuidParam() {
        return Optional.ofNullable(webContextProvider.get().getParameter("uuid")).orElse("");
    }
}
