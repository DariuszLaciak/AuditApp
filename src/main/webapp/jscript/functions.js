var actualIndex = 0;
var $ideaType = [["COMPANY_CLIENT", "Relacja firma-klient"], ["PROMOTION", "Wizerunek firmy i promocja"], ["INTERNAL", "Organizacja pracy i zarządzenie, relacje wewnętrzne"],
    ["SECURITY", "Bezpieczeństwo"], ["MACHINES", "Maszyny"], ["PRODUCTION", "Organizacja produkcji"], ["PRODUCT", "Produkty"], ["OTHER", "Inne"]];

$(function () {
    $("#loginPanel").submit(function (event) {
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
    $(".logoutUser").click(function () {
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
    $("#registerUser").click(function () {

    });
});

function newAudit() {
    switchTab("newAuditTab");
    if ($("#newAuditTab").length === 0) {
        $("#content").append("<div id='newAuditTab' class = 'innerContent'></div>");
        newAuditProcess();
    }
}

function nextAudit() {
    newAudit();
    newAuditProcess();
}

function newAuditProcess() {
    $.ajax({
        url: "/BeginAudit",
        method: "POST",
        dataType: "json",
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

function generateQuestions() {
    handleQuestionRequest(null, actualIndex);
}


function auditHistory() {
    switchTab("auditHistoryTab");
    if ($("#auditHistoryTab").length === 0) {
        $("#content").append("<div id='auditHistoryTab' class = 'innerContent'></div>");
        getAuditHistory();
    }
}


function manageQuestions() {
    switchTab("manageQuestionsTab");
    if ($("#manageQuestionsTab").length === 0) {
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
                scrollUp();
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
                $("#newAuditTab").html(data.data);
                //activateSwitchYes();
                scrollUp();
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function nextQuestions() {
    var dataToSave = [];
    var numberOfQuestions = $(".lickertRadio").length / 7;
    $.each($(".lickertRadio").filter(":checked"), function () {
        var singleData = {};
        var thisRadio = $(this);
        var real_id = thisRadio.attr("name");
        var id = real_id.substring(0, real_id.indexOf("_"));
        singleData.answer = thisRadio.filter(':checked').val();
        singleData.id = id;
        dataToSave.push(singleData);
    });
    if (dataToSave.length == numberOfQuestions) {
        handleQuestionRequest(dataToSave);
    }
    else {
        showInfo(false, "Wszystkie odpowiedzi są wymagane");
    }
}

function finishAudit() {
    nextQuestions();
}

function switchTab(tabName) {
    $(".innerContent").hide();
    $("#" + tabName).show();
}

function listIdeas() {
    switchTab("listIdeasTab");
    if ($("#listIdeasTab").length === 0) {
        $("#content").append("<div id='listIdeasTab' class = 'innerContent'></div>");

    }
    getIdeas();
}

function newIdea() {
    switchTab("newIdeaTab");
    if ($("#newIdeaTab").length === 0) {
        $("#content").append("<div id='newIdeaTab' class = 'innerContent'></div>");
        var html = "";
        html += "<form id='newIdeaForm'>Nowy pomysł: " +
            "<br /><input type='text' name='ideaName' id='ideaName' placeholder='Tytuł pomysłu'/>" +
            "<textarea id='ideaIdeaContent' name='content' rows='15' cols='70' placeholder='Opis'/>" +
            "Kategoria:<select id='ideaType' name='type'>";
        $($ideaType).each(function (ind, val) {
            if (val[0] == "OTHER") {
                html += "<option selected='selected' value='" + val[0] + "' >" + val[1] + "</option>";
            }
            else {
                html += "<option value='" + val[0] + "' >" + val[1] + "</option>";
            }
        });
        html += "</select><input type='button' class='userMenuButton' value='Wyślij' onclick='sendIdea()'/></form>";
        $("#newIdeaTab").html(html);
    }
}

function manageUsers() {
    switchTab("manageUsersTab");
    if ($("#manageUsersTab").length === 0) {
        $("#content").append("<div id='manageUsersTab' class = 'innerContent'></div>");
        getUsersFromDB();
    }
}

function getUsersFromDB() {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "getUsers"
        },
        success: function (data) {
            if (data.success) {
                $("#manageUsersTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function addUser() {
    var html = "<input class='newUserInput' type='text' placeholder='Nazwa użytkownika' id='username' />";
    html += "<input class='newUserInput' type='text' placeholder='Imię' id='name' />";
    html += "<input class='newUserInput' type='text' placeholder='Nazwisko' id='surname' />";
    html += "<input class='newUserInput' type='text' placeholder='E-mail' id='mail' />";
    html += "<br /><span class='labelSpan'>Typ: </span><select class='newUserInput' id='role' ><option value='admin'>Admin</option>" +
        "<option value='user'>Manager</option><option value='employee' selected='selected'>Pracownik</option></select>";
    html += "<br />Aktywny: <input class='newUserInput' type='checkbox' id='active' checked='checked' />";
    var button = {};
    button.value = "Dodaj";
    button.onclick = "confirmAddUser()";

    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(\"new\")";
    var buttons = [button, button2];
    makeOverlayWindow("new", "center", 220, 350, "Dodaj użytkownika", html, buttons);
}

function confirmAddUser() {
    var username = $("#username").val();
    var name = $("#name").val();
    var surname = $("#surname").val();
    var email = $("#mail").val();
    var role = $('#role').find(":selected").val();
    var active = $("#active").is(':checked');

    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "addUser",
            username: username,
            name: name,
            surname: surname,
            mail: email,
            role: role,
            active: active
        },
        success: function (data) {
            if (data.success) {
                $("#manageUsersTab").html(data.data);
                showInfo(true, data.message);
                closeOverlay("new");
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function editUser(userId) {

}

function deleteUser(userId) {

}

function newSwotPosition(category) {
    $("#swot_" + category).hide();
    $("#div_" + category).append("<div class='newSwotDiv' id='newSwotDiv_" + category + "' ><input type='text' id='newSwot_" + category + "' class='newSwot' placeholder='Wartość cechy' />" +
        "<img src='images/accept.png' class='swotIcon' onclick='addSwot(\"" + category + "\")'/></div>");
}

function addSwot(category) {
    var swotValue = $("#newSwot_" + category).val();
    if (swotValue != "") {
        $.ajax({
            url: "/Question",
            method: "POST",
            dataType: "json",
            data: {
                action: "newSwotPosition",
                value: swotValue,
                category: category
            },
            success: function (data) {
                if (data.success) {
                    showInfo(true, data.message);
                    $("#destination_" + category.toLowerCase()).append(data.data);
                    $("#newSwotDiv_" + category).remove();
                    $("#swot_" + category).show();
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
    else {
        showInfo(false, "Wpisz wartość cechy!");
    }
}

function sendIdea() {
    var form = $("#newIdeaForm");
    var content = form.find($('#ideaIdeaContent'));
    var type = form.find($('#ideaType')).find(":selected").val();
    var name = form.find($("#ideaName")).val();

    if (content.val() == "" || name == "") {
        showInfo(false, "Wszystkie pola są wymagane!");
    }
    else {
        $.ajax({
            url: "/Ideas",
            method: "POST",
            dataType: "json",
            data: {
                action: "newIdea",
                content: content.val(),
                type: type,
                name: name
            },
            success: function (data) {
                if (data.success) {
                    showInfo(true, data.message);
                    content.val("");
                    form.find($("#ideaName")).val("");
                    form.find($('#ideaType')).reset();
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function getIdeas() {
    $.ajax({
        url: "/Ideas",
        method: "POST",
        dataType: "json",
        data: {
            action: "listIdeas"
        },
        success: function (data) {
            if (data.success) {
                $("#listIdeasTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function moreInfoIdea(id) {
    $("#idea_" + id).slideToggle("slow");
}

function acceptIdea(id) {
    var button = {};
    button.value = "Potwierdź";
    button.onclick = "saveAccepted(" + id + ")";

    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(" + id + ")";
    var buttons = [button, button2];

    var form = "<form id='acceptIdeaForm'>";
    form += "<textarea id='ideaOpinion' placeholder='Treść opinii' rows='10' cols='45'/>";
    form += "</form>";
    makeOverlayWindow(id, $("#accept_" + id), 400, 300, "Akceptuj: wystaw opinię", form, buttons);
}

function rejectIdea(id) {
    var button = {};
    button.value = "Potwierdź";
    button.onclick = "saveRejected(" + id + ")";

    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(" + id + ")";
    var buttons = [button, button2];

    var form = "<form id='acceptIdeaForm'>";
    form += "<textarea id='ideaOpinion' placeholder='Treść opinii' rows='10' cols='45'/>";
    form += "</form>";
    makeOverlayWindow(id, $("#accept_" + id), 400, 300, "Odrzuć: wystaw opinię", form, buttons);
}

function saveAccepted(id) {
    saveOpinion(true, id);
}

function saveRejected(id) {
    saveOpinion(false, id);
}

function saveOpinion(positive, id) {
    if ($("#ideaOpinion").val() == "") {
        showInfo(false, "Napisz opinię");
    }
    else {
        $.ajax({
            url: "/Ideas",
            method: "POST",
            dataType: "json",
            data: {
                action: "saveOpinion",
                value: $("#ideaOpinion").val(),
                positive: positive,
                ideaId: id
            },
            success: function (data) {
                if (data.success) {
                    showInfo(true, data.message);
                    closeOverlay(id);
                    $("#listIdeasTab").html(data.data);

                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function decisionIdea(id) {
    $.ajax({
        url: "/Ideas",
        method: "POST",
        dataType: "json",
        data: {
            action: "moreInfoIdea",
            ideaId: id
        },
        success: function (data) {
            if (data.success) {
                var button = {};
                button.value = "Zamknij";
                button.onclick = "closeOverlay(" + id + ")";
                var buttons = [button];
                makeOverlayWindow(id, $("#decisionIdea_" + id), 400, 300, "Szczegóły decyzji: ", data.data, buttons);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function saveSwot(auditId) {
    var chosenOptions = $(".form-control").find("option");
    var toSend = [];
    chosenOptions.each(function () {
        var opt = $(this).val().substr($(this).val().indexOf("_") + 1, $(this).val().length);
        toSend.push(opt);
    });

    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "saveSwot",
            auditId: auditId,
            selected: toSend
        },
        success: function (data) {
            if (data.success) {
                $("#newAuditTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });

}

function auditOverview() {
    switchTab("auditOverviewTab");
    if ($("#auditOverviewTab").length === 0) {
        $("#content").append("<div id='auditOverviewTab' class = 'innerContent'></div>");
        var html = "<input type='text' name='beginDate' id='beginDate' placeholder='Data początkowa'/>";
        html += "<input type='text' name='endDate' id='endDate' placeholder='Data końcowa'/>";
        html += "<input type='button' value='Pokaż zestawienie' onclick='makeAuditOverview()' class='userMenuButton'/>";
        html += "<script type='application/javascript'>$(function (){" +
            "$('#beginDate').datepicker(); $('#endDate').datepicker();" +
            "$('#beginDate').datepicker('option', 'dateFormat', 'dd-mm-yy'); $('#endDate').datepicker('option', 'dateFormat', 'dd-mm-yy');" +
            "})</script>";
        html += "<div id='overviewContent'></div>";
        $("#auditOverviewTab").html(html);
    }
}

function makeAuditOverview() {
    var startDate = $("#beginDate").val();
    var endDate = $("#endDate").val();
    $.ajax({
        url: "/AuditHistory",
        method: "POST",
        dataType: "json",
        data: {
            action: "overview",
            startDate: startDate,
            endDate: endDate
        },
        success: function (data) {
            if (data.success) {
                $("#overviewContent").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}