$( document ).ready(function() {
    //variable enrolForm
    const $enrolForm = $('#enrol-form');
    const $inputEnrol = $enrolForm.find('input, select');
    const $submitEnrol = $enrolForm.find('button.btn__enrol');

    //variable profileForm
    const $formProfile = $('#profile-user-form');
    const $btnUpdate = $formProfile.find('.js-button-update');

    //variable loginForm
    const $loginForm = $("#login-form");
    const $inputLogin = $loginForm.find('input');
    const $submitLogin = $loginForm.find('button.btn__login');

    //variable forgotPWForm
    const $forgotPWForm = $('#send-code-form');
    const $inputForgotPW = $forgotPWForm.find('input');
    const $submitForgotPW = $forgotPWForm.find('.btn__sent');

    //variable resetPW
    const $resetPWForm = $('#reset-password-form');
    const $inputResetPW = $resetPWForm.find('input');
    const $submitResetPW = $resetPWForm.find('button.btn__reset');

    const messError = {
        required: fieldName => `Please enter your ${fieldName}`,
        maxlength: maxLength =>
            `Please enter no more than ${maxLength} characters.`,
        minlength: (fieldName, minLength) =>
            `${fieldName} must have at least ${minLength} characters`,
        valid: fieldTypes => `Please enter a valid ${fieldTypes}`,
        email: nameForm => `Please use ekino email to ${nameForm}`,
        confirmPW: 'Confirm password does not match',
    };

    //check the input is ekino email
    $.validator.addMethod('isEkinoEmail', function(
        value
    ) {
        const regexEmail = /[a-z]+@ekino.com$/;
        return regexEmail.test(value) ? true : false;
    });

    const buttonSubmitHandler = ($input, $btnSubmit, $form) => {
        $input.on('input keyup change', function() {
            const isValid = $form.validate().checkForm();
            if (isValid) {
                $btnSubmit.prop('disabled', false);
            } else {
                $btnSubmit.prop('disabled', true);
            }
        });
    };

    // validation for page enrol
    $enrolForm.validate({
        rules: {
            email: {
                required: true,
                isEkinoEmail: true,
            },
            username: {
                required: true,
            },
            password: {
                required: true,
                minlength: 6,
            },
            confirmPassword: {
                equalTo: '#enrol-password',
            },
            year: 'required',
            phase: 'required',
            program: 'required',
        },
        messages: {
            email: messError.email('enrol'),
            username: messError.required('full name'),
            password: messError.minlength('Password', 6),
            confirmPassword: messError.confirmPW,
            year: '',
            phase: '',
            program: '',
        },
    });

    buttonSubmitHandler($inputEnrol, $submitEnrol, $enrolForm);

    // validation for page login
    $loginForm.validate({
        rules: {
            emailLogin: {
                required: true,
                isEkinoEmail: true,
            },
            passwordLogin: {
                required: true,
                minlength: 6,
            },
        },
        messages: {
            emailLogin: messError.email('login'),
            passwordLogin: messError.minlength('Password', 6),
        },
        onkeyup: false
    });

    $inputLogin.on('input keyup change', function() {
        const isValid = $loginForm.validate().checkForm();
        if (isValid && $('#login-password').val().length > 5) {
            $submitLogin.prop('disabled', false);
        } else {
            $submitLogin.prop('disabled', true);
        }
    });


    // validation for sent email
    $forgotPWForm.validate({
        rules: {
            emailReset: {
                required: true,
                isEkinoEmail: true
            }
        },
        messages: {
            emailReset: messError.email('reset password')
        }
    });

    buttonSubmitHandler($inputForgotPW, $submitForgotPW, $forgotPWForm);

    // validation for email reset password
    $resetPWForm.validate({
        rules: {
            passwordReset: {
                required: true,
                minlength: 6,
            },
            confirmPasswordReset: {
                equalTo: '#reset-password',
            },
        },
        messages: {
            passwordReset: messError.minlength('Password', 6),
            confirmPasswordReset: messError.confirmPW,
        },
    });

    buttonSubmitHandler($inputResetPW, $submitResetPW, $resetPWForm);

    // validation for user profile
    $formProfile.validate({
        rules: {
            name: {
                required: true,
                maxlength: 255,
            },
            role: {
                maxlength: 255,
            },
            department: {
                maxlength: 255,
            },
            phone: {
                maxlength: 11,
                number: true,
            },
        },
        messages: {
            name: {
                required: messError.required('name'),
                maxlength: messError.maxlength(255),
            },
            phone: {
                number: messError.valid('number'),
                maxlength: messError.maxlength(11),
            },
            role: messError.maxlength(255),
            department: messError.maxlength(255),
        },
        onkeyup: () => {
            $btnUpdate.prop('disabled', false);
        },
        onfocusout: ele => {
            $(ele).valid();
        },
        invalidHandler: () => {
            $btnUpdate.prop('disabled', true);
        },
    });

    const $formChangePW = $('#change-password-form');
    const $submitChangePW = $formChangePW.find('.js-change-password-submit');
    const $inputChangePW = $formChangePW.find('input')
    const $validRule = {
        required: true,
        minlength: 6,
    }
    const checkFormChangePW = () => {
        const isValid = $formChangePW.validate().checkForm();
        if (isValid) {
            $submitChangePW.prop('disabled', false);
        } else {
            $submitChangePW.prop('disabled', true);
        }
    }

    // validation for change password
    $formChangePW.validate({
        rules: {
            currentPassword: $validRule,
            password: $validRule,
            confirmPassword: {
                ...$validRule,
                equalTo: '#new-password',
            },
        },
        messages: {
            currentPassword: {
                required: messError.required('current password'),
                minlength: messError.minlength('current password', 6),
            },
            password: {
                required: messError.required('new password'),
                minlength: messError.minlength('new password', 6),
            },
            confirmPassword: messError.confirmPW,
        },
        onkeyup: true
    });

    $inputChangePW.on('blur', function () {
        const $this = $(this);
        $this.valid();
        checkFormChangePW();
    })

});