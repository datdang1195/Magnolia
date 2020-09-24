[#assign years = model.getYears()]
[#assign authorPath = model.getAuthorPath()]
[#assign listPhases = model.getAllPhase()]
[#assign listProgram = model.getAllProgram()]
[#assign successPage = content.successPage!"/home"]
[#assign currentPage = model.getParent().getNode().getPath()!"/enrol"]
[#assign _year = ctx.year!"NA"]
[#assign _phaseId = ctx.phaseId!"NA"]
[#assign _programId = ctx.programId!"NA"]
<script>
    window.EKINO_ACADEMY['currentPage'] = "enrol"
    let successPage = "${successPage}";
    console.log(successPage, "successPage");
    let authorPath = "${authorPath}";
    console.log(authorPath, "authorPath");
    let phasesData = ${listPhases};
    console.log(phasesData, "listPhases")
    let programsData = ${listProgram};
    console.log(programsData, "listProgram")
    let _year = "${_year}";
    console.log(_year, "_year")
    let _phaseId = "${_phaseId}";
    console.log(_phaseId, "_phaseId")
    let _programId = "${_programId}";
    console.log(_programId, "_programId")
    window.EKINO_ACADEMY.page['enrol'] = {
        authorPath:authorPath,
        phasesData:phasesData,
        programsData:programsData,
        _year:_year,
        _phaseId:_phaseId,
        _programId:_programId,
    }
</script>
<div class="app-wrapper__enrol">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="container-form">
        <p class="page__title page__title--form">enrol</p>
        <form action="/.enrol" class="enrol-form" id="enrol-form" method="post">
            <input type="text" name="currentPage" value="${currentPage}" style="display: none"/>
            <input type="text" name="successPage" value="${successPage}" style="display: none"/>
            <div class="form-group">
                <label>email<span class="asterisk">&#42;</span></label>
                <input type="text" class="form-control__input" name="email" id="email">
            </div>
            <div class="form-group">
                <label>full name<span class="asterisk">&#42;</span></label>
                <input type="text" class="form-control__input" name="username" id="username">
            </div>
            <div class="form-group">
                <label>password<span class="asterisk">&#42;</span></label>
                <input type="password" class="form-control__input" id="enrol-password" name="password">
                <div class="eye" id="enrol-eye-password"></div>
            </div>
            <div class="form-group">
                <label>confirm password<span class="asterisk">&#42;</span></label>
                <input type="password" class="form-control__input" id="enrol-confirm" name="confirmPassword">
                <div class="eye" id="enrol-eye-confirm"></div>
            </div>
            <div class="horizontal-vertical">
                <div class="form-group form-group--left">
                    <label>Partipation year<span class="asterisk">&#42;</span></label>
                    <div class="form-control__select">
                        <span class="select-value select-value-year">&#45; &#45; &#45;</span>
                        <select class="select" id="select-year" name="year">
                        </select>
                    </div>
                </div>
                <div class="form-group form-group--width">
                    <label>phase<span class="asterisk">&#42;</span></label>
                    <div class="form-control__select">
                        <span class="select-value select-value-phase unselected">&#45; &#45; &#45;</span>
                        <select class="select" id="select-phase" name="phase" disabled>
                        </select>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label>program<span class="asterisk">&#42;</span></label>
                <div class="form-control__select">
                    <span class="select-value select-value-program unselected">&#45; &#45; &#45;</span>
                    <select class="select" id="select-program" name="program" disabled>
                    </select>
                </div>
            </div>
            <div class="btn btn--margin">
                <button type="submit" class="btn__enrol" id="btnEnrol" disabled>enrol</button>
            </div>
        </form>
    </div>
</div>
