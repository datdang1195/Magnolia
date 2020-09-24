$( document ).ready(function() {
    $('#select-year').change((event) => {
        let value = event.target.value;
        let textOption = $("#select-year option:selected").text();
        $('.select-value-year').html(textOption);
        if (value) {
            $('#select-phase').attr('disabled', false);
            $('.select-value-phase').removeClass('unselected');
            getListPhaseByYear(value);
        } else {
            changeSelectedPhase();
            changeSelectedProgram();
        }
    });

    $('#select-phase').change((event) => {
        let value = event.target.value;
        getTextSelectPhase();
        if (value) {
            $('#select-program').attr('disabled', false);
            $('.select-value-program').removeClass('unselected');
            getProgramByPhase(value);
        } else {
            changeSelectedProgram();
        }
    });

    $('#select-program').change((event) => {
        getTextSelectProgram();
    });

    $('#enrol-eye-password').click(() => {
        let elm = $('#enrol-eye-password');
        if (elm.hasClass('active')) {
            elm.removeClass('active');
            $('#enrol-password').attr('type', 'password');
        } else {
            elm.addClass('active');
            $('#enrol-password').attr('type', 'text');
        }
    });

    $('#enrol-eye-confirm').click(() => {
        let elm = $('#enrol-eye-confirm');
        if (elm.hasClass('active')) {
            elm.removeClass('active');
            $('#enrol-confirm').attr('type', 'password');
        } else {
            elm.addClass('active');
            $('#enrol-confirm').attr('type', 'text');
        }
    });

    if (_year !== "") {
        $('#select-year').val(_year).change();
    }
    if (_phaseId !== "") {
        $('#select-phase').val(_phaseId).change();
    }
    if (_programId !== "") {
        $('#select-program').val(_programId).change();
    }

    function getTextSelectProgram() {
        let textOption = $('#select-program option:selected').text();
        $('.select-value-program').html(textOption);
        $('#programName').val(textOption);
    }

    function getTextSelectPhase() {
        let textOption = $('#select-phase option:selected').text();
        $('.select-value-phase').html(textOption);
    }

    function changeSelectedPhase() {
        let elmPhase = $('#select-phase');
        elmPhase.attr('disabled', true);
        $('.select-value-phase').addClass('unselected');
        elmPhase.val('').change();
        getTextSelectPhase();
    }

    function changeSelectedProgram() {
        let elmProgram = $('#select-program');
        elmProgram.attr('disabled', true);
        $('.select-value-program').addClass('unselected');
        elmProgram.val('').change();
        getTextSelectProgram();
    }

    function getListPhaseByYear(year) {
        let data = listPhases.filter(itm => parseInt(year) === itm.startDate.date.year || parseInt(year) === itm.endDate.date.year);
        setData4Dropdown($('#select-phase'), data, "phase");
        let phaseId = $('#select-phase').val();
        getProgramByPhase(phaseId);
    }

    function getProgramByPhase(phaseId) {
        let data = [];
        if (phaseId.length > 0) {
            data = listProgram.filter(itm => {
                let currentDate = toStringByDate(new Date());
                if (itm.startDate === undefined || itm.endDate === undefined) {
                    return phaseId === itm.phase.uuid;
                }
                let startDate = toStringByDate(new Date(itm.startDate.date.year, itm.startDate.date.month - 1, itm.startDate.date.day + 1));
                let endDate = toStringByDate(new Date(itm.endDate.date.year, itm.endDate.date.month - 1, itm.endDate.date.day + 1));
                return (phaseId === itm.phase.uuid && startDate <= currentDate && endDate >= currentDate);
            });
        }

        setData4Dropdown($('#select-program'), data, "group");
    }

    function setData4Dropdown(obj, data, textObj) {
        let emptyItem = $(obj).find("option[value='']")[0];
        obj.empty();
        obj.append(emptyItem);
        for (let itm in data)
        {
            let option = data[itm];
            let newOption = $('<option/>');

            newOption.val(option.uuid);
            newOption.text(option[textObj].displayName);

            obj.append(newOption);
        }
        obj.val('').change();
    }


});


