package vn.ekino.certificate.service;

import com.google.common.collect.Lists;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.NodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import vn.ekino.certificate.dto.NewsDto;
import vn.ekino.certificate.repository.NewsRepository;
import vn.ekino.certificate.util.MapperUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final NodeNameHelper nodeNameHelper;
    private int totalNews;

    @Inject
    public NewsService(NewsRepository newsRepository, NodeNameHelper nodeNameHelper) {
        this.newsRepository = newsRepository;
        this.nodeNameHelper = nodeNameHelper;
    }

    public Optional<NewsDto> getNewsByUuid(String uuid) {
        Optional<Node> nodeOptional = newsRepository.findById(uuid);
        if (nodeOptional.isPresent()) {
            Node newsNode = nodeOptional.get();
            return MapperUtils.nodeToObject(newsNode, NewsDto.class);
        }
        return Optional.empty();
    }

    public List<NewsDto> getLatestNews(int quantityOfNews) {
        List<NewsDto> result = new ArrayList<>();
        List<Node> newsNodes = newsRepository.getLatestNews(quantityOfNews);

        newsNodes.forEach(newsNode
                -> MapperUtils.nodeToObject(newsNode, NewsDto.class)
                .ifPresent(result::add));

        updateTotalNews(newsNodes);

        return result;
    }

    private void updateTotalNews(List<Node> newsNodes) {
        if (CollectionUtils.isNotEmpty(newsNodes)) {
            try {
                setTotalNews(Lists.newArrayList(NodeUtil.getSiblings(newsNodes.get(0))).size());
            } catch (RepositoryException e) {
                setTotalNews(0);
                log.warn("Cannot get siblings node because: {}", e.getMessage());
            }

        }
    }

    public List<NewsDto> getAllNews(int offset, int limit) {
        List<NewsDto> result = new ArrayList<>();
        newsRepository.getAllNews(offset, limit)
                .forEach(newsNode
                        -> MapperUtils.nodeToObject(newsNode, NewsDto.class)
                        .ifPresent(result::add));
        return result;
    }

    public int getTotalNews() {
        return totalNews;
    }

    private void setTotalNews(int totalNews) {
        this.totalNews = totalNews;
    }

}
