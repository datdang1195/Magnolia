$(document).ready(function () {
    let names = ['sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat'];
    let months = [
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'June',
        'July',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec',
    ];
    let fullNamemonths = [
        'January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July',
        'August',
        'September',
        'October',
        'November',
        'December',
    ];
    let listPhase = [];
    let listGroup = [];
    let currentRole;
    let dataObject = [];
    let selectedMonth;
    let selectedWeek;
    let numberPreviousMonth = 0;
    let path = window.location.href.split(/[?#]/)[0];
    const $body = $('body');

    function getQueryVariable(strDay) {
        var query = window.location.search.substring(1);
        const result = query
            .split('&')
            .map(pair => pair.split('='))
            .find(([k]) => k === strDay);
        if (result) {
            return decodeURIComponent(result[1]);
        }
    }

    let strDay = getQueryVariable('selectedMonth');
    if (strDay) {
        var date = new Date(strDay);
    } else {
        var date = new Date();
    }

    function getAllDay(year, month) {
        var date = new Date(year, month, 1);
        var result = [];
        while (date.getMonth() == month) {
            var item = {
                name: names[date.getDay()],
                date: date.getDate(),
                month: months[date.getMonth()],
                year: date.getFullYear(),
                numberMonth: date.getMonth() + 1,
                fullDate: formatDate(
                    date.getFullYear(),
                    date.getMonth() + 1,
                    date.getDate()
                ),
            };
            result.push(item);
            date.setDate(date.getDate() + 1);
        }
        return result;
    }

    function formatDate(year, month, date) {
        let formatMonth = month < 10 ? `0${month}` : `${month}`;
        let formatDate = date < 10 ? `0${date}` : `${date}`;
        return year + '-' + formatMonth + '-' + formatDate;
    }

    function numberDatePreviousMonth(selectedMonth) {
        for (var i = 0; i < names.length; i++) {
            if (names[i] === selectedMonth[0].name) {
                return i;
            }
        }
    }

    function addDatePreMonth(selectedMonth, previousMonth, number) {
        for (var i = 0; i < number; i++) {
            selectedMonth.unshift(
                previousMonth[previousMonth.length - 1 - i]
            );
        }
    }

    function addDateNextMonth(selectedMonth, nextMonth, number) {
        for (var i = 0; i < number; i++) {
            selectedMonth.push(nextMonth[i]);
        }
    }

    function isToday(dateParameter) {
        var today = new Date();
        return (
            dateParameter.getDate() === today.getDate() &&
            dateParameter.getMonth() === today.getMonth() &&
            dateParameter.getFullYear() === today.getFullYear()
        );
    }

    function isDaySelectMonth(date, month) {
        return date.getMonth() + 1 === month;
    }

    function isFirstDayMonth(date) {
        return date === 1;
    }

    function showSelectdDate(day) {
        let selectedDate =
            fullNamemonths[day.getMonth()] + ' ' + day.getFullYear();
        $('#date__lable').html(selectedDate);
    }

    function getDateWeek(year, month, positionFirst) {
        var date = new Date(year, month, positionFirst);
        var result = [];
        for (var i = 0; i < 7; i++) {
            var item = {
                name: names[date.getDay()],
                date: date.getDate(),
                month: months[date.getMonth()],
                year: date.getFullYear(),
                numberMonth: date.getMonth() + 1,
                fullDate: formatDate(
                    date.getFullYear(),
                    date.getMonth() + 1,
                    date.getDate()
                ),
            };
            result.push(item);
            date.setDate(date.getDate() + 1);
        }
        return result;
    }

    // start filter

    function hasExistList(idSelected, listSelect) {
        for (var i = 0; i < listSelect.length; i++) {
            if (idSelected === listSelect[i]) {
                return true;
            }
        }
        return false;
    }

    function initDefaultOptionFilter() {
        if (sessionStorage.hasOwnProperty('listPhase')) {
            listGroup = sessionStorage.listGroup.length ? sessionStorage.listGroup.split(',') : [];
            listPhase = sessionStorage.listPhase.length ? sessionStorage.listPhase.split(',') : [];
        } else {
            if (objData.phases.length) {
                listPhase.push(objData.phases[0].id);
            }
            for (var i = 0; i < listGroupProgram.length; i++) {
                listGroup.push(listGroupProgram[i].id);
            }
        }

        $('input[name="selectMultiPhase"]').each(function () {
            if (hasExistList($(this)[0].value, listPhase)) {
                $(this).attr('checked', true);
            }
        });
        $('input[name="checkGroupProgram"]').each(function () {
            if (hasExistList($(this)[0].value, listGroup)) {
                $(this).attr('checked', true);
            }
        });
    }

    function toggleFilterByRole() {
        let elmParticipant = $('.participants-role');
        let elmSuperTrainer = $('.supervisors-trainer-role');
        if (isSupervisorOrTrainer()) {
            if (elmSuperTrainer.hasClass('hidden-role')) {
                elmSuperTrainer.removeClass('hidden-role');
            }
            elmParticipant.addClass('hidden-role');
        } else {
            if (elmParticipant.hasClass('hidden-role')) {
                elmParticipant.removeClass('hidden-role');
            }
            elmSuperTrainer.addClass('hidden-role');
        }
    }

    function setStyleTabRole(role) {
        if (roleAccount.length > 1) {
            let elmRoleActive = $('.role-active');
            $('.role-item').each(function () {
                if ($(this)[0].dataset.role === role) {
                    $(this).addClass('active');
                } else {
                    if ($(this).hasClass('active')) {
                        $(this).removeClass('active');
                    }
                }
            })
            if (role === 'supervisor') {
                elmRoleActive.removeClass('switch-right');
                elmRoleActive.addClass('switch-left');
            } else {
                if (role === 'trainer' && hasExistList('participant', roleAccount)) {
                    elmRoleActive.removeClass('switch-right');
                    elmRoleActive.addClass('switch-left');
                } else {
                    elmRoleActive.removeClass('switch-left');
                    elmRoleActive.addClass('switch-right');
                }
            }
        }
    }

    function showTabRole() {
        let elmTab = $('.container-swipe-role');
        if (roleAccount.length > 1) {
            if (elmTab.hasClass('hidden')) {
                elmTab.removeClass('hidden');
            }
        } else {
            elmTab.addClass('hidden');
        }
    }

    function setCurrentRole() {
        if (roleAccount.length > 1 && sessionStorage.hasOwnProperty('currentRole')) {
            if (hasExistList(sessionStorage.currentRole, roleAccount)) {
                currentRole = sessionStorage.currentRole;
            } else {
                currentRole = roleAccount[0];
            }
        } else {
            currentRole = roleAccount[0];
        }
        toggleFilterByRole();
        showTabRole();
        setStyleTabRole(currentRole);
    }

    function isSupervisorOrTrainer() {
        return currentRole === 'supervisor' || currentRole === 'trainer';
    }

    function saveCacheForFilter() {
        sessionStorage.setItem('currentRole', currentRole);
        sessionStorage.setItem('listPhase', listPhase);
        sessionStorage.setItem('listGroup', listGroup);
    }
    // end filter

    function isFilterByGroup(groupId, filterList) {
        for (var i = 0; i < filterList.length; i++) {
            if (groupId === filterList[i]) {
                return true;
            }
        }
        return false;
    }

    function getPhaseById(phaseId, listPhase) {
        for (var i = 0; i < listPhase.length; i++) {
            if (phaseId === listPhase[i].id) {
                return listPhase[i];
            }
        }
    }

    function formatGroupProgram(str) {
        return listGroupProgram.filter(itm => itm.id === str)[0].className;
    }

    function getAllSession(listPhase) {
        let listSession = [];
        for (var i = 0; i < listPhase.length; i++) {
            let phaseItem = getPhaseById(listPhase[i], objData.phases);
            for (var j = 0; j < phaseItem.programs.length; j++) {
                if (
                    isFilterByGroup(
                        phaseItem.programs[j].groupProgram,
                        listGroup
                    )
                ) {
                    let temp = formatGroupProgram(
                        phaseItem.programs[j].id
                    );
                    let sessionList = phaseItem.programs[j].listSession;
                    let arr = [];
                    for (var k = 0; k < sessionList.length; k++) {
                        let obj = Object.assign(sessionList[k], {
                            groupName: temp,
                        });
                        arr.push(obj);
                    }
                    listSession = listSession.concat(arr);
                }
            }
        }
        return listSession;
    }

    function getSessionByDate(listSession, date) {
        let array = [];
        for (var i = 0; i < listSession.length; i++) {
            if (listSession[i].date === date) {
                array.push(listSession[i]);
            }
        }
        return array;
    }

    function sortSessionByDate(listSession) {
        for (var i = 0; i < selectedMonth.length; i++) {
            let arraySession = getSessionByDate(
                listSession,
                selectedMonth[i].fullDate
            );
            if (arraySession.length) {
                let obj = {
                    date: selectedMonth[i].fullDate,
                    listSesstion: arraySession,
                };
                dataObject.push(obj);
            }
        }
    }

    function filterSessionTrainer(listSession) {
        let sessionTrainer = [];
        for (var i = 0; i < listSession.length; i++) {
            if (listSession[i].roleTrainer) {
                sessionTrainer.push(listSession[i]);
            }
        }
        return sessionTrainer;
    }
    // end filter

    initDefaultOptionFilter();
    setCurrentRole();
    generateScheduleDesktop(date);
    generateScheduleMobile(date);
    setPositionContent();

    function initTmpSupervisor() {
        let templateSchedule = '';
        let listSession = getAllSession(listPhase);
        if (currentRole === 'trainer') {
            let sessionTrainer = filterSessionTrainer(listSession);
            sortSessionByDate(sessionTrainer);
        } else {
            sortSessionByDate(listSession);
        }
        for (var i = 0; i < selectedMonth.length; i++) {
            let flag;
            let objSchedule;
            for (var j = 0; j < dataObject.length; j++) {
                if (selectedMonth[i].fullDate === dataObject[j].date) {
                    objSchedule = dataObject[j];
                    flag = 1;
                    break;
                }
            }
            if (flag) {
                let templateListSession = '';
                let templateSeeMore = '';
                let tmpAllSession = '';
                let arrayList = objSchedule.listSesstion;
                if (arrayList.length > 2) {
                    templateSeeMore += `<p class="seeMore__title">${arrayList.length -
                    2} more</p>`;
                }
                for (var j = 0; j < arrayList.length; j++) {
                    let session =
                        ' &#8722; ' + arrayList[j].session;
                    let strContent =
                        arrayList[j].title +
                        ' &#8722; ' +
                        arrayList[j].session;
                    tmpAllSession += `<div class="session-item">
                                            <a href="${courseDetailLink}/${arrayList[j].name}" class="wrapper-session">
                                                <p class="circle ${arrayList[j].groupName}"></p>
                                                <p class="session__title--style session-title">${strContent}</p>
                                            </a>
                                        </div>`;
                    if (arrayList.length === 1) {
                        templateListSession += `<div class="subject-item">
                                                        <a href="${courseDetailLink}/${arrayList[j].name}" class="session__title--style link-session">
                                                            <span class="circle ${arrayList[j].groupName}"></span>
                                                            <span class="sub__title-1">${strContent}</span>
                                                        </a>
                                                    </div>`;
                    } else {
                        if (j < 2) {
                            templateListSession += `<div class="subject-item">
                                                        <a href="${courseDetailLink}/${arrayList[j].name}" class="session__title--style link-session">
                                                            <span class="circle ${arrayList[j].groupName}"></span>
                                                            <span class="sub__title">${arrayList[j].name}</span>
                                                            <span>${session}</span>
                                                        </a>
                                                    </div>`;
                        }
                    }
                }
                templateSchedule += `<div class="date__item">
                                            <div class="day__number">
                                                <span class="month__title"></span>
                                                <span class="number">${selectedMonth[i].date}</span>
                                            </div>
                                                <div class="current-day__title"></div>
                                            <div class="subject">
                                                ${templateListSession}
                                            </div>
                                            <div class="see-more">
                                                ${templateSeeMore}
                                                <div class="dialog-wrapper dialog-desktop hidden-dialog">
                                                    <div class="dialog-content">
                                                        <p class="dialog-content--style daysOfWeek">${selectedMonth[i].name}</p>
                                                        <p class="dialog-content--style datesOfMonth">${selectedMonth[i].date}</p>
                                                        <div class="session-program">
                                                            ${tmpAllSession}
                                                        </div>
                                                        <div class="icon-close-schedule">
                                                            <p class="close">&times;</p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>`;
            } else {
                templateSchedule += `<div class="date__item">
                                    <div class="day__number">
                                        <span class="month__title"></span>
                                        <span class="number">${selectedMonth[i].date}</span>
                                    </div>
                                    <div class="current-day__title"></div>
                                </div>`;
            }
        }
        return templateSchedule;
    }

    function initTmpParticipant() {
        let templateSchedule = '';
        for (var i = 0; i < selectedMonth.length; i++) {
            let flag;
            let objSchedule;
            for (var j = 0; j < dataSchedule.length; j++) {
                if (selectedMonth[i].fullDate === dataSchedule[j].date) {
                    objSchedule = dataSchedule[j];
                    flag = 1;
                    break;
                }
            }
            if (flag) {
                let contentShow = '';
                if (objSchedule) {
                    contentShow =
                        objSchedule.title +
                        ' &#8722; ' +
                        objSchedule.session;
                }
                let imageAttend = contextPath + '/.resources/cert-checker-module/webresources/images/';
                imageAttend += objSchedule.attendance === 1 ? 'attend.svg' : 'absent.svg';
                let display = objSchedule.attendance < 0 ? "display: none" : "";
                templateSchedule += `<div class="date__item">
                                        <div class="day__number">
                                            <span class="month__title"></span>
                                            <span class="number">${selectedMonth[i].date}</span>
                                        </div>
                                        <div class="current-day__title"></div>
                                        <div class="subject">
                                            <div class="subject-item">
                                                <a href="${courseDetailLink}/${objSchedule.name}" class="session__title--style link-session">
                                                    <span>${contentShow}</span>
                                                </a>
                                            </div>
                                        </div>
                                        <div class="section__attend-trainer" style="${display}">
                                                <img src="${imageAttend}" alt="icon absent">
                                        </div>
                                    </div>`;
            } else {
                templateSchedule += `<div class="date__item">
                                        <div class="day__number">
                                            <span class="month__title"></span>
                                            <span class="number">${selectedMonth[i].date}</span>
                                        </div>
                                        <div class="current-day__title"></div>
                                    </div>`;
            }
        }
        return templateSchedule;
    }

    function initTmpParticipantMobile() {
        let templateScheduleMobile = '';
        for (var i = 0; i < selectedWeek.length; i++) {
            let flag;
            var objSchedule;
            for (var j = 0; j < dataSchedule.length; j++) {
                if (selectedWeek[i].fullDate === dataSchedule[j].date) {
                    objSchedule = dataSchedule[j];
                    flag = 1;
                    break;
                }
            }
            if (flag) {
                let session = ' &#8722; ' + objSchedule.session;
                let imageAttend = contextPath + '/.resources/cert-checker-module/webresources/images/';
                imageAttend += objSchedule.attendance === 1 ? 'attend.svg' : 'absent.svg';
                let display = objSchedule.attendance < 0 ? "display: none" : "";
                templateScheduleMobile += `<div class="schedule__item">
                                        <div class="day__number">
                                            <span class="month__title"></span>
                                            <span class="number">${selectedWeek[i].date}</span>
                                        </div>
                                        <div class="current-day__title"></div>
                                        <div class="subject">
                                            <div class="subject-item">
                                                <a href="${courseDetailLink}/${objSchedule.name}" class="session__title--style link-session">
                                                    <span class="sub__title">${objSchedule.name}</span>
                                                    <span>${session}</span>
                                                </a>
                                            </div>
                                        </div>
                                        <div class="section__attend-trainer" style="${display}">
                                             <img src="${imageAttend}" alt="icon absent">
                                        </div>

                                    </div>`;
            } else {
                templateScheduleMobile += `<div class="schedule__item">
                                        <div class="day__number">
                                            <span class="month__title"></span>
                                            <span class="number">${selectedWeek[i].date}</span>
                                        </div>
                                        <div class="current-day__title"></div>
                                    </div>`;
            }
        }
        return templateScheduleMobile;
    }

    function initTmpSupervisorMobile() {
        let templateScheduleMobile = '';
        for (var i = 0; i < selectedWeek.length; i++) {
            let flag;
            var objSchedule;
            for (var j = 0; j < dataObject.length; j++) {
                if (selectedWeek[i].fullDate === dataObject[j].date) {
                    objSchedule = dataObject[j];
                    flag = 1;
                    break;
                }
            }
            if (flag) {
                let templateListSession = '';
                let templateSeeMore = '';
                let tmpAllSession = '';
                let arrayList = objSchedule.listSesstion;
                if (arrayList.length > 1) {
                    templateSeeMore += `<p class="seeMore__title">${arrayList.length -
                    1} more</p>`;
                }
                for (var j = 0; j < arrayList.length; j++) {
                    let strContent =
                        arrayList[j].title +
                        ' &#8722; ' +
                        arrayList[j].session;
                    let session =
                        ' &#8722; ' + arrayList[j].session;
                    tmpAllSession += `<div class="session-item">
                                                <a href="${courseDetailLink}/${arrayList[j].name}" class="wrapper-session">
                                                <p class="circle ${arrayList[j].groupName}"></p>
                                                <p class="session__title--style session-title">${strContent}</p>
                                            </a>
                                        </div>`;
                    if (j < 1) {
                        templateListSession += `<div class="subject-item">
                                                    <a href="${courseDetailLink}/${arrayList[j].name}" class="session__title--style link-session">
                                                        <span class="circle ${arrayList[j].groupName}"></span>
                                                        <span class="sub__title">${arrayList[j].name}</span>
                                                        <span>${session}</span>
                                                    </a>
                                                </div>`;
                    }
                }

                templateScheduleMobile += `<div class="schedule__item">
                                        <div class="day__number">
                                            <span class="month__title"></span>
                                            <span class="number">${selectedWeek[i].date}</span>
                                        </div>
                                        <div class="current-day__title"></div>
                                        <div class="subject">
                                            ${templateListSession}
                                        </div>
                                        <div class="see-more">
                                            ${templateSeeMore}
                                            <div class="dialog-wrapper dialog-mobile hidden-dialog">
                                                <div class="dialog-content">
                                                    <p class="dialog-content--style daysOfWeek">${selectedWeek[i].name}</p>
                                                    <p class="dialog-content--style datesOfMonth">${selectedWeek[i].date}</p>
                                                    <div class="session-program">
                                                        ${tmpAllSession}
                                                    </div>
                                                    <div class="icon-close-schedule">
                                                        <p class="close">&times;</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>`;
            } else {
                templateScheduleMobile += `<div class="schedule__item">
                                        <div class="day__number">
                                            <span class="month__title"></span>
                                            <span class="number">${selectedWeek[i].date}</span>
                                        </div>
                                        <div class="current-day__title"></div>
                                    </div>`;
            }
        }
        return templateScheduleMobile;
    }

    function insertInfoCalendarDesk(numberPreviousMonth, day) {
        $('.date__item').each(function (index) {
            if (
                isToday(
                    new Date(
                        selectedMonth[index].numberMonth +
                        '-' +
                        selectedMonth[index].date +
                        '-' +
                        selectedMonth[index].year
                    )
                )
            ) {
                $(this).addClass('current-day');
            } else {
                if ($(this).hasClass('current-day')) {
                    $(this).removeClass('current-day');
                    $(this)
                        .children('.current-day__title')
                        .html('');
                }
            }

            if (isFirstDayMonth(selectedMonth[index].date)) {
                $(this)
                    .children('.day__number')
                    .children('.month__title')
                    .html(selectedMonth[index].month);
            } else {
                $(this)
                    .children('.day__number')
                    .children('.month__title')
                    .html('');
                if (
                    numberPreviousMonth &&
                    numberPreviousMonth === index + 1
                ) {
                    $(this)
                        .children('.day__number')
                        .children('.month__title')
                        .html(selectedMonth[index].month);
                }
            }

            if (!isDaySelectMonth(day, selectedMonth[index].numberMonth)) {
                $(this).addClass('not-current-month');
            } else {
                if ($(this).hasClass('not-current-month')) {
                    $(this).removeClass('not-current-month');
                }
            }
            const dateName = selectedMonth[index].name;
            if (dateName === 'sat' || dateName === 'sun') {
                $(this).addClass('date__weekend');
            }
        });
    }

    function insertInfoCalendarMobile(day) {
        $('.schedule__item').each(function (index) {
            if (
                isToday(
                    new Date(
                        selectedWeek[index].numberMonth +
                        '-' +
                        selectedWeek[index].date +
                        '-' +
                        selectedWeek[index].year
                    )
                )
            ) {
                $(this).addClass('current-day');
            } else {
                if ($(this).hasClass('current-day')) {
                    $(this).removeClass('current-day');
                    $(this)
                        .children('.current-day__title')
                        .html('');
                }
            }

            if (isFirstDayMonth(selectedWeek[index].date)) {
                $(this)
                    .children('.day__number')
                    .children('.month__title')
                    .html(selectedWeek[index].month);
            } else {
                $(this)
                    .children('.day__number')
                    .children('.month__title')
                    .html('');
                if (index !== 6) {
                    if (isFirstDayMonth(selectedWeek[index + 1].date)) {
                        $(this)
                            .children('.day__number')
                            .children('.month__title')
                            .html(selectedWeek[index].month);
                    }
                }
            }

            if (!isDaySelectMonth(day, selectedWeek[index].numberMonth)) {
                $(this).addClass('not-current-month');
            } else {
                if ($(this).hasClass('not-current-month')) {
                    $(this).removeClass('not-current-month');
                }
            }

            const dateName = selectedMonth[index].name;
            if (dateName === 'sat' || dateName === 'sun') {
                $(this).addClass('date__weekend');
            }
        });
    }

    function resetTmpDesk() {
        dataObject = [];
        $('#wrapper-date-desk').html('');
    }

    function resetTmpMobile() {
        dataObject = [];
        $('#wrapper-date-mobile').html('');
    }

    function generateScheduleDesktop(day) {
        selectedMonth = getAllDay(day.getFullYear(), day.getMonth());
        let selectedMonthNumber = day.getMonth() + 1;
        let previousMonth, nextMonth;
        if (selectedMonthNumber >= 2 && selectedMonthNumber <= 11) {
            previousMonth = getAllDay(
                day.getFullYear(),
                day.getMonth() - 1
            );
            nextMonth = getAllDay(day.getFullYear(), day.getMonth() + 1);
        } else {
            let d = new Date();
            if (selectedMonthNumber === 1) {
                d.setFullYear(day.getFullYear() - 1);
                d.setMonth(11);
                previousMonth = getAllDay(d.getFullYear(), 11);
                nextMonth = getAllDay(
                    day.getFullYear(),
                    day.getMonth() + 1
                );
            } else {
                d.setFullYear(day.getFullYear() + 1);
                d.setMonth(0);
                previousMonth = getAllDay(day.getFullYear(), 10);
                nextMonth = getAllDay(d.getFullYear(), 0);
            }
        }

        numberPreviousMonth = numberDatePreviousMonth(selectedMonth);
        let numberNextMonth;
        if (numberPreviousMonth + selectedMonth.length > 35) {
            numberNextMonth =
                42 - selectedMonth.length - numberPreviousMonth;
        } else {
            numberNextMonth =
                35 - selectedMonth.length - numberPreviousMonth;
        }
        addDatePreMonth(selectedMonth, previousMonth, numberPreviousMonth);
        addDateNextMonth(selectedMonth, nextMonth, numberNextMonth);

        showSelectdDate(day);

        let templateSchedule = '';

        if (isSupervisorOrTrainer()) {
            templateSchedule = initTmpSupervisor();
        } else {
            templateSchedule = initTmpParticipant();
        }
        $('#wrapper-date-desk').html(templateSchedule);

        insertInfoCalendarDesk(numberPreviousMonth, day);
    }

    function generateScheduleMobile(day) {
        let positionFirstDayWeek = day.getDate() - day.getDay();
        selectedWeek = getDateWeek(
            day.getFullYear(),
            day.getMonth(),
            positionFirstDayWeek
        );

        let templateScheduleMobile = '';

        if (isSupervisorOrTrainer()) {
            templateScheduleMobile = initTmpSupervisorMobile();
        } else {
            templateScheduleMobile = initTmpParticipantMobile();
        }
        $('#wrapper-date-mobile').html(templateScheduleMobile);

        insertInfoCalendarMobile(day);
    }

    function getPreviousWeek() {
        let month = date.getMonth();
        date.setDate(date.getDate() - 7);
        if (month !== date.getMonth()) {
            saveCacheForFilter();
            window.location.href = decodeURI(path + `?selectedMonth=${formatDate(date.getFullYear(), date.getMonth() + 1, date.getDate())}`);
        } else {
            generateScheduleMobile(date);
            $('.sub__title').trigger('shortNameSession');
        }
    }

    function getNextWeek() {
        let month = date.getMonth();
        date.setDate(date.getDate() + 7);
        if (month !== date.getMonth()) {
            saveCacheForFilter();
            window.location.href = decodeURI(path + `?selectedMonth=${formatDate(date.getFullYear(), date.getMonth() + 1, date.getDate())}`);
        } else {
            generateScheduleMobile(date);
            $('.sub__title').trigger('shortNameSession');
        }
    }

    function addLineCalendar() {
        $('.datepicker-days').append('<div class="line"></div>');
    }

    function setDateCalendarMobile(date) {
        let month = date.getMonth();
        let year = date.getFullYear();
        let day = date.getDate();
        $('#datetimepicker').datepicker(
            'update',
            new Date(year, month, day)
        );
    }

    function setDateCalendarDesktop(date) {
        let month = date.getMonth();
        let year = date.getFullYear();
        let day = date.getDate();
        $('#datetimepicker').datepicker(
            'setDate',
            new Date(year, month, day)
        );
    }

    function updateDataCalendar(dateUpdate) {
        let month = date.getMonth();
        let year = date.getFullYear();
        date.setDate(dateUpdate.getDate());
        date.setMonth(dateUpdate.getMonth());
        date.setFullYear(dateUpdate.getFullYear());
        if (
            month !== dateUpdate.getMonth() ||
            year !== dateUpdate.getFullYear()
        ) {
            saveCacheForFilter();
            window.location.href = decodeURI(path + `?selectedMonth=${formatDate(date.getFullYear(), date.getMonth() + 1, date.getDate())}`);
        } else {
            generateScheduleMobile(date);
            $('.sub__title').trigger('shortNameSession');
        }
    }

    function resetAllModal() {
        $('.dialog-wrapper').each(function () {
            $(this).addClass('hidden-dialog');
        });
    }

    function updateCalendarFilter() {
        resetTmpDesk();
        resetTmpMobile();
        let templateScheduleDesk = initTmpSupervisor();
        let templateScheduleMobile = initTmpSupervisorMobile();
        $('#wrapper-date-desk').html(templateScheduleDesk);
        $('#wrapper-date-mobile').html(templateScheduleMobile);
        insertInfoCalendarDesk(numberPreviousMonth, date);
        insertInfoCalendarMobile(date);
    }

    $('input[name="checkGroupProgram"]').click(function (event) {
        listGroup = [];
        $('input[name="checkGroupProgram"]').each(function () {
            let elm = $(this)[0];
            if (elm.checked) {
                listGroup.push(elm.value);
            }
        });
        updateCalendarFilter();
        $('.sub__title').trigger('shortNameSession');
    });

    $('input[name="selectMultiPhase"]').click(function (event) {
        listPhase = [];
        $('input[name="selectMultiPhase"]').each(function () {
            let elm = $(this)[0];
            if (elm.checked) {
                listPhase.push(elm.value);
            }
        });
        updateCalendarFilter();
        $('.sub__title').trigger('shortNameSession');
    });

    function initShortNameSession() {
        let configEllipsis = {
            ellipsis: '…',
            className: '.sub__title',
            responsive: true,
            lines: 2,
        };
        Ellipsis(configEllipsis);
    }

    $(document).on('shortNameSession', '.sub__title', function () {
        initShortNameSession();
    });

    $('#previous-month').click(function () {
        let selectedMonth = date.getMonth() + 1;
        if (selectedMonth === 1) {
            date.setFullYear(date.getFullYear() - 1);
            date.setMonth(11);
        } else {
            date.setMonth(date.getMonth() - 1);
        }
        saveCacheForFilter();
        window.location.href = decodeURI(path + `?selectedMonth=${formatDate(date.getFullYear(), date.getMonth() + 1, date.getDate())}`);
    });

    $('#next-month').click(function () {
        let selectedMonth = date.getMonth() + 1;
        if (selectedMonth === 12) {
            date.setFullYear(date.getFullYear() + 1);
            date.setMonth(0);
        } else {
            date.setMonth(date.getMonth() + 1);
        }
        saveCacheForFilter();
        window.location.href = decodeURI(path + `?selectedMonth=${formatDate(date.getFullYear(), date.getMonth() + 1, date.getDate())}`);
    });

    function setPositionContent() {
        if (window.innerWidth <= 768) {
            $('html,body').animate({
                    scrollTop: $('.calendar').offset().top - 5,
                },
                300
            );
        }
    }

    $('.arrow-previous-week').click(function () {
        getPreviousWeek();
    });

    $('.arrow-next-week').click(function () {
        getNextWeek();
    });

    $('.role-item').click(function (event) {
        currentRole = event.target.dataset.role;
        sessionStorage.setItem('currentRole', currentRole);
        resetTmpDesk();
        resetTmpMobile();
        generateScheduleDesktop(date);
        generateScheduleMobile(date);
        toggleFilterByRole();
        setStyleTabRole(currentRole);
        $('.sub__title').trigger('shortNameSession');
    })

    $body.delegate('.see-more', 'click', function (event) {
        let elmModal = $(this).children('.dialog-wrapper');
        event.stopPropagation();
        resetAllModal();
        if (elmModal.hasClass('hidden-dialog')) {
            $(this)
                .children('.dialog-wrapper')
                .removeClass('hidden-dialog');
        }
    });

    $body.delegate('.icon-close-schedule', 'click', function (event) {
        event.stopPropagation();
        let elmModal = $(this)
            .parent()
            .parent();
        if (!elmModal.hasClass('hidden-dialog')) {
            elmModal.addClass('hidden-dialog');
        }
    });

    let h = function (e) {
        if (e.type === 'swl') {
            getPreviousWeek();
        } else {
            getNextWeek();
        }
    };

    $('.app-wrapper__academy').on('swl swr', h);

    const settingDateMobile = {
        format: 'mm/dd/yyyy',
        minViewMode: 'days',
        templates: {
            leftArrow: '&#10094;',
            rightArrow: '&#10095;',
        },
        todayHighlight: true,
        weekStart: 0,
        disableTouchKeyboard: true,
        autoclose: true,
        orientation: 'center',
        container: '#container__datepicker',
    };

    const settingDateDesk = {
        format: 'mm/yyyy',
        minViewMode: 'months',
        templates: {
            leftArrow: '&#10094;',
            rightArrow: '&#10095;',
        },
        autoclose: true,
        orientation: 'center',
        container: '#container__datepicker',
    };

    function setupTimePicker(setting, eventChange) {
        $('#datetimepicker')
            .datepicker(setting)
            .on('show', function () {
                addLineCalendar();
            })
            .on(eventChange, function (e) {
                updateDataCalendar(e.date);
            });
    }

    function initTimePicker(mq) {
        const isDesktop = mq.matches;
        if (isDesktop) {
            $('.datetimepicker').datepicker('destroy');
            setupTimePicker(settingDateDesk, 'changeMonth');
            setDateCalendarDesktop(date);
        } else {
            $('.datetimepicker').datepicker('destroy');
            setupTimePicker(settingDateMobile, 'changeDate');
            setDateCalendarMobile(date);
        }
    }

    const matchMedia = window.matchMedia('(min-width: 1024px)');
    matchMedia.addListener(initTimePicker);
    initTimePicker(matchMedia);

    $(window).resize(() => {
        $('.datetimepicker').datepicker('hide');
        $('.datetimepicker').blur();
    });

    $(window).on('click', function (event) {
        let classElm = event.currentTarget.className;
        event.stopPropagation();
        if (classElm !== 'see-more') {
            resetAllModal();
        }
    });

    initShortNameSession();

});
