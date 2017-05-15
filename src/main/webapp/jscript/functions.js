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
    }
}

function questionsLookout(){
    switchTab("questionsLookoutTab");
    if($("#questionsLookoutTab").length ===0){
        $("#content").append("<div id='questionsLookoutTab' class = 'innerContent'></div>");
        getQuestions($("#questionsLookoutTab"));
    }

}


function manageQuestions(){
    switchTab("manageQuestionsTab");
    if($("#manageQuestionsTab").length ===0){
        $("#content").append("<div id='manageQuestionsTab' class = 'innerContent'></div>");
        $("#manageQuestionsTab").html("<input type='button' class='userMenuButton' onclick='newQuestion()' value='Dodaj nowe pytanie'/></div>" +
            "");

        getQuestions($("#manageQuestionsTab"));
    }

}

function getQuestions(selector){
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "list"
        },
        success: function (data) {
            if (data.success) {
                selector.append(data.data);
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
    var dataToSave = new Array();
    var numberOfQuestions = 0;
    /*if($(".yesNoRadio").length > 0){
        numberOfQuestions = $(".yesNoRadio").length / 2;
        //yesNo
        $.each($(".yesNoRadio").filter(":checked"),function(){
            var singleData = new Object();
            var thisRadio = $(this);
            var real_id = thisRadio.attr("id");
            var id = real_id.substring(0,real_id.indexOf("_"));
            singleData.answer = thisRadio.filter(':checked').val();
            singleData.id = id;
            if(singleData.answer == 1)
                singleData.yesVal = $("#"+id+"_yesVal").val();
            dataToSave.push(singleData);
        });
    }
     else {*/
        numberOfQuestions = $(".lickertRadio").length / 7;
            // lickert
        $.each($(".lickertRadio").filter(":checked"),function(){
            var singleData = new Object();
            var thisRadio = $(this);
            var real_id = thisRadio.attr("id");
            var id = real_id.substring(0,real_id.indexOf("_"));
            singleData.answer = thisRadio.filter(':checked').val();
            singleData.id = id;
            dataToSave.push(singleData);
        });
    //}
    if(dataToSave.length == numberOfQuestions) {
        handleQuestionRequest(dataToSave);
    }
    else {
        showInfo(false,"Wszystkie odpowiedzi są wymagane");
    }
}

function finishAudit(){
    // generowanie wyniku i zapisywanie w bazie
    nextQuestions();
}

function activateSwitchYes(){
    $(function(){
        if($(".yesNoRadio").length > 0){
            $.each($(".yesNoRadio"),function(){
                var thisRadio = $(this);
                var id = thisRadio.attr("id");
                id = id.substring(0,id.indexOf("_"));
                thisRadio.change(function(){
                    if(thisRadio.val() == 1){
                        $("#"+id+"_yesVal").show();
                    }
                    else {
                        $("#"+id+"_yesVal").hide();
                    }
                });
            });
        }
    });
}

function switchTab(tabName){
    $(".innerContent").hide();
    $("#"+tabName).show();
}

function newQuestion(){
    $("#manageQuestionsTab").html("<p>Treść pytania: <textarea id='questionContent' cols='60' rows='4'/></p>" +
        "<p>Typ pytania: <select id='questionType' >" +
        "<option value='Yes_No'>Tak/Nie</option>" +
        "<option value='Lickert'>Skala Lickerta</option></select></p>");
    $("#manageQuestionsTab").append("<p id='addYes'>Dodatkowe pytanie dla 'tak': <input type='checkbox' id='additionalYes' /></p>");
    $("#questionType").change(function(){
        if($("#questionType").val() == "Yes_No" && $("#additionalYes").length == 0) {
            $("#confirmQuestion").before("<p id='addYes'>Dodatkowe pytanie dla 'tak': <input type='checkbox' id='additionalYes' /></p>");
        }
        else if($("#additionalYes").length == 1){
            $("#addYes").remove();
        }
    });
    $("#manageQuestionsTab").append("<p>Kategoria pytania: <select id='questionCategory' >" +
        "<option value='General'>Ogólne</option>" +
        "<option value='Strategic'>Strategia</option>" +
        "<option value='Processes'>Procesy</option>" +
        "<option value='Organization'>Organizacja</option>" +
        "<option value='Couplings'>Powiązania</option>" +
        "<option value='Learning'>Uczenie się</option></select></p>");
    $("#manageQuestionsTab").append("<input type='button' class='userMenuButton' id='confirmQuestion' onclick='confirmQuestion()' value='Dodaj'/></div>");
}

function confirmQuestion(){
    var questionContent = $("#questionContent").val();
    var questionType = $("#questionType").val();
    var addidtionalYes = $("#additionalYes").is(':checked');
    var questionCategory = $("#questionCategory").val();

    if(questionContent != "") {
        $.ajax({
            url: "/NewQuestion",
            method: "POST",
            dataType: "json",
            data: {
                content: questionContent,
                type: questionType,
                additional: addidtionalYes,
                category: questionCategory
            },
            success: function (data) {
                if (data.success) {
                    newQuestion();
                    showInfo(true, data.message);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
    else{
        showInfo(false,"Wpisz treść pytania");
    }
}