$(document).ready(function () {
    $('.wrapper-goTop').click(() => {
        $('html,body').animate({
            scrollTop: 0
        }, 300);
    });
});