$(document).ready(function () {

    const $sliderWrapper = $('.js-slider-wrapper');
    const $sliderShortText = $('.js-slider_short_text');
    const $newsItemTitle = $('.js-news-item-title');
    const MOBILE_WIDTH = 1025;
    const $window = $(window);
    const $windowWidth = $window.innerWidth();
    const SETTING_SLIDE = {
        dots: true,
        infinite: true,
        slidesToShow: 1,
        vertical: false,
        speed: 300,
        arrows: true,
        variableWidth: true,
        responsive: [
            {
                breakpoint: MOBILE_WIDTH,
                settings: {
                    variableWidth: false,
                    adaptiveHeight: false,
                    arrows: false,
                },
            },
        ],
    };

    const ellipsizeTextBox = el => {
        const wordArray = el.innerHTML.split(' ');
        while (el.scrollHeight > el.offsetHeight) {
            wordArray.pop();
            el.innerHTML = wordArray.join(' ');
            if (el.scrollHeight === el.offsetHeight) {
                const textElement = el.innerHTML;
                el.innerHTML = textElement.slice(0, textElement.length - 50)+'...';
                return;
            }
        }
    }

    const ellipsTextMobile = () => {
        const slideText = $sliderShortText.filter((i, e) => {
            return (
                e.classList.value.indexOf(
                    'slider__item__text-short-text'
                ) !== -1
            );
        });

        if ($windowWidth < MOBILE_WIDTH) {
            slideText.map((i, e) => {
                const innerText = e.innerHTML;
                e.innerHTML =
                    innerText.length > 200
                        ? innerText.slice(0, 200) + '...'
                        : innerText + '...';
            });
        }
    }

    const initEllipsizeTextBox = () => {
        $sliderShortText.map((i, e) => {
            ellipsizeTextBox(e);
        });
    };

    const initNews = () => {
        initEllipsizeTextBox();

        $window.resize(() => {
            if ($windowWidth > $window.innerWidth()) {
                initEllipsizeTextBox();
            }
        })

        ellipsTextMobile();

        // init Slide
        $sliderWrapper.slick(SETTING_SLIDE);

        //match height news item title
        $(function() {
            $newsItemTitle.matchHeight();
        });
    }

    initNews();
});