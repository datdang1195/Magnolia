$(document).ready(function () {
    $(function () {
        let orderDefault = ['foundation', 'profession', 'expert', 'mastery'];
        const startYear = 2019;
        let currentSelectedYear;
        let currentSelectedPhase;

        let currentYear = new Date().getFullYear();
        if (!isCurrentYear(currentYear)) {
            currentYear = startYear;
        };

        init();

        function init() {
            initSliderYear();
            initPhase();
            showDataYear(currentYear);
        }

        function initSliderYear() {
            for (var i = 0; i < yearsData.length; i++) {
                let elm = `<div class="year-wrapper">
                    <div class="year--background"></div>
                    <div class="year" data-year=${yearsData[i].year}>
                        <p class="link link-year">${yearsData[i].year}</p>
                    </div>
                </div>`;
                $('.slider-years').append(elm);
            }
            var endYear = yearsData[yearsData.length - 1].year;
            if (yearsData.length < 4) {
                let num = 4 - yearsData.length;
                for (var i = 1; i <= num; i++) {
                    let elm = `<div class="year-wrapper">
                                    <div class="year--background"></div>
                                    <div class="year disable-click" data-year=${endYear + i}>
                                        <p class="link link-year">${endYear + i}</p>
                                    </div>
                                </div>`;
                    $('.slider-years').append(elm);
                }
            } else {
                let elm = `<div class="year-wrapper">
                                <div class="year--background"></div>
                                <div class="year disable-click" data-year=${endYear + 1}>
                                    <p class="link link-year">${endYear + 1}</p>
                                </div>
                            </div>`;
                $('.slider-years').append(elm);
            }
        }

        function initPhase() {
            for (var i = 0; i < availablePhases.length; i++) {
                let elm = `<div class="square"><p class="square__item" data-title=${availablePhases[i].title}>${availablePhases[i].title}</p></div>`;
                $('#list-phase').append(elm);
            }
            $('#list-phase').append('<div class="arrow-up"></div>');
        }

        function isPhase(phase) {
            for (var i = 0; i < currentSelectedYear.phases.length; i++) {
                if (phase === currentSelectedYear.phases[i].title) {
                    return true;
                }
            }
            return false;
        }

        function isSelectedPhase(phase) {
            return phase === currentSelectedPhase.title;
        }

        function isSelectedYear(year) {
            return year === currentSelectedYear.year;
        }

        function isCurrentYear(currentYear) {
            for (var i = 0; i < yearsData.length; i++) {
                if (currentYear === yearsData[i].year) {
                    return true;
                }
            }
            return false;
        }

        function getPositionSelectedYear() {
            for (var i = 0; i < yearsData.length; i++) {
                if (currentYear === yearsData[i].year) {
                    return i;
                }
            }
        }

        function initNumSlideDesk() {
            let pos = getPositionSelectedYear();
            if (pos < 2) {
                return 0;
            } else {
                if (pos === yearsData.length - 1) {
                    return pos - 2;
                } else {
                    return pos - 1;
                }
            }
        }

        function initNumSlideMobile() {
            let pos = getPositionSelectedYear();
            if (pos < 2) {
                return 0;
            } else {
                return pos - 1;
            }
        }

        function resetPhase() {
            $('.square').each(function (index) {
                if ($(this).hasClass('active')) {
                    $(this).removeClass('active');
                }
                if ($(this).hasClass('not-click-allow')) {
                    $(this).removeClass('not-click-allow');
                }
            })
        }

        function resetYear() {
            $('.year').each(function (index) {
                if ($(this).hasClass('active')) {
                    $(this).removeClass('active');
                }
                if ($(this).parent().hasClass('disable-click')) {
                    $(this).parent().removeClass('disable-click');
                }
            })
        }

        function showDataYear(year, phase) {
            for (var i = 0; i < yearsData.length; i++) {
                if (yearsData[i].year === year) {
                    currentSelectedYear = yearsData[i];
                    let objPhase;
                    let flag;
                    if (arguments.length === 1) {
                        let yearObj = yearsData[i].phases;
                        for (var j = 0; j < orderDefault.length; j++) {
                            for (var k = 0; k < yearObj.length; k++) {
                                if (orderDefault[j] === yearObj[k].title.toLowerCase()) {
                                    objPhase = yearObj[k];
                                    flag = 1;
                                    break;
                                }
                            }
                            if (flag) {
                                break;
                            }
                        }
                    } else {
                        for (var j = 0; j < yearsData[i].phases.length; j++) {
                            if (phase === yearsData[i].phases[j].title) {
                                objPhase = yearsData[i].phases[j];
                            }
                        }
                    }
                    currentSelectedPhase = objPhase;
                    $('.definition__title').html(objPhase.definition);
                    let programItem = '';
                    for (var j = 0; j < objPhase.programs.length; j++) {
                        let objProgram = objPhase.programs[j];
                        programItem = programItem + `
                        <li class="project-item">
                            <a href="${objProgram.url}">${objProgram.title}</a>
                        </li>
                        `;
                    }
                    $('.wrapper-project-content').html(programItem);
                }
            }
            $('.square__item').each(function (index) {
                if (!isPhase($(this)[0].dataset.title)) {
                    $(this).parent().addClass('not-click-allow');
                } else {
                    if (isSelectedPhase($(this)[0].dataset.title)) {
                        $(this).parent().addClass('active');
                    }
                }
            })

            $('.year').each(function (index) {
                if (isSelectedYear(+$(this)[0].dataset.year)) {
                    $(this).addClass('active');
                }
            })
        }

        function getPositionSlideYear(year) {
            for (var i = 0; i < yearsData.length; i++) {
                if (year === yearsData[i].year) {
                    return i;
                }
            }
        };

        $('.link-year').click(function (event) {
            resetPhase();
            resetYear();
            showDataYear(+event.target.innerText);
            let pos = getPositionSlideYear(+event.target.innerText);
            var numberItemsShow = $('.slider-years').slick('slickGetOption', 'slidesToShow');
            if (pos < 2) {
                $('.slider-years').slick('slickGoTo', 0);
            } else {
                const currentSlide = $('.slider-years').slick('slickCurrentSlide');
                const currentIndex = $(event.target).closest('.year-wrapper').data('slick-index');
                if (numberItemsShow === 3) {
                    if (currentIndex <= currentSlide) {
                        $('.slider-years').slick('slickPrev');
                    } else if (currentIndex >= currentSlide + 2) {
                        $('.slider-years').slick('slickNext');
                    }
                } else {
                    if (currentIndex <= currentSlide + 1) {
                        $('.slider-years').slick('slickPrev');
                    } else {
                        $('.slider-years').slick('slickNext');
                    }
                }
            }
        })

        $('.square__item').click(function (event) {
            resetPhase();
            showDataYear(currentSelectedYear.year, event.target.dataset.title);
        });

        function setHiddenArrow() {
            let elmNextArrow = $('.slick-next.slick-arrow');
            let elmPrevArrow = $('.slick-prev.slick-arrow');
            let arrowLeft = $('.arrow-slider-left');
            let arrowRight = $('.arrow-slider-right');
            if (elmNextArrow.length && elmPrevArrow.length) {
                if (elmNextArrow.hasClass('slick-disabled')) {
                    arrowRight.addClass('hidden');
                } else {
                    if (arrowRight.hasClass('hidden')) {
                        arrowRight.removeClass('hidden');
                    }
                }
                if (elmPrevArrow.hasClass('slick-disabled')) {
                    arrowLeft.addClass('hidden');
                } else {
                    if (arrowLeft.hasClass('hidden')) {
                        arrowLeft.removeClass('hidden');
                    }
                }
            } else {
                arrowLeft.addClass('hidden');
            }
        }
        $('.slider-years').on('afterChange', function (event, slick, direction) {
            setHiddenArrow();
        });
        $('.slider-years').on('init', function (event, slick, direction) {
            setHiddenArrow();
        });

        $('.slider-years').slick({
            slidesToShow: 4,
            slidesToScroll: 1,
            speed: 300,
            infinite: false,
            arrows: true,
            variableWidth: true,
            initialSlide: initNumSlideDesk(),
            responsive: [{
                breakpoint: 768,
                settings: {
                    slidesToShow: 3,
                    slidesToScroll: 1,
                    initialSlide: initNumSlideMobile(),
                }
            }]
        });
    });
});