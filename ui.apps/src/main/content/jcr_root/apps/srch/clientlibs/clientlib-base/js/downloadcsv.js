$(document).ready(function() {
    $('#downloadCsv').click(function() {
        if (!window.searchResponseData) {
            return;
        }
        $.ajax({
            url: '/bin/customtools/convertJsonToCsv',
            type: 'POST',
            data: JSON.stringify(window.searchResponseData),
            contentType: 'application/json',
            success: function(response, status, xhr) {
                let filename = "";
                let disposition = xhr.getResponseHeader('Content-Disposition');
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    let matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
                }

                let type = xhr.getResponseHeader('Content-Type');
                let blob = new Blob([response], { type: type });

                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    window.navigator.msSaveBlob(blob, filename);
                } else {
                    let URL = window.URL || window.webkitURL;
                    let downloadUrl = URL.createObjectURL(blob);

                    if (filename) {
                        let a = document.createElement("a");
                        if (typeof a.download === 'undefined') {
                            window.location = downloadUrl;
                        } else {
                            a.href = downloadUrl;
                            a.download = filename;
                            document.body.appendChild(a);
                            a.click();
                        }
                    } else {
                        window.location = downloadUrl;
                    }
                    setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100);
                }
            },
            error: function(xhr, status, error) {
                alert('Error: ' + error.message);
            }
        });
    });
});