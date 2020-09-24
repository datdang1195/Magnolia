[#assign programName = ctx.programName!]
[#assign resultModel = model.getCMPMessage()!]
[#assign title = resultModel.getTitle()]
[#assign sendForgotMailSuccessMode = ctx.forgotMode!"false"]
[#assign resetPasswordMode = ctx.resetMode!"false"]
[#assign changePassMode = ctx.changePassMode!"false"]

<link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/result.css">

<div class="Container container--aligned-layout confirm">
    <div class="confirm__wrapper">
        [#if changePassMode == "true"]
            [#assign messageChange = resultModel.getMessage()]
            <div class="confirm__title page__title">
                <div class="success__checkmark"></div>
            </div>
            <p class="confirm__body">
                ${messageChange}
            </p>
            <button class="confirm__button" onclick="location.href='/general';">
                Go to home page
            </button>
        [#elseif resetPasswordMode == "true"]
            [#assign messageReset = resultModel.getMessage()]
            [#assign linkPageReset = content.linkPageReset!"/login"]
            [#assign linkTitleReset = content.linkTitleReset!" GO TO LOGIN"]
            <div class="confirm__title page__title">
                <div class="success__checkmark"></div>
            </div>
            <p class="confirm__body">
                ${messageReset}
            </p>
            <button class="confirm__button" onclick="location.href='${linkPageReset}';">
                ${linkTitleReset}
            </button>
        [#elseif sendForgotMailSuccessMode == "true"]
            [#assign messageForgot = resultModel.getMessage()]
            [#assign linkPageForgot = content.linkPageForgot!"/general"]
            [#assign linkTitleForgot = content.linkTitleForgot!" GO TO HOMEPAGE"]
            <div class="confirm__title page__title">
                <div class="success__checkmark"></div>
            </div>
            <p class="confirm__body">
                ${messageForgot}
            </p>
            <button class="confirm__button" onclick="location.href='${linkPageForgot}';">
                ${linkTitleForgot}
            </button>
        [#else ]
            [#assign message = resultModel.getMessage(programName)]
            [#assign linkPage = content.linkPage!"/home"]
            [#assign linkTitle = content.linkTitle!"Go to homepage"]
            <p class="confirm__title page__title">
                ${title}
            </p>
            <p class="confirm__body">
                ${message}
            </p>
            <button class="confirm__button" onclick="location.href='${linkPage}';">
                ${linkTitle}
            </button>
        [/#if]
    </div>
</div>