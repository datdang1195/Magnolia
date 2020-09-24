$( document ).ready(function() {

    $(function () {
        const paramError =  Object.keys(window.CMP.common.queryParams());
        if(paramError.includes("error")) {
            $('#message-error').addClass('error__show');
        }
        const $form = $('#change-password-form');
        const $iconEye = $form.find('.js-form-icon-eye');
        const inputGroup = '.js-form-group';
        const input = '.js-form-input';

        $iconEye.click(function () {
            const $this = $(this);
            $this.toggleClass('active')
            const $inputGroup = $this.closest(inputGroup);
            const $input = $inputGroup.find(input);
            const oldTypeInput = $input.attr('type');
            const updateTypeInput =
                oldTypeInput === 'password' ? 'text' : 'password';
            $input.attr('type', updateTypeInput);
        })

    })
});