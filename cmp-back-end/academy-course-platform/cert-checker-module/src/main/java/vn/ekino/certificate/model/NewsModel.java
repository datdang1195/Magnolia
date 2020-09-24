package vn.ekino.certificate.model;

import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import vn.ekino.certificate.CertificateServicesModule;
import vn.ekino.certificate.dto.NewsDto;
import vn.ekino.certificate.service.NewsService;
import vn.ekino.certificate.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class NewsModel extends BaseModel {

    private final NewsService newsService;
    private final Provider<WebContext> webContextProvider;
    private final CertificateServicesModule certificateServicesModule;

    private final NodeNameHelper nodeNameHelper;

    @Inject
    public NewsModel(Node content,
                     ConfiguredTemplateDefinition definition,
                     RenderingModel<?> parent,
                     CertificateServicesModule servicesModule,
                     NewsService newsService,
                     Provider<WebContext> webContextProvider,
                     CertificateServicesModule certificateServicesModule,
                     NodeNameHelper nodeNameHelper) {
        super(content, definition, parent, servicesModule);
        this.newsService = newsService;
        this.webContextProvider = webContextProvider;
        this.certificateServicesModule = certificateServicesModule;
        this.nodeNameHelper = nodeNameHelper;
    }

    public List<NewsDto> getLatestNews(int quantityOfNews) {
        List<NewsDto> news = newsService.getLatestNews(quantityOfNews);
        news.forEach(newsDto -> newsDto.setNodeName(nodeNameHelper.getValidatedName(newsDto.getNodeName())));
        return news;
    }

    public List<NewsDto> getAllNews() {
        List<NewsDto> news = newsService.getAllNews((getCurrentPage() - 1) * getPagesize(), getPagesize());
        news.forEach(newsDto -> newsDto.setNodeName(nodeNameHelper.getValidatedName(newsDto.getNodeName())));
        return news;
    }

    public int getTotalNews() {
        return newsService.getTotalNews();
    }

    public int getTotalPage() {
        int totalNews = getTotalNews();
        int pageSize = getPagesize();

        return totalNews % pageSize == 0 ? totalNews / pageSize : totalNews / pageSize + 1;
    }

    public Integer getCurrentPage() {
        return Optional.ofNullable(webContextProvider.get().getParameter("pageNumber"))
                .map(Integer::parseInt)
                .orElse(1);
    }

    public String toDateString(LocalDateTime time) {
        return Optional.ofNullable(time)
                .map(localDateTime ->
                        localDateTime.format(TimeUtils.DATE_TIME_FORMATTER_OF_PROGRAM_DETAIL))
                .orElse("");
    }

    private int getQuantityOfLatestNews() {
        return Integer.parseInt(PropertyUtil.getString(content, "quantityOfLatestNews", "5"));
    }

    private int getPagesize() {
        return Integer.parseInt(PropertyUtil.getString(content, "pageSize", "10"));
    }

    public String getCurrentPath() {
        return certificateServicesModule.getNewsUrl();
    }

}
