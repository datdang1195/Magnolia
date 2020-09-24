[#assign participantModel = model.getProgramStatusInformation()]
[#assign yearsData = model.getListYear()]
[#assign courseLink = content.courseLink!"#"]
[#assign ojtLink = content.ojtLink!"#"]
[#assign participantLink = content.participantLink!"#"]
[#assign year = ctx.year!""]
[#assign phases = participantModel['phases']!]
[#assign listPhase = participantModel['listPhase']!]
[#assign data = participantModel['data']!]
[#assign dateSemesters = participantModel['semestersOfProgram']!]

<script>
    window.EKINO_ACADEMY['currentPage'] = "programStatus"
    var yearsData = JSON.parse('${yearsData}');
    var phases = JSON.parse('${phases}');
    var dataObj = JSON.parse('${data}');
    var dateSemesters = JSON.parse('${dateSemesters}');
    var attitudeSize = 10;
    var contextPath = '${ctx.contextPath}';
    var ojtLink = '${ojtLink}';
    var participantLink = '${participantLink}';
    var courseLink = '${courseLink}';

    window.EKINO_ACADEMY.page['programStatus'] = {
        yearsData:yearsData,
        phases:phases,
        dataObj:dataObj,
        attitudeSize:attitudeSize,
        contextPath:contextPath,
        ojtLink:ojtLink,
        participantLink:participantLink,
        courseLink:courseLink,
        dateSemesters:dateSemesters
    }
</script>

<div class="app-wrapper__programStatus">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="container-programStatus">
        <div class="progressbar-wrapper">
            <div class="arrow-slider-left">
                <div class="arrow-left"></div>
            </div>
            <div class="dotted-line"></div>
            <div class="line"></div>
            <div class="container-slider">
                <div class='slider-progress js-slider-progress' id="program-status-slider">
                </div>
            </div>
            <div class="arrow-slider-right">
                <div class="arrow-right"></div>
            </div>
        </div>
        <p class="page__title">program status</p>
        <div class="content-programStatus">
            <div class="session-filter session-filter--phase">
                <div class="wrapper-filter">
                    [#list listPhase as result]
                        <div class="wrapper-item wrapper-item--phase js-item-phase-program-status" data-value="${result.id}">
                            <div class="content">
                                <p class="content__title content__title--margin">${result.name}</p>
                            </div>
                        </div>
                    [/#list]
                    <div class="phase-bar"></div>
                </div>
            </div>
            <div class="session-filter">
                <div class="wrapper-filter filter-program-status">
                </div>
            </div>
            <div class="wrapper-section participant-session hidden">
                <div class="section-item">
                    <div class="wrapper-table">
                        <p class="total-participants"></p>
                        <div class="table-overflow table-desktop" id="participant-table">
                            <table class="table-content">
                                <thead>
                                <tr class="table-head-desk">
                                    <th>No</th>
                                    <th>
                                        <div class="search__box-desktop" >
                                            <span> Name</span>
                                            <input type="text" class="search__participants__table" id="search-participants-input" placeholder="Search Name">
                                        </div>
                                    </th>
                                    <th>
                                        <div class="participant-col" data-type="team">
                                            <span>Department</span>
                                            <div class="dropdown-filter-content">
                                                <div class="checkbox-container js-wrapper-option">
                                                    <div class="dropdown-filter-item js-select-all">
                                                        <input type="checkbox" value="all" checked="checked" name="filter-col" class="dropdown-filter-menu-item select-all">
                                                        Select All
                                                    </div>
                                                    <div class="js-option-item"></div>
                                                </div>
                                            </div>
                                            <span class="arrow-dropdown js-arrow-filter"></span>
                                        </div>
                                    </th>
                                    <th>
                                        <div class="participant-col" data-type="profile">
                                            <span>Profile</span>
                                            <div class="dropdown-filter-content">
                                                <div class="checkbox-container js-wrapper-option">
                                                    <div class="dropdown-filter-item js-select-all">
                                                        <input type="checkbox" value="all" checked="checked" name="filter-col" class="dropdown-filter-menu-item select-all">
                                                        Select All
                                                    </div>
                                                    <div class="js-option-item"></div>
                                                </div>
                                            </div>
                                            <span class="arrow-dropdown js-arrow-filter"></span>
                                        </div>
                                    </th>
                                    <th>
                                        <div class="participant-col" data-type="semester">
                                            <span>Enroll Type</span>
                                            <div class="dropdown-filter-content">
                                                <div class="checkbox-container js-wrapper-option">
                                                    <div class="dropdown-filter-item js-select-all">
                                                        <input type="checkbox" value="all" checked="checked" name="filter-col" class="dropdown-filter-menu-item select-all">
                                                        Select All
                                                    </div>
                                                    <div class="js-option-item"></div>
                                                </div>
                                            </div>
                                            <span class="arrow-dropdown js-arrow-filter"></span>
                                        </div>
                                    </th>
                                    <th>
                                        <div class="participant-col" data-type="status">
                                            <span>Status</span>
                                            <div class="dropdown-filter-content">
                                                <div class="checkbox-container js-wrapper-option">
                                                    <div class="dropdown-filter-item js-select-all">
                                                        <input type="checkbox" value="all" checked="checked" name="filter-col" class="dropdown-filter-menu-item select-all">
                                                        Select All
                                                    </div>
                                                    <div class="js-option-item"></div>
                                                </div>
                                            </div>
                                            <span class="arrow-dropdown js-arrow-filter"></span>
                                        </div>
                                    </th>
                                </tr>
                                </thead>
                                <tbody class="scroll-table scroll-table-desk scroll__table__parti" id="scroll-table-parti">
                                </tbody>
                            </table>
                        </div>
                        <div class="wrapper-see see-more-participants hidden">
                            <div class="see__label">See more</div>
                        </div>
                        <div class="wrapper-see see-less-participants hidden">
                            <div class="see__label">See less</div>
                        </div>
                        <div class="search__box-mobile">
                            <input type="text" class="search__participants__table search__input-mobile" placeholder="Search Name">
                        </div>
                        <div class="table-overflow table-mobile table-mobile-1">
                            <table class="table-content">
                                <thead>
                                <tr>
                                    <th>No</th>
                                    <th>Name</th>
                                    <th>Department</th>
                                    <th>Profile</th>
                                    <th>Enroll Type</th>
                                    <th>Status</th>
                                </tr>
                                </thead>
                                <tbody class="scroll-table scroll-table-mobile scroll__table__parti">
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="section-item course-session hidden">
                <div class="section__label">
                    A. <span>Courses</span>
                </div>
                <div class="wrap-info-course"></div>
                <div class="section__btn">
                    <a class="evn-btn course-url" href="#url">Details</a>
                </div>
            </div>
            <div class="section-item onJobTraining-session hidden">
                <div class="section__label">
                    B. <span>On the job training</span>
                </div>
                <div class="section__content section__onJobTraining">
                </div>
            </div>
        </div>
    </div>
</div>
