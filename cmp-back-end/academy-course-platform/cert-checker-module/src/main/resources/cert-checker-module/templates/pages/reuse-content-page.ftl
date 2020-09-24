[#assign title = content.title!]
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=0" />

    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/common.css">
    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/footerAcademy.css">
    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/headerAcademy.css">
    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/fonts.css">
    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/main.css">
    <link rel="stylesheet" href="${ctx.contextPath}/.resources/cert-checker-module/webresources/css/academy/breadcrumb.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/jquery/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/nav/navMenu.js"></script>
    <script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/swipe.js"></script>
    <script type="text/javascript" src="${ctx.contextPath}/.resources/cert-checker-module/webresources/js/schedule.js"></script>

[@cms.page /]
</head>
<body>
    [@cms.area name="header"/]

    [@cms.area name="footer"/]
</body>
</html>
