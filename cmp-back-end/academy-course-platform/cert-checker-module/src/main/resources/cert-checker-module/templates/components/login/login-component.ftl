[#assign enrolLink = content.enrolLink!"/enrol"]
[#assign forgotLink = content.forgotLink!"/forgot-password"]
[#assign errorCode = ctx.errorCode!]
[#assign forgotPasswordErrorCode = ctx.forgotPasswordErrorCode!"no fail"]
[#assign mail = ctx.username!]
[#assign mailInForgotFrm = ctx.usernameInForgotFrm!""]
[#assign successPage = content.successPage!"/home"]
[#assign currentPage = model.getParent().getNode().getPath()!"/login"]
[#assign userName = model.getCurrentUser().getName()!"anonymous"]

<script>
    window.EKINO_ACADEMY['currentPage'] = "login"

    if ("anonymous" != '${userName}') {
        window.location.replace('${successPage}');
    }
</script>


<div class="app-wrapper__login">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="container-form">
        <p class="page__title page__title--form">login</p>
        <form action=".authenticate" class="login-form" id="login-form" method="post">
            <input type="text" name="currentPage" value="${currentPage}" style="display: none"/>
            <input type="text" name="successPage" value="${successPage}" style="display: none"/>
            <div class="form-group">
                <label>email</label>
                <input type="text" class="form-control__input" name="emailLogin" id="emailLogin" value="${mail}">
            </div>
            <div class="form-group">
                <label>password</label>
                <input type="password" class="form-control__input" id="login-password" name="passwordLogin">
                <div class="eye" id="login-eye"></div>
                <p class="credentials-error">Your credentials are not correct</p>
            </div>

            <div class="wrapper-action">
                <div class="remember">
                    <label class="remember__label" for="login-remember">
                        <input type="checkbox" class="form-control__checkbox" id="login-remember"
                               name="rememberMe" />
                        Remember me
                    </label>
                </div>
                <div class="forgot-password">
                    <a class="forgot__title" href="${forgotLink}">Forgot Your Password?</a>
                </div>
            </div>
            <div class="btn">
                <button type="submit" class="btn__login" disabled>login</button>
            </div>
            <div class="wrapper-enrol">
                <p class="enrol-label">Don't have an account?</p>
                <a href="${enrolLink}" class="enrol__link">enrol here</a>
            </div>
        </form>
    </div>
</div>