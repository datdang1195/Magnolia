[#assign news = model.getCurrentNews()!"news not found!"]

[#if news != "news not found!"]
    <div class="newsdetail">
        <div id="loading" class="loading">
            <div class="loading__content">
                <i></i>
            </div>
        </div>
        <div class="newsdetail__container container">
            <div class="page__title__wrapper page__title">${news.getTitle()}</div>
            [#if news.getDate()??]
                <p class="newsdetail__text-date newsdetail__text">
                    ${model.formatDate(news.getDate())}
                </p>
            [/#if]

            <div class="newsdetail__subtitle text_content">${news.getSubtitle()}</div>

            [#if news.getDisplayImage()??]
                [#assign isDisplay = news.getDisplayImage()?then("true", "false")]
                [#if isDisplay == "true"]
                    [#assign myMediumRendition = damfn.getRendition(news.getImage(), "medium")!]
                    [#if myMediumRendition??]
                        <div class="newsdetail__image"
                             style="background-image: url('${myMediumRendition.getLink()}')">
                        </div>
                    [/#if]
                [/#if]

            [/#if]
            <div class="newsdetail__content text_content">

                    ${news.getContent()}

            </div>

            [#if news.getAuthor()??]
                <p class="newsdetail__text-author newsdetail__text ">
                    ${news.getAuthor()}
                </p>
            [/#if]

        </div>
    </div>
[#else ]
    <div class="newsdetail">
        <h2>News not found!</h2>
    </div>
[/#if]

