$(document).ready(function () {
    const $body = $('body');
    const $document = $(document);
    const $course = $('.js-course');
    const $formProgram = $course.find('.js-form-program');
    const $formProgramWrapper = $formProgram.closest('.section-form__program')
    const $input = $course.find('.js-course-input');
    const $listItems = $course.find('.js-course-items');
    const $infoWrapper = $course.find('.js-info-wrapper');
    const $searchBox = $course.find('.js-course-search-box');
    const $switchWrapper = $course.find('.js-course-switch');
    const $switchRole = $switchWrapper.find('.js-switch__checkbox');
    const item = '.js-item';
    const $itemWrapper = $course.find('.js-course-block');
    const $timeLine = $('#course-status-slide');
    const $btnPhases = $course.find('.js-item-phase');
    const selection = new Choices('#semester-dropdown');
    const selectSemester = $('#semester-dropdown');
    const selectClass = $course.find('.dropdown-semester');

    let dataProgramID;
    let dataCourseID;
    let prevIdProgram = 0;
    let prevIdPhases = 0;
    let dataPhases = [];
    let listCoursesOfPhase = [];
    let listCoursesSemesterFilter = [];
    let dataPhaseGeted = [];
    let dataProgramsGeted = [];
    let idProgram = '';
    let listIdChecked = "";
    let semesterChanged = "";
    const getParam = () => {
        const query = window.CMP.common.queryParams();
        const {year, phase, program} = query;
        if (year && phase && program) {
            return query
        } else {
            return false
        }
    }

    const setAttrDisabled = (listElement, value) => {
        for (let i = 0; i < listElement.length; i++) {
            listElement[i].prop('disabled', value);
        }
    }

    const getDataById = (id, dataId, data) => {
        return data.filter(e => e[dataId] === id)[0];
    };
    const handleRoles = () => {
        if (roles.includes("trainer")) {
            $switchWrapper.show();
            $switchRole.prop('checked', false);
            $(".js-switch__checkbox").attr('disabled', true)
        }
        if (roles.includes("supervisor")) {
            $switchWrapper.show();
            $switchRole.prop('checked', true);
            $(".js-switch__checkbox").attr('disabled', true)
        }
        if (roles.includes("supervisor") && roles.includes("trainer")) {
            $switchWrapper.show();
            $switchRole.prop('checked', true);
            $(".js-switch__checkbox").attr('disabled', false)
        }
    }

    const renderProgram = (listProgram, dataDate) => {
        const params = getParam();
        const htmlProgram = listProgram.reduce((acc, programs) => {
            const isHasParam = params ? programs.id === params.program : '';
            let date1;
            let date2;
            dataDate.forEach(e => {
                if (e.programId == programs.id) {
                    date1 = e.semesterDates[0];
                    date2 = e.semesterDates[1];
                }
            });
            const checked =
                listProgram.length ||
                isHasParam;
            return (acc += `
                    <label class="wrapper-item">
                        <input
                        type="checkbox"
                        name="checkProgram"
                        class="js-item"
                        data-id=${programs.id}
                        data-type=program
                        checked = ${checked && 'checked'}
                        />
                        <span class="checkmark"></span>
                        <div class="radio-item">
                            <p class="radio__title">
                                ${programs.name}
                            </p>
                            <p class="radio__time">
                                ${date1}
                            </p>
                            <p class="radio__time">
                                ${date2}
                            </p>
                        </div>
                    </label>
                    `);
        }, '');
        $formProgram.html(htmlProgram);
        $formProgramWrapper.show();
        selectClass.show();
        $searchBox.css({display: 'flex'});
        setAttrDisabled([$input, $switchRole], false);
        handleRoles();
        if (listProgram.length === 1) {
            const uidProgram = listProgram[0].id;
            renderCourses(listCoursesOfPhase);
            prevIdProgram = uidProgram;
        }
        if (params || listProgram.length === 1) {
            handleRoles();
        }
        getProgramChecked();
    };

    const resetData = () => {
        $input.val('');
        $infoWrapper.html('');
        $infoWrapper.hide();
        $listItems.html('');
    };

    const renderInfo = course => {
        const htmlPaticipants = course.participants.reduce((acc, e) => {
            acc += `
                    <tr>
                        <td data-label="Name">
                            ${e.fullName}
                        </td>
                        <td data-label="Attendances">
                            ${e.attendant === null ? "-" : e.attendant}
                        </td>
                        <td data-label="Quiz">
                            ${e.quiz === null ? "-" : e.quiz}
                        </td>
                        <td data-label="Homework">
                            ${e.homework === null ? "-" : e.homework}
                        </td>
                        <td data-label="Score">
                            ${e.score === null ? "-" : e.score}
                        </td>
                    </tr>
                `;
            return acc;
        }, '');
        const htmlInfo = `
            <div class="course__info">
            <div class="evn-row">
                <div class="evn-col evn-col-lg--12 evn-col--12">
                    <div class="evn-row js-course-info">
                        <div class="course__info__col evn-col-lg--8 evn-col--12">
                            <span class="course__info__label course__label">Status:</span>
                            <div class="course__info__progress wrapper-progress status-${course.status.toLowerCase()}">
                                <div class="progress-item">
                                    <div class="progress progress--borderLeft">
                                        <div class="progress-status"></div>
                                    </div>
                                    <p class="progress__lable">open</p>
                                </div>
                                <div class="progress-item">
                                    <div class="progress">
                                        <div class="progress-status"></div>
                                    </div>
                                    <p class="progress__lable">class</p>
                                </div>
                                <div class="progress-item">
                                    <div class="progress">
                                        <div class="progress-status"></div>
                                    </div>
                                    <p class="progress__lable">quiz</p>
                                </div>
                                <div class="progress-item">
                                    <div class="progress">
                                        <div class="progress-status"></div>
                                    </div>
                                    <p class="progress__lable">homework</p>
                                </div>
                                <div class="progress-item">
                                    <div class="progress progress--borderRight">
                                        <div class="progress-status"></div>
                                    </div>
                                    <p class="progress__lable">finished</p>
                                </div>
                            </div>
                        </div>
                        <div class="course__info__col evn-col-lg--4 evn-col--12">
                            <span class="course__info__label course__label">Semester:</span>
                            <span class="course__info__value">${course.semester}</span>
                        </div>
                        <div class="course__info__col evn-col-lg--4 evn-col--12">
                            <span class="course__info__label course__label">Compulsory:</span>
                            <span class="course__info__value">${course.compulsory ? 'Yes' : 'No'}</span>
                        </div>
                        <div class="course__info__col evn-col-lg--4 evn-col--12">
                            <span class="course__info__label course__label">Supervisor:</span>
                            <span class="course__info__value">${course.supervisor}</span>
                        </div>
                        <div class="course__info__col evn-col-lg--4 evn-col--12">
                            <span class="course__info__label course__label">Trainer:</span>
                            <span class="course__info__value">${course.trainer}</span>
                        </div>
                        <div class="course__info__col evn-col-lg--2 evn-col--12">
                            <span class="course__info__label course__label">Duration:</span>
                            <span class="course__info__value">${course.duration}<span class="course__info__sub-value">
                                    Hours</span></span>
                        </div>
                        <div class="course__info__col evn-col-lg--12 evn-col--12">
                            <div class="course__info__description">
                                <span class="course__info__label course__label">Description:</span>
                                <span class="course__info__value">${course.desc}</span>
                            </div>
                            <div class="course__info-button">
                                <a class="evn-btn" href="/courseList">Details</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="course__participants js-participants">
            <div class="course__label">participants:</div>
            <div class="program-tab__content__item">
                <div class="course__input__wrapper js-participants-search">
                    <span class="course__input__line"></span>
                    <input type="text" class="course__input__text" id="search__participants__input"
                        placeholder="Search Name here" />
                </div>
                <div class="table-session table-courses">
                    <table class="table-content table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Attendances</th>
                                <th>Quiz</th>
                                <th>Homework</th>
                                <th class="sort__score js-sort__score" id="sort-score">Score</th>
                            </tr>
                        </thead>
                        <tbody class="scroll-table" id="js-scroll-table">
                            ${htmlPaticipants}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
            `;
        $infoWrapper.html(htmlInfo);
        $infoWrapper.show();
        $('.js-list-course').hide();
        selectClass.hide();
        searchParticipants();
        orderStatus(0, 'text');
        $('#sort-score').click(function () {
            orderStatus(4, 'number');
        });
        orderDashInStatus();
    };

    const renderCourses = listCourses => {
        const htmlCourse = listCourses.length
            ? listCourses.reduce((acc, e) => {
                return (acc += `<li class="course__input__item js-item" data-id=${e.courseId} data-type=course>${e.courseName}</li>`);
            }, '')
            : `<li class="course__input__item">Not found</li>`;
        $listItems.html(htmlCourse);
    };

    const initProgram = idPhases => {
        const getPhases = getDataById(idPhases, 'id', dataPhases);
        dataProgramID = getPhases.programs;
        renderProgram(dataProgramID, dataDates);
    };

    const getData = target => {
        const $this = $(target);
        const result = {
            uid: $this.data('id'),
            type: $this.data('type'),
        };
        return result;
    };

    let renderListCourses = (data) => {
        let tmp = '';
        data.forEach((data, index) => {
            tmp += `<div class="project__item">
                                <div class="project-item-wrapper">
                                    <div class="item__title">
                                        <span class="item__number">${index + 1}</span>
                                        <span class="item__lable">${data.courseName}</span>
                                    </div>
                                    <div class="item__content">
                                        <div class="read-more">
                                            <a href="#" class="js-read-more" data-courseid="${data.courseId}">Read more</a>
                                        </div>
                                        <div class="wrapper-item status-${data.status.toLowerCase()}">
                                            <div class="wrapper-progress">
                                                <div class="progress-item">
                                                    <div class="progress progress__open progress--borderLeft">
                                                        <div class="progress-status"></div>
                                                    </div>
                                                    <p class="progress__lable">open</p>
                                                </div>
                                                <div class="progress-item">
                                                    <div class="progress progress__class">
                                                        <div class="progress-status"></div>
                                                    </div>
                                                    <p class="progress__lable">class</p>
                                                </div>
                                                <div class="progress-item">
                                                    <div class="progress progress__quiz">
                                                        <div class="progress-status"></div>
                                                    </div>
                                                    <p class="progress__lable">quiz</p>
                                                </div>
                                                <div class="progress-item">
                                                    <div class="progress progress__homework">
                                                        <div class="progress-status"></div>
                                                    </div>
                                                    <p class="progress__lable">h-work</p>
                                                </div>
                                                <div class="progress-item">
                                                    <div class="progress progress--borderRight progress__finished">
                                                    </div>
                                                    <p class="progress__lable">finished</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>`;
        })
        $('.js-list-course').html(tmp);
    };

    let getCourses = (data) => {

        let getYears = window.CMP.common.queryParams().year;
        if (!getYears) {
            getYears = new Date().getFullYear();
        }
        let getPhases = $btnPhases.data("id");
        let arr = [];
        let arrPhase = [];
        if (data) {
            if (getYears) {
                if (data.year == getYears) {
                    arr.push(data);
                }
                ;
            }
            ;

            if (getPhases) {
                let arrYear = arr;
                arrYear.forEach(e => {
                    dataPhases = e.phases;
                    e.phases.forEach(e2 => {
                        if (e2.id == getPhases) {
                            arrPhase.push(e2);
                        }
                    });
                });
            }
            ;
        }
        ;
        return arr;
    }
    getCourses(data);
    let dataPrograms = (arrdata) => {
        let allPrograms = [];
        arrdata.forEach(e => {
            e.programs.forEach(e1 => {
                allPrograms.push(e1);
            });
        });
        return allPrograms;
    }

    let getCoursesOfTrainer = (dataListCourse) => {
        return dataListCourse.filter(e => {
            return e.trainer === fullNameOfCurrentUser;
        })
    }
    let getPhaseId = (idData) => {
        return dataPhases.filter(e => {
            return e.id === idData;
        });
    }
    let getProgramId = (idData) => {

        return dataProgramsGeted.filter(e => {
            return e.id === idData;
        });
    }

    let getCoursesOfPhase = (data) => {
        let allCoursesTypePhases = [];
        data.forEach(e => {
            e.programs.forEach(e1 => {
                e1.courses.forEach(e2 => {
                    allCoursesTypePhases.push(e2)
                });
            });
        });
        return allCoursesTypePhases;
    }

    let unCheck = () => {
        if (idProgram.length > 1) {
            $("input[name='checkProgram']").prop("checked", true);
        }
    }

    let removeDuplicateOption = (arr) => {
        let filteredArr = arr.reduce((acc, current) => {
            const x = acc.find(e => e.value === current.value);
            if (!x) {
                return acc.concat([current]);
            } else {
                return acc;
            }
        }, []);
        return filteredArr;
    }
    const handlePhases = () => {
        $btnPhases.click(function () {
            $infoWrapper.hide();
            $('.js-list-course').show();
            selectClass.show();
            unCheck();
            //----idProgram null when from Program jump to Phase
            idProgram = '';
            let clickedPhases = $(this).data("id");
            selection.setChoiceByValue('all');
            selection.clearChoices();
            let dataIdPhase = getPhaseId(clickedPhases);
            dataPhaseGeted = dataIdPhase;
            listCoursesOfPhase = getCoursesOfPhase(dataIdPhase);
            dataCourseID = listCoursesOfPhase;
            dataProgramsGeted = dataPrograms(dataIdPhase);
            let dataOption = formatSelectSemester(listCoursesOfPhase);
            let option = removeDuplicateOption(dataOption);
            setOptionSelect(option);
            const {uid} = getData(this);
            if (prevIdPhases !== uid) {
                $btnPhases.removeClass('active');
                $(this).addClass('active');
                resetData();
                initProgram(uid);
                prevIdPhases = uid;
            }
            listIdChecked = [];
            $.each($("input[name='checkProgram']:checked"), function () {
                listIdChecked.push($(this).data("id"));
            });

            listCoursesRendered(listCoursesOfPhase);
            //----clickedPhases null when from Phase jump to Phase
            clickedPhases = '';
        });
    };

    const handlCourseClick = courseId => {
        const course = getDataById(courseId, 'courseId', dataCourseID);
        renderInfo(course);
        $itemWrapper.hide();
    };
    const listCoursesRendered = (listCourses) => {
        let getChecked = $switchRole.prop("checked");
        if (getChecked === false) {
            renderListCourses(getCoursesOfTrainer(listCourses));
            renderCourses(getCoursesOfTrainer(listCourses));
        } else {
            renderListCourses(listCourses);
            renderCourses(listCourses);
        }
    }

    const dataSwitchChange = () => {
        let dataSwitch = [];
        if (!semesterChanged) {
            dataSwitch = getCourseProgramsChecked();
        } else {
            dataSwitch = listCoursesSemesterFilter;
        }
        return dataSwitch;
    }

    const handleSwitch = () => {
        $switchRole.change(e => {
            resetData();
            listCoursesRendered(dataSwitchChange());
        });
    }

    const handleSearchClick = () => {
        $input.on('click keyup', function (e) {
            const keyCode = e.keyCode;
            if (keyCode && dataCourseID.length) {
                const keyword = e.currentTarget.value.toLowerCase();
                const listResult = dataCourseID.filter(
                    e => e.courseName.toLowerCase().search(keyword) !== -1
                );
                renderCourses(listResult);
            } else {
                $itemWrapper.show();
            }
        });
    };


    const handleUnFocus = () => {
        $document.click(e => {
            const isInput = $(e.target).is(
                "input[type='text'].js-course-input"
            );
            const isCourse = $(e.target).is('li.js-item');
            if (!isInput && !isCourse) {
                $itemWrapper.hide();
            }
        });
    };
    const getProgramChecked = () => {
        let arrIdChecked = [];
        listIdChecked = arrIdChecked;
        $.each($("input[name='checkProgram']:checked"), function () {
            arrIdChecked.push($(this).data("id"));
        });
    }
    const getCourseProgramsChecked = () => {
        let listProgramsChecked = [];
        let listCoursesChecked = [];
        listIdChecked.forEach(e => {
            listProgramsChecked.push(getProgramId(e))
        });
        listProgramsChecked.forEach(e => {
            listCoursesChecked = [...listCoursesChecked, ...e[0].courses];
        });
        dataCourseID = listCoursesChecked;
        return listCoursesChecked;
    }

    const handleData = selector => {
        $body.delegate(selector, 'click', function (e) {
            $infoWrapper.hide();
            $('.js-list-course').show();
            selectClass.show();
            semesterChanged = "";
            const {uid, type} = getData(this);
            idProgram = uid;
            getProgramChecked();
            let dataOption = formatSelectSemester(getCourseProgramsChecked());
            let optionSelect = removeDuplicateOption(dataOption);
            setOptionSelect(optionSelect);
            listCoursesRendered(getCourseProgramsChecked());
            switch (type) {
                case 'program':
                    const isChecked = $switchRole.prop('checked');
                    if (uid.length) {
                        resetData();
                        handleRoles();
                        // setAttrDisabled([$input, $switchRole], false);
                        prevIdProgram = uid;
                        if (isChecked === false) {
                            renderCourses(getCoursesOfTrainer(getCourseProgramsChecked()));
                        } else {
                            renderCourses(getCourseProgramsChecked());
                        }
                    }
                    break;
                case 'course':
                    $input.val($(this).text());
                    handlCourseClick(uid);
                    break;
            }
        });
    };

    $('.contentPage').on('click', '.js-read-more', function (e) {
        var courseId = $(this).data('courseid');
        const course = getDataById(courseId, 'courseId', listCoursesOfPhase);
        renderInfo(course);
        $('.js-list-course').hide();
    });

    const formatSelectSemester = (dataFormat) => {
        let arr = [];
        arr.push({
            value: 'all',
            label: 'All Semester',
            selected: true
        });
        dataFormat.forEach(e => {
            let obj = {
                value: e.semester,
                label: e.semester
            };
            arr.push(obj);
        });
        return arr;
    };
    let setOptionSelect = (configOption) => {
        if (selectSemester.length) {
            selection.clearChoices();
            selection.setChoices(
                configOption,
                'value',
                'label',
                false,
            );
        }
    }

    let getSemesterId = (listData, semester) => {
        return listData.filter(e => {
            return e.semester === semester;
        });
    }
    let handleSemesterSelect = () => {
        selectSemester.change((event) => {
            let selected = event.target.value;
            semesterChanged = selected;
            let arrCoursesTypeSemester = [];
            let arrCoursesOfPhase = getCoursesOfPhase(dataPhaseGeted);
            if (selected === 'all') {
                if (listIdChecked.length) {
                    arrCoursesTypeSemester = getCourseProgramsChecked();
                } else {
                    arrCoursesTypeSemester = arrCoursesOfPhase;
                }

            } else {
                if (listIdChecked.length) {
                    arrCoursesTypeSemester = getSemesterId(getCourseProgramsChecked(), selected);
                } else {
                    arrCoursesTypeSemester = getSemesterId(arrCoursesOfPhase, selected);
                }

            }
            listCoursesSemesterFilter = arrCoursesTypeSemester;
            listCoursesRendered(arrCoursesTypeSemester);
        });
    }
    handleSemesterSelect();

    //Search Participants Name.
    let searchParticipants = () => {
        $('#search__participants__input').keyup(function () {
            let value = $(this).val().toLowerCase();
            $('#js-scroll-table tr').filter(function () {
                $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
            });
        });
    }

    let orderStatus = (column, type) => {
        let order = $('.table thead tr>th:eq(' + column + ')').data('order');
        order = order === 'ASC' ? 'DESC' : 'ASC';
        $('.table thead tr>th:eq(' + column + ')').data('order', order);

        //Sort the table
        $('.table tbody tr').sort(function (a, b) {
            a = $(a).find('td:eq(' + column + ')').text();
            b = $(b).find('td:eq(' + column + ')').text();

            switch (type) {
                case 'text':
                    let splitA = a.trim().split(" ");
                    let splitB = b.trim().split(" ");
                    let lastA = splitA[splitA.length - 1];
                    let lastB = splitB[splitB.length - 1];
                    if (lastA < lastB) return -1;
                    if (lastA > lastB) return 1;
                    break;
                case 'number':
                    return order === 'ASC' ? a - b : b - a;
                    break;
            }

        }).appendTo('.table tbody');

    }
    let orderDashInStatus = () => {
        $('.table tbody tr').sort(function (a, b) {
            let a1 = $(a).find('td').eq(1).text().trim();
            let a2 = $(a).find('td').eq(2).text().trim();
            let a3 = $(a).find('td').eq(3).text().trim();
            let a4 = $(a).find('td').eq(4).text().trim();

            let b1 = $(b).find('td').eq(1).text().trim();
            let b2 = $(b).find('td').eq(2).text().trim();
            let b3 = $(b).find('td').eq(3).text().trim();
            let b4 = $(b).find('td').eq(4).text().trim();

            if (a1 === '-' && b1 !== '-' || a2 === '-' && b2 !== '-' || a3 === '-' && b3 !== '-' || a4 === '-' && b4 !== '-') {
                return 1;
            } else if (b1 === '-' && a1 !== '-' || b2 === '-' && a2 !== '-' || b3 === '-' && a3 !== '-' || b4 === '-' && a4 !== '-') {
                return -1;
            }
        }).appendTo('.table tbody');

    }

    const initCourseStatus = () => {
        const queryParam = getParam();
        if ($timeLine.length) {
            $timeLine
                .yearSlider({listYears: dataYears})
                .on('year:redirect', function (event, year) {
                    window.CMP.common.redirectURL({year});
                });
        }

        // if have param on URL default render data program and course
        if (queryParam) {
            //get uuid from param on URL
            const {phase, dataCourseID} = queryParam;
            $btnPhases
                .filter((i, e) => e.dataset.id === phase)[0]
                .classList.add('active');
            initProgram(phase);
            renderCourses(dataCourseID);
        }
        handlePhases();
        handleSwitch();
        handleSearchClick();
        handleData(item);
        handleUnFocus();
    };

    initCourseStatus();
});
