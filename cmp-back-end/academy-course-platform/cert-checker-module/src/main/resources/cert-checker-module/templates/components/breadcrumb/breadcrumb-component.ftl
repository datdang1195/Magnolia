[#assign breadcrumbList = model.getBreadcrumbData()!""]

<div class="breadcrumb">
    [#assign parent = breadcrumbList['parent']]
    [#assign child = breadcrumbList['child']!]
    <ul class="breadcrumb-list">
        <li class="breadcrumb-item-root">
            [#list parent as key, value]
                [#if child?size > 0]
                    <a href="/${key}">${value}</a>
                    [#else ]
                        <span>${value}</span>
                [/#if]
            [/#list]
        </li>
        [#if child?has_content]
            [#assign childCount = child?size]
            [#list child?keys as key]
                <li class="breadcrumb-item" >
                [#if key?index != childCount - 1]
                    <a href="/${key}">${child[key]}</a>
                    [#else ]
                        <span>${child[key]}</span>
                [/#if]
                </li>
            [/#list]
        [/#if]
    </ul>
</div>
