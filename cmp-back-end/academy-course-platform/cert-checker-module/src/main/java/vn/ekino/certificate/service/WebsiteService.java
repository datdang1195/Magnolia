package vn.ekino.certificate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import vn.ekino.certificate.dto.WebsiteDto;
import vn.ekino.certificate.repository.WebsiteRepository;
import vn.ekino.certificate.util.MapperUtils;
import vn.ekino.certificate.util.NodeUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class WebsiteService {
    private final WebsiteRepository websiteRepository;

    @Inject
    public WebsiteService(WebsiteRepository websiteRepository){
        this.websiteRepository = websiteRepository;
    }

    public List<String> findAllWebsiteUrl(){
        return websiteRepository.findAll().stream()
                .map(NodeUtils::getNodePath)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    public Optional<WebsiteDto> getWebsiteByPath(String path) {
        Optional<Node> websiteNode = websiteRepository.getWebsiteByPath(path);
        if (websiteNode.isPresent()) {
            return MapperUtils.nodeToObject(websiteNode.get(), WebsiteDto.class);
        }
        return Optional.empty();
    }
}
