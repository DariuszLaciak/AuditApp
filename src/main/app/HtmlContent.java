package main.app;

import com.mchange.io.FileUtils;
import main.app.enums.*;
import main.app.orm.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class HtmlContent {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    private static String makeButton(String nameToDisplay, String onclick) {
        String html = "";
        html += "<input class='userMenuButton' type='button' value='" + nameToDisplay + "' onclick='" + onclick + "()'/>";
        return html;
    }

    private static String makeButton(String nameToDisplay, String onclick, String argument) {
        String html = "";
        html += "<input class='userMenuButton' type='button' value='" + nameToDisplay + "' onclick='" + onclick + "(" + argument + ")'/>";
        return html;
    }

    public static String makeButton(String nameToDisplay, String onclick, String argument, String id) {
        String html = "";
        html += "<input id= '" + id + "' class='userMenuButton' type='button' value='" + nameToDisplay + "' onclick='" + onclick + "(" + argument + ")'/>";
        return html;
    }

    public static String makeLoginForm() {
        String html = "";

        html += "<form id='loginPanel' >";
        html += "<input type='text' id='user_id' name='user_id' placeholder='Login' />";
        html += "<input type='password' id='user_password' name='user_password' placeholder='Hasło'/>";
        html += "<input type='submit' value='Zaloguj się'/>";
        //html += "<div id='registerUser' class='registerNew link'>Zapomniałem hasła</div>";
        html += "</form>";

        return html;
    }

    public static String makeLoggedPanel(HttpSession session) {
        String html = "";

        User loggedUser = (User) session.getAttribute("userData");
        String username = loggedUser.getUsername();

        html += "<div id='loggedPanel'>";
        html += "<div id='editProfile'>";
        html += makeButton("Profil", "editUser", loggedUser.getId() + "") + "</div>";
        html += "<div class='loggedIn'>Witaj " + username + "! </div>";
        html += "<div class='link logoutUser'>Wyloguj</div>";
        html += "</div>";

        return html;
    }

    public static String makeUserMenu(LoginType userType) {
        String html = "";
        html += "<div id='userMenu'>";
        if (!userType.equals(LoginType.EMPLOYEE)) {
            html += makeButton("Ocena ogólna", "newAudit");
            html += makeButton("Ocena szczegółowa", "newDetailedAudit");
            html += makeButton("Analiza SWOT", "newSwot");
            html += makeButton("Źródła innowacyjności", "innovationSources");
            html += makeButton("Bariery innowacyjności", "innovationImpediments");
            html += makeButton("Historia", "auditHistory");
            html += makeButton("Zestawienie", "auditOverview");
        }
        html += makeButton("Zgłoś pomysł", "newIdea");
        html += makeButton("Zgłoszone pomysły", "listIdeas");
        if (userType == LoginType.ADMIN) {
            html += makeButton("Wszystkie pytania", "manageQuestions");
            html += makeButton("Zarządzaj użytkownikami", "manageUsers");
        }
        html += "</div>";


        return html;
    }

    public static String makeNonLoggedMessage() {
        String html = "";
        html += "<div id='innerContent'>";
        html += "<h1>Witaj w aplikacji zarządzającej innowacjami w przedsiębiorstwie</h1>";
        html += "<h2>Zaloguj się, aby uzyskać dostęp</h2>";
        html += "</div>";

        return html;
    }

    public static String makeQuestionTable(List<Question> questions) {
        StringBuilder html = new StringBuilder("<div id='tableWrapper'><table class='myTable'>");
        html.append("<thead><tr><th>Id</th><th class='widestCol'>Treść</th><th>Kategoria</th></tr></thead><tbody>");
        for (Question q : questions) {
            html.append("<tr><td>").append(q.getId()).append("</td><td class='widestCol'>").append(q.getContent()).append("</td><td>").append(q.getCategory().getVisible()).append("</td></tr>");
        }
        html.append("</tbody></table></div>");
        return html.toString();
    }

    public static String makeQuestions(List<Question> questions, HttpSession session) {
        if (session.getAttribute("startNumber") == null) {
            session.setAttribute("startNumber", 1);
        }
        int shownQuestions = 0;
        StringBuilder html = new StringBuilder("");
        String buttonValue = "Następne pytania";
        if (questions.size() <= Constraints.NUMBER_OF_QUESTIONS_PER_PAGE) {
            buttonValue = "Zakończ ocenę";
        }
        html.append("<h2>Pozostało pytań: ").append(questions.size()).append("</h2>");
        html.append("<ol start='" + session.getAttribute("startNumber") + "'>");
        for (Question q : Common.getRandomQuestionsAndRemoveAskedFromSession(questions, session)) {
            html.append("<li><span class='questionLabel'>").append(q.getContent()).append("</span>");
            html.append(makeLickertScale(q.getId()));
            html.append("</li>");
            shownQuestions++;
        }
        html.append(makeButton(buttonValue, "nextQuestions"));
        html.append("</ol>");
        session.setAttribute("startNumber", (int) session.getAttribute("startNumber") + shownQuestions);
        return html.toString();
    }

    //<input type="radio" id="option-one" name="selector"><label for="option-one">Tak</label><input type="radio" id="option-two" name="selector"><label for="option-two">Raczej tak</label><input type="radio" id="option-three" name="selector"><label for="option-three">Zdecydowanie tak</label>
    private static String makeLickertScale(long id) {
        String html = "<div class='radio-group'><input class='lickertRadio' type='radio' id='" + id + "_lickert_1' name='" + id + "_lickert' value='" + Constraints.LICKERT_1 + "'><label for='" + id + "_lickert_1'>Zdecydowanie nie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_2' name='" + id + "_lickert' value='" + Constraints.LICKERT_2 + "'><label for='" + id + "_lickert_2'>Nie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_3' name='" + id + "_lickert' value='" + Constraints.LICKERT_3 + "'><label for='" + id + "_lickert_3'>Raczej nie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_4' name='" + id + "_lickert' value='" + Constraints.LICKERT_4 + "'><label for='" + id + "_lickert_4'>Nie wiem</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_5' name='" + id + "_lickert' value='" + Constraints.LICKERT_5 + "'><label for='" + id + "_lickert_5'>Raczej tak</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_6' name='" + id + "_lickert' value='" + Constraints.LICKERT_6 + "'><label for='" + id + "_lickert_6'>Tak</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_7' name='" + id + "_lickert' value='" + Constraints.LICKERT_7 + "'><label for='" + id + "_lickert_7'>Zdecydowanie tak</label></div>";
        return html;
    }

    public static String prepareResults(Audit audit) {
        List<Answer> answers = audit.getAnswers();
        StringBuilder html = new StringBuilder("<div class='auditResults'>");
        int resultTotal = Math.round(Common.getResultFromAnswers(answers) * 100);
        html.append(prepareAuditResultHeader(audit));
        html.append("<div class='mainResult'>Całkowity wynik: ").append(resultTotal).append(" % </div>");
        html.append(prepareSpiderChart(answers));
        for (QuestionCategory category : QuestionCategory.values()) {
                html.append("<div class='otherResult'>" + "<div class='otherResultHeader'>").
                        append(category.getVisible().toUpperCase()).
                        append("</div>").
                        append("<div class='otherResultDesc'>").
                        append(category.getDesription()).
                        append("</div>").
                        append("<div class='otherResultPercent'>Spełnione w ").
                        append(Math.round(Common.getResultFromAnswersForLickert(answers, category, true) * 100)).
                        append(" % </div>" +
                        "<div class='resultSeparator'></div></div>");
        }

        //html.append(makeSwotResult(audit)); przenieść do nowego
        html.append("</div>");
        html.append(HtmlContent.makeButton("Rozpocznij nowy audyt", "nextAudit"));
        return html.toString();
    }

    private static String prepareAuditResultHeader(Audit audit) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String html = "<div class='auditResultHeader'>";
        html += "<div class='auditedCompany'>" +
                "<span>Data przeprowadzenia audytu: " + sdf.format(audit.getAuditDate()) + "</span></div>";
        html += "<div class='auditorData'>Audytor: " + audit.getAuditor().getName() + " " + audit.getAuditor().getSurname() + "</div>";
        html += "</div>";

        return html;
    }

    private static String prepareSpiderChart(List<Answer> answers) {
        String html = "";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (QuestionCategory qc : QuestionCategory.values()) {
                float result = Common.getResultFromAnswersForLickert(answers, qc, false) / 3;
                dataset.addValue(result, "wynik", qc.getVisible());
                dataset.addValue(10, "max10", qc.getVisible());
                dataset.addValue(8, "max8", qc.getVisible());
                dataset.addValue(6, "max6", qc.getVisible());
                dataset.addValue(4, "max4", qc.getVisible());
                dataset.addValue(2, "max2", qc.getVisible());
        }

        SpiderWebPlot spiderWebPlot = new SpiderWebPlot(dataset);
        spiderWebPlot.setWebFilled(true);
        spiderWebPlot.setLabelGenerator(new IntervalCategoryItemLabelGenerator());
        spiderWebPlot.setInteriorGap(0.4);
        spiderWebPlot.setMaxValue(10);
        spiderWebPlot.setBackgroundPaint(null);
        spiderWebPlot.setOutlineStroke(null);
        spiderWebPlot.setBackgroundAlpha(0);
        spiderWebPlot.setSeriesPaint(0, Color.GREEN);
        spiderWebPlot.setSeriesOutlineStroke(0, new BasicStroke(3f));
        spiderWebPlot.setSeriesPaint(1, Color.WHITE);
        spiderWebPlot.setSeriesPaint(2, Color.WHITE);
        spiderWebPlot.setSeriesPaint(3, Color.WHITE);
        spiderWebPlot.setSeriesPaint(4, Color.WHITE);
        spiderWebPlot.setSeriesPaint(5, Color.WHITE);
        spiderWebPlot.setLabelPaint(Color.WHITE);

        JFreeChart chart = new JFreeChart(null, null, spiderWebPlot, false);
        chart.setBackgroundPaint(null);


        File tempFile = new File("temporary.png");
        try {
            ChartUtilities.saveChartAsPNG(tempFile, chart, 400, 400);
            html += "<img class='resultSpider' src=\"data:image/png;base64, " + Base64.getEncoder().encodeToString(FileUtils.getBytes(tempFile)) + "\"/>";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    public static String makeSwotResult(Swot swot) {
        String html = "<h2>Wynik analizy SWOT</h2>";
        SwotResult result = Common.getSwotResultDescription(swot);
        html += "<div class='otherResult bold'>Zalecana strategia: " + result.getStrategy() + "</div>";
        html += "<div class='otherResultDesc'>" + result.getDescription() + "</div>";

        html += makeButton("Rozpocznij nową analizę", "newSwotAnalysis");
        return html;
    }

    public static String getAuditHitory(List<Audit> audits, boolean general) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        String note = "ogólna";
        if (!general) {
            note = "szczegółowa";
        }
        StringBuilder html = new StringBuilder("<div id='tableWrapper'><h2>Ocena " + note + "</h2><table class='myTable'>");
        html.append("<thead><tr><th>Data oceny</th><th>Wynik</th><th></th></tr></thead><tbody>");
        Collections.sort(audits, (o1, o2) -> o2.getAuditDate().compareTo(o1.getAuditDate()));
        for (Audit audit : audits) {
            if (audit.getResult() != null) {
                html.append("<tr>");
                html.append("<td>").append(sdf.format(audit.getAuditDate())).append("</td>");
                html.append("<td>").append(audit.getResult().getResultValue()).append("</td>");
                html.append("<td>").append(makeButton("Raport", "makeReport", String.valueOf(audit.getId()))).append("</td>");
                html.append("</tr>");
            }
        }
        html.append("</tbody></table></div>");
        return html.toString();
    }

    public static String getSwotHistory(List<Swot> swots) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        StringBuilder html = new StringBuilder("<div id='tableWrapper'><h2>Analiza SWOT</h2><table class='myTable'>");
        html.append("<thead><tr><th>Data analizy</th><th>Strategia</th><th></th></tr></thead><tbody>");
        Collections.sort(swots, (o1, o2) -> o2.getSwotDate().compareTo(o1.getSwotDate()));
        for (Swot swot : swots) {
            html.append("<tr>");
            html.append("<td>").append(sdf.format(swot.getSwotDate())).append("</td>");
            html.append("<td>").append(Common.getSwotResultDescription(swot).getStrategy()).append("</td>");
            html.append("<td>").append(makeButton("Raport", "makeSwotReport", String.valueOf(swot.getId()))).append("</td>");
            html.append("</tr>");
        }
        html.append("</tbody></table></div>");
        return html.toString();
    }

    public static String getReport(Audit audit) {
        String html = prepareResults(audit);
        html += makeButton("Wróć", "getAuditHistory");
        return html;
    }

    public static String makeUsersForm(List<User> users) {
        String html = "";
        html += makeButton("Dodaj użytkownika", "addUser");
        html += "<div id='tableWrapper'>";
        html += "<table class='myTable'><thead>";
        html += "<tr><th>Nazwa użytkownika</th><th>Imię</th><th>Nazwisko</th><th>E-mail</th><th>Data dodania</th>" +
                "<th>Typ</th><th>Przełożony</th><th>Aktywny</th><th>Akcje</th>";
        html += "</tr></thead><tbody>";
        for (User user : users) {
            html += "<tr id='user_" + user.getId() + "' class='tableTR'><td id='username_" + user.getId() + "'>" + user.getUsername() + "</td><td>" + user.getName() + "</td><td>" + user.getSurname() + "</td>" +
                    "<td>" + user.getEmail() + "</td><td>" + sdf.format(user.getAccountCreated()) + "</td><td>" + user.getRole().getDisplayName() + "</td>" +
                    "<td>" + (user.getManager() != null ? user.getManager().getName() + " " + user.getManager().getSurname() : "BRAK") +
                    "</td><td>" + (user.isActive() ? "tak" : "nie") + "</td><td>";
            html += "<img src='images/edit.png' title='Edytuj użytkownika' class='ideaOption' onclick='editUser(" + user.getId() + ")' />";
            html += "<img id='delete_" + user.getId() + "' src='images/reject.png' title='Usuń użytkownika' class='ideaOption' onclick='deleteUser(" + user.getId() + ")' />";
            html += "<img id='password_" + user.getId() + "' src='images/password.png' title='Zmień hasło użytkownikowi' class='ideaOption' onclick='changePass(" + user.getId() + ")' />";
            html += "</td></tr>";
        }
        html += "</tbody></table></div>";

        return html;
    }

    private static String displayIdeasTable(List<Idea> ideas, User user) {
        String html = "";
        if (!ideas.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
            int numberOfTDs = 6;
            html += "<div id='tableWrapper'>";
            html += "<table class='myTable'><thead>";
            html += "<tr><th class='widestCol'>Tutuł pomysłu</th><th>Kategoria</th><th>Status</th><th>Data dodania</th>";
            if (!user.getRole().equals(LoginType.EMPLOYEE)) {
                html += "<th>Autor</th>";
                numberOfTDs++;
            }
            html += "<th>Data aktualizacji</th>";
            html += "<th>Akcje</th>";
            html += "</tr></thead><tbody>";
            for (Idea i : ideas) {
                html += "<tr class='tableTR'><td onclick='moreInfoIdea(" + i.getId() + ")' title='Szczegółowy opis pomysłu' class='widestCol moreInfo'>" + i.getName() + "</td><td  class='widestCol'>" + i.getType().getValue() + "</td><td>" + i.getStatus().getValue() + "</td><td>" + sdf.format(i.getAddedDate()) + "</td>";
                if (!user.getRole().equals(LoginType.EMPLOYEE)) {
                    html += "<td>" + i.getEmployee().getName() + " " + i.getEmployee().getSurname() + "</td>";
                }
                html += "<td>";
                if (i.getActionDate() != null) {
                    html += sdf.format(i.getActionDate());
                } else {
                    html += "BRAK";
                }
                html += "</td>";
                html += "<td>";
                if (!user.getRole().equals(LoginType.EMPLOYEE) && i.getEmployee().getId() != user.getId()) {
                    if (i.getStatus().equals(Status.OPEN)) {
                        html += "<img src='images/circle-close.png' id='reject_" + i.getId() + "' class='ideaOption'  onclick='rejectIdea(" + i.getId() + ")' title='Zamknij'/>";
                    } else {
                        html += "<img src='images/play.png' id='accept_" + i.getId() + "' class='ideaOption'  onclick='acceptIdea(" + i.getId() + ")' title='Otwórz'/>";
                    }
                }
                html += "<img src='images/comment.png' id='decisionIdea_" + i.getId() + "' class='ideaOption' onclick='decisionIdea(" + i.getId() + ")' title='Pokaż historię komentarzy'/>";

                html += "</td>";
                html += "</tr>";
                html += "<tr class='moreInfoTR'><td colspan='" + numberOfTDs + "'><div id='idea_" + i.getId() + "' class='hidden' >" + i.getContent() + "</div></td></tr>";
            }
            html += "</tbody></table></div>";
            html += "</html>";
        } else {
            html += "<h1>Nie dodano jeszcze zgłoszeń pomysłów</h1>";
        }

        return html;
    }

    public static String displayIdeas(List<Idea> ideas, User user) {
        String html = "";
        List<List<Idea>> listsOfIdeas = Common.getIdeasOfEmployeesOfManager(ideas, user);
        if (!listsOfIdeas.get(0).isEmpty()) {
            html += "<h2>Pomysły podwładnych</h2>";
            html += displayIdeasTable(listsOfIdeas.get(0), user);
            html += "<h2>Pozostałe pomysły</h2>";
            html += displayIdeasTable(listsOfIdeas.get(1), user);
        } else {
            html += displayIdeasTable(ideas, user);
        }
        return html;
    }

    public static String makeMoreInfoIdea(Idea i) {
        String html = "";
        html += HtmlContent.makeButton("Dodaj komentarz", "newIdeaComment", "" + i.getId(), "newComment");
        i.getOpinions().sort((o1, o2) -> o2.getOpinionDate().compareTo(o1.getOpinionDate()));
        for (Opinion o : i.getOpinions()) {
            html += "<div class='ideaOpinionContent'>";
            html += o.getContent();
            html += "</div>";
            html += "<div class='ideaOpinionAuthor'>";
            html += "(" + sdf.format(o.getOpinionDate()) + ") Autor: " + o.getAuthor().getName() + " " + o.getAuthor().getSurname();
            html += "</div>";
            html += "<div class='ideaSeparator'></div>";
        }
        return html;
    }

    public static String makeSwotTables(List<SwotAlternatives> list, long auditId) {
        String html = "<h2>Analiza SWOT</h2>";
        for (SwotCategory category : SwotCategory.values()) {
            html += "<div class='row style-select'>";
            html += "<div class='tableHeader'>" + category.getValue() + "</div>";
            html += "<div class='col-md-12'>";
            html += "<div id='div_" + category.toString() + "' class='subject-info-box-1'>";
            html += "<label>Dostępne</label>";
            html += "<select multiple id='source_" + category.toString().toLowerCase() + "'>";
            for (SwotAlternatives sw : list) {
                if (sw.getCategory().equals(category)) {
                    html += "<option value='swot_" + sw.getId() + "'>" + sw.getText() + "</option>";
                }
            }
            html += "</select>";

            html += makeButton("Dodaj nową pozycję", "newSwotPosition", "\"" + category.toString() + "\"", "swot_" + category.toString());

            html += "</div>";
            html += "<div class='subject-info-arrows text-center'>";
            html += "</br></br>";
            html += "<input type='button' class='userMenuButton' id='btnAllRight_" + category.toString().toLowerCase() + "' value='>>'/>";
            html += "</br>";
            html += "<input type='button' class='userMenuButton' id='btnRight_" + category.toString().toLowerCase() + "' value='>'/>";
            html += "</br>";
            html += "<input type='button' class='userMenuButton' id='btnLeft_" + category.toString().toLowerCase() + "' value='<'/>";
            html += "</br>";
            html += "<input type='button' class='userMenuButton' id='btnAllLeft_" + category.toString().toLowerCase() + "' value='<<'/>";
            html += "</div>";

            html += "<div class='subject-info-box-2'>";
            html += "<label>Wybrane</label>";
            html += "<select multiple class='form-control' id='destination_" + category.toString().toLowerCase() + "'></select>";
            html += "</div>";
            html += "<div class='clearfix'></div>";

            html += "</div></div>";

            String script = "$('#btnRight_" + category.toString().toLowerCase() + "').click(function(e) {\n" +
                    "    $('select').moveToListAndDelete('#source_" + category.toString().toLowerCase() + "', '#destination_" + category.toString().toLowerCase() + "');\n" +
                    "    e.preventDefault();\n" +
                    "  });\n" +
                    "\n" +
                    "  $('#btnAllRight_" + category.toString().toLowerCase() + "').click(function(e) {\n" +
                    "    $('select').moveAllToListAndDelete('#source_" + category.toString().toLowerCase() + "', '#destination_" + category.toString().toLowerCase() + "');\n" +
                    "    e.preventDefault();\n" +
                    "  });\n" +
                    "\n" +
                    "  $('#btnLeft_" + category.toString().toLowerCase() + "').click(function(e) {\n" +
                    "    $('select').moveToListAndDelete('#destination_" + category.toString().toLowerCase() + "', '#source_" + category.toString().toLowerCase() + "');\n" +
                    "    e.preventDefault();\n" +
                    "  });\n" +
                    "\n" +
                    "  $('#btnAllLeft_" + category.toString().toLowerCase() + "').click(function(e) {\n" +
                    "    $('select').moveAllToListAndDelete('#destination_" + category.toString().toLowerCase() + "', '#source_" + category.toString().toLowerCase() + "');\n" +
                    "    e.preventDefault();\n" +
                    "  });";

            html += makeJS(script);
        }

        html += makeButton("Dalej", "saveSwot", String.valueOf(auditId));


        return html;
    }

    public static String makeSwotRelations(Swot swot) {
        String html = "<h2>Czy mocne strony pozwalają na pełne wykorzystanie szans?<br/>" +
                "Czy słabe strony mogą uniemożliwić wykorzystanie szans?<br/>" +
                "Czy mocne strony pomogą w likwidacji zagrożeń?<br/>" +
                "Czy słabe strony wpływają negatywnie na zagrożenia?<br/></h2>";
        html += "<div id='tableWrapper'>";
        html += "<table class='myTable'><tbody>";
        html += "<tr class='firstRowRelations'><td></td>";
        String swotClass = "negative";
        for (SwotAlternatives col : Common.separateCategoriesOfSwot(swot.getAlternatives()).get("cols")) {
            if (col.getCategory().equals(SwotCategory.OPPORTUNITES) || col.getCategory().equals(SwotCategory.STRENGHTS)) {
                swotClass = "positive";
            } else {
                swotClass = "negative";
            }
            html += "<td class='" + swotClass + "'>" + col.getText() + "</td>";
        }
        html += "</tr>";
        for (SwotAlternatives row : Common.separateCategoriesOfSwot(swot.getAlternatives()).get("rows")) {
            if (row.getCategory().equals(SwotCategory.OPPORTUNITES) || row.getCategory().equals(SwotCategory.STRENGHTS)) {
                swotClass = "positive";
            } else {
                swotClass = "negative";
            }
            html += "<tr><td class='firstColRelations " + swotClass + "'>" + row.getText() + "</td>";
            for (SwotAlternatives col : Common.separateCategoriesOfSwot(swot.getAlternatives()).get("cols")) {
                html += "<td class='fixedWidth'>" + makeNumber(0, 2, 1,
                        Common.getValueOfRelation(row, col, swot.getRelations()), "swot_" + row.getId() + "_" + col.getId()) + "</td>";
            }
            html += "</tr>";
        }
        html += "<tbody></table></div>";

        html += makeButton("Zapisz i pokaż raport", "swotRelations", String.valueOf(swot.getId()));

        return html;
    }

    private static String makeNumber(int min, int max, int step, int value, String id) {
        String html = "<input class='numberInput' type='number' min='" + min + "' max='" + max + "' step='" + step + "' id='" + id + "' value='" + value + "'/>";
        return html;
    }

    public static String getSingleAlternativeOption(long id, String text) {
        String html = "<option value='swot_" + id + "'>" + text + "</option>";
        return html;
    }

    private static String makeJS(String script) {
        String html = "<script type='application/javascript'>$(function() {";
        html += script;
        html += "});</script>";

        return html;
    }

    public static String makeOverviewContent(List<Audit> audits, List<Swot> swots) {
        String html = "";
        if (audits.size() >= 2) {
            html += makeOverviewChart(audits);
            html += makeSwotOverview(swots);
        } else {
            html += "<h1>Do zestawienia są potrzebne co najmniej dwa audyty</h1>";
        }
        return html;
    }

    private static String makeOverviewChart(List<Audit> audits) {
        String html = "";

        TimeSeriesCollection collection = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries("Końcowy wynik");
        for (Audit audit : audits) {
            series.addOrUpdate(new Day(audit.getAuditDate()), Common.getResultFromAnswers(audit.getAnswers()) * 100);
        }
        collection.addSeries(series);

        html += makeTimeChart(collection, new BasicStroke(5.0f), "Wyniki końcowe", false);

        collection = new TimeSeriesCollection();

        for (QuestionCategory cat : QuestionCategory.values()) {
            series = new TimeSeries(cat.getVisible());
            for (Audit audit : audits) {
                series.addOrUpdate(new Day(audit.getAuditDate()), Common.getResultFromAnswersForLickert(audit.getAnswers(), cat, true) * 100);
            }
            collection.addSeries(series);
        }


        html += makeTimeChart(collection, new BasicStroke(3.0f), "Wyniki poszczególnych kategorii", true);


        return html;
    }

    private static String makeSwotOverview(List<Swot> swots) {
        String html = "<h1>Zestawienie wyników analizy SWOT</h1>";
        boolean areSwotChanges = false;
        List<Long> shownAlternatives = new ArrayList<>();

        if (swots.size() < 2) {
            html += "<h2>Brak danych SWOT dla podanego okresu</h2>";
        } else {
            Swot firstSwot = swots.get(0);
            Swot lastSwot = swots.get(swots.size() - 1);
            for (SwotCategory cat : SwotCategory.values()) {
                html += "<h2>" + cat.getValue() + "</h2><ul>";

                for (SwotAlternatives sw : firstSwot.getAlternatives()) {
                    if (sw.getCategory().equals(cat) && !shownAlternatives.contains(sw.getId())) {
                        areSwotChanges = true;
                        if (lastSwot.getAlternatives().contains(sw)) {
                            html += "<li>" + sw.getText() + "</li>";
                        } else {
                            if (cat.equals(SwotCategory.OPPORTUNITES) || cat.equals(SwotCategory.STRENGHTS)) {
                                html += "<li>" + printIconOverview(false, false) + sw.getText() + "</li>";
                            } else {
                                html += "<li>" + printIconOverview(false, true) + sw.getText() + "</li>";
                            }
                            shownAlternatives.add(sw.getId());
                        }
                    }
                }
                for (SwotAlternatives sw : lastSwot.getAlternatives()) {
                    if (sw.getCategory().equals(cat)) {
                        areSwotChanges = true;
                        if (!firstSwot.getAlternatives().contains(sw) && !shownAlternatives.contains(sw.getId())) {
                            if (cat.equals(SwotCategory.OPPORTUNITES) || cat.equals(SwotCategory.STRENGHTS)) {
                                html += "<li>" + printIconOverview(true, true) + sw.getText() + "</li>";
                            } else {
                                html += "<li>" + printIconOverview(true, false) + sw.getText() + "</li>";
                            }
                            shownAlternatives.add(sw.getId());
                        }
                    }
                }
                if (!areSwotChanges) {
                    html += "<li>BRAK ZMIAN</li>";
                }
                html += "</ul>";
            }
        }
        return html;
    }

    private static String makeTimeChart(TimeSeriesCollection collection, Stroke lineStroke, String title, boolean showLegend) {
        String html = "";
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Data", "Wynik [%]", collection, showLegend, false, false);
        chart.setBackgroundPaint(null);
        if (showLegend) {
            chart.getLegend().setBackgroundPaint(Color.lightGray);
        }
        Paint[] paints = {Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.BLACK};
        chart.getTitle().setPaint(Color.WHITE);
        chart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.WHITE);
        chart.getXYPlot().getDomainAxis().setLabelPaint(Color.WHITE);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < chart.getXYPlot().getSeriesCount(); ++i) {
            renderer.setSeriesStroke(i, lineStroke);
            renderer.setSeriesShapesFilled(i, true);
            if (showLegend)
                renderer.setSeriesPaint(i, paints[i]);
        }
        chart.getXYPlot().setRenderer(renderer);
        chart.getXYPlot().getRangeAxis().setTickLabelPaint(Color.WHITE);
        chart.getXYPlot().getRangeAxis().setLabelPaint(Color.WHITE);

        File tempFile = new File("temporary.png");
        try {
            ChartUtilities.saveChartAsPNG(tempFile, chart, 640, 480);
            html += "<img class='resultTimeChart' src=\"data:image/png;base64, " + Base64.getEncoder().encodeToString(FileUtils.getBytes(tempFile)) + "\"/>";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return html;
    }

    private static String printIconOverview(boolean isPlus, boolean positive) {
        String html = "<img src='images/";
        if (isPlus) {
            if (!positive) {
                html += "plus.png";
            } else {
                html += "plusG.png";
            }
        } else {
            if (positive) {
                html += "minus.png";
            } else {
                html += "minusR.png";
            }
        }
        html += "' class='swotOverviewIcon'/>";

        return html;
    }

    public static String getManagersSelect(List<User> managers) {
        String html = "";
        html += "<br /><span class='labelSpan'>Manager: </span><select id='manager' class='newUserInput'>";
        for (User manager : managers) {
            html += "<option value='manager_" + manager.getId() + "'>" + manager.getName() + " " + manager.getSurname() + "</option>";
        }
        html += "</select>";
        return html;
    }

    public static String makeEditUserHtml(User user, User loggedUser, List<User> managers) {
        String html = "";
        if (!loggedUser.getRole().equals(LoginType.ADMIN)) {
            html += "</br>";
        }
        html += "<p>Imię: <input type='text' id='name' value='" + user.getName() + "' placeholder='Imię'/></p>";
        html += "<p>Nazwisko: <input type='text' id='surname' value='" + user.getSurname() + "' placeholder='Nazwisko'/></p>";
        html += "<p>Email: <input type='text' id='email' value='" + user.getEmail() + "' placeholder='Email'/></p>";
        html += "<p>Zmienić hasło? <input type='checkbox' id='isPassword'/></p>";
        html += "<p id='newPassword'><input type='password' id='password' placeholder='Nowe hasło'/></p>";
        if (loggedUser.getRole().equals(LoginType.ADMIN)) {
            html += "<p>Typ: <select id='type'>";
            for (LoginType type : LoginType.values()) {
                String selected = "";
                if (user.getRole().equals(type)) {
                    selected = "selected=selected";
                }
                html += "<option value='" + type.toString().toLowerCase() + "'" + selected + ">" + type.getDisplayName() + "</option>";
            }
            html += "</select></p>";
            String checked = "";
            if (user.isActive()) checked = "checked=checked";
            html += "<p>Aktywny: <input type='checkbox' id='active' " + checked + "/></p>";
            html += "<p>Przełożony: <select id='manager'><option value='-1'>BRAK</option>";
            for (User manager : managers) {
                String selected = "";
                if (user.getManager() != null && manager.getId() == user.getManager().getId()) {
                    selected = "selected=selected";
                }
                html += "<option value='" + manager.getId() + "'" + selected + ">" + manager.getName() + " " + manager.getSurname() + "</option>";
            }
            html += "</select></p>";
        }
        String js = "$('#isPassword').change(function(){ if($(this).is(':checked')) {" +
                " $('#newPassword').show(); } else {$('#newPassword').hide();} });";
        html += makeJS(js);
        return html;
    }
}