[#assign userProfile = model.loadUserProfile()!]
[#assign changePwdLink = content.changePasswordPage!"/change-password"]

<script>
    window.EKINO_ACADEMY['currentPage'] = "profile"
    window.userData = JSON.parse('${userProfile}');
    userData = JSON.parse('${userProfile}');
    window.EKINO_ACADEMY.page['profile'] = {
        userData
    }
</script>

<div class="app-wrapper__progress">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="breadcrumb">
        <ul class="breadcrumb-list">
            <li class="breadcrumb-item-root">
                <a href="#url">user profile</a>
            </li>
        </ul>
    </div>
    <div class="container--aligned-layout profile">
        <div class="page__title">user profile</div>
        <form action="/.profile" class="profile__form js-profile-form" id="profile-user-form" method="post" enctype="multipart/form-data">
            <div class="evn-row">
                <div class="evn-col--12 profile__form__col">
                    <div class="profile__avatar">
                        <label for="profile-upload" class="profile__avatar__label">change</label>
                        <input type='file' id="profile-upload" name="image" class="profile__avatar__upload js-profile-input" />
                        <img src="" alt="" class="profile__avatar__img js-profile-avatar"/>
                    </div>
                </div>
                <div class="evn-row evn-col--12 profile__form__col">
                    <div class="profile__form__group evn-col-lg--6 evn-col--12">
                        <label class="profile__label">Name</label>
                        <input
                                type="text"
                                class="profile__input profile__label js-profile-input"
                                name="name"
                                placeholder="Name"
                        />
                    </div>
                    <div class="profile__form__group evn-col-lg--6 evn-col--12">
                        <label class="profile__label">email</label>
                        <input
                                type="text"
                                class="profile__input profile__label js-profile-input"
                                name="email"
                                placeholder="Email"
                                readonly
                                disabled
                        />
                    </div>
                    <div class="profile__form__group evn-col-lg--6 evn-col--12">
                        <label class="profile__label">phone</label>
                        <input
                                type="text"
                                class="profile__input profile__label js-profile-input"
                                name="phone"
                                placeholder="Phone"
                        />
                    </div>
                    <div class="profile__form__group evn-col-lg--6 evn-col--12">
                        <label class="profile__label">Profile</label>
                        <div class="profile__select">
                            <select id="profile-form-dropdown" name="profile" class="js-select-box">
                                <option placeholder value="0">Select your profile</option>
                            </select>
                        </div>
                    </div>
                    <div class="profile__form__group evn-col-lg--6 evn-col--12">
                        <label class="profile__label">Department</label>
                        <div class="profile__select">
                            <select id="department-form-dropdown" name="department" class="js-select-box">
                                <option placeholder value="0">Select your department</option>
                            </select>
                        </div>
                    </div>
                    <a href="${changePwdLink}" class="profile__link">change password</a>
                </div>
                <div class="evn-col--12 profile__form__col">
                    <button class="profile__button js-button-update" type="button" disabled>update</button>
                    <button class="profile__button js-button-submit" type="submit">update</button>
                </div>
            </div>
        </form>
    </div>
    <div class="modal-message hidden js-profile-modal-message">
        <div class="modal-dialog modal-dialog--enrol">
            <div class="modal-content modal-content--enrol">
                <div class="message__label message__label--enrol js-profile-modal-label"></div>
                <div class="evn-row message__row">
                    <div class="message__col">
                        <button class="evn-btn evn-btn--modal evn-btn--cancel js-profile-cancel-button">CANCEL</button>
                    </div>
                    <div class="message__col">
                        <button class="evn-btn evn-btn--modal js-profile-submit-button">SUBMIT</button>
                    </div>
                    <div class="message__success js-message-success">
                        <div class="success__checkmark"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="modal-backdrop hidden js-profile-modal-backdrop">
    </div>
</div>
