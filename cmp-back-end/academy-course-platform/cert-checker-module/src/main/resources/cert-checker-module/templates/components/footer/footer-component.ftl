[#assign logo = content.logo!]
[#assign email = content.email!"academy@ekino.vn"]

<footer class="app-footer__academy">
    <div class="app-footer__wrapper">
        <div class="footer__wrapper--logo">
            <div class="app-footer__wrapper--left">
                <a class="active" href="https://ekino.vn">
                    <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/ekino.png" alt="logo ekino">
                </a>
            </div>
            <div class="app-footer__wrapper--right">
                <a href="/general">
                [#if logo??]
                    [#assign imgRef = damfn.getAssetLink(logo)!]
                    [#if imgRef??]
                        <img src="${imgRef}" alt="logo ekino footer" />
                    [/#if]
                [/#if]
                </a>
            </div>
        </div>
        <div class="app-footer__contact">
            <span>email: </span><a href="mailto:${email}" class="mail-to">${email}</a>
        </div>
    </div>
</footer>
