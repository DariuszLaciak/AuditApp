function showInfo(success, message) {
    $("#infoBox").removeClass();
    var FADE_SPEED = 1000;
    var MESSAGE_TIME = 2000;
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

function scrollUp() {
    $("html, body").animate({scrollTop: 0}, "slow");
}

function makeOverlayWindow(id, element, width, height, title, content, buttons) {
    var top = 0;
    var left = 0;
    if (element === "center") {
        top = $(window).height() / 2 - height / 2;
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

    var html = "<div id='overlayWindow_" + id + "' class='overlayWindow' style='width: " + width + "; height: " + height + " ;top: " + top + " ;left: " + left + "'>";
    html += "<div class='innerOverlay'>";
    html += "<div class='overlayTitle'>";
    html += title;
    html += "</div>";
    html += "<div class='overlayContent'>";
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
}

function closeOverlay(id) {
    $("#overlayWindow_" + id).remove();
    $("#overlayBG").remove();
}