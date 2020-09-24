[#assign currentUser = model.getCurrentUser()!"User is anonymous!"]
[#assign coursesStatus = model.getCoursesStatus()!"Courses Status not found."]
[#assign phases = model.getPhases()!"Phases not found."]
[#assign years = model.getListYear()!"Years not found."]
[#assign courseDetailPage = content.courseDetailUrl!"/course-detail"]
[#assign roleAccount = model.getRoleAccount()!]
[#assign semestersByPrograms = model.getSemestersOfPrograms()]

<script>
    window.EKINO_ACADEMY['currentPage'] = "courseStatus"

    var fullNameOfCurrentUser = '${currentUser.title}'
    var dataDates = JSON.parse('${semestersByPrograms}')
    var dataYears = JSON.parse('${years}');
    var data = ${coursesStatus};
    const courseDetailLink = '${courseDetailPage}';
    const roles = JSON.parse('${roleAccount}');
    const courseStatus = '${coursesStatus}'

    window.EKINO_ACADEMY.page['courseStatus'] = {
        fullNameOfCurrentUser:fullNameOfCurrentUser,
        dataDates:dataDates,
        dataYears:dataYears,
        data:data,
        courseDetailLink:courseDetailLink,
        roles:roles
    }
</script>

<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/jquery/smooth-scrollbar.js"></script>

<div class="app-wrapper__progress app-wrapper__course">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="container--aligned-layout program course js-course">
        <div class="progressbar-wrapper">
            <div class="arrow-slider-left">
                <div class="arrow-left"></div>
            </div>
            <div class="dotted-line"></div>
            <div class="line"></div>
            <div class="container-slider">
                <div class="slider-progress js-slider-progress" id="course-status-slide"></div>
            </div>
            <div class="arrow-slider-right">
                <div class="arrow-right"></div>
            </div>
        </div>
        <div class="page__title">
            course status
        </div>
        <div class="contentPage">
            <div class="evn-row justify-content-center course__row-select">
                <div class="evn-col evn-col-lg--12 evn-col--12">
                    <div class="section-form section-form__phase">
                        <div class="form__phase__wrapper">
                            <div class="session-filter session-filter--phase">
                                <div class="wrapper-filter">
                                    [#list phases as phase]
                                        [#assign phaseCssClass = '']
                                        [#assign phaseId = '']
                                        [#if phase.disabled == false]
                                            [#assign phaseCssClass = 'wrapper-item wrapper-item--phase js-item-phase']
                                            [#assign phaseId = phase.id]
                                        [#else ]
                                            [#assign phaseCssClass = 'wrapper-item wrapper-item--phase disabled']
                                        [/#if]
                                        <div class="${phaseCssClass}" data-id="${phaseId}">
                                            <div class="content">
                                                <p class="content__title content__title--margin">
                                                    ${phase.name}
                                                </p>
                                            </div>
                                        </div>
                                    [/#list]
                                    <div class="phase-bar"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="section-form section-form__program">
                        <form
                                action=""
                                class="form-radio js-form-radio js-form-program"
                                data-form="program"
                        ></form>
                    </div>
                    <div class="section-filter">
                        <div class="dropdown-semester">
                            <select class="dropdown cat1" id="semester-dropdown">
                            </select>
                        </div>
                        <div class="course__input__wrapper js-course-search-box">
                            <span class="course__input__line"></span>
                            <input
                                    type="text"
                                    class="course__input__text js-course-input"
                                    placeholder="search course here"
                            />
                            <div class="course__bg__overlay"></div>
                            <label class="course__switch__wrapper js-course-switch">
                                <div class="switch">
                                    <input type="checkbox" class="switch__checkbox js-switch__checkbox" id="supervisor" checked/>
                                    <span class="switch__label"></span>
                                    <span class="switch__text"></span>
                                </div>
                            </label>
                            <div class="course__input__list js-course-block">
                                <ul class="js-course-items"></ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="course__row-info js-info-wrapper"></div>
            <div class="project filter-results js-list-course"></div>
            <div class="news-pagination">
                <div class="pagination__wrapper">
                </div>
            </div>
        </div>
    </div>
</div>
