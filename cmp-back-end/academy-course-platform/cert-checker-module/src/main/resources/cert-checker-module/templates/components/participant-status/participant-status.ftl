[#assign participantModel = model.getParticipantStatusInformation()]
[#assign yearsData = model.getListYear()]
[#assign courseLink = content.courseLink!"#"]
[#assign ojtLink = content.ojtLink!"#"]
[#assign year = ctx.year!""]
[#assign phase = ctx.phase!""]
[#assign program = ctx.program!""]
[#assign userParam = ctx.user!""]
[#assign phases = participantModel['phases']!]
[#assign listPhase = participantModel['listPhase']!]
[#assign isOnlyEnrollSemester2 = model.isOnlyEnrollSemester2()]
[#assign isOnlyEnrollSemester1 = model.isOnlyEnrollSemester1()]
[#assign isFullProgram = model.isFullProgram()]

<script>
    window.EKINO_ACADEMY['currentPage'] = "participantStatus"

    let yearsData = JSON.parse('${yearsData}');
    let programs = [];
    let semestersOfProgram = [];
    let courseList = [];
    let phases = JSON.parse('${phases}');
    let attitudeSize = 10;
    [#if phase?has_content]
    [#assign programs = participantModel['programs'] !]
    programs = JSON.parse('${programs}');
    console.log(programs)
    [/#if]
    [#if year?has_content && phase?has_content && program?has_content && userParam?has_content]
    courseList = ${participantModel['courseListString']};
    [/#if]
    [#if year?has_content && phase?has_content]
    [#assign semestersOfProgram = participantModel['semestersOfProgram']!]
    semestersOfProgram = ${semestersOfProgram}
    console.log(semestersOfProgram)
    [/#if]

    window.EKINO_ACADEMY.page['participantStatus'] = {
        yearsData:yearsData,
        phases:phases,
        programs:programs,
        courseList:courseList,
        semestersOfProgram:semestersOfProgram,
        attitudeSize:attitudeSize
    }
</script>

<div class="app-wrapper__progress">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="breadcrumb">
        <ul class="breadcrumb-list">
            <li class="breadcrumb-item-root">
                <a href="#url">Paticipant Detail</a>
            </li>
        </ul>
    </div>
    <div class="container--aligned-layout program">
        <div class="progressbar-wrapper">
            <div class="arrow-slider-left">
                <div class="arrow-left"></div>
            </div>
            <div class="dotted-line"></div>
            <div class="line"></div>
            <div class="container-slider">
                <div class="slider-progress js-slider-progress" id="participant-status-slider"></div>
            </div>
            <div class="arrow-slider-right">
                <div class="arrow-right"></div>
            </div>
        </div>
        <div class="page__title">PARTICIPANT DETAIL</div>
        <div class="contentPage">
            <div class="session-filter session-filter--phase">
                <div class="wrapper-filter">
                    [#list listPhase as result]
                        <div class="wrapper-item wrapper-item--phase js-item-phase" data-value="${result.id}">
                            <div class="content">
                                <p class="content__title content__title--margin">${result.name}</p>
                            </div>

                        </div>
                    [/#list]
                    <div class="phase-bar"></div>
                </div>
            </div>
            [#if year?has_content && phase?has_content]
                [#assign programs = participantModel['programs']!]
                [#assign listProgram = participantModel['listProgram']!]
                [#assign semesterList = participantModel['semesterList']!]
                <div class="session-filter">
                    <div class="wrapper-filter">
                        [#list listProgram as result]
                            <label class="wrapper-item">
                                <input type="radio" name="checkBox" class="js-participant-status-program-radio"
                                       value="${result.id!" "}" checked/>
                                <span class="checkmark"></span>
                                <div class="content">
                                    <p class="content__title">${result.name!""}</p>
                                    [#list semesterList as semester]
                                        <p class="content__time">
                                            <span>${semester.title!""}: </span> ${semester.getSemesterStartDate()!""}
                                            - ${semester.getSemesterEndDate()!""}</p>
                                    [/#list]
                                </div>
                            </label>
                        [/#list]
                    </div>
                </div>
                <div class="session-search">
                    <div class="course__input__wrapper">
                        <span class="course__input__line"></span>
                        <input type="text" class="course__input__text
                                js-participant-status-username-input" placeholder="search name here"/>
                        <div class="course__input__list js-participant-list">
                            <ul class="course__input__ul js-participant-ul"></ul>
                        </div>
                    </div>
                </div>
            [/#if]
            [#if year?has_content && phase?has_content && program?has_content && userParam?has_content]
                [#assign users = participantModel['user']!]
                [#assign enrolProgramDto = participantModel['listEnrolProgramDto']!]
                [#if participantModel['courseSemesters2']?has_content]
                    [#assign courseSemesters2 = participantModel['courseSemesters2']]
                [/#if]
                [#if participantModel['courseSemesters1']?has_content]
                    [#assign courseSemesters1 = participantModel['courseSemesters1']]
                [/#if]
                [#assign ojtTraining = participantModel['ojtTraining']]
                [#if participantModel['summary']?has_content]
                    [#assign summary = participantModel['summary']]
                [/#if]
                [#if participantModel['summarySemester1']?has_content]
                    [#assign summarySemester1 = participantModel['summarySemester1']]
                [/#if]
                [#if participantModel['summarySemester2']?has_content]
                    [#assign summarySemester2 = participantModel['summarySemester2']]
                [/#if]
                [#assign courseList = participantModel['courseList']]
                [#assign listOjtUserAssessment = participantModel['listOjtUserAssessment']]
                [#assign attitudeList = participantModel['attitudeList']]
                [#assign userAttitudeResult = participantModel['userAttitudeResult']]
                [#assign certPath = participantModel['certPath']!]
                <div class="program-item js-program-item">
                    <div class="program-summary">
                        <div class="program-summary__label">
                            A.
                            <span class="program-summary__label__name">User information</span>
                        </div>
                        <div
                                class="evn-row program-participant-status
                                js-program-participant-status">
                            <div
                                    class="program-participant-status__col
                                    evn-col evn-col-lg--4 evn-col--12">
                                    <span
                                            class="program-participant-status__label">
                                        Name:
                                    </span>
                                <span
                                        class="program-participant-status__value">
                                        ${users.fullName!"-"}
                                    </span>
                            </div>
                            <div
                                    class="program-participant-status__col
                                    evn-col evn-col-lg--5 evn-col--12">
                                    <span class="program-status__label program-status__label--email">
                                        Email:
                                    </span>
                                <span
                                        class="program-participant-status__value">
                                        ${users.email!"-"}
                                    </span>
                            </div>
                            <div
                                    class="program-participant-status__col
                                    evn-col evn-col-lg--3 evn-col--12">
                                    <span class="program-status__label program-status__label--phone">
                                        Enroll type:
                                    </span>
                                    <span class="program-participant-status__value">${enrolProgramDto.enrollType!"-"}</span>

                            </div>
                            <div
                                    class="program-participant-status__col
                                    evn-col evn-col-lg--4 evn-col--12">
                                    <span class="program-status__label">
                                        Department:
                                    </span>
                                [#if users.department?has_content]
                                    <span class="program-participant-status__value">${users.department.displayName!"-"}</span>
                                [#else]
                                    <span class="program-participant-status__value">-</span>
                                [/#if]
                            </div>
                            <div
                                    class="program-participant-status__col
                                    evn-col evn-col-lg--5 evn-col--12">
                                    <span class="program-status__label program-status__label--role">
                                        Profile:
                                    </span>
                                [#if users.profile?has_content]
                                    <span class="program-participant-status__value">${users.profile.displayName!"-"}</span>
                                [#else]
                                    <span class="program-participant-status__value">-</span>
                                [/#if]

                            </div>
                            <div
                                    class="program-participant-status__col
                                    evn-col evn-col-lg--3 evn-col--12">
                                    <span class="program-status__label program-status__label--participants">
                                        Participation:
                                    </span>
                                [#if users.participantStatus?has_content]
                                    <span class="program-participant-status__value">${users.participantStatus!"-"}</span>
                                [#else ]
                                    <span class="program-participant-status__value">-</span>
                                [/#if]

                            </div>
                        </div>

                    </div>
                    <div class="program-summary">
                        <div class="program-summary__label">
                            B.
                            <span class="program-summary__label__name">Program Information</span>
                        </div>
                        [#if year?has_content && phase?has_content && program?has_content && userParam?has_content]
                            [#assign selectedProgramInfo = participantModel['programInfo']!]
                            <div class="evn-row program-participant-status js-program-participant-status">
                                <div class="program-participant-status__col evn-col evn-col-lg--4 evn-col--12">
                                    <span class="program-participant-status__label">Start date: </span>
                                    <span class="program-participant-status__value">${selectedProgramInfo.getProgramStartDate()!""}</span>
                                </div>
                                <div class="program-participant-status__col evn-col evn-col-lg--5 evn-col--12">
                                    <span class="program-participant-status__label">End date: </span>
                                    <span class="program-participant-status__value">${selectedProgramInfo.getProgramEndDate()!""}</span>
                                </div>
                                <div class="program-participant-status__col evn-col evn-col-lg--4 evn-col--12">
                                    <span class="program-participant-status__label">Status: </span>
                                    <span class="program-participant-status__value">${selectedProgramInfo.status.displayName!""}</span>
                                </div>
                            </div>

                            <div class="program-summary__label">
                                C.
                                <span class="program-summary__label__name">Courses</span>
                            </div>

                        [/#if]
                        [#if isOnlyEnrollSemester2]
                            [#if courseSemesters2?has_content]
                                <div class="evn-row-name">semester 2</div>
                                <div class="evn-row">
                                    <div class="evn-col">
                                        <div class="program-result evn-row">
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Total courses</div>
                                                <div class="program-result__number">${courseSemesters2.totalCourse!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Completed</div>
                                                <div class="program-result__number">${courseSemesters2.completed!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">In progress</div>
                                                [#if courseSemesters2.inProgress?has_content && courseSemesters2.inProgress == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters2.inProgress!"-"}</div>
                                                [/#if]

                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">To-do</div>
                                                [#if courseSemesters2.todo?has_content && courseSemesters2.todo == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters2.todo!"-"}</div>
                                                [/#if]

                                            </div>
                                        </div>
                                        <hr class="divide evn-d-lg-none">
                                        <div class="program-result evn-row">
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Total hours</div>
                                                <div class="program-result__number">${courseSemesters2.totalHours!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Attended</div>
                                                <div class="program-result__number">${courseSemesters2.attended!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Absent</div>
                                                <div class="program-result__number">${courseSemesters2.absent!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">To-do</div>
                                                [#if courseSemesters2.inProgressHours?has_content && courseSemesters2.inProgressHours == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters2.inProgressHours!"-"}</div>
                                                [/#if]

                                            </div>
                                        </div>
                                    </div>
                                </div>
                            [/#if]
                        [#else]
                            [#if courseSemesters1?has_content]
                                <div class="evn-row-name">semester 1</div>
                                <div class="evn-row">
                                    <div class="evn-col">
                                        <div class="program-result evn-row">
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Total courses</div>
                                                <div class="program-result__number">${courseSemesters1.totalCourse!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Completed</div>
                                                <div class="program-result__number">${courseSemesters1.completed!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">In progress</div>
                                                [#if courseSemesters1.inProgress?has_content && courseSemesters1.inProgress == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters1.inProgress!"-"}</div>
                                                [/#if]

                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">To-do</div>
                                                [#if courseSemesters1.todo?has_content && courseSemesters1.todo == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters1.todo!"-"}</div>
                                                [/#if]

                                            </div>
                                        </div>
                                        <hr class="divide evn-d-lg-none">

                                        <div class="program-result evn-row">
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Total hours</div>
                                                <div class="program-result__number">${courseSemesters1.totalHours!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Attended</div>
                                                <div class="program-result__number">${courseSemesters1.attended!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Absent</div>
                                                <div class="program-result__number">${courseSemesters1.absent!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">To-do</div>
                                                [#if courseSemesters1.inProgressHours?has_content && courseSemesters1.inProgressHours == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters1.inProgressHours!"-"}</div>
                                                [/#if]
                                            </div>
                                        </div>
                                    </div>

                                </div>

                            [/#if]
                            [#if courseSemesters2?has_content]
                                <div class="evn-row-name">semester 2</div>
                                <div class="evn-row">
                                    <div class="evn-col">
                                        <div class="program-result evn-row">
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Total courses</div>
                                                <div class="program-result__number">${courseSemesters2.totalCourse!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Completed</div>
                                                <div class="program-result__number">${courseSemesters2.completed!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">In progress</div>
                                                [#if courseSemesters2.inProgress?has_content && courseSemesters2.inProgress == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters2.inProgress!"-"}</div>
                                                [/#if]

                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">To-do</div>
                                                [#if courseSemesters2.todo?has_content && courseSemesters2.todo == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters2.todo!"-"}</div>
                                                [/#if]

                                            </div>
                                        </div>
                                        <hr class="divide evn-d-lg-none">
                                        <div class="program-result evn-row">
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Total hours</div>
                                                <div class="program-result__number">${courseSemesters2.totalHours!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Attended</div>
                                                <div class="program-result__number">${courseSemesters2.attended!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">Absent</div>
                                                <div class="program-result__number">${courseSemesters2.absent!"-"}</div>
                                            </div>
                                            <div class="program-result__item evn-col evn-col-lg--3 evn-col--6">
                                                <div class="program-result__label">To-do</div>
                                                [#if courseSemesters2.inProgressHours?has_content && courseSemesters2.inProgressHours == -1]
                                                    <div class="program-result__number">-</div>
                                                [#else]
                                                    <div class="program-result__number">${courseSemesters2.inProgressHours!"-"}</div>
                                                [/#if]

                                            </div>
                                        </div>
                                    </div>
                                </div>
                            [/#if]
                        [/#if]


                        <div class="program-summary__button">
                            <a class="evn-btn" href="${courseLink}">Details</a>
                        </div>
                    </div>

                    <div class="program-summary">
                        <div class="program-summary__label">
                            D.
                            <span class="program-summary__label__name">On the job training</span>
                        </div>
                        <div class="program-training evn-row">
                            <div class="program-training__item evn-col-lg--6">
                                <span class="program-training__label">Project name: </span>
                                <span class="program-training__text">${ojtTraining.projectName!"-"}</span>
                            </div>
                            <div class="program-training__item evn-col-lg--6">
                                <span class="program-training__label">Project status: </span>
                                <span class="program-training__text">${ojtTraining.status!"-"}</span>
                            </div>
                            <div class="program-training__item evn-col-lg--6">
                                <span class="program-training__label">Your role: </span>
                                <span class="program-training__text">${ojtTraining.role!"-"}</span>
                            </div>
                            <div class="program-training__item evn-col-lg--6">
                                <span class="program-training__label">Your mentor: </span>
                                <span class="program-training__text">${ojtTraining.mentor!"-"}</span>
                            </div>

                        </div>
                        [#if ojtTraining.id?has_content]
                            <div class="program-summary__button">
                                <a class="evn-btn" href="${ojtLink}/${ojtTraining.uriName!""}">Details</a>
                            </div>
                        [#else]
                            <div class="program-summary__button program-sumary--disabled">
                                <button class="evn-btn evn-btn--disabled" disabled>Details</button>
                            </div>
                        [/#if]
                    </div>

                    <div class="program-summary">
                        <div class="program-summary__label">
                            E.
                            <span class="program-summary__label__name">
                                        Evaluation
                                    </span>
                        </div>
                        <div class="program-tab" role="tablist">

                            <div class="program-tab__list">
                                <label class="program-tab__name
                                            js-program-tab-name" data-content-id="summary-panel">
                                    Summary
                                </label>
                                <label class="program-tab__name
                                            js-program-tab-name" data-content-id="course-panel">
                                    Courses
                                </label>
                                <label class="program-tab__name
                                            js-program-tab-name" data-content-id="job-training-panel">
                                    On job training
                                </label>
                                <label class="program-tab__name
                                            js-program-tab-name" data-content-id="attitude-panel">
                                    Attitude
                                </label>
                            </div>

                            <div class="program-tab__content">
                                <div id="summary-panel" aria-labelledby="summary-tab" class="program-tab__content__item
                                            js-program-tab-content-item">
                                    [#if isFullProgram]
                                        [#if summarySemester1?has_content]
                                            <div class="evn-row-name">SEMESTER 1</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester1.courseAverage!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester1.finalExam!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester1.semesterResult!"-"}</div>
                                                </div>
                                            </div>
                                        [#else]
                                            <div class="evn-row-name">SEMESTER 1</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                            </div>

                                        [/#if]

                                        [#if summarySemester2?has_content]
                                            <div class="evn-row-name">SEMESTER 2</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester2.courseAverage!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester2.finalExam!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester2.semesterResult!"-"}</div>
                                                </div>
                                            </div>
                                        [#else]
                                            <div class="evn-row-name">SEMESTER 2</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                            </div>

                                        [/#if]


                                        [#if summary?has_content]
                                            <div class="evn-row-name">Full Program</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Semesters
                                                    </div>
                                                    <div class="program-summary-item__cell">${summary.semesterAverage!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        On-Job-Training
                                                    </div>
                                                    <div class="program-summary-item__cell">${summary.OJTScore!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Attitude
                                                    </div>
                                                    <div class="program-summary-item__cell">${summary.attitudeScore!"-"}</div>
                                                </div>
                                                <div class="program-summary-item total evn-col-lg--6 evn-col--12">
                                                    <div class="program-summary-item__label">
                                                        Final score
                                                    </div>
                                                    <div class="program-summary-item__cell">${summary.finalScore!"-"}</div>
                                                </div>
                                            </div>
                                        [#else]
                                            <div class="evn-row-name">Full Program</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Semesters
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        On-Job-Training
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Attitude
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item total evn-col-lg--6 evn-col--12">
                                                    <div class="program-summary-item__label">
                                                        Final score
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                            </div>
                                        [/#if]
                                    [/#if]
                                    [#if isOnlyEnrollSemester2]

                                        [#if summarySemester2?has_content]
                                            <div class="evn-row-name">SEMESTER 2</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester2.courseAverage!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester2.finalExam!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester2.semesterResult!"-"}</div>
                                                </div>
                                            </div>
                                        [#else]
                                            <div class="evn-row-name">SEMESTER 2</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                            </div>

                                        [/#if]

                                    [/#if]
                                    [#if isOnlyEnrollSemester1]

                                        [#if summarySemester1?has_content]
                                            <div class="evn-row-name">SEMESTER 1</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester1.courseAverage!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester1.finalExam!"-"}</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">${summarySemester1.semesterResult!"-"}</div>
                                                </div>
                                            </div>
                                        [#else]
                                            <div class="evn-row-name">SEMESTER 1</div>
                                            <div class="evn-row">
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Average of Courses
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Final exam
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                                <div class="program-summary-item evn-col evn-col-lg--3 evn-col--6">
                                                    <div class="program-summary-item__label">
                                                        Semester Result
                                                    </div>
                                                    <div class="program-summary-item__cell">-</div>
                                                </div>
                                            </div>

                                        [/#if]

                                    [/#if]
                                    [#if certPath?has_content]
                                        <div class="program__certificate">
                                            <img class="program__certificate-image" src="${certPath}"/>
                                        </div>
                                    [/#if]
                                </div>

                                <div id="course-panel" aria-labelledby="course-tab" class="program-tab__content__item
                                        js-program-tab-content-item">
                                    <div class="table-session table-courses">
                                        <div class="program-tab-name-status">
                                            <div class="program-tab-name-status-semester choices__inner">
                                                <select class="dropdown" id="dropdown-status-semester">
                                                    <option value="all">full program</option>
                                                    <option value="Semester 1">semester 1</option>
                                                    <option value="Semester 2">semester 2</option>
                                                </select>
                                            </div>
                                            <div class="program-tab-name-status-search">
                                                <input class="program-tab-name-search-input"
                                                       id="program-tab-name-status-search-input" type="text"
                                                       placeholder="Search course here">
                                            </div>
                                        </div>
                                        [#if courseList?has_content]
                                            <div class="js-status-content-semester">
                                                [#list courseList as result]

                                                [/#list]
                                            </div>
                                        [/#if]
                                    </div>
                                </div>

                                <div id="job-training-panel" aria-labelledby="job-training-tab"
                                     class="program-tab__content__item js-program-tab-content-item">

                                    [#if listOjtUserAssessment?has_content]
                                        <div class="table-session table-training">
                                            <table class="table-content table">
                                                <thead>
                                                <tr>
                                                    <th>Criteria</th>
                                                    <th>Score</th>
                                                    <th>Comment</th>
                                                </tr>
                                                </thead>
                                                <tbody class="scroll-table">
                                                [#list listOjtUserAssessment as result]
                                                    <tr>
                                                        <td data-label="Criteria">${result.assessment.nodeName!"-"}</td>
                                                        <td data-label="Score">${result.assessmentScore!"-"}</td>
                                                        <td data-label="Comment">${result.assessmentComment!"-/-"}</td>
                                                    </tr>
                                                [/#list]
                                                </tbody>
                                            </table>
                                        </div>
                                        <div class="table-training">
                                            <div class="program-attitude__commnet__label">
                                                <div class="program-attitude__left-label">General Evaluation</div>
                                            </div>
                                            <div class="table-session">
                                                <table class="table-content table table--last">
                                                    <tbody class="scroll-table">
                                                    <tr>
                                                        <td data-label="Criteria" class="program-statistic__cell">
                                                            OJT evaluation
                                                        </td>
                                                        <td data-label="Score" class="program-statistic__cell">
                                                            ${ojtTraining.score!"-"}
                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                        <div class="program-attitude__commnet__text">
                                            <div class="program-attitude__commnet__text-area">${ojtTraining.comment!"-/-"}</div>
                                        </div>
                                    [/#if]
                                </div>
                                <div id="attitude-panel" aria-labelledby="attitude-tab"
                                     class="program-tab__content__item js-program-tab-content-item">
                                    [#if attitudeList?has_content]
                                        <div class="evn-row table-header-title">
                                            <div class="evn-col table-header__first-column">
                                                <div class="program-attitude__top-label">
                                                    Behavior
                                                </div>
                                            </div>
                                            <div class="evn-col table-header__second-column">
                                                <div class="program-attitude__top-label">
                                                    Score
                                                </div>
                                            </div>
                                            <div class="evn-col table-header__third-column">
                                                <div class="program-attitude__top-label">
                                                    Comment
                                                </div>
                                            </div>
                                        </div>
                                        [#list attitudeList?keys as key]
                                            <div class="attitude-session">
                                                [#assign subkeys = attitudeList[key]]
                                                <div class="program-attitude__left-label evn-col--12">${key}</div>
                                                <div class="table-session table-attitude table-attitude-${(key?index + 1)}">
                                                    <table class="table-content table">
                                                        <tbody class="scroll-table">
                                                        [#list subkeys as subKey]
                                                            <tr>
                                                                <td data-label="Behavior">${subKey.assessment.nodeName!""}</td>
                                                                <td data-label="Score">${subKey.score!"-"}</td>
                                                                <td data-label="Comment">${subKey.comment!"-/-"}</td>
                                                            </tr>
                                                        [/#list]
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        [/#list]
                                        <div class="program-attitude__commnet">
                                            <div class="program-attitude__commnet__label">
                                                <div class="program-attitude__left-label">
                                                    General Evaluation
                                                </div>
                                            </div>

                                            <div class="table-session">
                                                <table class="table table-content">
                                                    <tbody>
                                                    <tr>
                                                        <td data-label="Behavior">
                                                            Attitude evaluation
                                                        </td>
                                                        <td data-label="Score">
                                                            ${userAttitudeResult.attitudeEvaluation!"-"}
                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <div class="program-attitude__commnet__text">
                                                <div class="program-attitude__commnet__text-area">
                                                    ${userAttitudeResult.generalComment!"-/-"}
                                                </div>
                                            </div>
                                        </div>
                                    [/#if]
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            [/#if]
        </div>
    </div>
</div>
