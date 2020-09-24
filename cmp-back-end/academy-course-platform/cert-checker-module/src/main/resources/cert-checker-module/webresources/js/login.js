$( document ).ready(function() {
    $('.credentials-error').hide();
    let emailLogin = $('#emailLogin').val();
    if (emailLogin.length > 0) {
        $('.credentials-error').show();
    }
    $('#login-eye').click(() => {
        let elm = $('.eye');
        if (elm.hasClass('active')) {
            $('.eye').removeClass('active');
            $('#login-password').attr('type', 'password');
        } else {
            $('.eye').addClass('active');
            $('#login-password').attr('type', 'text');
        }
    })
});