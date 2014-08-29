

function WidgetStarter(url, callback) {
    $.ajax({type: "GET",
        url: url,
        success: callback,
        headers:
                {
                    Accept: "application/json; charset=utf-8",
                    "Content-Type": "application/json; charset=utf-8"
                },
        dataType: "script"
    });
}

function XMLLoader(url, callback) {
    $.ajax({type: "GET",
        url: url,
        success: callback,
        headers:
                {
                    Accept: "application/xml; charset=utf-8",
                    "Content-Type": "text/xml; charset=utf-8"
                },
        dataType: "xml"
    });
}

