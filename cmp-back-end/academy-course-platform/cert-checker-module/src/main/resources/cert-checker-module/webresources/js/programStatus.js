$(document).ready(function () {
    let currentPhaseObj;
    let currentProgramObj;
    let heightTableDesk = 1;
    const $body = $('body');
    let elmParticipants = $('.participant-session');
    let elmOnTraining = $('.onJobTraining-session');
    let elmTotalCourse = $('.total-course');
    let elmTotalHours = $('.total-hours');
    let elmCompletedCourse = $('.completed-course');
    let elmProgressCourse = $('.progress-course');
    let elmOpenCourse = $('.open-course');
    let elmSeeMore = $('.see-more-participants');
    let elmSeeLess = $('.see-less-participants');
    let phaseTab = '.js-item-phase-program-status';
    let elmLinkCourse = $('.course-url');


    const $timeLine = $('#program-status-slider');
    if ($timeLine.length) {
        $timeLine
            .yearSlider({
                listYears: yearsData
            })
            .on('year:redirect', function (event, year) {
                window.CMP.common.redirectURL({
                    year
                });
            });

    }

    const resetPrograms = () => {
        let elmProgramList = $('.filter-program-status');
        elmProgramList.html('');
        if (elmProgramList.hasClass('wrapper-filter--center')) {
            elmProgramList.removeClass('wrapper-filter--center');
        }
    }


    const resetOnJobTraining = () => {
        elmOnTraining.addClass('hidden');
        $('.section__onJobTraining').html('');
    }

    const resetCourse = () => {
        let emlCourse = $('.course-session');
        emlCourse.addClass('hidden');
        elmTotalCourse.html('');
        elmTotalHours.html('');
        elmCompletedCourse.html('');
        elmProgressCourse.html('');
        elmOpenCourse.html('');
    }

    const resetParticipantsDesk = () => {
        elmParticipants.addClass('hidden');
        elmSeeLess.addClass('hidden');
        elmSeeMore.addClass('hidden');

        $('.scroll-table-desk').html('');
    }

    const resetParticipantsMobile = () => {
        $('.scroll-table-mobile').html('');
    }

    const resetDataPage = () => {
        resetParticipantsDesk();
        resetParticipantsMobile();
        resetOnJobTraining();
        resetCourse();
    }

    const getPhaseById = (idPhase) => {
        for (var i = 0; i < dataObj.phases.length; i++) {
            if (idPhase === dataObj.phases[i].id) {
                return dataObj.phases[i];
            }
        }
    }

    const getProgramById = (idProgram) => {
        for (var i = 0; i < currentPhaseObj.programs.length; i++) {
            if (idProgram === currentPhaseObj.programs[i].id) {
                return currentPhaseObj.programs[i];
            }
        }
    }

    const initPrograms = () => {
        let tmpPrograms = '';
        for (var i = 0; i < currentPhaseObj.programs.length; i++) {
            let item = currentPhaseObj.programs[i];
            tmpPrograms += `<label class="wrapper-item">
                                    <input type="radio" name="checkProgram" value="${item.id}">
                                    <span class="checkmark"></span>
                                    <div class="content">
                                        <p class="content__title">${item.name}</p>
                                        <p class="content__time">${item.startDate} &#45; ${item.endDate}</p>
                                    </div>
                                </label>`;
        }
        $('.filter-program-status').append(tmpPrograms);
        if (currentPhaseObj.programs.length === 1) {
            $('.filter-program-status').addClass('wrapper-filter--center');
        }
    }

    const getUrlCourse = () => {
        const queryParams = [];
        queryParams.push('year=' + dataObj.year);
        queryParams.push('phase=' + currentPhaseObj.id);
        queryParams.push('program=' + currentProgramObj.id);
        const queryString = queryParams.join("&");
        return courseLink + '?' + queryString;
    }

    const initCourse = () => {
        let objCourse = currentProgramObj.course;
        let elmCourse = $('.course-session');
        let urlCourseDetail = '';
        if (elmCourse.hasClass('hidden')) {
            elmCourse.removeClass('hidden');
            elmTotalCourse.html(objCourse.totalCourse);
            elmTotalHours.html(objCourse.totalHours);
            elmCompletedCourse.html(objCourse.completed);
            elmProgressCourse.html(objCourse.inProgress);
            elmOpenCourse.html(objCourse.todo);
        }
        urlCourseDetail = getUrlCourse();
        elmLinkCourse.attr('href', urlCourseDetail);
    }

    const initOnJobTraining = () => {
        let tmpTraining = '';
        let elmTraining = $('.onJobTraining-session');
        if (elmTraining.hasClass('hidden')) {
            elmTraining.removeClass('hidden');
        }
        for (var i = 0; i < currentProgramObj.onJobTraining.length; i++) {
            let item = currentProgramObj.onJobTraining[i];
            let path = ojtLink + "/" + item.uriName;
            tmpTraining += `<div class="onJobTraining-item">
                                    <div class="wrapper-groups">
                                        <div class="info-group">
                                            <p class="group__label">Project name&#58;</p>
                                            <p class="group__value">${item.projectName}</p>
                                        </div>
                                        
                                        <div class="info-group">
                                            <p class="group__label">Participants&#58;</p>
                                            <p class="group__value">${item.participants}</p>
                                        </div>

                                        <div class="info-group">
                                            <p class="group__label">Project lead&#58;</p>
                                            <p class="group__value">${item.projectLead}</p>
                                        </div>

                                        <div class="info-group">
                                            <p class="group__label">Start date&#58;</p>
                                            <p class="group__value">${item.startDate}</p>
                                        </div>

                                        <div class="info-group">
                                            <p class="group__label">Project status&#58;</p>
                                            <p class="group__value">${item.projectStatus}</p>
                                        </div>
                                        
                                        <div class="info-group">
                                            <p class="group__label">End date&#58;</p>
                                            <p class="group__value">${item.endDate}</p>
                                        </div>
                                    </div>
                                    <div class="section__btn">
                                        <a class="evn-btn" href="${path}">Details</a>
                                    </div>
                                </div>`;
        }
        $('.section__onJobTraining').append(tmpTraining);
    }

    const initParticipants = (selectElm) => {
        let tmpParticipants = '';
        for (var i = 0; i < currentProgramObj.participants.length; i++) {
            let item = currentProgramObj.participants[i];
            let path = contextPath + "/.resources/cert-checker-module/webresources/images/check-here.png";
            let detailPath = participantLink + item.link;
            tmpParticipants += `<tr>
                                            <td data-label="No">${i + 1}</td>
                                            <td data-label="Name">${item.name}</td>
                                            <td data-label="Department">${item.team}</td>
                                            <td data-label="Profile">${item.profile}</td>
                                            <td data-label="Semester">${item.semester}</td>
                                            <td data-label="Participation Status">
                                                <a class="link-check-here" href="${detailPath}">
                                                    ${item.status}<img src="${path}" class="icon-check-here"
                                                        alt="go to participants detail">
                                                </a>
                                            </td>
                                        </tr>`;
        }
        $(`.${selectElm}`).append(tmpParticipants);
    }

    const initParticipantsDesk = () => {
        if (elmParticipants.hasClass('hidden')) {
            elmParticipants.removeClass('hidden');
        }
        $('.total-participants').html(currentProgramObj.participants.length + ' total participants');
        initParticipants('scroll-table-desk');
    }

    const initParticipantsMobile = () => {
        if (elmParticipants.hasClass('hidden')) {
            elmParticipants.removeClass('hidden');
        }
        initParticipants('scroll-table-mobile');
    }

    const initDataPage = () => {
        initParticipantsDesk();
        initParticipantsMobile();
        setHeightTableDesk();
        initCourse();
        initOnJobTraining();
    }

    const setHeightTableDesk = () => {
        if (heightTableDesk > 1) {
            heightTableDesk = 1;
        }
        heightTableDesk += $('.table-head-desk')[0].clientHeight;
        $('.scroll-table-desk > tr').each(function (item) {
            if (item < 5) {
                heightTableDesk += $(this)[0].clientHeight;
            } else {
                elmSeeMore.removeClass('hidden');
            }
        })
        $('.table-desktop').css('height', heightTableDesk + 'px');
    }

    $('input[name="checkPhase"]').click(function (event) {
        let idPhase = event.target.value
        currentPhaseObj = getPhaseById(idPhase);
        resetPrograms();
        resetDataPage();
        initPrograms();
        if (currentPhaseObj.programs.length === 1) {
            currentProgramObj = currentPhaseObj.programs[0];
            initDataPage();
            $('input[name="checkProgram"]')[0].checked = true;
        }
    });

    $body.delegate('input[name="checkProgram"]', 'click', event => {
        let idProgram = event.target.value;
        currentProgramObj = getProgramById(idProgram);
        resetDataPage();
        initDataPage();
    });

    const isExistPhase = (idPhase) => {
        for (var i = 0; i < dataObj.phases.length; i++) {
            if (idPhase === dataObj.phases[i].id) {
                return true;
            }
        }
        return false;
    }

    elmSeeMore.click(function () {
        $(this).addClass('hidden');
        elmSeeLess.removeClass('hidden');
        let heightTable = $('.table-desktop > .table-content')[0].clientHeight + 1;
        $('.table-desktop').css('height', heightTable + 'px');
    })

    elmSeeLess.click(function () {
        $(this).addClass('hidden');
        elmSeeMore.removeClass('hidden');
        $('.table-desktop').css('height', heightTableDesk + 'px');
    })

    $(window).resize(function (e) {
        e.stopPropagation();
        if (window.innerWidth > 768) {
            if (!elmSeeMore.hasClass('hidden') && elmSeeMore.length) {
                setHeightTableDesk();
            }
        }
    })

    const disablePhase = () => {
        $('.wrapper-item > input[name="checkPhase"]').each(function (index) {
            if (!isExistPhase($(this)[0].value)) {
                $(this)[0].disabled = true;
            }
        })
    }

    const disabledPhaseTab = () => {
        phases.map(phase => {
            if (phase.disabled) {
                $(`${phaseTab}[data-value=${phase.id}]`).addClass('disabled');
            }
        })
    }

    const resetActivePhase = () => {
        $(phaseTab).each(function () {
            if ($(this).hasClass('active')) {
                $(this).removeClass('active');
            }
        })
    }

    $(phaseTab).click(function () {
        const phaseID = $(this).data('value');
        const phase = phases.find(phase => phase.id === phaseID);
        if (!phase.disabled) {
            resetActivePhase();
            $(this).addClass('active');
            currentPhaseObj = getPhaseById(phaseID);
            resetPrograms();
            resetDataPage();
            initPrograms();
            if (currentPhaseObj.programs.length === 1) {
                currentProgramObj = currentPhaseObj.programs[0];
                initDataPage();
                $('input[name="checkProgram"]').attr('checked', true);
            }
        }
    })

    const initiation = () => {
        disablePhase();
        disabledPhaseTab();
    }

    initiation();
});
