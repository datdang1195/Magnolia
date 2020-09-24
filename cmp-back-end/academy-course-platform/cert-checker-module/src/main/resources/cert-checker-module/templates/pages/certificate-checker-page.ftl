[#assign title = content.title!"Title not found."]
[#assign certificateImageUUID = content.certificateImage!"Cannot found uuid of certificate image."]

<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">

    [#list def.cssFiles as cssFile]
        <link rel="stylesheet" href="${cssFile.link}">
    [/#list]

    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/common.css">

    <script type="module">
        [#list def.jsFiles as jsFile]
            import '${jsFile.link}';
        [/#list]
    </script>

    [@cms.page /]
</head>
<body>

<!-- Header -->
<header class="app-header">
    <a href="https://www.ekino.vn">
        <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/logo-ekino.png" alt="logo ekino"/>
    </a>
</header>
<div class="app-wrapper">

<!-- End Header -->

<!-- Content -->

    [#if content.certificateImage?has_content && damfn.getAssetLink(certificateImageUUID)?has_content]

        <div class="Container">
            <div class="success__checkmark"></div>
            <p class="Container__title">This certificate is VALID</p>
            <a class="zoomable-picture"
                href="${damfn.getAssetLink(certificateImageUUID)}"
                target="_blank"
                data-width="1200"
                data-height="900">
                <img class="Container__certificate"
                    src="${damfn.getAssetLink(certificateImageUUID)}"
                    alt="certificate ekino" />
            </a>
        </div>
    [#else ]
        <div class="Container">
            <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/icon-error.svg" alt="icon error"/>
            <p class="Container__title">
                SORRY! the certificate you are looking for does not exist.
            </p>
        </div>
    [/#if]
</div>
<!-- End Content -->

<!-- Footer -->
<footer class="app-footer">
    <div class="app-footer__container">
        <div class="app-footer__container--Top">
            <div class="total">
                <ul class="lable">
                    <li>
                        <a class="active" href="https://www.ekino.vn/about">
                            <span>MEET TEAM</span>
                        </a>
                    </li>
                    <li>
                        <a class="active" href="https://jobs.ekino.vn/en/offers" >
                            <span>CAREER</span>
                        </a>
                    </li>
                </ul>
                <div class="app-footer__paragraph">
                    <p>
                        Concerned about quality of your experience on our
                        website, we have ensured compliance with Opquast
                        Website's quality criteria. This compliance is verified
                        on our
                        <a class="subText" href="http://partners.opquast.com/fr/declarations/2" >public statement</a>
                        and checked regularly.
                    </p>
                </div>
                <ul class="app-footer__ShareLinks">
                    <li class="app-footer__ShareLinks--listItem">
                        <a href="https://www.linkedin.com/company/ekino.asia/" class="itemShare" >
                            <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/icon-linked.svg"
                                 alt="icon linked"
                                 width="20"
                                 height="20" />
                        </a>
                    </li>
                    <li class="app-footer__ShareLinks--listItem">
                        <a href="https://www.ccebook.com/ekinovn/" class="itemShare" >
                            <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/icon-facebook.svg"
                                 alt="icon facebook"
                                 width="20"
                                 height="20" />
                        </a>
                    </li>
                    <li class="app-footer__ShareLinks--listItem">
                        <a href="https://www.vietnamworks.com/company/ekino"
                           class="itemShare" >
                            <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/icon-vietnamwork.svg"
                                 alt="icon vietnamwork"
                                 width="20"
                                 height="20" />
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="app-footer__container--Bottom">
            <div class="brand">
                <a class="active" href="https://www.ekino.vn">
                    <span>ekino.</span>
                </a>
            </div>
            <div class="app-footer__Bottom--Glyficon">
                <a href="http://partners.opquast.com/fr/declarations/2">
                    <div class="LazyLoad is-visible">
                        <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/opquast-footer.png"
                             alt="logo opquast" />
                    </div>
                </a>
            </div>
        </div>
    </div>
</footer>
<!-- End Footer -->

</body>
</html>
