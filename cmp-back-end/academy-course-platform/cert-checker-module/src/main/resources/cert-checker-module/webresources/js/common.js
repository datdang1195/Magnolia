$(document).ready(function () {
    window.CMP = window.CMP || {};

    const queryParams = () => {
        const queryString = decodeURIComponent(
            window.location.search.substring(1)
        ).split('&');
        const params = queryString.reduce((parametersObject, keyValue) => {
            const [key, val] = keyValue.split('=');
            parametersObject[key] = val;
            return parametersObject;
        }, {});
        return params;
    }

    const redirectURL = (data = {}) => {
        const param = $.param(data);
        window.location.assign(
            `${window.location.origin}${window.location.pathname}?${param}`
        );
    };

    window.CMP.common = {
        queryParams,
        redirectURL,
    };
});