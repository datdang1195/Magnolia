$(document).ready(function () {
    $('#reset-eye-password').click(() => {
        let elm = $('#reset-eye-password');
        if (elm.hasClass('active')) {
            elm.removeClass('active');
            $('#reset-password').attr('type', 'password');
        } else {
            elm.addClass('active');
            $('#reset-password').attr('type', 'text');
        }
    })

    $('#reset-eye-confirmPassword').click(() => {
        let elm = $('#reset-eye-confirmPassword');
        if (elm.hasClass('active')) {
            elm.removeClass('active');
            $('#reset-password-confirm').attr('type', 'password');
        } else {
            elm.addClass('active');
            $('#reset-password-confirm').attr('type', 'text');
        }
    });
})