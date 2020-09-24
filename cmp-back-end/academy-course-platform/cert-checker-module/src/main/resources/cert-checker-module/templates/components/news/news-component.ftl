[#assign quantityOfLatestNews = content.quantityOfLatestNews!5]
[#assign pageSize = content.pageSize!10]
[#assign latestNews = model.getLatestNews(quantityOfLatestNews?number)!]
[#assign allNews = model.getAllNews()!]
[#assign newsPath = model.getParent().getNode().getPath()!"/news"]
[#assign currentPage = model.getCurrentPage()!1]
[#assign totalPage = model.getTotalPage()!1]
[#assign newsDetailPage = content.newsDetailPage!"/news-detail"]
[#assign currentPath = model.getCurrentPath()!"/news"]

<script>
    window.EKINO_ACADEMY['currentPage'] = "news"

    var currentPage = ${model.getCurrentPage()};
    var totalPage = ${model.getTotalPage()};

    window.EKINO_ACADEMY.page['pagination'] = {
        currentPage:currentPage,
        totalPage:totalPage,
    }
</script>

<div class="app-wrapper__news">
    <div class="container-news container">
        <div class="page__title__wrapper">
            <p class="news__title page__title">news</p>
        </div>
        <div class="contentPage">
            <div class="slider__news">
                <p class="slider__label">latest news</p>
                <div class="slider__content js-slider-wrapper">
                    [#list latestNews as news]
                        <div class="slider__item" data-href="${currentPath}/${news.getNodeName()}">
                            <a href="${currentPath}/${news.getNodeName()}" class="news-link"></a>
                            <div class="slider__item__image">
                                [#if news.getDisplayImage()??]
                                    [#assign myMediumRendition = damfn.getRendition(news.getImage(), "medium")!]
                                    [#if myMediumRendition??]
                                        <img src="${myMediumRendition.getLink()}" alt="images_Slider"/>
                                    [/#if]
                                [/#if]
                            </div>
                            <div class="slider__item__text">
                                <h2 class="slider__item__text-title news__item__title">
                                    ${news.getTitle()!}
                                </h2>
                                <div class="slider__item__text-short-text news__short__text js-slider_short_text">
                                    ${news.getSubtitle()!}
                                </div>
                                <p class="news__date">${model.toDateString(news.getDate())}</p>
                            </div>
                        </div>
                    [/#list]
                </div>
            </div>
            <div class="wrapper__listNews">
                [#list allNews as news]
                    <div class="news-item">
                        <a href="${currentPath}/${news.getNodeName()}" class="news-link">
                            <div class="info__label news__item__title js-news-item-title">
                                ${news.getTitle()!}
                            </div>
                            <div class="news__item__wrapper">
                                <div class="news__info">
                                    <div class="info__description">
                                        <div class="news__short__text info__description__text js-slider_short_text">
                                            ${news.getSubtitle()!}
                                        </div>
                                    </div>
                                    <p class="news__date">${model.toDateString(news.getDate())}</p>
                                </div>

                                <div class="news__img">
                                    [#if news.getDisplayImage()??]
                                        [#assign myMediumRendition = damfn.getRendition(news.getImage(), "thumbnail")!]
                                        [#if myMediumRendition??]
                                            <img src="${myMediumRendition.getLink()}" alt="images_Slider"/>
                                        [/#if]
                                    [/#if]
                                </div>
                            </div>
                        </a>
                    </div>
                [/#list]
            </div>
        </div>
        [#if totalPage > 1]
            <div class="news-pagination">
                <div class="pagination__wrapper">
                    [#--first button--]

                    <a
                            [#if currentPage == 1]
                                href="#" style="pointer-events: none;cursor: not-allowed;"
                            [#else]
                                href="${newsPath}?pageNumber=1"
                            [/#if]
                            class="pagination__control js-pagination-control"
                            data-control="frist">&#171;</a>

                    [#--prev button--]
                    <a
                            [#if currentPage > 1]
                                href="${newsPath}?pageNumber=${currentPage - 1}"
                            [#else ]
                                href="#" style="pointer-events: none;cursor: not-allowed;"
                            [/#if]
                            class="pagination__control js-pagination-control"
                            data-control="prev">&#8249;</a>

                    <ul class="pagination__list js-pagination-list"></ul>

                    [#--next button--]
                    <a
                            [#if currentPage < totalPage]
                                href="${newsPath}?pageNumber=${currentPage + 1}"
                            [#else ]
                                href="#" style="pointer-events: none;cursor: not-allowed;"
                            [/#if]
                            class="pagination__control js-pagination-control"
                            data-control="next">&#8250;</a>

                    [#--last button--]
                    <a
                            [#if currentPage == totalPage]
                                href="#" style="pointer-events: none;cursor: not-allowed;"
                            [#else]
                                href="${newsPath}?pageNumber=${totalPage}"
                            [/#if]
                            class="pagination__control js-pagination-control"
                            data-control="last">&#187;</a>
                </div>
            </div>
        [/#if]
    </div>
</div>