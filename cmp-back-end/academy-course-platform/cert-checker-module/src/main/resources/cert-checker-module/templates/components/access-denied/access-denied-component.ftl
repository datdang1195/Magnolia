[#assign loginPage = content.loginPage!"/login"]

<div class="app-wrapper__accessDenied">
    <div class="container-accessDenied">
        <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/accessDenied.svg" alt="not access denied icon" class="accessDenied-icon" />
        <p class="accessDenied-title">access denied!</p>
        <p class="accessDenied-error">Oh what?! It seems like you are trying to access a private page without
            revealing your
            identity.</p>
[#--        <div class="btn">--]
[#--            <a href="${loginPage}" class="goTo-home-page">go to log in page</a>--]
[#--        </div>--]
    </div>
</div>