[#assign currentUser = model.getCurrentUser()!"User is anonymous!"]
[#assign myCoursePagePath = model.getParent().getNode().getPath()!"/my-course"]
[#assign scheduleIconUrl1 = content.scheduleIconUrl!"/my-course/calendar"]
[#assign trainingUrl1 = content.OJTTrainingUrl!"/my-course/ojt"]
[#assign courseDetailUrl1 = content.courseDetailUrl!"/my-course/course-detail"]
[#assign ojtProjectName1 = model.getOJTProjectName()!""]
[#assign allCourses = model.getCurrentCoursesOfUser()!]
[#assign allCategories = model.findAllCategoriesOfCourse()!]

<script>
    window.EKINO_ACADEMY['currentPage'] = "courseList"

    var categories = JSON.parse('${allCategories}');
    var courseList = JSON.parse('${allCourses}');
    var courseDetailUrl = '${courseDetailUrl1}';
    var trainingUrl = '${trainingUrl1}';
    var ojtProjectName = '${ojtProjectName1}';
    var scheduleIconUrl = '${scheduleIconUrl1}';

    window.EKINO_ACADEMY.page['courseList'] = {
        categories:categories,
        courseList:courseList,
        courseDetailUrl:courseDetailUrl,
        trainingUrl:trainingUrl,
        ojtProjectName:ojtProjectName,
        scheduleIconUrl:scheduleIconUrl
    }
</script>

[#if currentUser?has_content]
    <div class="app-wrapper__courseList">
        <div id="loading" class="loading">
            <div class="loading__content">
                <i></i>
            </div>
        </div>

        <div class="container-courseList">

            [#-- Generate page title --]
            <div class="courseList">
                [#assign couseInfos = model.getCourseInformation()!"Course infomation not found."]
                [#if couseInfos?has_content]
                    [#if couseInfos[0]?has_content]
                        <p class="page__title">${couseInfos[0]}</p>
                    [/#if]
                    [#if couseInfos[1]?has_content]
                        <p class="page__title">${couseInfos[1]}</p>
                    [/#if]
                    <div class="courseList__date">
                        [#if couseInfos[3]?has_content]
                            <p class="semester__date">SEMESTER 1: <span>${couseInfos[3]}</span></p>
                        [/#if]
                        [#if couseInfos[4]?has_content]
                            <p class="semester__date">SEMESTER 2: <span>${couseInfos[4]}</span></p>
                        [/#if]
                    </div>
                [/#if]
            </div>
            [#-- End generate page title --]

            [#-- Start of page content --]
            <div class="contentPage">
                <div class="course__total" id="number-course">
                </div>
                <div class="menu">
                    <div class="schedule">
                        <div class="schedule__img"></div>
                        <span class="schedule__lable">SCHEDULE</span>
                    </div>
                    [#if ojtProjectName1?has_content]
                        <div class="training">
                            <div class="training__img"></div>
                            <span class="training__lable">ON JOB TRAINING</span>
                        </div>
                    [#else]
                        <div class="training evn-btn--disabled">
                            <div class="training__img"></div>
                            <span class="training__lable">ON JOB TRAINING</span>
                        </div>
                    [/#if]
                </div>


                <div class="dropdown filtering">
                    <div class="filter-semester dropdown-semester">
                        <select class="dropdown cat1" id="select-sem-courseList">
                        </select>
                    </div>
                    <div class="dropdown-categories">
                        <select class="dropdown cat1" id="dropdown-project"> </select>
                    </div>
                    <div class="dropdown-status">
                        <select id="js-dropdown__status"> </select>
                    </div>
                </div>


                <div class="project filter-results js-course-list"> </div>

                <div class="news-pagination">
                    <div class="pagination__wrapper">
                    </div>
                </div>

                <div class="goToTopPage" id="goToTopPage">
                    <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/goToTop.svg" alt="go to top page" />
                    <p class="goTo__title">Top</p>
                </div>
            </div>
            [#-- End of page content --]

        </div>
    </div>
[/#if]