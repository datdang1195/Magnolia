$(document).ready(function () {
    const $timeLine = $('#participant-status-slider');
    const $usernameInput = $('.js-participant-status-username-input');
    const $usernameListWrapper = $('.js-participant-list');
    const $usernameList = $('.js-participant-ul');
    const $document = $(document);
    const phaseTab = '.js-item-phase';
    const query = window.CMP.common.queryParams();
    const programRadio = '.js-participant-status-program-radio';
    const participantItemClass = '.js-participant-status-item';
    const $participantResult = $('.js-program-item');

    const tabActive = () => {
        const $tabLabel = $('.js-program-tab-name');
        const $tabContent = $('.js-program-tab-content-item');

        const initTabActive = () => {
            if ($tabLabel.length) {
                $tabLabel.eq(0).addClass('active');
            }

            if ($tabContent.length) {
                $tabContent.eq(0).addClass('show');
            }
        };

        $tabLabel.click(e => {
            const $target = $(e.target);
            const currentIDTarget = $target.data('content-id');
            $tabContent.removeClass('show');
            $tabLabel.removeClass('active');
            $target.addClass('active');
            $(`#${currentIDTarget}`).addClass('show');
        });

        initTabActive();
    };

    tabActive();

    function analyzingPhase() {
        if (query && query.year && query.phase) {
            const phaseID = query.phase;
            const hasPhase = phases.some(phase => phase.id === phaseID && !phase.disabled);
            return hasPhase;
        }
        return false;
    }

    function init() {
        phases.map(phase => {
            if(phase.disabled) {
                $(`${phaseTab}[data-value=${phase.id}]`).addClass('disabled');
            }
        })
        if (analyzingPhase()) {
            const phaseID = query.phase;
            const lastPhaseID = localStorage.getItem('lastPhase');
            if(query.program && query.user) {
                const program = programs.find(
                    program => program.id === query.program
                );
                const participant =
                    program &&
                    program.participants.find(
                        participant => participant.id === query.user
                    );
                const username = participant && participant.name;

                $usernameInput.val(username);
            }
            $(`${phaseTab}[data-value=${lastPhaseID}]`).addClass('active');
            setTimeout(function() {
                $(phaseTab).removeClass('active');
                $(`${phaseTab}[data-value=${phaseID}]`).addClass('active');
            }, 100);

            if(programs.length === 1) {
                $(programRadio).prop({checked: true});
            } else if(programs.length > 1) {
                $(`${programRadio}[value=${query.program}]`).prop({
                    checked: true
                });
            }
        }
    }

    function slide() {
        if ($timeLine.length) {
            $timeLine
                .yearSlider({ listYears: yearsData })
                .on('year:redirect', function(event, year) {
                    window.CMP.common.redirectURL({ year });
                });
        }
    }

    function renderParticipants($listWrapper, $list, data = []) {
        let htmlCourse = '';
        if (data.length) {
            data.map(e => {
                return (htmlCourse += `<li class="course__input__item js-participant-status-item" data-id=${e.id}>${e.name}</li>`);
            });
        } else {
            htmlCourse += `<li class="course__input__item">Not found</li>`;
        }
        $list.html(htmlCourse);
        $listWrapper.show();
    }

    function clickPhase(phaseClass) {
        $(phaseClass).click(function() {
            const phaseID = $(this).data('value');
            const phase = phases.find(phase => phase.id===phaseID);
            if(phase && !phase.disabled) {
                $usernameInput.val('');
                localStorage.setItem('lastPhase', $(`${phaseClass}.active`).data('value'));
                const year = query.year || new Date().getFullYear();
                window.CMP.common.redirectURL({
                    year,
                    phase: phaseID
                });
            }
        });
    }

    function handleAutocomplete(
        $autocomplete,
        radio,
        $listWrapper,
        $list,
        data = []
    ) {
        if (analyzingPhase()) {
            $autocomplete.click(function() {
                const programID = $(`${radio}:checked`).val();
                const result = data.find(program => program.id === programID);
                if (result && result.participants) {
                    renderParticipants(
                        $listWrapper,
                        $list,
                        result.participants
                    );
                }
            });

            $autocomplete.on('keyup', function(e) {
                const programID = $(`${radio}:checked`).val();
                const keyword = e.currentTarget.value.toLowerCase();
                const result = data.find(program => program.id === programID);

                if (result && result.participants) {
                    const filteredData = result.participants.filter(
                        e => e.name.toLowerCase().search(keyword) !== -1
                    );
                    renderParticipants($listWrapper, $list, filteredData);
                }
            });

            $document.click(function(e) {
                if (!$(e.target).is($autocomplete)) {
                    $listWrapper.hide();
                }
            });
        }
    }

    function clickItemList($autocomplete, radio, participantItemClass) {
        $document.on('click', participantItemClass, function(evt) {
            const programID = $(`${radio}:checked`).val();
            const $target = $(evt.target);
            const participantID = $target.data('id');
            const year = query.year || new Date().getFullYear();
            const phaseID = query.phase || false;

            if(phaseID) {
                $autocomplete.val($target.html());
                window.CMP.common.redirectURL({
                    year,
                    phase: phaseID,
                    program: programID,
                    user: participantID,
                });
            }
        });
    }

    function clickProgram(radio, $input, $result) {
        $(radio).change(function() {
            $input.val('');
            $result.addClass('hide');
        });
    }

    // render status semester
    function renderStatusSemester(courseList) {
        let tmpStatusContent = renderStatusContent(courseList);
        const tmpStatusSemester = `<div class="content-semester">                                    
                                    <table class="table-content table show-table-status-one">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Course name</th>
                                                <th>Quiz</th>
                                                <th>Homework</th>
                                                
                                                <th>Rate</th>
                                                <th>Score</th>
                                            </tr>
                                        </thead>
                                        <tbody class="scroll-table">
                                            ${tmpStatusContent}
                                        </tbody>
                                    </table>
                                 </div>`;
        $('.js-status-content-semester').append(tmpStatusSemester);
    }


    //render content of status semester
    function renderStatusContent(courseList) {
        let tmpStatusContent = '';
        for (let i = 0; i < courseList.length; i++){
            const course = courseList[i];
            tmpStatusContent += `<tr>
                                <td data-label="#">                                                      
                                    ${i + 1}
                                <td data-label="Name">
                                    ${course.course.nodeName||"-"}
                                </td>
                                <td data-label="Quiz">
                                    ${course.quiz||"-"}
                                </td>
                                <td
                                    data-label="Homework">
                                    ${course.homework||"-"}                          
                                </td>                           
                                <td data-label="Rate">                            
                                    ${course.conditionalRate||"-"}                         
                                </td>
                                <td data-label="Score">
                                    ${course.score||"-"}
                                </td>
                            </tr>`
        }
        return tmpStatusContent;
    }

    function searchStatusCourse() {
        $(document).ready(function () {
            $('#program-tab-name-status-search-input').keyup(function () {

                const searchField = $(this).val();
                const regex = new RegExp(searchField, 'i');

                const textOption = $('#dropdown-status-semester option:selected').val();
                $('.dropdown-value-project').html(textOption);

                let dataShow = courseList.filter(item => {
                    return item.course.nodeName.match(regex);
                });

                let dataSearch = [];
                if(textOption === 'all') {
                    dataSearch = dataShow;
                    $('.js-status-content-semester').html('');
                    renderStatusSemester(dataSearch);
                } else {
                    dataSearch = dataShow.filter((course) =>{
                        return course.semester === textOption;
                    });
                    $('.js-status-content-semester').html('');
                    renderStatusSemester(dataSearch);
                }
                if(!this.value){ // = searchField === ''
                    $('.js-status-content-semester').html('');
                    renderStatusSemester(dataSearch);
                }
            });
        })
    }


    function nameStatusProgramActive () {
        const $nameStatusSemster = $('#dropdown-status-semester');

        $nameStatusSemster.change(() => {
            $('.js-status-content-semester').html('');
            const textOption = $('#dropdown-status-semester option:selected').val();
            $('.dropdown-value-project').html(textOption);

            // find course of status semester
            let data = [];
            if (textOption === 'all') {
                data = courseList;
            } else {
                data = courseList.filter((course) => {
                    return course.semester == textOption;
                });
            }
            renderStatusSemester(data, textOption);
        });
    }


    nameStatusProgramActive();
    renderStatusSemester(courseList, 'All');
    searchStatusCourse();

    init();
    slide();
    clickPhase(phaseTab);
    handleAutocomplete(
        $usernameInput,
        programRadio,
        $usernameListWrapper,
        $usernameList,
        programs
    );
    clickItemList($usernameInput, programRadio, participantItemClass);
    clickProgram(programRadio, $usernameInput, $participantResult);
});
