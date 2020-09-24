$(document).ready(function () {
    const params = window.CMP.common.queryParams();
    const classActive = 'active';
    const $body = $('body');
    const itemYear = '.js-link-year';
    const wrapperYear = '.js-year';
    let currentSelectedYear;
    let currentYear;

    const renderYear = ($element, yearsData) => {
        for (let i = 0; i < yearsData.length; i++) {
            let elm = `<div class="year-wrapper">
          <div class="year--background"></div>
          <div class="year js-slide-year" data-year=${yearsData[i].year}>
              <p class="link link-year js-link-year">${yearsData[i].year}</p>
          </div>
      </div>`;
            $element.append(elm);
        }
        let endYear = yearsData[yearsData.length - 1].year;
        if (yearsData.length < 4) {
            let num = 4 - yearsData.length;

            for (let i = 1; i <= num; i++) {
                let elm = `<div class="year-wrapper">
                          <div class="year--background fs"></div>
                          <div class="year disable-click" data-year=${endYear +
                              i}>
                              <p class="link link-year js-link-year">${endYear +
                                  i}</p>
                          </div>
                      </div>`;
                $element.append(elm);
            }
        } else {
            let elm = `<div class="year-wrapper">
                      <div class="year--background"></div>
                      <div class="year disable-click" data-year=${endYear + 1}>
                          <p class="link link-year js-link-year">${endYear +
                              1}</p>
                      </div>
                  </div>`;
            $element.append(elm);
        }
    };

    const setHiddenArrow = $element => {
        const elmNextArrow = $element.find('.slick-next.slick-arrow');
        const elmPreletrow = $element.find('.slick-prev.slick-arrow');
        const arrowLeft = $('.arrow-slider-left');
        const arrowRight = $('.arrow-slider-right');
        if (elmNextArrow.length && elmPreletrow.length) {
            if (elmNextArrow.hasClass('slick-disabled')) {
                arrowRight.addClass('hidden');
            } else {
                if (arrowRight.hasClass('hidden')) {
                    arrowRight.removeClass('hidden');
                }
            }
            if (elmPreletrow.hasClass('slick-disabled')) {
                arrowLeft.addClass('hidden');
            } else {
                if (arrowLeft.hasClass('hidden')) {
                    arrowLeft.removeClass('hidden');
                }
            }
        } else {
            arrowLeft.addClass('hidden');
        }
    };

    const getPositionSelectedYear = yearsData => {
        for (let i = 0; i < yearsData.length; i++) {
            if (currentYear === yearsData[i].year) {
                return i;
            }
        }
    };

    const initNumSlideDesk = yearsData => {
        let pos = getPositionSelectedYear(yearsData);
        if (pos < 2) {
            return 0;
        } else {
            if (pos === yearsData.length - 1) {
                return pos - 2;
            } else {
                return pos - 1;
            }
        }
    };

    const initNumSlideMobile = yearsData => {
        let pos = getPositionSelectedYear(yearsData);
        if (pos < 2) {
            return 0;
        } else {
            return pos - 1;
        }
    };

    const getPositionSlideYear = (year, yearsData) => {
        for (let i = 0; i < yearsData.length; i++) {
            if (year === yearsData[i].year) {
                return i;
            }
        }
    };

    const isSelectedYear = year => {
        return year === currentSelectedYear.year;
    };

    const showDataYear = (year, yearsData) => {
        for (let i = 0; i < yearsData.length; i++) {
            if (yearsData[i].year === year) {
                currentSelectedYear = yearsData[i];
            }
        }

        $('.js-slide-year').each(function() {
            if (isSelectedYear(+$(this)[0].dataset.year)) {
                $(this).addClass(classActive);
            }
        });
    };

    const handlerYear = ($element, yearsData) => {
        $body.delegate(itemYear, 'click', event => {
            const $wrapperYear = $(wrapperYear);
            const currentItem = $(event.currentTarget).closest(wrapperYear);
            $wrapperYear.removeClass(classActive);
            currentItem.addClass(classActive);
            const year = event.target.innerHTML;
            let pos = getPositionSlideYear(+year, yearsData);
            showDataYear(+year, yearsData);
            const numberItemsShow = $element.slick(
                'slickGetOption',
                'slidesToShow'
            );

            $element.trigger('year:redirect', [year]);

            if (pos < 2) {
                $element.slick('slickGoTo', 0);
            } else {
                const currentSlide = $element.slick('slickCurrentSlide');
                const currentIndex = $(event.target)
                    .closest('.year-wrapper')
                    .data('slick-index');
                if (numberItemsShow === 3) {
                    if (currentIndex <= currentSlide) {
                        $element.slick('slickPrev');
                    } else if (currentIndex >= currentSlide + 2) {
                        $element.slick('slickNext');
                    }
                } else {
                    if (currentIndex <= currentSlide + 1) {
                        $element.slick('slickPrev');
                    } else {
                        $element.slick('slickNext');
                    }
                }
            }
        });
    };

    const progressBar = ($element, listYears = []) => {
        if (listYears && $element) {
            const settingSlider = {
                slidesToShow: 4,
                slidesToScroll: 1,
                speed: 300,
                infinite: false,
                arrows: true,
                variableWidth: true,
                initialSlide: initNumSlideDesk(listYears),
                responsive: [
                    {
                        breakpoint: 768,
                        settings: {
                            slidesToShow: 3,
                            slidesToScroll: 1,
                            initialSlide: initNumSlideMobile(listYears),
                        },
                    },
                ],
            };
            renderYear($element, listYears);
            showDataYear(currentYear, listYears);

            $element.on('afterChange', function(event, slick, direction) {
                setHiddenArrow($element);
            });

            $element.on('init', function(event, slick, direction) {
                setHiddenArrow($element);
            });

            $element.slick(settingSlider);

            handlerYear($element, listYears);
        }
    };

    const isCurrentYear = (currentYear, yearsData) => {
        for (var i = 0; i < yearsData.length; i++) {
            if (currentYear === yearsData[i].year) {
                return true;
            }
        }
        return false;
    }

    $.fn.yearSlider = function yearSlider(options) {
        if (this.length !== 1 || $.fn.yearSlider.instances > 0) {
            console.log('yearSlider should be executed over 1 element');
            return;
        }
        $.fn.yearSlider.instances++;
        options = options || {};
        options.listYears = options.listYears || [];
        const initialYear = 2019;
        const startYear = options.listYears[0].year||initialYear;
        if (
            params.year &&
            options.listYears.some(data => data.year === +params.year)
        ) {
            currentYear = +params.year;
        } else {
            currentYear = new Date().getFullYear();
        }
        if(!isCurrentYear(currentYear, options.listYears)) {
            currentYear = startYear;
        }
        progressBar(this, options.listYears);
        return this;
    };
    $.fn.yearSlider.instances = 0;
});
