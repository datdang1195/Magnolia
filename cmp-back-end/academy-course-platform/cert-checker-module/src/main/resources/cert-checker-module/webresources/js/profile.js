$(document).ready(function () {
    const data = window.userData;
    const dropdownProfile = '#profile-form-dropdown';
    const dropdownDepartment = '#department-form-dropdown';
    const $form = $('.js-profile-form');
    const $input = $form.find('.js-profile-input');
    const $select = $form.find('.js-select-box');
    const $avatar = $form.find('.js-profile-avatar');
    const $btnUpdate = $form.find('.js-button-update');
    const $btnSubmit = $form.find('.js-button-submit');
    const $inputUpload = $('#profile-upload');
    const $modalMessage = $('.js-profile-modal-message');
    const $modalLabel = $modalMessage.find('.js-profile-modal-label');
    const $modalBackdrop = $('.js-profile-modal-backdrop');
    const $modalSubmit = $('.js-profile-submit-button');
    const $modalCancel = $('.js-profile-cancel-button');
    const $modalSuccess = $('.js-message-success');
    let imgSrc = data ? data.image : '';
    let newUSER;//use this variable value let update profile user
    let choicesProfile;
    let choicesDepartment;

    const initFormDropdown = (eleId, datas) => {
        const choices =  new Choices(eleId, {
            searchEnabled: false,
            silent: 'select-single',
            choices: datas,
        });

        const listSelected = choices._currentState.items;
        if (listSelected.length === 1) {
            choices.setChoiceByValue(1);
        } else {
            const valueSelected = listSelected.filter(
                e => !e.placeholder
            )[0].value;
            choices.setChoiceByValue(valueSelected);
        }
        return choices
    }

    $select.change(function() {
        $btnUpdate.prop('disabled', false);
    });

    const openModal = () => {
        if ($form.validate().checkForm()) {
            $modalCancel.show();
            $modalSubmit.show();
            $modalLabel.html('Are you sure you want to update this profile ?');
            $modalMessage.removeClass('hidden');
            $modalBackdrop.removeClass('hidden');
        } else {
            $btnUpdate.prop('disabled', true);
        }
    };

    const closeModal = () => {
        $modalMessage.addClass('hidden');
        $modalBackdrop.addClass('hidden');
        $modalSuccess.hide();
        $btnUpdate.prop('disabled', true);
    };

    //fill data to the input
    const fillData = data => {
        $btnSubmit.attr('data-uid', data.uuid);
        $input.each(function () {
            const $this = $(this);
            let keyData = $this.attr('name');
            if (keyData === 'image' && data[keyData] !== '') {
                $avatar.show();
                $avatar.attr('src', data[keyData]);
            } else {
                $this.val(data[keyData]);
            }
        })
    };

    // resize image has upload by user
    const uploadImage = () => {
        $inputUpload.change(function () {
            $avatar.show();
            const maxSize = 130;
            const file = this.files[0];
            if (file && file.type.match(/image.*/)) {
                const canvas = document.createElement('canvas');
                const context = canvas.getContext('2d');
                const image = new Image();
                image.onload = () => {
                    const iw = Math.round(image.width / 15);
                    const ih = Math.round(image.height / 15);
                    canvas.width = iw < maxSize ? maxSize : iw;
                    canvas.height = ih < maxSize ? maxSize : ih;
                    context.drawImage(
                        image,
                        0,
                        0,
                        image.width,
                        image.height,
                        0,
                        0,
                        canvas.width,
                        canvas.height
                    );
                    $avatar.attr('src', canvas.toDataURL());
                };
                imgSrc = file;
                image.src = URL.createObjectURL(file);;
            }
            $btnUpdate.prop('disabled', false);
        });
    };

    const setSelected = field => {
        return data[field.name].map(e => {
            if (e.value === field.value) {
                e.selected = true;
            } else {
                e.selected = false;
            }
            return e;
        });
    };

    const handleConfirm = () => {
        $btnSubmit.trigger('click');
        $modalCancel.hide();
        $modalSubmit.hide();
        $modalLabel.html('User information was updated successfully');
        $modalSuccess.show();
        setTimeout(() => closeModal(), 1500);
    }

    $btnSubmit.click(function (event) {
        const isValid = $form.validate().checkForm();
        if (isValid) {
            event.preventDefault();
            const [fieldName,fieldPhone, fieldProfile, fieldDeparment] = $form.serializeArray();
            console.log($form.serializeArray());
            const listProfile = setSelected(fieldProfile);
            const listDeparment = setSelected(fieldDeparment);
            newUSER = {
                ...data,
                image: imgSrc,
                name: fieldName.value,
                phone: fieldPhone.value,
                profile: listProfile,
                department: listDeparment
            };

            let formData = new FormData();
            formData.append("name", fieldName.value);
            formData.append("phone", fieldPhone.value);
            formData.append("profile", fieldProfile.value);
            formData.append("department", fieldDeparment.value);
            formData.append("image", imgSrc);

            let request = new XMLHttpRequest();
            request.open("POST", "/.profile");
            request.send(formData);
        }
    });

    const getSelected = listSelect => {
        return listSelect.filter(e => e.selected);
    }

    const cancelUpdateHandler = () => {
        closeModal();
        $inputUpload.val('');
        if (choicesProfile && choicesDepartment && data) {
            const getSelectedProfile = getSelected(data.profile);
            const getSelectdDepartment = getSelected(data.department);
            const valueDepartment = getSelectdDepartment.length ? getSelectdDepartment[0].value : "0";
            const valueProfile = getSelectedProfile.length ? getSelectedProfile[0].value : "0";
            choicesDepartment.setChoiceByValue(valueDepartment);
            choicesProfile.setChoiceByValue(valueProfile);
        }
        if (!newUSER && data) {
            fillData(data);
        } else {
            fillData(newUSER);
        }
    };

    const initProfile = () => {
        if (data && !choicesProfile && !choicesDepartment) {
            fillData(data);
            choicesProfile = initFormDropdown(
                dropdownProfile,
                data.profile
            );
            choicesDepartment = initFormDropdown(
                dropdownDepartment,
                data.department
            );
        }
        uploadImage();
        $modalCancel.click(() => cancelUpdateHandler());
        $btnUpdate.click(() => openModal());
        $modalSubmit.click(() => handleConfirm());
    };

    initProfile();
});
