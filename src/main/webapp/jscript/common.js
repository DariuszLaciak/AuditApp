function showInfo(success, message) {
    $("#infoBox").removeClass();
    var FADE_SPEED = 1000;
    var MESSAGE_TIME = 4000;
    var cl = "failed";
    if (success) {
        cl = "success";
    }
    $("#infoBox").addClass(cl).text(message).fadeIn(FADE_SPEED, function () {
        setTimeout(function () {
            $("#infoBox").fadeOut(FADE_SPEED);
        }, MESSAGE_TIME);
    });
}

function makeLoading(message) {
    if (!message) message = "";
    var background = "<div id='loadingBG'><div id='loadingText'>" + message + "</div></div>";
    $("body").append(background);
}

function scrollUp() {
    $("html, body").animate({scrollTop: 0}, "slow");
}

function makeOverlayWindow(id, element, width, height, title, content, buttons, heightFixed, maxHeightInner, isStacked) {
    var top = 0;
    var left = 0;
    if (element === "center") {
        top = $(document).height() / 2 - height / 2;
        left = $(window).width() / 2 - width / 2;
    }
    else {
        top = element.offset().top;
        left = element.offset().left - width;
    }


    var bodyW = $("body").width();
    var bodyH = $("body").height();

    if ((top + height) > bodyH) {
        top = (bodyH - height - 10);
    }
    if ((left + width) > bodyW) {
        left = (bodyW - width - 10);
    }
    var classToAdd = "";
    if (heightFixed) {
        classToAdd = "fixedH80";
    }
    var html = "<div id='overlayWindow_" + id + "' class='overlayWindow " + classToAdd + "' style='width: " + width + "; height: " + height + " ;top: " + top + " ;left: " + left + "'>";
    html += "<div class='innerOverlay'>";
    html += "<div class='overlayTitle'>";
    html += title;
    html += "</div>";
    var styleInner = "";
    if (maxHeightInner) {
        styleInner = "style='height: " + maxHeightInner + "; overflow: auto;'"
    }
    html += "<div class='overlayContent' " + styleInner + ">";
    html += content;
    html += "</div>";
    html += "<div class='overlayButtons'>";
    $(buttons).each(function () {
        html += "<input type='button' class='userMenuButton' value='" + $(this)[0].value + "' onclick='" + $(this)[0].onclick + "'>";
    });
    html += "</div>";
    html += "</div>";
    html += "</div>";
    var background = "<div id='overlayBG'></div>";
    $("body").append(background);
    $("body").append(html);

    if (isStacked) {
        var overlayIndex = $(".overlayWindow").css("z-index");
        var bgIndex = $("#overlayBG").css("z-index");
        $("#overlayBG").css("z-index", overlayIndex + 1);
        $("#overlayWindow_" + id).css("z-index", overlayIndex + 2);
    }
    $("#overlayWindow_" + id).draggable();
}

function closeOverlay(id) {
    $("#overlayWindow_" + id).remove();
    $("#overlayBG").remove();
}

function finishLoading() {
    $("#loadingBG").remove();
}

function updateLabels(event) {
    var labels = event.chart.chartDiv.getElementsByClassName("amcharts-axis-title");
    for (var i = 0; i < labels.length; i++) {
        var color = event.chart.dataProvider[i].color;
        if (color !== undefined) {
            labels[i].setAttribute("fill", color);
        }
    }
    $(".amcharts-chart-div").find("a").remove();
}