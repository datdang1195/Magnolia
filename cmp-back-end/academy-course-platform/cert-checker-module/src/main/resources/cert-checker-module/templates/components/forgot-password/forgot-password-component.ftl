[#assign forgotPasswordErrorCode = ctx.forgotPasswordErrorCode!"no fail"]
[#assign mailInForgotFrm = ctx.usernameInForgotFrm!""]
[#assign successPage = content.successPage!"/home"]
[#assign resetPasswordPage = content.resetPasswordPage!"/reset"]
[#assign currentPage = model.getParent().getNode().getPath()!"/login"]

<div class="app-wrapper__forgotPassword">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="forgotPassword_container">
        <p class="page__title page__title--form">forgot password</p>
        <p style="text-align: center;padding: 35px 0px;">Please provide your email address. We will send you the reset link shortly.</p>
        <form action=".forgotPassword" class="forgot-password" id="send-code-form" method="post">
            <input type="text" name="currentPageForForgot" value="${currentPage}" style="display: none"/>
            <input type="text" name="successPageForForgot" value="${successPage}" style="display: none"/>
            <input type="text" name="resetPasswordPage" value="${resetPasswordPage}" style="display: none"/>
            <div class="form-group">
                <label>email</label>
                <input type="text" class="form-control__input" name="emailReset" value="${mailInForgotFrm}">
            </div>
            [#if forgotPasswordErrorCode != "no fail"]
                <p class="message-error">Your account does not exist</p>
            [/#if]
            <div class="btn">
                <button type="submit" class="btn__sent" disabled>send</button>
            </div>
        </form>
    </div>
</div>