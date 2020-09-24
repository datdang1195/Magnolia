$(document).ready(function () {
    $(function () {
        let flag;
        let elmBody = $('body');
        const navMobile = '.js-nav-menu';
        const $navMobile = $('.js-nav-menu');
        const $navItem = $navMobile.find('.js-menu-item');
        const $iconBack = $navMobile.find('.js-icon-back');
        const subMenu = '.js-sub-menu';
        const clasActive = 'fade_in';
        $navItem.on('click', function () {
            const $this = $(this);
            const $subMenu = $this.find(subMenu);
            if ($subMenu.length) {
                $subMenu.addClass(clasActive);
                $iconBack.addClass(clasActive);
            }
        });

        $iconBack.on('click', function() {
            const $this = $(this);
            $this.removeClass(clasActive);
            $this
                .closest(navMobile)
                .find(subMenu)
                .removeClass(clasActive);
        });

        $('.nav-icon').click((event) => {
            $('.nav-menu').css("width", "100%");
            elmBody.addClass('hidden-body');
            flag = 1;
        })
        $('.icon-close').click(() => {
            $('.nav-menu').css("width", "0");
            elmBody.removeClass('hidden-body');
            flag = 0;
        })
        $(window).resize(function () {
            if (window.innerWidth > 1024) {
                $('.nav-menu').css("width", "0");
            } else {
                if (flag) {
                    $('.nav-menu').css("width", "100%");
                }
            }
        });

        let current_site = document.location.pathname;

        let item_links = $('div.item-link').parent().siblings();

        //remove all li tag have class 'active' in nav bar
        item_links.removeClass('active');

        for (let i=0; i < item_links.length; i++){
            //check for bold and underline on the current page
            if($(item_links[i]).children('div').children('a').attr('href') === current_site){
                $(item_links[i]).addClass('active');
            }
        }

        let links = $('div.link').parent().siblings();

        //remove all li tag have class 'active' in nav bar
        links.removeClass('active');

        for (let i=0; i < links.length; i++){
            //check for bold and underline on the current page
            if($(links[i]).children('div').children('a').attr('href') === current_site){
                $(links[i]).addClass('active');
            }
        }
    });
});
