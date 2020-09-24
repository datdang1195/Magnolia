$(document).ready(function () {
    const $document = $(document)
    const $blockInfo = $('.js-icon-info');
    const $notification = $('.js-notification');
    const $notificationItem = $notification.find('.js-notificaiton-item');
    const $notificationClose = $notification.find('.js-notificaiton-close');
    const notificationIcon = '.js-notification-icon';
    const $notificationIcon = $(notificationIcon);
    const showNotification = 'icon-info-show';
    const hideNotificationItem = 'notification-item-hide';
    const BREAK_POINT_DESK = '(min-width: 1024px)';

    const handleUnFocus = (mq, target) => {
        const isDesktop = mq.matches;
        const isIconBell = target.is(notificationIcon);
        if (isDesktop && !isIconBell) {
            $blockInfo.removeClass(showNotification);
        }
    }

    const toggleBox = () => {
        // Toggle notification box
        $notificationIcon.click(e => {
            $blockInfo.toggleClass(showNotification);
        });

        // Handle when click outsite notification will close the notification block just on desktop
        $document.click(e => {
            // define the break point
            const matchMedia = window.matchMedia(BREAK_POINT_DESK);
            matchMedia.addListener(handleUnFocus);
            const $target = $(e.target);
            handleUnFocus(matchMedia, $target);
        });

        // Handle click to button close on mobile
        $notificationClose.click(e => {
            $blockInfo.removeClass(showNotification);
        })
    }

    //Handle Item clicked
    $notificationItem.click(e => {
        const $currentTarget = $(e.currentTarget);
        const $notificationLink = $currentTarget.find('.js-item-link');
        const url = $notificationLink.data('link');
        const notificationId = $notificationLink.data('notification');
        const notification = {userId: userId, notificationId: notificationId};

        $.ajax({
            url: '/.rest/certChecker/v1/viewNotification',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            success: function () {
                $currentTarget.addClass(hideNotificationItem);
                window.location.replace(url);
            },
            data: JSON.stringify(notification)
        });
    })

    toggleBox();
})