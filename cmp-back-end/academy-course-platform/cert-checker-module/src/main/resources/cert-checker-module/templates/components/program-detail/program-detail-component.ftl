[#assign title = content.title!"Title not found." /]
[#assign detailProgram = model.getDetailProgram()!"detailed program not found!" /]
[#assign modifiedDateText = model.getModifiedDateOfDetailProgram()!"end date not found!" /]
[#assign haveEnrolButton = model.haveEnrolButton() /]
[#assign currentUser = model.getCurrentUser()!"User is anonymous!"]
[#assign resultPage = content.resultPage!"/result"]

<link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/programDetail.css">

<script>
    var activeEnrolBtn = ${haveEnrolButton?c};
</script>

<div class="app-wrapper__enrolLanding">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="container-landingPage">
        <div class="landingPage">
            <p class="page__title">${detailProgram.phase.nodeName}</p>
            <p class="page__title">${detailProgram.group.displayName}</p>
            <p class="landingPage__date">${modifiedDateText}</p>
        </div>
        <div class="contentPage">
            [#if detailProgram.descriptionList??]
                [#list detailProgram.descriptionList as description]
                    <div class="section">
                        <div class="section__lable">${description.title}</div>
                        <div class="section__description">${description.content}</div>
                    </div>
                [/#list]
            [/#if]
            <div class="btn">
                [#if currentUser?has_content && currentUser.getName() != 'anonymous']
                    <button id="enrolBtn" type="submit" class="js-enrol-landing-button">enrol</button>
                [#else ]
                    <button type="button" id="enrolBtn" onclick="location.href = '${model.getEnrolButtonUrl()}';">enrol</button>
                [/#if]
            </div>
        </div>
    </div>
</div>
<div class="modal-message hidden js-enrol-modal-message">
    <div class="modal-dialog modal-dialog--enrol js-modal-enrol-dialog">
        <div class="modal-content modal-content--enrol">
            <div class="message__label message__label--enrol">You are going to enrol ${detailProgram.phase.nodeName} - ${detailProgram.group.nodeName}.
                <b class="message__question">Would you wish to submit?</b></div>
            <div class="evn-row message__row">
                <div class="message__col">
                    <button class="evn-btn evn-btn--modal evn-btn--cancel js-enrol-cancel-button">CANCEL</button>
                </div>
                <div class="message__col">
                    <form method="post" action="/.enrol">
                        <button class="evn-btn evn-btn--modal js-enrol-submit-button" id="submitBtn" type="submit">SUBMIT</button>
                        <input style="display: none" value="${currentUser.getName()}" name="email"/>
                        <input style="display: none" value="" name="username"/>
                        <input style="display: none" value="" name="password"/>
                        <input style="display: none" value="${detailProgram.nodeName}" name="programName"/>
                        <input style="display: none" value="${detailProgram.uuid}" name="program"/>
                        <input style="display: none" value="${resultPage}" name="successPage"/>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal-backdrop hidden js-enrol-modal-backdrop">
</div>

<script>
    $(document).ready(function() {
        if (!activeEnrolBtn) {
            $('#enrolBtn').prop('disabled', true);
        }
    });
</script>

<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/programDetail/programDetail.js"></script>


