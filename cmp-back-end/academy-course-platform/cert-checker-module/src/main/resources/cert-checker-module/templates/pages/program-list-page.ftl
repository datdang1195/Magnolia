[#assign title = content.title!"Title not found." /]
[#assign phases = model.getPhaseDtos()!"phases not found!" /]
[#assign selectedPhase = model.getSelectedPhase()!"phases not found!" /]
[#assign programs = model.getProgramDtos()!"progran not found!" /]

<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">
    <script type="module">
        [#list def.jsFiles as jsFile]
        import '${jsFile.link}';
        [/#list]
    </script>
    [@cms.page /]
</head>
<body>

<!-- Content -->
<div class="app-wrapper">

    <select class="form-control" id="year-select">
        <option value="2019" selected> 2019 </option>
        <option value="2020"> 2020 </option>
        <option value="2021"> 2021 </option>
    </select>

    [#if phases?has_content]
        [#list phases as phase]
            [#if phase.title??]
                <button type="button" name="phaseBtn" id="${phase.uuid}">${phase.title}</button>
            [/#if]
        [/#list]
    [/#if]

    [#if selectedPhase.description?has_content]
        <h2>${selectedPhase.description}</h2>
    [#else ]
        <h2>Phase description is empty</h2>
    [/#if]

    [#if programs?has_content && selectedPhase.title?has_content]
        <ul id="program-list">
            [#list programs as program]
            <li>${program.title}</li>
            [/#list]
        </ul>
    [#else ]
        <h3>Program is empty</h3>
    [/#if]

</div>
<!-- End Content -->

</body>
</html>
