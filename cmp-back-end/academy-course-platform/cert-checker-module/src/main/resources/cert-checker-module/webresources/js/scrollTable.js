$(document).ready(function () {
    let elmCourse = $('.table-courses');
    let elmTraining = $('.table-training');
    let numberAttitude = attitudeSize;
    const options = {
        alwaysShowTracks: true,
        thumbMinSize: 5,
    }

    if (elmCourse.length) {
        Scrollbar.init(document.querySelector('.table-courses'), options);
    }
    if (elmTraining.length) {
        Scrollbar.init(document.querySelector('.table-training'), options);
    }

    for (var i = 0; i < numberAttitude; i++) {
        let elmAttitude = $(`.table-attitude-${i + 1}`);
        if (elmAttitude.length) {
            Scrollbar.init(document.querySelector(`.table-attitude-${i + 1}`), options);
        }
    }
});