$(document).ready(function () {
    $("#downloadCsv").hide();
    function getLink(element) {
        return /* html */`
            <div>
                <span>   
                    ${element.componentType} on page:
                </span>
                <a href="${element.pagePath}.html">${element.pagePath}</a>
            </div>
        `;
    }
    function onSearchSuccess(data) {
        $('.results-wrapper').remove();
        var $result = $(".result").append('<div class="results-wrapper"></div>');
        window.searchResponseData = data;

        data.forEach(element => {
            $result.append(getLink(element));
        });
        $("#btnSubmit").prop("disabled", false);
        $("#downloadCsv").show();
    }
    function onSearchError(err) {
        $(".result label").text(err.responseText);
        $("#btnSubmit").prop("disabled", false);
    }

    $("#btnSubmit").click(function (event) {
        if ($("#propName").val().length > 1 && $("#propVal").val().length > 1 && $("#searchPath").val().length > 0) {
            event.preventDefault();

            $("#btnSubmit").prop("disabled", true);
            $.ajax({
                type: "GET",
                enctype: 'multipart/form-data',
                url: `/apps/customtools/search?searchPath=${$("#searchPath").val()}&propName=${$("#propName").val()}&propVal=${$("#propVal").val()}`,
                processData: false,
                contentType: false,
                cache: false,
                success: onSearchSuccess,
                error: onSearchError
            });
        }
    });
});