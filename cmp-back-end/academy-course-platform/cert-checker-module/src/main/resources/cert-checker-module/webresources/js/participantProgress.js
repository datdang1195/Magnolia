import $ from 'jquery';

const courseData = JSON.parse('${model.getMyProgressInformation}');
console.log(courseData);

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

export default () => {
    $(() => {

        let yearsData = [{
            year: 2019,
        },
            {
                year: 2020,
            },
            {
                year: 2021,
            },
            {
                year: 2022,
            }
        ]
        tabActive();
        // semsterActive();
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
        function renderSemester(courseData, semester) {
            let tmpContent = renderContent(courseData);
            var tmpSemester = `<div class="content-semester">                                
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
        function renderContent(courseData) {
            var tmpContent = '';
            for (var i = 0; i < courseData.length; i++) {
                let course = courseData[i];
                tmpContent += `<tr>
                            <td data-label="#">
                                ${i + 1}
                            </td>
                            <td data-label="Name">
                                ${course.course}
                            </td>
                            <td data-label="Quiz">
                                ${course.quiz}
                            </td>
                            <td
                                data-label="Homework">
                                ${course.homework}
                            </td>
                            <td data-label="Rate">
                                ${course.conditionalRate}
                            </td>
                            <td data-label="Score">
                                ${course.score}
                            </td>
                        </tr>`;

            }
            return tmpContent;
        }

        function searchCourse() {
            $(document).ready(function () {
                $('#program-tab-name-search-input').keyup(function () {
                    var searchField = $(this).val();
                    var regex = new RegExp(searchField, 'i');

                    const textOption = $('#dropdown-semester option:selected').val();
                    $('.dropdown-value-project').html(textOption);

                    var dataShow = courseData.filter(item => {
                        return item.course.match(regex);
                    });

                    if(textOption === 'all') {
                        $('.js-content-semester').html('');
                        renderSemester(dataShow);
                    } else {
                        $('.js-content-semester').html('');
                        renderSemester(dataShow, textOption);
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
                var data = [];
                if (textOption === 'all') {
                    data = courseData;
                } else {
                    data = courseData.filter((course) => {
                        return course.semester == textOption;
                    });
                }
                renderSemester(data, textOption);
            });
        }
        nameProgramActive();
        renderSemester(courseData, 'All');
        searchCourse();
    });
};










