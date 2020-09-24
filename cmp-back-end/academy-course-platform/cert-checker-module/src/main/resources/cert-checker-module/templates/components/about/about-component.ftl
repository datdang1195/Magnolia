<div class="app-wrapper__courseDetail">
    <div class="container-courseDetail">
        <div class="courseDetail">
            <p class="courseDetail__tilte page__title">About Academy</p>
        </div>
        <div class="contentPage">

            [#list cmsfn.children(content, "mgnl:contentNode") as child ]
                <div class="section-info">
                    <div class="section__lable" style="margin-bottom: 20px;">${child.subtitle!}</div>
                    <div class="section__description">
                        [#if child.content?has_content]
                            ${cmsfn.decode(child).content}
                        [/#if]
                    </div>
                </div>
            [/#list]

        </div>
    </div>
</div>