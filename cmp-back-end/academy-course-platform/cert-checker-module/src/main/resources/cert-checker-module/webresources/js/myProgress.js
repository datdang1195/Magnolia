$(document).ready(function () {
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

    const $timeLine = $('#participant-progress-slider');
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

    // render semester
    function renderSemester(courseList) {
        let tmpContent = renderContent(courseList);
        const tmpSemester = `<div class="content-semester">                                
                                <table class="table-content table show-table-one">
                                    <thead class="js-table-header">
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
                                        ${tmpContent}
                                    </tbody>
                                </table>
                            </div>`;
        $('.js-content-semester').append(tmpSemester);
    }

    //render content for semester
    function renderContent(courseList) {
        let tmpContent = '';
        for (let i = 0; i < courseList.length; i++) {
            let course = courseList[i];

            tmpContent += `<tr>
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
                        </tr>`;

        }
        return tmpContent;
    }
    function searchCourse() {
        $(document).ready(function () {
            $('#program-tab-name-search-input').keyup(function () {
                const searchField = $(this).val();
                const regex = new RegExp(searchField, 'i');

                const textOption = $('#dropdown-semester option:selected').val();
                $('.dropdown-value-project').html(textOption);

                let dataShow = courseList.filter(item => {
                    return item.course.nodeName.match(regex);
                });
                //
                // switch (textOption) {
                //     case 'all':
                //         break;
                //     case 'Semester 1':
                //         break;
                //     case 'Semester 2':
                //         break;
                //     default:
                //
                //         break;
                // }
                let dataSearch = [];
                if(textOption === 'all') {
                    dataSearch = dataShow;
                    $('.js-content-semester').html('');
                    renderSemester(dataSearch);
                } else {
                    dataSearch = dataShow.filter((course)=>{
                        return course.semester === textOption;
                    })
                    $('.js-content-semester').html('');
                    renderSemester(dataSearch);
                }
                if(!this.value){
                    $('.js-content-semester').html('');
                    renderSemester(dataSearch);
                }
            });
        })
    }

    function nameProgramActive () {
        const $nameSemster = $('#dropdown-semester');

        $nameSemster.change(() => {
            $('.js-content-semester').html('');
            const textOption = $('#dropdown-semester option:selected').val();
            $('.dropdown-value-project').html(textOption);

            // find course of semester
            let data = [];
            if (textOption === 'all') {
                data = courseList;
            } else {
                data = courseList.filter((course) => {
                    console.log(course);
                    return course.semester == textOption;
                });
            }
            renderSemester(data, textOption);
        });
    }
    nameProgramActive();
    renderSemester(courseList, 'All');
    searchCourse();

});