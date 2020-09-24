[#assign currentUser = model.getCurrentUser()!"User is anonymous!"]
[#assign userFullName = model.getUserFullName()!""]
[#assign enrolPage = content.enrolPage!"/enrol"]
[#assign profilePage = content.profilePage!"/user-profile"]
[#assign loginPage = content.loginPage!"/login"]
[#assign navigation = content.navigation!]
[#assign logo = content.logo!]
[#assign currentUserRoles = model.getCurrentUserRoles()!"empty role"]
[#assign notifications = model.getNotifications()!""]
[#assign notificationSize = notifications ? size]

<script>
    window.EKINO_ACADEMY['currentPage'] = "notification"

    var userId = '${currentUser.getIdentifier()}';

    window.EKINO_ACADEMY.page['notification'] = {
        userId:userId
    }
</script>

<header class="app-header__academy">
    <div class="wrapper-app-header">
        <div class="app-header__academylogo">
            <a href="/general">
                [#if logo??]
                    [#assign imgRef = damfn.getAssetLink(logo)!]
                    [#if imgRef??]
                        <img src="${imgRef}"/>
                    [/#if]
                [/#if]
            </a>
        </div>
        <div class="app-header__academymenu">

            [#if currentUser?has_content && !model.isAnonymous()]
                <!-- when use already login -->
                <div class="desk-account">
                    <span class="name__account">${userFullName}</span>
                    <div class="icon-account">
                        <a href="#">
                            [#assign avatarSrc = model.getUserAvatar()]
                            [#if avatarSrc == ""]
                                <img class="img-login"
                                     src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/account-desk.png"
                                     alt="Login Ekino" />
                            [#else]
                                <img class="img-login img-login25x25"
                                     src="${avatarSrc}"
                                     alt="Login Ekino" />
                            [/#if]
                        </a>
                        <div class="icon-logout">
                            <div class="item-profile">
                                <a href="${profilePage}" class="link-dashboard wrapper-icon">
                                    <img class="img-login" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/updateProfile.svg"
                                         alt="Logout Ekino" />
                                    <p>My Profile</p>
                                </a>
                            </div>
                            <div class="item-profile">
                                <a href="/.authenticate?mgnlLogout" class="wrapper-icon">
                                    <img class="img-login" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/logout.png" alt="Logout Ekino" />
                                    <p>Log out</p>
                                </a>
                            </div>

                        </div>
                    </div>
                    <div class="icon-info js-icon-info">
                        <span class="notification__amount js-notification-icon" data-amount='${notificationSize}'></span>
                        <div class="notification js-notification">
                            <div class="notification-item__warpper js-notification-wrapper">
                                [#if notificationSize == 0]
                                    <div class="notification-item notificaiton-item--default">
                                        <div class="notification-sender__action">
                                            <p class="notification__action-desc notification__action-text">Currently, There is no new notification</p>
                                        </div>
                                    </div>
                                [/#if]

                                [#list notifications as notification]
                                    <div class="notification-item js-notificaiton-item">
                                        <div class="notification-item__link js-item-link" data-link="${notification.link}" data-notification="${notification.uuid}">
                                            <div class="notification-sender__action">
                                                <p class="notification__action-desc notification__action-text js-notification-desc">${notification.title!""}<span class="notification__desc--content">${notification.description!""}</span></p>
                                                <p class="notification__action-time notification__action-text">${notification.notificationDate!""}</p>
                                            </div>
                                        </div>
                                    </div>
                                [/#list]

                            </div>
                        </div>
                    </div>
                </div>
            [#else ]
                <!-- when use not yet login -->
                <div class="login">
                    <div class="wrapper-loginEnrol">
                        <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/accountLock.png"
                             alt="logo account lock">
                        <a href="${loginPage}">
                            Login
                        </a>
                        <span>&sol;</span>
                        <a href="${enrolPage}">
                            Enrol
                        </a>
                    </div>
                </div>
            [/#if]

            <div class="menu">
                <ul class="description">
                    [#list cmsfn.children(content, "mgnl:contentNode") as navItem]
                        [#assign isShow = model.roleHasPermission(currentUserRoles, navItem.grantedRoles)?then(true, false)]
                        [#if isShow == true]
                            <li>
                                <div class="item-link">
                                    [#--if nav has sub navs--]
                                    [#if cmsfn.children(navItem, "mgnl:contentNode")?size > 0]
                                    [#--if user have a role --]
                                        [#assign isUserHasARole = model.userHasARole(currentUserRoles)?then(true, false)]
                                        [#if (isUserHasARole == true)]
                                            [#assign navLink = "#"]
                                            [#assign countSubMenu = 0]
                                            [#list cmsfn.children(navItem, "mgnl:contentNode") as subNavItem]
                                                [#assign isShowSubMenu = model.roleHasPermission(currentUserRoles, subNavItem.subGrantedRoles)?then(true, false)]
                                            [#-- if grantedRoles of sub = user roles --]
                                                [#if isShowSubMenu == true]
                                                    [#assign countSubMenu = countSubMenu + 1]
                                                    [#assign navLink = subNavItem.linkSub]
                                                [/#if]
                                            [/#list]
                                        [#-- if parent nav has a sub, set the link and title for parent nav--]
                                            [#if countSubMenu == 1]
                                                <a href="${navLink}">${navItem.title}</a>
                                            [#-- else set none for the parent's link and get the subs of parent--]
                                            [#else]
                                                <a href="#">${navItem.title}</a>
                                                <div class="sub-menu">
                                                    [#list cmsfn.children(navItem, "mgnl:contentNode") as subNavItem]
                                                        [#if model.roleHasPermission(currentUserRoles, subNavItem.subGrantedRoles)?then(true, false)]
                                                            <div class="sub-menu-link">
                                                                <a href="${subNavItem.linkSub}">${subNavItem.titleSub}</a>
                                                            </div>
                                                        [/#if]
                                                    [/#list]
                                                </div>
                                            [/#if]
                                        [#--else if user have more one role--]
                                        [#else]
                                            <a href="#">${navItem.title}</a>
                                            <div class="sub-menu">
                                                [#list cmsfn.children(navItem, "mgnl:contentNode") as subNavItem]
                                                    [#if model.roleHasPermission(currentUserRoles, subNavItem.subGrantedRoles)?then(true, false)]
                                                        <div class="sub-menu-link">
                                                            <a href="${subNavItem.linkSub}">${subNavItem.titleSub}</a>
                                                        </div>
                                                    [/#if]
                                                [/#list]
                                            </div>
                                        [/#if]
                                    [#--else if nav doesn't have sub navs--]
                                    [#else]
                                        [#assign link = navItem.link!"#"]
                                        <a href="${link}">${navItem.title}</a>
                                    [/#if]
                                </div>
                            </li>
                        [/#if]
                    [/#list]
                </ul>
            </div>
            <div class="nav-icon">
                <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/nav.png" alt="Nav menu">
            </div>
            <div class="nav-menu js-nav-menu">
                <div class="mobile-nav-icon">
                    <img class="icon-close"
                         src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/removeNav.png"
                         alt="close navbar">
                </div>
                <div class="sub-menu__icon js-icon-back"></div>
                <div class="wrapper-content-mobile">
                    <div class="mobile-menu">
                        <ul class="list-menu">
                            [#--                          [#if navigation?has_content]
                                                          [#list cmsfn.children(navigation) as navItem]
                                                              [#if !navItem.login || (navItem.login && currentUser?has_content && !model.isAnonymous())]
                                                                  <li class="menu__item">
                                                                      <div class="link">
                                                                          <a href="${navItem.link}">${navItem.title}</a>
                                                                      </div>
                                                                  </li>
                                                              [/#if]
                                                          [/#list]
                                                      [/#if]

                          --]
                            [#list cmsfn.children(content, "mgnl:contentNode") as navItem]
                                [#assign isShow = model.roleHasPermission(currentUserRoles, navItem.grantedRoles)?then(true, false)]
                                [#if isShow == true]
                                    <li class="menu__item js-menu-item">
                                        <div class="link">
                                            [#--if nav has sub navs--]
                                            [#if cmsfn.children(navItem, "mgnl:contentNode")?size > 0]
                                            [#--if user have a role --]
                                                [#assign isUserHasARole = model.userHasARole(currentUserRoles)?then(true, false)]
                                                [#if (isUserHasARole == true)]
                                                    [#assign navLink = "#"]
                                                    [#assign countSubMenu = 0]
                                                    [#list cmsfn.children(navItem, "mgnl:contentNode") as subNavItem]
                                                        [#assign isShowSubMenu = model.roleHasPermission(currentUserRoles, subNavItem.subGrantedRoles)?then(true, false)]
                                                    [#-- if grantedRoles of sub = user roles --]
                                                        [#if isShowSubMenu == true]
                                                            [#assign countSubMenu = countSubMenu + 1]
                                                            [#assign navLink = subNavItem.linkSub]
                                                        [/#if]
                                                    [/#list]
                                                [#-- if parent nav has a sub, set the link and title for parent nav--]
                                                    [#if countSubMenu == 1]
                                                        <a href="${navLink}">${navItem.title}</a>
                                                    [#-- else set none for the parent's link and get the subs of parent--]
                                                    [#else]
                                                        <a href="#">${navItem.title}</a>
                                                        <div class="sub-menu js-sub-menu">
                                                            <div class="sub-menu__content">
                                                                [#list cmsfn.children(navItem, "mgnl:contentNode") as subNavItem]
                                                                    [#if model.roleHasPermission(currentUserRoles, subNavItem.subGrantedRoles)?then(true, false)]
                                                                        <div class="sub-menu-link">
                                                                            <a href="${subNavItem.linkSub}">${subNavItem.titleSub}</a>
                                                                        </div>
                                                                    [/#if]
                                                                [/#list]
                                                            </div>
                                                        </div>
                                                    [/#if]
                                                [#--else if user have more one role--]
                                                [#else]
                                                    <a href="#">${navItem.title}</a>
                                                    <div class="sub-menu js-sub-menu">
                                                        <div class="sub-menu__content">
                                                            [#list cmsfn.children(navItem, "mgnl:contentNode") as subNavItem]
                                                                [#if model.roleHasPermission(currentUserRoles, subNavItem.subGrantedRoles)?then(true, false)]
                                                                    <div class="sub-menu-link">
                                                                        <a href="${subNavItem.linkSub}">${subNavItem.titleSub}</a>
                                                                    </div>
                                                                [/#if]
                                                            [/#list]
                                                        </div>
                                                    </div>
                                                [/#if]
                                            [#--else if nav doesn't have sub navs--]
                                            [#else]
                                                [#assign link = navItem.link!"#"]
                                                <a href="${link}">${navItem.title}</a>
                                            [/#if]
                                        </div>
                                    </li>
                                [/#if]
                            [/#list]
                        </ul>
                    </div>

                    [#if currentUser?has_content && !model.isAnonymous()]
                    <!-- when user already login -->
                    <div class="log-out">
                        <a href="/.authenticate?mgnlLogout">Log out</a>
                    </div>

                    <div class="mobile-account">
                        <div class="account">
                            <a href="${profilePage}">
                                [#if avatarSrc == ""]
                                    <img class="img-login"
                                         src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/account-desk.png"
                                         alt="Login Ekino" />
                                [#else]
                                    <img class="img-login img-login60x60"
                                         src="${avatarSrc}"
                                         alt="Login Ekino" />
                                [/#if]
                            </a>
                        </div>
                        <div class="icon-info js-icon-info">
                            <span class="notification__amount js-notification-icon" data-amount='${notificationSize}'></span>
                            <div class="notification js-notification">
                                <span class="notification-close js-notificaiton-close"></span>
                                <div class="notification-item__warpper js-notification-wrapper">
                                    [#if notificationSize == 0]
                                        <div class="notification-item notificaiton-item--default">
                                            <div class="notification-sender__action">
                                                <p class="notification__action-desc notification__action-text">Currently, There is no new notification</p>
                                            </div>
                                        </div>
                                    [/#if]

                                    [#list notifications as notification]
                                        <div class="notification-item js-notificaiton-item">
                                            <div
                                                    class="notification-item__link js-item-link" data-link="${notification.link}" data-notification="${notification.uuid}">
                                                <div class="notification-sender__action">
                                                    <p class="notification__action-desc notification__action-text js-notification-desc">${notification.title!""}<span class="notification__desc--content">${notification.description!""}</span></p>
                                                    <p class="notification__action-time notification__action-text">${notification.notificationDate!""}</p>
                                                </div>
                                            </div>
                                        </div>
                                    [/#list]

                                </div>
                            </div>
                        </div>
                        [#else ]
                            <!-- when use not yet login -->
                            <div class="mobile-login">
                                <div class="wrapper-loginEnrol">
                                    <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/accountLock.png"
                                         alt="logo account lock">
                                    <a href="${loginPage}">
                                        Login
                                    </a>
                                    <span>&sol;</span>
                                    <a href="${enrolPage}">
                                        Enrol
                                    </a>
                                </div>
                            </div>
                        [/#if]
                    </div>
                </div>
            </div>
        </div>
</header>
