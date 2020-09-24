$(document).ready(function () {

    const $paginationWrapper = $('.js-pagination-list');
    const $paginationControl = $('.js-pagination-control');
    const paginationItem = '.js-pagination-item';
    const currentItemClass = 'current';
    const controlDisabled = 'disabled';
    const STEP = 2; //page before and after current
    const pagination = {
        current: currentPage,
        total: totalPage,
        data: '',
        dots: `<li>...</li>`,
    };

    const addItem = (begin, last) => {
        for (let i = begin; i < last; i++) {
            if (begin <= 0)
                begin = 1;
            pagination.data += `<li class="pagination__item js-pagination-item">${i}</li>`;
        }
    };

    const bindingItem = () => {
        const $paginationItem = $(paginationItem);
        $paginationItem.map((i, e) => {
            if (e.innerHTML === pagination.current.toString()) {
                $(e).addClass(currentItemClass);
            }
            $(e).click(() => clickItem(e));
        });
    };

    const clickItem = e => {
        pagination.current = e.innerHTML;
        handlerDots();
        window.location.href = decodeURI('/news?pageNumber=' + pagination.current);
    };

    const handlerDots = () => {
        const {current, total, dots} = pagination;
        if (total < STEP * 2 + 2) {
            // when the total page less 5 page
            total > 6 ? addItem(1, 6) : addItem(1, total + 1);
        } else if (current > total - STEP * 2) {
            //When current Page is 28,29,30 the dots will show on the frist
            pagination.data += dots;
            addItem(total - STEP * 2, total + 1);
        } else if (current < STEP * 2 + 1) {
            //When current Page is 1,2,3 the dots will show on the last
            addItem(1, STEP * 3);
            pagination.data += dots;
        } else {
            //the dots will show on the last and the frist
            pagination.data += dots;
            addItem(current - STEP, parseInt(current) + STEP + 1);
            pagination.data += dots;
        }
        writePagination();
    };

    const writePagination = () => {
        $paginationWrapper.html(pagination.data);
        pagination.data = '';
        bindingItem();
        handlerDisabled();
    };

    const handlerDisabled = () => {
        const {current, total} = pagination;
        $paginationControl.map((i, e) => {
            if (i === 0 || i === 1) {
                if (current === 1) {
                    $(e).addClass(controlDisabled);
                } else {
                    $(e).removeClass(controlDisabled);
                }
            } else if (i === 2 || i === 3) {
                if (current.toString() === total.toString()) {
                    $(e).addClass(controlDisabled);
                } else {
                    $(e).removeClass(controlDisabled);
                }
            }
        });
    };

    const handlerControl = () => {
        const {current, total} = pagination;
        $paginationControl.click(e => {
            if (current === 1 || CURRENT === total) {
                handlerDisabled();
            }
            const $currentBtn = $(e.currentTarget);
            const controlType = $currentBtn.data('control');
        });
        switch (controlType) {
            case 'next':
                gotoNextItem($currentBtn);
                break;
            case 'prev':
                gotoPrevItem($currentBtn);

                break;
            case 'frist':
                gotoFristItem($currentBtn);
                break;
            case 'last':
                gotoLastItem($currentBtn);
                break;
        }
    };

    const gotoNextItem = e => {
        pagination.current++;
        if (pagination.current > pagination.total) {
            pagination.current = pagination.total;
        }
        handlerDots();
    };

    const gotoPrevItem = e => {
        pagination.current--;
        if (pagination.current < 1) {
            pagination.current = 1;
        }
        handlerDots();
    };

    const gotoFristItem = e => {
        pagination.current = 1;
        handlerDots();
    };

    const gotoLastItem = e => {
        pagination.current = pagination.total;
        handlerDots();
    };

    const initPagination = () => {
        handlerDots();
        handlerControl();
    };

    initPagination();
});