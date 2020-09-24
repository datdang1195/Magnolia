[#assign title = content.title!"Title not found." /]
[#assign activePhases= model.getAvailablePhases()!"phase not found!" /]
[#assign availableYears= model.getListPhasesInAllYears()!"phase not found!" /]


<script>
    window.EKINO_ACADEMY['currentPage'] = "programList"
    let yearsData = ${availableYears};
    console.log(yearsData);

    let availablePhases = [];
    [#list activePhases as p]
    availablePhases.push({id: '${p.uuid}', title: '${p.displayName}'});
    [/#list]
    console.log(availablePhases);
    const orderDefault = ['foundation', 'profession', 'expert', 'mastery'];

    window.EKINO_ACADEMY.page['programList'] = {
        yearsData:yearsData,
        availablePhases:availablePhases,
        orderDefault:orderDefault
    }

</script>


<div class="container-programList">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>
    <div class="progressbar-wrapper">
        <div class="arrow-slider-left">
            <div class="arrow-left"></div>
        </div>
        <div class="dotted-line"></div>
        <div class="line"></div>
        <div class="container-slider">
            <div class='slider-years'>
            </div>
        </div>
        <div class="arrow-slider-right">
            <div class="arrow-right"></div>
            <div class="arrow-right-again"></div>
        </div>
    </div>
    <div class="wrapperContent">
        <div class="chart">
            <h1 class="page__title">Academy</h1>
            <p>Standard model</p>
            <div class="chart-content">
                <div class="chart__line"></div>
                <div class="cell well" id="list-phase">
                </div>
            </div>
        </div>
        <div class="programs">
            <div class="program-content">
                <p class="project-lable">programs</p>
                <ul class="wrapper-project-content">
                </ul>
            </div>
        </div>
        <div class="definition">
            <h5>Definition</h5>
            <span class="definition__title"></span>
        </div>
    </div>
</div>
