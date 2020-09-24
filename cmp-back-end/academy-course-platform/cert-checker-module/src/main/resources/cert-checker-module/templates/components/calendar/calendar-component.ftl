[#assign courseDetail = content.courseDetail!"/course-detail"]

[#assign listCalendar = model.getCalendar()!"calendar not found"]
[#assign currentUser = model.getCurrentUser()!"User is anonymous!"]
[#assign showFilter = model.showFilter()!]
[#assign roleAccount = model.getRoleAccount()!]
[#assign dataSchedule = model.getDataSchedule()]
[#assign toggleRole = model.getRole4Toggle()]

[#assign listGroupProgram = dataSchedule["listGroupProgram"]]
[#assign groupPrograms = dataSchedule["groupPrograms"]]
[#assign listPhase = dataSchedule["listPhase"]]
[#assign objData = dataSchedule["objData"]]

[#assign displayParticipant = ""]
[#assign displayOther = ""]
[#if showFilter]
    [#assign displayParticipant = "display: none"]
[#else]
    [#assign displayOther = "display: none"]
[/#if]

<script>
    window.EKINO_ACADEMY['currentPage'] = "schedule"

    var contextPath = '${ctx.contextPath}';
    var roleAccount = JSON.parse('${roleAccount}');
    var dataSchedule = JSON.parse('${listCalendar}');
    var listGroupProgram = JSON.parse('${groupPrograms}');
    var objData = JSON.parse('${objData}');
    var courseDetailLink = '${courseDetail}';

    window.EKINO_ACADEMY.page['schedule'] = {
        contextPath:contextPath,
        roleAccount:roleAccount,
        dataSchedule:dataSchedule,
        listGroupProgram:listGroupProgram,
        objData:objData,
        courseDetailLink:courseDetailLink
    }
</script>

<div class="app-wrapper__schedule">
    <div class="container-schedule">
        <div class="wrapper-schedule-header">
            <div id="loading" class="loading">
                <div class="loading__content">
                    <i></i>
                </div>
            </div>
            [#if toggleRole?size == 2]
                <div class="container-swipe-role">
                    <div class="swipe-role">
                        [#list toggleRole as role]
                            <div class="role-item" data-role="${role}">${role}</div>
                        [/#list]
                        <div class="role-active"></div>
                    </div>
                </div>
            [/#if]


            <div class="wrapper-date">
                <div class="date">
                    <div class="previous-month" id="previous-month">
                    </div>
                    <p class="date__lable" id="date__lable">FEBRUARY 2020</p>
                    <div class="next-month" id="next-month">
                    </div>
                </div>
                <div class="calendar">
                    <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/calendar.png" alt="calendar">
                    <input type='text' class="datetimepicker" id='datetimepicker' autocomplete="off" />
                    <div id="container__datepicker" class="container__datepicker"></div>
                </div>
            </div>

            [#assign programInfor = model.getProgramInformationByDate()!]
            [#if programInfor?has_content && programInfor?size == 3]
                <div class="participants-role">
                    <div class="wrapper-course" style="${displayParticipant}">
                        <p class="course__level">${programInfor[0]}</p>
                        <p class="line">&nbsp;‐&nbsp;</p>
                        <p class="course__subject">${programInfor[1]}</p>
                    </div>
                    <p class="schedule__duration" style="${displayParticipant}">${programInfor[2]}</p>
                </div>
            [/#if]

            <div class="supervisors-trainer-role">
                <div class="session-filter-phase" style="${displayOther}">
                    <div class="filter-wrapper">
                        <div class="line"></div>
                        <div class="container-filter">
                            [#list listPhase as result]
                                <div class="phase-item phase-item-${result?index + 1}">
                                    <label class="wrapper-item">
                                        <input type="checkbox" name="selectMultiPhase" value="${result.id}">
                                        <span class="checkmark"></span>
                                        <div class="label-content">${result.name}</div>
                                    </label>
                                </div>
                            [/#list]
                        </div>
                    </div>
                </div>
                <div class="session-filter" style="${displayOther}">
                    <div class="wrapper-filter">
                        [#list listGroupProgram as result]
                            <label class="wrapper-item ${result.className}">
                                <input type="checkbox" name="checkGroupProgram" value=${result.id}>
                                <span class="checkmark"></span>
                                <div class="content">
                                    <p class="content__title">${result.name}</p>
                                </div>
                            </label>
                        [/#list]
                    </div>
                </div>
            </div>

        </div>
        <div class="contentPage">
            <div class="content-desk">
                <div class="wrapper-day">
                    <p class="day__item">SUN</p>
                    <p class="day__item">MON</p>
                    <p class="day__item">TUE</p>
                    <p class="day__item">WED</p>
                    <p class="day__item">THU</p>
                    <p class="day__item">FRI</p>
                    <p class="day__item">SAT</p>
                </div>
                <div class="wrapper-date" id="wrapper-date-desk">
                </div>
            </div>
            <div class="content-mobile">
                <div class="wrapper-content__mobile">
                    <div class="list-day">
                        <p class="day__item">SUN</p>
                        <p class="day__item">MON</p>
                        <p class="day__item">TUE</p>
                        <p class="day__item">WED</p>
                        <p class="day__item">THU</p>
                        <p class="day__item">FRI</p>
                        <p class="day__item">SAT</p>
                    </div>
                    <div class="list-schedule" id="wrapper-date-mobile">
                    </div>
                </div>
                <div class="action">
                    <div class="arrow-previous-week">&#10094;</div>
                    <div class="action__swipe">
                        <img id="action__drag" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/drag.png" alt="drag">
                        <span class="drag__title">drag</span>
                    </div>
                    <div class="arrow-next-week">&#10095;</div>
                </div>
            </div>
        </div>
    </div>
</div>
