[#assign currentPage = model.getParent().getNode().getPath()!"/reset"]
[#assign successPage = content.successPage!"/home"]
[#assign resetPasswordErrorCode = ctx.resetPasswordErrorCode!"fail"]

<link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/resetPassword.css">
<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/validation.js"></script>
<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/resetPassword.js"></script>

<div class="app-wrapper__resetPassword">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="contentPage">
        <p class="page__title page__title--form">reset password</p>
        <form action=".resetPassword" method="post" class="reset-password" id="reset-password-form">
            <input type="text" name="currentPageForReset" value="${currentPage}" style="display: none"/>
            <input type="text" name="successPageForReset" value="${successPage}" style="display: none"/>
            <input type="text" name="key" value="${ctx.key!""}" style="display: none"/>
            <div class="form-group">
                <label>new password</label>
                <input type="password" class="form-control__input" id="reset-password" name="passwordReset">
                <div class="eye" id="reset-eye-password"></div>
            </div>
            <div class="form-group">
                <label>confirm password</label>
                <input type="password" class="form-control__input" id="reset-password-confirm"
                       name="confirmPasswordReset">
                <div class="eye" id="reset-eye-confirmPassword"></div>
            </div>
            [#if resetPasswordErrorCode != "fail"]
                [#if resetPasswordErrorCode == "CMP_007"]
                    <p class="message-error">Your credentials are not correct</p>
                [#else ]
                    <p class="message-error">We apologize but this link is no longer valid.</p>
                [/#if]
            [/#if]
            <div class="btn">
                <button type="submit" class="btn__reset" disabled>Reset</button>
            </div>
        </form>
    </div>
</div>