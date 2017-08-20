var actualIndex = 0;
var $ideaType = [["COMPANY_CLIENT", "Relacja firma-klient"], ["PROMOTION", "Wizerunek firmy i promocja"], ["INTERNAL", "Organizacja pracy i zarządzenie, relacje wewnętrzne"],
    ["SECURITY", "Bezpieczeństwo"], ["MACHINES", "Maszyny"], ["PRODUCTION", "Organizacja produkcji"], ["PRODUCT", "Produkty"], ["OTHER", "Inne"]];
var $ideaDesc = [["PROMOTION", "Dotyczące promocji marki, wizerunku i osiągnięć firmy"],
    ["INTERNAL", "Dotyczące poprawy komunikacji i obiegu informacji wewnątrz firmy, usprawnienia organizacji pracy działu oraz współpracy pomiędzy działami, dotyczące spraw socjalnych, dotyczące wprowadzenia nowoczesnych metod zarządzania."],
    ["SECURITY", "Dotyczące podniesienia poziomu bezpieczeństwa (osób i danych), zwiększenia poziomu ochrony przeciwpożarowej."],
    ["MACHINES", "Dotyczące technicznego ulepszenia maszyn, propozycje automatyzacji procesów produkcyjnych."],
    ["PRODUCTION", "Dotyczące zmian organizacyjnych w obszarze produkcji."],
    ["COMPANY_CLIENT", "Dotyczące ulepszenia obsługi klientów, komunikacji z dostawcami i klientami."],
    ["PRODUCT", "Dotyczące produktów wykorzystywanych w firmie"],
    ["OTHER", "Niezwiązane z żadną z powyższych kategorii"]];
var timer = 1000;
$.ajaxSetup({
    beforeSend: function () {
        clearTimeout(timer);
        timer = setTimeout(function () {
            makeLoading("Proszę czekać...");
        }, 500);
    },
    complete: function () {
        clearTimeout(timer);
        finishLoading();
    }
});
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
        data: {
            type: "general"
        },
        success: function (data) {
            if (data.success) {
                generateQuestions(true);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function newDetailedAuditProcess() {
    $.ajax({
        url: "/BeginAudit",
        method: "POST",
        dataType: "json",
        data: {
            type: "detailed"
        },
        success: function (data) {
            if (data.success) {
                generateQuestions(false);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function generateQuestions(isGeneral) {
    handleQuestionRequest(null, isGeneral);
}

function newDetailedAudit() {
    switchTab("newDetailedAuditTab");
    if ($("#newDetailedAuditTab").length === 0) {
        $("#content").append("<div id='newDetailedAuditTab' class = 'innerContent'></div>");
        newDetailedAuditProcess();
    }
}

function innovationSources() {
    switchTab("innovationSourcesTab");
    if ($("#innovationSourcesTab").length === 0) {
        $("#content").append("<div id='innovationSourcesTab' class = 'innerContent'></div>");
        generateSources();
    }
}

function innovationImpediments() {
    switchTab("innovationImpedimentsTab");
    if ($("#innovationImpedimentsTab").length === 0) {
        $("#content").append("<div id='innovationImpedimentsTab' class = 'innerContent'></div>");
        generateImpediments();
    }
}

function newSwot() {
    switchTab("newSwotTab");
    if ($("#newSwotTab").length === 0) {
        $("#content").append("<div id='newSwotTab' class = 'innerContent'></div>");
        generateSwot();
    }
}

function manageSources() {
    switchTab("manageSourcesTab");
    if ($("#manageSourcesTab").length === 0) {
        $("#content").append("<div id='manageSourcesTab' class = 'innerContent'></div>");
        getSourcesTable();
    }
}

function manageImpediments() {
    switchTab("manageImpedimentsTab");
    if ($("#manageImpedimentsTab").length === 0) {
        $("#content").append("<div id='manageImpedimentsTab' class = 'innerContent'></div>");
        getImpedimentsTable();
    }
}

function innovationIdentification() {
    switchTab("innovationIdentificationTab");
    if ($("#innovationIdentificationTab").length === 0) {
        $("#content").append("<div id='innovationIdentificationTab' class = 'innerContent'></div>");
        makeIdentificationMenu();
    }
}

function makeIdentificationMenu() {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "innovationIdentification"
        },
        success: function (data) {
            if (data.success) {
                $("#innovationIdentificationTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function newInnovation() {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "newInnovationForm"
        },
        success: function (data) {
            if (data.success) {
                $("#innovationIdentificationTab").html(data.data);
                scrollUp();
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function saveInnovation() {
    var allDivs = $(".innovationDiv");
    var questionAnswers = [];
    var additionalAnswers = [];
    var allAnswered = true;
    var input, textarea;
    $(function () {
        $(".innovationDiv").click(function () {
            $(this).css("border", "");
        });
    });
    var object = {};
    var id, val;
    $.each(allDivs, function () {
        if ($(this).find("input").length > 0) {
            if ($(this).find("input").is(":radio")) {
                val = $(this).find("input:checked").val();
                id = $(this).find("input").attr("name");
                id = id.substring(id.indexOf("_") + 1, id.length);
                if (typeof val === "undefined") {
                    allAnswered = false;
                    $(this).css("border", "2px solid red");
                }
                else {
                    object = {};
                    object.id = id;
                    object.value = val;
                    questionAnswers.push(object);
                }
            }
            else {
                val = $(this).find("input").val();
                id = $(this).find("input").attr("id");
                if (id.indexOf("_") != -1) {
                    id = id.substring(id.indexOf("_") + 1, id.length);
                    if (typeof val === "undefined" || val == "") {
                        allAnswered = false;
                        $(this).css("border", "2px solid red");
                    }
                    else {
                        object = {};
                        object.id = id;
                        object.value = val;
                        questionAnswers.push(object);
                    }
                }
            }
        }
        else if ($(this).find("textarea").length > 0) {
            val = $(this).find("textarea").val();
            id = $(this).find("textarea").attr("name");
            var type = id.substring(0, id.indexOf("_"));
            if (id.indexOf("_") != -1) {
                id = id.substring(id.indexOf("_") + 1, id.length);
                if (val == "" || typeof val === "undefined") {
                    allAnswered = false;
                    $(this).css("border", "2px solid red");
                }
                if (type == "questionAdditional") {
                    object = {};
                    object.id = id;
                    object.value = val;
                    additionalAnswers.push(object);
                }
                else {
                    object = {};
                    object.id = id;
                    object.value = val;
                    questionAnswers.push(object);
                }
            }
        }
    });
    if (!allAnswered) {
        showInfo(false, "Wszystkie odpowiedzi są wymagane");
    }
    else {
        var innovationName = $("#innovationName").val();
        var innovationCompany = $("#innovationCompany").val();
        var innovationAttachments = $("#innovationAttachments").val();
        var innovationSigns = $("#innovationSigns").val();
        $.ajax({
            url: "/Manage",
            method: "POST",
            dataType: "json",
            data: {
                action: "saveInnovation",
                name: innovationName,
                company: innovationCompany,
                attachments: innovationAttachments,
                signs: innovationSigns,
                answers: JSON.stringify(questionAnswers),
                additional: JSON.stringify(additionalAnswers)
            },
            success: function (data) {
                if (data.success) {
                    $("#innovationIdentificationTab").html(data.data);
                    showInfo(true, data.message);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function deleteInnovation(innovationId) {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "deleteInnovation",
            innovationId: innovationId
        },
        success: function (data) {
            if (data.success) {
                showInfo(true, data.message);
                $("#innovationIdentificationTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function viewInnovations() {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "viewInnovations"
        },
        success: function (data) {
            if (data.success) {
                $("#innovationIdentificationTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function getSourcesTable() {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "getSources"
        },
        success: function (data) {
            if (data.success) {
                $("#manageSourcesTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function getImpedimentsTable() {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "getImpediments"
        },
        success: function (data) {
            if (data.success) {
                $("#manageImpedimentsTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function newSwotAnalysis() {
    newSwot();
    generateSwot();
}

function generateSources() {
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "newSources"
        },
        success: function (data) {
            if (data.success) {
                $("#innovationSourcesTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function newSource() {
    var button = {};
    button.value = "Zapisz";
    button.onclick = "confirmAddSource()";

    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(\"newSource\")";
    var buttons = [button, button2];
    var html = "<input class='allWidthInput' type='text' placeholder='Treść źródła' id='newSourceText' />";
    html += "<textarea class='allWidthInput' placeholder='Opis do wskazówki (w przypadku niewybrania)' id='newSourceDescription' rows='6'></textarea>";
    html += "Typ: <select id='sourceType'><option value='1'>Wewnętrzne</option><option value='0'>Zewnętrzne</option></select>";
    makeOverlayWindow("newSource", "center", 400, 280, "Nowe źrodło", html, buttons);
}

function confirmAddSource() {
    var text = $("#newSourceText").val();
    var longText = $("#newSourceDescription").val();
    var isInternal = $('#sourceType').find(":selected").val();

    if (text == "" || longText == "") {
        showInfo(false, "Wypełnij wszystkie pola!");
    }
    else {
        $.ajax({
            url: "/Manage",
            method: "POST",
            dataType: "json",
            data: {
                action: "saveSource",
                text: text,
                description: longText,
                isInternal: isInternal
            },
            success: function (data) {
                if (data.success) {
                    showInfo(true, data.message);
                    $("#manageSourcesTab").html(data.data);
                    closeOverlay("newSource");
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function saveImpediment() {
    var chosenOptions = $("#destination_Impediment").find("option");
    var noData = $("#noDataCheckbox_Impediment").is(":checked");
    if (chosenOptions.length == 0 && !noData) {
        showInfo(false, "Wybierz conajmniej jedną barierę!");
    }
    else {
        var toSend = [];
        $.each(chosenOptions, function () {
            var opt = $(this).val().substr($(this).val().indexOf("_") + 1, $(this).val().length);
            toSend.push(opt);
        });
        $.ajax({
            url: "/Question",
            method: "POST",
            dataType: "json",
            data: {
                action: "impedimentAudit",
                impediments: toSend,
                noData: noData
            },
            success: function (data) {
                if (data.success) {
                    $("#innovationImpedimentsTab").html(data.data);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function saveSource() {
    var chosenOptions = $("#destination_Source").find("option");
    var noData = $("#noDataCheckbox_Source").is(":checked");
    if (chosenOptions.length == 0 && !noData) {
        showInfo(false, "Wybierz conajmniej jedno źródło!");
    }
    else {
        var toSend = [];
        $.each(chosenOptions, function () {
            var opt = $(this).val().substr($(this).val().indexOf("_") + 1, $(this).val().length);
            toSend.push(opt);
        });
        $.ajax({
            url: "/Question",
            method: "POST",
            dataType: "json",
            data: {
                action: "sourceAudit",
                sources: toSend,
                noData: noData
            },
            success: function (data) {
                if (data.success) {
                    $("#innovationSourcesTab").html(data.data);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function newImpediment() {
    var button = {};
    button.value = "Zapisz";
    button.onclick = "confirmAddImpediment()";

    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(\"newImpediment\")";
    var buttons = [button, button2];
    var html = "<input class='allWidthInput' type='text' placeholder='Bariera innowacyjności' id='newImpedimentText' />";
    html += "<h3>Wskazówki do barier</h3>";
    html += "<input type='text' class='allWidthInput adviceInput' placeholder='Rada do bariery' id='newImpedimentAdvice_1' />";
    html += "<img id='adviceButton_1' title='Kolejna rada' src='images/plusG.png' class='ideaOption' onclick='nextAdvice(2)'/>";
    makeOverlayWindow("newImpediment", "center", 400, 350, "Nowa bariera", html, buttons, false, 200);
}

function showReportSource(auditId) {
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "showSources",
            auditId: auditId,
        },
        success: function (data) {
            if (data.success) {
                $("#innovationSourcesTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function showAdvices(impedimentId) {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "getAdvices",
            impedimentId: impedimentId,
        },
        success: function (data) {
            if (data.success) {
                var button2 = {};
                button2.value = "Zamknij";
                button2.onclick = "closeOverlay(\"impedimentAdvices\")";
                var buttons = [button2];
                makeOverlayWindow("impedimentAdvices", "center", 400, 350, "Wskazówki do barier", data.data, buttons, false, 200);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });

}

function showReportImpediment(auditId) {
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "showImpediments",
            auditId: auditId,
        },
        success: function (data) {
            if (data.success) {
                $("#innovationImpedimentsTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function nextAdvice(num) {
    $("#overlayWindow_newImpediment").find(".overlayContent").append("<input class='allWidthInput adviceInput' type='text' placeholder='Rada do bariery' id='newImpedimentAdvice_" + num + "' />" +
        "<img id='adviceButton_" + num + "' title='Kolejna rada' src='images/plusG.png' class='ideaOption' onclick='nextAdvice(" + (num + 1) + ")'/>");
    $("#overlayWindow_newImpediment").find(".overlayContent").find("#adviceButton_" + (num - 1)).remove();
}

function confirmAddImpediment() {
    var text = $("#newImpedimentText").val();
    var advices = [];
    $.each($(".adviceInput"), function () {
        if ($(this).val != "" && $(this).val() && typeof($(this).val) != 'undefined') {
            advices.push($(this).val());
        }
    });

    if (text == "" || advices.length == 0) {
        showInfo(false, "Podaj treść bariery i przynajmniej jedną wskazówkę!");
    }
    else {
        $.ajax({
            url: "/Manage",
            method: "POST",
            dataType: "json",
            data: {
                action: "saveImpediment",
                text: text,
                advices: advices
            },
            success: function (data) {
                if (data.success) {
                    showInfo(true, data.message);
                    $("#manageImpedimentsTab").html(data.data);
                    closeOverlay("newImpediment");
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
}

function generateImpediments() {
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "newImpediments"
        },
        success: function (data) {
            if (data.success) {
                $("#innovationImpedimentsTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function deleteImpediment(impedimentId) {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "deleteImpediment",
            impedimentId: impedimentId
        },
        success: function (data) {
            if (data.success) {
                showInfo(true, data.message);
                $("#manageImpedimentsTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function deleteSource(sourceId) {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "deleteSource",
            sourceId: sourceId
        },
        success: function (data) {
            if (data.success) {
                showInfo(true, data.message);
                $("#manageSourcesTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function generateSwot() {
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "newSwot"
        },
        success: function (data) {
            if (data.success) {
                $("#newSwotTab").html(data.data);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
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

function makeSwotReport(swotId) {
    $.ajax({
        url: "/AuditHistory",
        method: "POST",
        dataType: "json",
        data: {
            action: "swotReport",
            swotId: swotId
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

function handleQuestionRequest(dataToSave, isGeneral) {
    $.ajax({
        url: "/Question",
        method: "POST",
        dataType: "json",
        data: {
            action: "getQuestions",
            toSave: JSON.stringify(dataToSave),
            isGeneral: isGeneral
        },
        success: function (data) {
            if (data.success) {
                if (isGeneral) {
                    $("#newAuditTab").html(data.data);
                }
                else {
                    $("#newDetailedAuditTab").html(data.data);
                }
                //activateSwitchYes();
                scrollUp();
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function nextQuestions(isGeneral) {
    var dataToSave = [];
    var numberOfQuestions = $(".lickertRadio").length / 7;
    if (!isGeneral) {
        numberOfQuestions = $(".lickertRadio").length / 5;
    }
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
        handleQuestionRequest(dataToSave, isGeneral);
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
        html += "<form id='newIdeaForm'><h2>Nowy pomysł</h2>" +
            "<input type='text' name='ideaName' id='ideaName' placeholder='Tytuł pomysłu'/>" +
            "<textarea id='ideaIdeaContent' name='content' rows='15' cols='70' placeholder='Opis'/>" +
            "Kategoria:<select id='ideaType' name='type'>";
        $($ideaType).each(function (ind, val) {
            if (val[0] == "OTHER") {
                html += "<option selected='selected' value='" + val[0] + "'>" + val[1] + "</option>";
            }
            else {
                html += "<option value='" + val[0] + "' >" + val[1] + "</option>";
            }
        });

        html += "</select><input type='button' class='userMenuButton' value='Wyślij' onclick='sendIdea()'/></form>";
        html += "<div id='helpInfo'>";
        html += "<ul>";
        $($ideaType).each(function (ind, val) {
            html += "<li><b>" + val[1] + "</b> - " + $ideaDesc[ind][1] + "</li>";
        });
        html += "</ul></div>";
        $("#newIdeaTab").html(html);
        $(function () {
            $("#ideaType").hover(function (e) {
                    $("#helpInfo").show();
                },
                function () {
                    $("#helpInfo").hide();
                });
        });
    }
}

function openHelpWindow() {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "getHelp"
        },
        success: function (data) {
            if (data.success) {
                var button2 = {};
                button2.value = "Zamknij";
                button2.onclick = "closeOverlay(\"helpWindow\")";
                var buttons = [button2];
                makeOverlayWindow("helpWindow", "center", 800, 600, "Słownik pojęć", data.data, buttons, false, 450);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });

}

function addHelpWord() {
    var html = "<input type='text' id='helpWord' class='allWidthInput' placeholder='Podaj nazwę pojęcia'/>";
    html += "<textarea id='helpContent' placeholder='Wyjaśnienie pojęcia' rows='7' class='allWidthInput'></textarea>";
    var button = {};
    button.value = "Dodaj";
    button.onclick = "saveAddNewHelp()";
    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(\"addNewHelp\")";
    var buttons = [button, button2];
    makeOverlayWindow("addNewHelp", "center", 400, 300, "Słownik pojęć", html, buttons, false, false, true);
}

function saveAddNewHelp() {
    var word = $("#helpWord").val();
    var content = $("#helpContent").val();
    if (word == "" || content == "") {
        showInfo(false, "Wypełnij wszyskie pola!");
    }
    else {
        $.ajax({
            url: "/Manage",
            method: "POST",
            dataType: "json",
            data: {
                action: "addHelp",
                word: word,
                content: content
            },
            success: function (data) {
                if (data.success) {
                    closeOverlay("addNewHelp");
                    var div = $("#overlayWindow_helpWindow").find(".overlayContent").first();
                    div.html(data.data);
                    showInfo(true, data.message);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
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
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "getManagers"
        },
        success: function (data) {
            if (data.success) {
                var html = "<input class='newUserInput' type='text' placeholder='Nazwa użytkownika' id='username' />";
                html += "<input class='newUserInput' type='text' placeholder='Imię' id='name' />";
                html += "<input class='newUserInput' type='text' placeholder='Nazwisko' id='surname' />";
                html += "<input class='newUserInput' type='text' placeholder='E-mail' id='mail' />";
                html += "<br /><span class='labelSpan'>Typ: </span><select class='newUserInput' id='role' ><option value='admin'>Admin</option>" +
                    "<option value='user'>Manager</option><option value='employee' selected='selected'>Pracownik</option></select>";
                html += "<br />Aktywny: <input class='newUserInput' type='checkbox' id='active' checked='checked' />";
                html += data.data;
                var button = {};
                button.value = "Dodaj";
                button.onclick = "confirmAddUser()";

                var button2 = {};
                button2.value = "Anuluj";
                button2.onclick = "closeOverlay(\"new\")";
                var buttons = [button, button2];
                makeOverlayWindow("new", "center", 220, 390, "Dodaj użytkownika", html, buttons);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });

}

function confirmAddUser() {
    var username = $("#username").val();
    var name = $("#name").val();
    var surname = $("#surname").val();
    var email = $("#mail").val();
    var role = $('#role').find(":selected").val();
    var active = $("#active").is(':checked');
    var manager = $("#manager").val();
    manager = manager.substr(manager.indexOf("_") + 1, manager.length);

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
            active: active,
            manager: manager
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

function deleteUser(userId) {
    var username = $("#username_" + userId).text();
    var html = "Czy na pewno chcesz usunąć uzytkownika: <b>" + username + "</b>?</br></br>UWAGA: Zostaną usunięte również pomysły i opinie przypisane do tego użytkownika!";
    var button2 = {};
    button2.value = "Tak";
    button2.onclick = "confirmDeleteUser(" + userId + ")";
    var button = {};
    button.value = "Anuluj";
    button.onclick = "closeOverlay(" + userId + ")";
    var buttons = [button, button2];
    makeOverlayWindow(userId, "center", 228, 270, "Usuń użytkownika: " + username, html, buttons);
}

function confirmDeleteUser(userId) {
    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "deleteUser",
            userId: userId
        },
        success: function (data) {
            if (data.success) {
                showInfo(true, data.message);
                closeOverlay(userId);
                if ($("#manageUsersTab").is(":visible")) {
                    $("#manageUsersTab").html(data.data);
                }
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function changePass(userId) {
    var html = "<input type='password' id='newPassword' class='newUserInput' placeholder='Nowe hasło'/><input class='newUserInput' type='password' id='confNewPassword' placeholder='Potwierdź nowe hasło'/>";
    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(" + userId + ")";
    var button = {};
    button.value = "Zapisz";
    button.onclick = "saveNewPassword(" + userId + ")";
    var buttons = [button, button2];
    makeOverlayWindow(userId, $("#password_" + userId), 228, 200, "Zmień hasło", html, buttons);
}

function saveNewPassword(userId) {
    var newPass = $("#newPassword").val();
    var newPassRepeated = $("#confNewPassword").val();
    if (newPass != newPassRepeated) {
        showInfo(false, "Hasła nie mogą się od siebie różnić!");
    }
    else {
        $.ajax({
            url: "/Manage",
            method: "POST",
            dataType: "json",
            data: {
                action: "changePass",
                userId: userId,
                password: newPass
            },
            success: function (data) {
                if (data.success) {
                    showInfo(true, data.message);
                    closeOverlay(userId);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
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
    makeConfirmWindowChangeIdeaStatus(id, true);
}

function rejectIdea(id) {
    makeConfirmWindowChangeIdeaStatus(id, false);
}

function makeConfirmWindowChangeIdeaStatus(id, open) {
    var button = {};
    button.value = "Potwierdź";
    button.onclick = "changeIdeaStatus(" + id + "," + open + ")";

    var button2 = {};
    button2.value = "Anuluj";
    button2.onclick = "closeOverlay(" + id + ")";
    var buttons = [button, button2];

    var html = "Czy na pewno chcesz ";
    if (open) {
        html += "otworzyć ";
    }
    else {
        html += "zamknąć ";
    }

    html += "pomysł?";
    var elem;
    if (open) {
        elem = $("#accept_" + id);
    }
    else {
        elem = $("#reject_" + id);
    }
    makeOverlayWindow(id, elem, 225, 200, "Zmień status pomysłu", html, buttons);
}

function changeIdeaStatus(id, open) {
    $.ajax({
        url: "/Ideas",
        method: "POST",
        dataType: "json",
        data: {
            action: "changeIdeaStatus",
            ideaId: id,
            isOpen: open
        },
        success: function (data) {
            if (data.success) {
                showInfo(true, data.message);
                $("#listIdeasTab").html(data.data);
                closeOverlay(id);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function saveOpinion(id) {
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
                ideaId: id
            },
            success: function (data) {
                if (data.success) {
                    showInfo(true, data.message);
                    $(".overlayContent").html(data.data.opinion);
                    $("#listIdeasTab").html(data.data.ideas);
                    $("#acceptIdeaForm").remove();
                    $("#newComment").show();
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
                makeOverlayWindow(id, $("#decisionIdea_" + id), 400, 600, "Historia: ", data.data, buttons, true);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}

function newIdeaComment(id) {
    $("#newComment").hide();
    var form = "<form id='acceptIdeaForm'>";
    form += "<textarea id='ideaOpinion' placeholder='Treść opinii' rows='4' cols='43'/>";
    form += "<input type='button' value='Dodaj' onclick='saveOpinion(" + id + ")'/>";
    form += "</form>";
    $(".overlayContent").prepend(form);
}

function swotRelations(swotId) {
    $(function () {
        $(".numberInput").click(function () {
            $(this).css("background-color", "");
        });
    });
    // swot_id_id
    $(".numberInput");
    var allGood = true;
    var dataToSave = [];
    $.each($(".numberInput"), function () {
        var singleData = {};
        var real_id = $(this).attr("id");
        var id1 = real_id.substring(real_id.indexOf("_") + 1, real_id.lastIndexOf("_"));
        var id2 = real_id.substring(real_id.lastIndexOf("_") + 1, real_id.length);
        if ($(this).val() < 0 || $(this).val() > 2) {
            $(this).css("background-color", "red");
            //return;
            allGood = false;
        }
        singleData.value = $(this).val();
        singleData.id1 = id1;
        singleData.id2 = id2;
        dataToSave.push(singleData);
    });
    if (allGood) {
        $.ajax({
            url: "/Question",
            method: "POST",
            dataType: "json",
            data: {
                action: "swotRelations",
                swotId: swotId,
                relations: JSON.stringify(dataToSave)
            },
            success: function (data) {
                if (data.success) {
                    $("#newSwotTab").html(data.data);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
    else {
        showInfo(false, "Wartości mogą mieć wyłącznie wartość 0, 1 lub 2");
    }
}


function saveSwot() {
    var chosenOptions = $(".form-control").find("option");
    var toSend = [];
    var areTablesFilled = true;
    $.each($(".form-control"), function () {
        if ($(this).find("option").length == 0) {
            areTablesFilled = false;
        }
    });
    if (!areTablesFilled) {
        showInfo(false, "Proszę uzupełnić wszystkie kategorie!");
    }
    else {
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
                selected: toSend
            },
            success: function (data) {
                if (data.success) {
                    $("#newSwotTab").html(data.data);
                }
                else {
                    showInfo(false, data.message);
                }
            }
        });
    }
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

function editUser(userId) {

    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "editUser",
            userId: userId
        },
        success: function (data) {
            if (data.success) {
                var button = {};
                button.value = "Zapisz";
                button.onclick = "confirmEditUser(" + userId + ")";

                var button2 = {};
                button2.value = "Anuluj";
                button2.onclick = "closeOverlay(" + userId + ")";
                var buttons = [button, button2];
                makeOverlayWindow(userId, "center", 220, 460, "Edytuj profil", data.data, buttons);
            }
            else {
                showInfo(false, data.message);
            }
        }
    });


}

function confirmEditUser(userId) {
    var name = $("#name").val();
    var surname = $("#surname").val();
    var email = $("#email").val();
    var changePass = $("#isPassword").is(":checked");
    var password = $("#password").val();
    var type = $("#type").find(":selected").val();
    var active = $("#active").is(":checked");
    var manager = $("#manager").find(":selected").val();

    $.ajax({
        url: "/Manage",
        method: "POST",
        dataType: "json",
        data: {
            action: "confirmEditUser",
            userId: userId,
            name: name,
            surname: surname,
            email: email,
            changePass: changePass,
            password: password,
            type: type,
            active: active,
            manager: manager
        },
        success: function (data) {
            if (data.success) {
                showInfo(true, data.message);
                closeOverlay(userId);
                if ($("#manageUsersTab").is(":visible")) {
                    $("#manageUsersTab").html(data.data);
                }
            }
            else {
                showInfo(false, data.message);
            }
        }
    });
}