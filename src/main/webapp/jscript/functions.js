var questionTypes = ["Yes_no","Lickert"];
var questionCategories = ["GENERAL", "STRATEGIC", "PROCESSES", "ORGANIZATION", "COUPLINGS", "LEARNING"];
var actualIndex = 0;

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
    $("#newAuditTab").append("<div id='contentData'><span>Podaj dane przedsiębiorstwa: </span>" +
        "<p>Nazwa: <input type='text' id='companyName' /></p>" +
        "<p>Nr REGON: <input type='text' id='companyREGON' /></p>" +
        "<p>Nr KRS: <input type='text' id='companyKRS' /></p>" +
        "<p>Rok założenia: <input type='text' id='companyYear' /></p>" +
        "<p>Ilość zatrudnionych pracowników: <input type='text' id='companyEmployees' /></p>" +
        "<input type='button' class='userMenuButton' onclick='newAuditProcess()' value='Rozpocznij'/></div>");

}

function nextAudit(){
    $("#newAuditTab").html("");
    newAudit();
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
    handleQuestionRequest(null, actualIndex);
}


function auditHistory(){
    switchTab("auditHistoryTab");
    if($("#auditHistoryTab").length ===0){
        $("#content").append("<div id='auditHistoryTab' class = 'innerContent'></div>");
        getAuditHistory();
    }
}


function manageQuestions(){
    switchTab("manageQuestionsTab");
    if($("#manageQuestionsTab").length ===0){
        $("#content").append("<div id='manageQuestionsTab' class = 'innerContent'></div>");
        getQuestions();
    }

}

function getAuditHistory() {
    $.ajax({
        url: "/AuditHistory",
        method: "POST",
        dataType: "json",
        data: {
            action: "table"
        },
        success: function (data) {
            if (data.success) {
                $("#auditHistoryTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function makeReport(auditId) {
    $.ajax({
        url: "/AuditHistory",
        method: "POST",
        dataType: "json",
        data: {
            action: "report",
            auditId: auditId
        },
        success: function (data) {
            if (data.success) {
                $("#auditHistoryTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function getQuestions() {
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "list"
        },
        success: function (data) {
            if (data.success) {
                $("#manageQuestionsTab").append(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function handleQuestionRequest(dataToSave) {
        $.ajax({
            url: "/Question",
            method: "POST",
            dataType: "json",
            data: {
                action: "getQuestions",
                toSave: JSON.stringify(dataToSave)
            },
            success: function (data) {
                if (data.success) {
                    $("#contentData").html(data.data);
                    //activateSwitchYes();
                    $("html, body").animate({ scrollTop: 0 }, "slow");
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
}

function nextQuestions(){
    var dataToSave = [];
    var numberOfQuestions = $(".lickertRadio").length / 7;
        $.each($(".lickertRadio").filter(":checked"),function(){
            var singleData = new Object();
            var thisRadio = $(this);
            var real_id = thisRadio.attr("name");
            var id = real_id.substring(0,real_id.indexOf("_"));
            singleData.answer = thisRadio.filter(':checked').val();
            singleData.id = id;
            dataToSave.push(singleData);
        });
    if(dataToSave.length == numberOfQuestions) {
        handleQuestionRequest(dataToSave);
    }
    else {
        showInfo(false,"Wszystkie odpowiedzi są wymagane");
    }
}

function finishAudit(){
    nextQuestions();
}

function switchTab(tabName){
    $(".innerContent").hide();
    $("#"+tabName).show();
}