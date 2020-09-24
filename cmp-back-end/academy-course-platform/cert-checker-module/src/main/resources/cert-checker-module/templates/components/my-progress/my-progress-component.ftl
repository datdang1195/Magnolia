[#assign myProgressModel = model.getMyProgressInformation()]
[#assign yearsData = model.getListYear()]
[#assign courseLink = content.courseLink!"#"]
[#assign ojtLink = content.ojtLink!"#"]

<script>
    window.EKINO_ACADEMY['currentPage'] = "participantProgress"

    let yearsData = JSON.parse('${yearsData}');
    let Scrollbar = window.Scrollbar;
    let attitudeSize = 0;
    [#if myProgressModel?has_content]
    attitudeSize = ${myProgressModel['attitudeList']?size};
    let courseList = ${myProgressModel['courseListString']};
    [/#if]

    window.EKINO_ACADEMY.page['participantProgress'] = {
        yearsData:yearsData,
        courseList:courseList,
        attitudeSize:attitudeSize,
        Scrollbar:Scrollbar
    }
</script>

<script type="text/javascript"
        src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/jquery/smooth-scrollbar.js"></script>

<div class="app-wrapper__progress">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="container--aligned-layout program">
        <div class="progressbar-wrapper">
            <div class="arrow-slider-left">
                <div class="arrow-left"></div>
            </div>
            <div class="dotted-line"></div>
            <div class="line"></div>
            <div class="container-slider">
                <div class='slider-progress js-slider-progress' id="participant-progress-slider">
                </div>
            </div>
            <div class="arrow-slider-right">
                <div class="arrow-right"></div>
            </div>
        </div>
        <div class="page__title">
            MY PROGRESS
        </div>
        [#if myProgressModel?has_content]
            [#assign titleInformation = myProgressModel['titleInformation']]
            [#if myProgressModel['courseSemesters2']?has_content]
                [#assign courseSemesters2 = myProgressModel['courseSemesters2']]
            [/#if]
            [#if myProgressModel['courseSemesters1']?has_content]
                [#assign courseSemesters1 = myProgressModel['courseSemesters1']]
            [/#if]
            [#assign ojtTraining = myProgressModel['ojtTraining']]
            [#if myProgressModel['summary']?has_content]
                [#assign summary = myProgressModel['summary']]
            [/#if]
            [#if myProgressModel['summarySemester1']?has_content]
                [#assign summarySemester1 = myProgressModel['summarySemester1']]
            [/#if]
            [#if myProgressModel['summarySemester2']?has_content]
                [#assign summarySemester2 = myProgressModel['summarySemester2']]
            [/#if]

            [#assign courseList = myProgressModel['courseList']]
            [#assign listOjtUserAssessment = myProgressModel['listOjtUserAssessment']]
            [#assign attitudeList = myProgressModel['attitudeList']]
            [#assign userAttitudeResult = myProgressModel['userAttitudeResult']]
            [#assign certPath = myProgressModel['certPath']!]
            [#assign isOnlyEnrollSemester2 = model.isOnlyEnrollSemester2()]
            [#assign isOnlyEnrollSemester1 = model.isOnlyEnrollSemester1()]
            [#assign isFullProgram = model.isFullProgram()]
            <div class="page_sub_title">
                <span class="page_sub_title--line">${titleInformation.phaseName?upper_case!""}</span>
                <span class="page_sub_title--line">${titleInformation.programName?upper_case!""}</span>
            </div>
            <div class="program-status">
                [#assign semesterLists = myProgressModel['semesterLists']!]
                [#list semesterLists as semester]
                    <p class="content__time"><span>${semester.title!""}: </span> ${semester.getSemesterStartDate()!""} - ${semester.getSemesterEndDate()!""}</p>
                [/#list]
                <div class="program-status__value status">status: <span>${titleInformation.status!""}</span></div>
            </div>
            <div class="contentPage">
                <div class="program-item js-program-item">
                    <div class="program-summary">
                        <div class="program-summary__label">A.<span class="program-summary__label__name">COURSES</span>
                        </div>


                        [#if isFullProgram]
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
                        [#if isOnlyEnrollSemester1]
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
                        [/#if]
                        <div class="program-summary__button">
                            <a class="evn-btn" href="${courseLink}">Details</a>
                        </div>
                    </div>
                    <div class="program-summary">
                        <div class="program-summary__label">B.<span class="program-summary__label__name">ON-THE-JOB
                TRAINING</span></div>
                        <div class="program-training">
                            <div class="program-training__item"><span
                                        class="program-training__label">Project name:</span><span
                                        class="program-training__text">${ojtTraining.projectName!"-"}</span></div>


                            <div class="program-training__item"><span
                                        class="program-training__label">Project status:</span><span
                                        class="program-training__text">${ojtTraining.status!"-"}</span></div>


                            <div class="program-training__item"><span
                                        class="program-training__label">Your role:</span><span
                                        class="program-training__text">${ojtTraining.role!"-"}</span></div>

                            <div class="program-training__item"><span
                                        class="program-training__label">Your mentor:</span><span
                                        class="program-training__text">${ojtTraining.mentor!"-"}</span></div>


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
                        <div class="program-summary__label">C.<span
                                    class="program-summary__label__name">EVALUATION</span></div>
                        <div class="program-tab" role="tablist">

                            <div class="program-tab__list">
                                <label class="program-tab__name js-program-tab-name" data-content-id="summary-panel">Summary</label>
                                <label class="program-tab__name js-program-tab-name" data-content-id="course-panel">Courses</label>
                                <label class="program-tab__name js-program-tab-name"
                                       data-content-id="job-training-panel">On job
                                    training</label>
                                <label class="program-tab__name js-program-tab-name" data-content-id="attitude-panel">Attitude</label>
                            </div>

                            <div class="program-tab__content">
                                <div id="summary-panel" aria-labelledby="summary-tab"
                                     class="program-tab__content__item js-program-tab-content-item">


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
                                            <img class="program__certificate-image" src="${certPath}"
                                                 alt="certificate ekino"/>
                                        </div>
                                    [/#if]
                                </div>
                                <div id="course-panel" aria-labelledby="course-tab"
                                     class="program-tab__content__item js-program-tab-content-item">

                                    <div class="table-session table-courses">
                                        <div class="program-tab-name">
                                            <div class="program-tab-name-semester choices__inner">
                                                <select class="dropdown" id="dropdown-semester">
                                                    <option value="all">full program</option>
                                                    <option value="Semester 1">semester 1</option>
                                                    <option value="Semester 2">semester 2</option>
                                                </select>
                                            </div>
                                            <div class="program-tab-name-search">
                                                <input class="program-tab-name-search-input"
                                                       id="program-tab-name-search-input" type="text"
                                                       placeholder="Search course here">
                                            </div>
                                        </div>
                                        <div class="table-session table-courses">
                                            [#if courseList?has_content]
                                                <div class="js-content-semester">
                                                    [#list courseList as result]

                                                    [/#list]
                                                </div>
                                            [/#if]
                                        </div>
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
                                                        <td data-label="Criteria">${result.assessment.nodeName!""}</td>
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
            </div>
        [/#if]
    </div>
</div>
