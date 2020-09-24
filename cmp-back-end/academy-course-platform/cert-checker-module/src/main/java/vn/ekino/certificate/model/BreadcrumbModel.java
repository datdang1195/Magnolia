package vn.ekino.certificate.model;

import info.magnolia.context.WebContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.dto.WebsiteDto;
import vn.ekino.certificate.service.WebsiteService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BreadcrumbModel extends RenderingModelImpl<ConfiguredTemplateDefinition> {

    private final Provider<WebContext> webContextProvider;
    private final WebsiteService websiteService;

    @Inject
    public BreadcrumbModel(Node content,
                           ConfiguredTemplateDefinition definition,
                           RenderingModel<?> parent,
                           Provider<WebContext> webContextProvider,
                           WebsiteService websiteService) {
        super(content, definition, parent);
        this.webContextProvider = webContextProvider;
        this.websiteService = websiteService;
    }

    public Map<String, Object> getBreadcrumbData() {
        Map<String, Object> res = new LinkedHashMap<>();
        String currentUrl = webContextProvider.get().getRequest().getRequestURI().replace(".html", "");
        String[] paths = StringUtils.split(currentUrl, "/");

        WebsiteDto parentWeb = websiteService.getWebsiteByPath(paths[0]).get();
        Map<String, Object> parent = new LinkedHashMap<>();
        parent.put(paths[0], parentWeb.getTitle());
        res.put("parent", parent);

        if (paths.length > 1) {
            Map<String, Object> child = new LinkedHashMap<>();
            String path = paths[0];
            int size = paths.length;
            for (int i = 1; i < size; i++) {
                path += "/" + paths[i];
                Optional<WebsiteDto> websiteDto = websiteService.getWebsiteByPath(path);
                if (websiteDto.isPresent()) {
                    child.put(path, websiteDto.get().getTitle());
                }
            }
            res.put("child", child);
        }
        return res;
    }
}
