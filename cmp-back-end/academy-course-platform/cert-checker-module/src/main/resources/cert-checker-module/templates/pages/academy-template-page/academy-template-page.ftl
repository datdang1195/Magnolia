[#assign title = content.title!"Dummy page (created by maven archetype)"]
<!DOCTYPE html>
<html>
<head>
    <title>${title?capitalize}</title>
    <meta charset="utf8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=0" />
    <link rel="icon" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/logo-ekino-academy-mb.png" type="image/png" sizes="16x16">

    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/bundled/css/index.css">

    [@cms.page /]
</head>
<body>

<script>
    window.EKINO_ACADEMY = window.EKINO_ACADEMY || { page: {} };
</script>
[@cms.area name="header"/]

<div class="app-wrapper__academy">
    [@cms.area name="main"/]
</div>

[@cms.area name="footer"/]
<script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/bundled/js/index.js"></script>
</body>
</html>
