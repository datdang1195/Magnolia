$(document).ready(function () {
    $(function () {

        const selection = new Choices('#dropdown-project');
        const checkSemester = $('input[name="checkSemester"]');
        var cacheSemester = 'all';

        function allHours(listItem) {
            var sum = 0;
            for (let i = 0; i < listItem.length; i++) {
                sum += (listItem[i].hours);
            }
            return sum;
        }

        courseTotal(courseList.length, allHours(courseList))

        function courseTotal(numCourse, numHours) {
            let $num = $(`<p>Total <span class="course__total--bold">${numCourse}</span> courses /
            <span class="course__total--bold">${numHours}</span> hours </p>`)
            $("#number-course").html($num);
        }

        $('div.schedule').click(function (e) {
            window.location.href = decodeURI(scheduleIconUrl);
        });

        let trainingIconUrl = trainingUrl + "/" + ojtProjectName;
        $('div.training').click(function (e) {
            window.location.href = decodeURI(trainingIconUrl);
        });


        function formatConfigSelect(dataFormat) {
            var arr = [];
            arr.push({
                value: 'all',
                label: 'All categories',
                selected: true
            });
            for (var i = 0; i < dataFormat.length; i++) {
                var obj = {
                    value: dataFormat[i].displayName,
                    label: dataFormat[i].displayName
                };
                arr.push(obj);
            }
            return arr;
        }

        function renderCourse(courseList, courseDetailUrl) {
            var tmpContent = '';
            for (var i = 0; i < courseList.length; i++) {
                var course = courseList[i];
                tmpContent += `<div class="project__item">
                                <div class="project-item-wrapper">
                                    <div class="item__title">
                                        <span class="item__number">${i + 1}</span>
                                        <span class="item__lable">${course.courseDetail.nodeName}</span>
                                    </div>
                                    <div class="item__content">
                                        <div class="read-more">
                                            <a href="${courseDetailUrl}/${course.readMoreLink}">Read more</a>
                                        </div>
                                        <div class="wrapper-item status-${course.courseStatus.displayName.toLowerCase()}">
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
                                            <div class="lever">
                                                <img src="${course.courseDetail.group.icon.link||"#"}" alt="category-icon">
                                                <span>${course.courseDetail.group.displayName||"No Group"}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>`;
            }
            $('.js-course-list').html(tmpContent);
        }

        function initation() {
            renderCourse(courseList, courseDetailUrl);
        }

        initation();
        $('#dropdown-project').change((event) => {
            let selected = event.target.value;
            let allPrograms = [];
            if (selected === 'all') {
                if (cacheSemester === 'all') {
                    allPrograms = courseList;
                } else {
                    allPrograms = getCourseAllPrograms(cacheSemester);
                }

            } else {
                allPrograms = getCourse(selected, $('input[name="checkSemester"]:checked').val());
            }
            renderCourse(allPrograms, courseDetailUrl);
            courseTotal(allPrograms.length, allHours(allPrograms));

        })

        function getCourse(idCategory, typeCourse) {
            if (typeCourse == 'all') {
                return courseList.filter((course) => {
                    if (course.courseDetail.category.displayName === idCategory) {
                        return true;
                    }
                });
            }
            return courseList.filter((course) => {
                if (course.courseDetail.category.displayName === idCategory && course.semester == typeCourse) {
                    return true;
                }
            });
        }

        checkSemester.click((event) => {
            var selected = event.target.value;
            cacheSemester = selected;
            var listCategories = [];
            var allCourse = [];
            if (selected === 'all') {
                listCategories = categories;
                allCourse = courseList;
            } else {
                let listCourse = getSemesterId(selected);
                listCategories = getCategoriesOfTypeCourse(listCourse);
                allCourse = getSemesterId(selected);
            }
            var optionSelect = formatConfigSelect(listCategories);
            setOptionSelect(optionSelect);
            // call renderCourse
            renderCourse(allCourse, courseDetailUrl);
            courseTotal(allCourse.length, allHours(allCourse))
        });

        function getCategoriesOfTypeCourse(listCourse) {
            let listCategories = [];
            if (listCourse.length) {
                listCourse.forEach(element => {
                    if (element.courseDetail && element.courseDetail.category) {
                        let checkExist = true;
                        listCategories.forEach(element2 => {
                            if (element2.displayName == element.courseDetail.category.displayName) {
                                checkExist = false;
                            }
                        });
                        if (checkExist) {
                            let item = {
                                displayName: element.courseDetail.category.displayName,
                                // id: '0001',
                            };
                            listCategories.push(item);
                        }
                    }
                });
            }
            return listCategories;
        }

        function getSemesterId(idSemester) {
            return courseList.filter((course) => {
                return course.semester === idSemester;
            });
        }

        function getCourseAllPrograms(cacheSemester) {
            return courseList.filter((course) => {
                return course.semester === cacheSemester;
            })
        }

        $('#dropdown-project').change(() => {
            let textOption = $('#dropdown-project option:selected').text();
            $('.dropdown-value-project').html(textOption);
        });

        $('#goToTopPage').click(() => {
            $('html,body').animate({
                scrollTop: 0
            }, 300);
        });

        function setOptionSelect(configOption) {
            selection.clearChoices();
            selection.setChoices(
                configOption,
                'value',
                'label',
                false,
            );
        }

        if ($('#dropdown-project').length) {
            selection.setChoices(
                formatConfigSelect(categories),
                'value',
                'label',
                false,
            );
        }
    });
})


