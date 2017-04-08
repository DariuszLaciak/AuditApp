$(function () {
    $("#loginPanel").submit(function(event){
        event.preventDefault();
        var login = $("#user_id").val();
        var password = $("#user_password").val();
        $.ajax({
            url: "/Login",
            method: "POST",
            dataType: "json",
            data: {
                login: login,
                password: password
            },
            success: function(data){
                if(data.success){
                    window.location.reload();
                }
                else {
                    showInfo(false,data.message);
                }
            }
        });
    });
    $(".logoutUser").click(function() {
        $.ajax({
            url: "/Logout",
            method: "POST",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    window.location.reload();
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    });
    $("#registerUser").click(function() {

    });
});

function newAudit(){
    switchTab("newAuditTab");
    if($("#newAuditTab").length ===0){
        $("#content").append("<div id='newAuditTab' class = 'innerContent'></div>");
    }
    if($("#contentTitle").length === 0)
        $("#newAuditTab").append("<div id='contentTitle'>Nowy audyt innowacji przedsiębiorstwa</div>");
    if($("#contentData").length === 0)
    $("#newAuditTab").append("<div id='contentData'><span>Podaj dane: </span>" +
        "<p>Nazwa: <input type='text' id='companyName' /></p>" +
        "<p>Nr REGON: <input type='text' id='companyREGON' /></p>" +
        "<p>Nr KRS: <input type='text' id='companyKRS' /></p>" +
        "<p>Rok założenia: <input type='text' id='companyYear' /></p>" +
        "<p>Ilość zatrudnionych pracowników: <input type='text' id='companyEmployees' /></p>" +
        "<input type='button' class='userMenuButton' onclick='newAuditProcess()' value='Rozpocznij'/></div>");

}

function newAuditProcess(){
    var inputs = $("#contentData").find("input[type='text']");
    var allChecked = true;
    var data = {};
    $.each(inputs,function(){
        data[""+$(this).attr("id")] = $(this).val();
        if($(this).val() === ""){
            allChecked = false;
        }
    });
    if(!allChecked){
        showInfo(false,"Wszystkie pola są wymagane!");
    }
    else{
        $.ajax({
            url: "/BeginAudit",
            method: "POST",
            dataType: "json",
            data: data,
            success: function (data) {
                if (data.success) {
                    $("#innerContent").html("");
                    generateQuestions();
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function generateQuestions(){

}

function auditHistory(){
    switchTab("auditHistoryTab");
    if($("#auditHistoryTab").length ===0){
        $("#content").append("<div id='auditHistoryTab' class = 'innerContent'></div>");
    }
}

function questionsLookout(){
    switchTab("questionsLookoutTab");
    if($("#questionsLookoutTab").length ===0){
        $("#content").append("<div id='questionsLookoutTab' class = 'innerContent'></div>");
    }
}

function manageQuestions(){
    switchTab("manageQuestionsTab");
    if($("#manageQuestionsTab").length ===0){
        $("#content").append("<div id='manageQuestionsTab' class = 'innerContent'></div>");
    }
}

function switchTab(tabName){
    $(".innerContent").hide();
    $("#"+tabName).show();
}