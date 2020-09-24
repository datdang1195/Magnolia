[#assign targetPage = content.targetPage!"/home"]
[#assign currentPage = model.getParent().getNode().getPath()!"/change-password"]
[#assign error = ctx.error!]

<link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/change_password.css">
<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/common.js"></script>
<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/validation.js"></script>
<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/changePassword.js"></script>

<div class="change-password">
    <div class="container--aligned-layout">
        <div id="loading" class="loading">
            <div class="loading__content">
                <i></i>
            </div>
        </div>
        <h1 class="page__title">change password</h1>
        <form action="/.changePassword" class="change-password__form" id="change-password-form" method="post">
            <input type="text" name="currentPage" value="${currentPage}" style="display: none"/>
            <input type="text" name="targetPage" value="${targetPage}" style="display: none"/>
            <div class="form__group js-form-group">
                <label class="form__label">current password</label>
                <input type="password" class="form__input js-form-input" name="currentPassword" autocomplete="current-password">
                <span class="form__icon-eye js-form-icon-eye"></span>
            </div>
            <div class="form__group js-form-group">
                <label class="form__label">new password</label>
                <input type="password" class="form__input js-form-input" name="password" id="new-password" autocomplete="new-password">
                <span class="form__icon-eye js-form-icon-eye"></span>
            </div>
            <div class="form__group js-form-group">
                <label class="form__label">confirm password</label>
                <input type="password" class="form__input js-form-input"
                       name="confirmPassword" autocomplete="new-password">
                <span class="form__icon-eye js-form-icon-eye"></span>
            </div>
            <p class="form__message-error" id="message-error">Your current password not correct</p>
            <button type="submit" class="change-password__button js-change-password-submit" disabled>Change password</button>
        </form>
    </div>
</div>
