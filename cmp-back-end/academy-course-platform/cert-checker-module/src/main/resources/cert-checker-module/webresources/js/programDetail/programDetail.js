$(document).ready(function () {
    $('#submitBtn').click(function() {
        $('#enrolBtn').prop('disabled', true);
    });

    const $enrolButton = $('.js-enrol-landing-button');
    const $modalMessage = $('.js-enrol-modal-message');
    const $modalBackdrop = $('.js-enrol-modal-backdrop');
    const $submitButton = $('.js-enrol-submit-button');
    const $cancelButton = $('.js-enrol-cancel-button');
    const $document = $(document);

    function openModal() {
        $modalMessage.removeClass('hidden');
        $modalBackdrop.removeClass('hidden');
    }
    function closeModal() {
        $modalMessage.addClass('hidden');
        $modalBackdrop.addClass('hidden');
    }
    $enrolButton.click(function() {
        openModal();
    });
    $submitButton.click(function() {
        closeModal();
    });
    $cancelButton.click(function() {
        closeModal();
    });
    $document.click(function(evt) {
        if (!$(evt.target).closest(".js-enrol-landing-button, .js-modal-enrol-dialog").length) {
            closeModal();
        }
    });
});