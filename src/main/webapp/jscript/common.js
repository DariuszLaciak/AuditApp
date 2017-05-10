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