package main.app;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import main.app.enums.*;
import main.app.orm.*;
import main.app.orm.methods.AuditMethods;
import main.app.orm.methods.QuestionMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HtmlContent {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public static String makeButton(String nameToDisplay, String onclick) {
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
        html += "</form>";

        return html;
    }

    public static String makeLoggedPanel(HttpSession session) {
        String html = "";

        User loggedUser = (User) session.getAttribute("userData");
        String username = loggedUser.getUsername();


        html += "<div id='helpContext'>";
        html += "<img src='images/question-mark.png' id='openHelp' class='ideaOption'  onclick='openHelpWindow()' title='Pokaż pomoc'/>";
        html += "</div>";
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
        html += makeButton("Identyfikacja innowacji", "innovationIdentification");
        html += makeButton("Zgłoś pomysł", "newIdea");
        html += makeButton("Zgłoszone pomysły", "listIdeas");
        if (userType == LoginType.ADMIN) {
            html += makeButton("Wszystkie pytania", "manageQuestions");
            html += makeButton("Zarządzaj użytkownikami", "manageUsers");
            html += makeButton("Zarządzaj źródłami", "manageSources");
            html += makeButton("Zarządzaj barierami", "manageImpediments");
        }
        html += "</div>";


        return html;
    }

    public static String makeSourcesTable(List<Source> sources) {
        StringBuilder html = new StringBuilder(makeButton("Dodaj nowe źródło", "newSource"));
        if (sources.isEmpty()) {
            html.append("<h2>Brak źródeł do wyświetlenia</h2>");
        } else {
            html.append("<div class='tableHeader'>Źródła innowacyjności</div>");
            html.append("<div id='tableWrapper'><table class='myTable'>");
            html.append("<thead><tr><th>Id</th><th>Treść</th><th class='widestCol'>Wskazówka (w przypadku nie wybrania)</th><th>Typ</th><th>Akcja</th></tr></thead><tbody>");
            for (Source q : sources) {
                html.append("<tr><td>").append(q.getId()).append("</td><td class='widestCol'>").append(q.getText()).append("</td><td class='pre'>").append(q.getLongDescription()).append("</td><td>").append(q.isInternal() ? "Wewnętrzne" : "Zewnętrzne");
                if (q.getAudits().isEmpty()) {
                    html.append("<td><img id='delete_" + q.getId() + "' src='images/reject.png' title='Usuń źródło' class='ideaOption' onclick='deleteSource(" + q.getId() + ")' /></td>");
                }
                html.append("</td></tr>");
            }
            html.append("</tbody></table></div>");
        }
        return html.toString();
    }

    public static String makeImpedimentsTable(List<Impediment> impediments) {
        StringBuilder html = new StringBuilder(makeButton("Dodaj nową barierę", "newImpediment"));
        if (impediments.isEmpty()) {
            html.append("<h2>Brak barier do wyświetlenia</h2>");
        } else {
            html.append("<div class='tableHeader'>Bariery innowacyjności</div>");
            html.append("<div id='tableWrapper'><table class='myTable'>");
            html.append("<thead><tr><th>Id</th><th class='widestCol'>Treść</th><th>Type</th><th>Zalecenia</th><th>Akcja</th></tr></thead><tbody>");
            for (Impediment q : impediments) {
                html.append("<tr><td>").append(q.getId()).append("</td><td class='widestCol'>").append(q.getText()).append("</td><td>").append(q.getType().getDisplay()).append("</td><td>").append(makeButton("Pokaż", "showAdvices", q.getId() + ""));
                if (q.getAudits().isEmpty()) {
                    html.append("<td><img id='delete_" + q.getId() + "' src='images/reject.png' title='Usuń barierę' class='ideaOption' onclick='deleteImpediment(" + q.getId() + ")' /></td>");
                }
                html.append("</td></tr>");
            }
            html.append("</tbody></table></div>");
        }
        return html.toString();
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
        QuestionType type = questions.get(0).getType();
        if (questions.size() <= Constraints.NUMBER_OF_QUESTIONS_PER_PAGE) {
            buttonValue = "Zakończ ocenę";
        }
        html.append("<h2>Pozostało pytań: ").append(questions.size()).append("</h2>");
        html.append("<ol start='" + session.getAttribute("startNumber") + "'>");
        for (Question q : Common.getRandomQuestionsAndRemoveAskedFromSession(questions, session)) {
            html.append("<li><span class='questionLabel'>").append(q.getContent()).append("</span>");
            if (type.equals(QuestionType.LICKERT))
                html.append(makeLickertScale(q.getId()));
            else
                html.append(makeDetailedLickertScale(q.getId()));
            html.append("</li>");
            shownQuestions++;
        }
        String argument = true + "";
        if (type.equals(QuestionType.DETAILED)) {
            argument = false + "";
        }
        html.append(makeButton(buttonValue, "nextQuestions", argument));
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

    private static String makeDetailedLickertScale(long id) {
        String html = "<div class='radio-group'><input class='lickertRadio' type='radio' id='" + id + "_lickert_1' name='" + id + "_lickert' value='" + Constraints.LICKERT_DETAILED_1 + "'><label for='" + id + "_lickert_1'>Brak/niedostatecznie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_2' name='" + id + "_lickert' value='" + Constraints.LICKERT_DETAILED_2 + "'><label for='" + id + "_lickert_2'>Miernie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_3' name='" + id + "_lickert' value='" + Constraints.LICKERT_DETAILED_3 + "'><label for='" + id + "_lickert_3'>Dostatecznie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_4' name='" + id + "_lickert' value='" + Constraints.LICKERT_DETAILED_4 + "'><label for='" + id + "_lickert_4'>Dobrze</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_5' name='" + id + "_lickert' value='" + Constraints.LICKERT_DETAILED_5 + "'><label for='" + id + "_lickert_5'>Bardzo dobrze</label>";
        return html;
    }

    public static String prepareResults(Audit audit) {
        List<Answer> answers = audit.getAnswers();
        QuestionType type = audit.getAnswers().get(0).getQuestion().getType();
        StringBuilder html = new StringBuilder("<div class='auditResults'>");
        int resultTotal = Math.round(Common.getResultFromAnswers(answers, true) * 100);
        html.append(prepareAuditResultHeader(audit));
        html.append("<div class='mainResult'>Całkowity wynik: ").append(resultTotal).append(" % </br>");
        if (type.equals(QuestionType.DETAILED)) {
            int rawTotal = (int) Common.getResultFromAnswers(answers, false);
            html.append(Common.calculateResult(rawTotal)).append("</div>");
        }
        html.append(prepareSpiderChart(answers));
        int iter = 0;

        for (QuestionCategory category : QuestionCategory.values()) {
            if (category.getType().equals(type)) {
                if (type.equals(QuestionType.LICKERT)) {
                    html.append("<div class='otherResult'>" + "<div class='otherResultHeader bold' style='color:" + Constraints.colors[iter++] + "'>").
                            append(category.getVisible().toUpperCase()).
                            append("</div>").
                            append("<div class='otherResultDesc'>").
                            append(category.getDesription()).
                            append("</div>").
                            append("<div class='otherResultPercent'>Spełnione w ").
                            append(Math.round(Common.getResultFromAnswersForLickert(answers, category, true) * 100)).
                            append(" % </div>" +
                                    "<div class='resultSeparator'></div></div>");
                } else {
                    html.append("<div class='otherResult'>" + "<div class='otherResultHeader bold' style='color:" + Constraints.colors[iter++] + "'>").
                            append(category.getVisible().toUpperCase()).
                            append("</div>").
                            append("<div class='otherResultDesc'>").
                            append(Common.calculateResultForCategory((int) Common.getResultFromAnswersForDetailedLickert(answers, category, false))).
                            append("</div>").
                            append("<div class='otherResultPercent'>Wynik procentowy: ").
                            append(Math.round(Common.getResultFromAnswersForDetailedLickert(answers, category, true) * 100)).
                            append(" % </div>" +
                                    "<div class='resultSeparator'></div></div>");
                }
            }
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
                "<span>Data raportu: " + sdf.format(audit.getAuditDate()) + "</span></div>";
        html += "<div class='auditorData'>Przeprowadził: " + audit.getAuditor().getName() + " " + audit.getAuditor().getSurname() + "</div>";
        html += "</div>";

        return html;
    }

    private static String prepareSpiderChart(List<Answer> answers) {
        String html = "<div id='chartdiv'></div>";
        QuestionType type = answers.get(0).getQuestion().getType();
        AuditType auditType = answers.get(0).getAudit().getType();

        int iter = 0;

        String js = "var chart = AmCharts.makeChart( \"chartdiv\", {\n" +
                "  \"type\": \"radar\",\n" +
                "  \"theme\": \"light\",\n" +
                "  \"fontSize\": 16," +
                "  \"fontFamily\": \"Arial\"," +
                "  \"addClassNames\": true," +
                "  \"dataProvider\": [ \n";
        for (QuestionCategory qc : QuestionCategory.values()) {
            if (qc.getType().equals(type)) {
                float result;
                if (auditType.equals(AuditType.GENERAL))
                    result = Math.round(Common.getResultFromAnswersForLickert(answers, qc, true) * 100);
                else
                    result = Math.round(Common.getResultFromAnswersForDetailedLickert(answers, qc, true) * 100);
                js += "    {\n" +
                        "\"category\": \"" + qc.getVisible() + "\",\n" +
                        "    \"result\": " + result + ",\n" +
                        "\"color\": \"" + Constraints.colors[iter] + "\"" +
                        "  } ";
                if (iter++ != QuestionCategory.values().length - 1) {
                    js += ", ";
                }
            }
        }
        js +=
                "   ],\n" +
                        "  \"valueAxes\": [ {\n" +
                        "    \"axisTitleOffset\": 20,\n" +
                        "    \"minimum\": 0,\n" +
                        "    \"maximum\": 100,\n" +
                        "    \"axisAlpha\": 0.80,\n" +
                        "      \"guides\": [{\n" +
                        "      \"value\": 0,\n" +
                        "      \"toValue\": 20,\n" +
                        "      \"fillColor\": \"#fff\",\n" +
                        "      \"fillAlpha\": 0.3\n" +
                        "    }, {\n" +
                        "      \"value\": 20,\n" +
                        "      \"toValue\": 40,\n" +
                        "      \"fillColor\": \"#fff\",\n" +
                        "      \"fillAlpha\": 0.25\n" +
                        "    }, {\n" +
                        "      \"value\": 40,\n" +
                        "      \"toValue\": 60,\n" +
                        "      \"fillColor\": \"#fff\",\n" +
                        "      \"fillAlpha\": 0.2\n" +
                        "    }, {\n" +
                        "      \"value\": 60,\n" +
                        "      \"toValue\": 80,\n" +
                        "      \"fillColor\": \"#fff\",\n" +
                        "      \"fillAlpha\": 0.15\n" +
                        "    }, {\n" +
                        "      \"value\": 80,\n" +
                        "      \"toValue\": 100,\n" +
                        "      \"fillColor\": \"#fff\",\n" +
                        "      \"fillAlpha\": 0.1\n" +
                        "    }]" +
                        "  } ],\n" +
                        "  \"startDuration\": 0,\n" +
                        "  \"graphs\": [ {\n" +
                        "    \"balloonText\": \"[[category]]: [[value]] %\",\n" +
                        "    \"bullet\": \"round\",\n" +
                        "    \"lineThickness\": 3,\n" +
                        "    \"valueField\": \"result\"\n" +
                        "  } ],\n" +
                        "  \"categoryField\": \"category\",\n" +
                        "   \"listeners\": [{\n" +
                        "    \"event\": \"rendered\",\n" +
                        "    \"method\": updateLabels\n" +
                        "  }, {\n" +
                        "    \"event\": \"resized\",\n" +
                        "    \"method\": updateLabels\n" +
                        "  }]" +
                        "} );";
        html += makeJS(js);

        /*DefaultCategoryDataset dataset = new DefaultCategoryDataset();
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
        }*/

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

    public static String makeSwotTables(List<SwotAlternatives> list) {
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

        html += makeButton("Dalej", "saveSwot");


        return html;
    }

    public static String makeSourceOrImpedimentTable(Object listOfItems, boolean isSource) {
        AuditType type = AuditType.SOURCES;
        String html = "";
        String source = "Source";
        String sources_dek = "źródeł";
        boolean isEmpty = true;
        if (isSource) {
            html += "<h2>Źródła innowacyjności</h2>";
        } else {
            html += "<h2>Bariery innowacyjności</h2>";
            source = "Impediment";
            sources_dek = "barier";
            type = AuditType.IMPEDIMENTS;
        }
        Audit previousAudit = Common.getLastAuditOfType(AuditMethods.getAudits(), type);

        html += "<p>Brak " + sources_dek + ": <input id='noDataCheckbox_" + source + "' type='checkbox' value='no_data'/></p>";
        html += "<div id='table_" + source + "' class='row style-select'>";
        html += "<div class='col-md-12'>";
        html += "<div id='div_" + source + "' class='subject-info-box-1'>";
        html += "<label>Dostępne</label>";
        html += "<select multiple id='source_" + source + "'>";
        if (isSource) {
            List<Source> sources = (List<Source>) listOfItems;
            for (Object src : sources) {
                Source s = (Source) src;
                if (previousAudit == null || !previousAudit.getSources().contains(s)) {
                    html += "<option value='source_" + s.getId() + "'>" + s.getText() + "</option>";
                }
                isEmpty = false;
            }
        } else {
            List<Impediment> impediments = (List<Impediment>) listOfItems;
            for (Object src : impediments) {
                Impediment s = (Impediment) src;
                if (previousAudit == null || !previousAudit.getImpediments().contains(s)) {
                    html += "<option value='impediment_" + s.getId() + "'>" + s.getText() + "</option>";
                }
                isEmpty = false;
            }
        }

        html += "</select>";
        html += "</div>";
        html += "<div class='subject-info-arrows text-center'>";
        html += "</br></br>";
        html += "<input type='button' class='userMenuButton' id='btnAllRight_" + source + "' value='>>'/>";
        html += "</br>";
        html += "<input type='button' class='userMenuButton' id='btnRight_" + source + "' value='>'/>";
        html += "</br>";
        html += "<input type='button' class='userMenuButton' id='btnLeft_" + source + "' value='<'/>";
        html += "</br>";
        html += "<input type='button' class='userMenuButton' id='btnAllLeft_" + source + "' value='<<'/>";
        html += "</div>";

        html += "<div class='subject-info-box-2'>";
        html += "<label>Wybrane</label>";
        html += "<select multiple class='form-control' id='destination_" + source + "'>";
        if (isSource) {
            List<Source> sources = (List<Source>) listOfItems;
            for (Object src : sources) {
                Source s = (Source) src;
                if (previousAudit != null && previousAudit.getSources().contains(s)) {
                    html += "<option value='source_" + s.getId() + "'>" + s.getText() + "</option>";
                }
                isEmpty = false;
            }
        } else {
            List<Impediment> impediments = (List<Impediment>) listOfItems;
            for (Object src : impediments) {
                Impediment s = (Impediment) src;
                if (previousAudit != null && previousAudit.getImpediments().contains(s)) {
                    html += "<option value='impediment_" + s.getId() + "'>" + s.getText() + "</option>";
                }
                isEmpty = false;
            }
        }
        html += "</select>";
        html += "</div>";
        html += "<div class='clearfix'></div>";

        html += "</div></div>";

        String script = "$('#btnRight_" + source + "').click(function(e) {\n" +
                "    $('select').moveToListAndDelete('#source_" + source + "', '#destination_" + source + "');\n" +
                "    e.preventDefault();\n" +
                "  });\n" +
                "\n" +
                "  $('#btnAllRight_" + source + "').click(function(e) {\n" +
                "    $('select').moveAllToListAndDelete('#source_" + source + "', '#destination_" + source + "');\n" +
                "    e.preventDefault();\n" +
                "  });\n" +
                "\n" +
                "  $('#btnLeft_" + source + "').click(function(e) {\n" +
                "    $('select').moveToListAndDelete('#destination_" + source + "', '#source_" + source + "');\n" +
                "    e.preventDefault();\n" +
                "  });\n" +
                "\n" +
                "  $('#btnAllLeft_" + source + "').click(function(e) {\n" +
                "    $('select').moveAllToListAndDelete('#destination_" + source + "', '#source_" + source + "');\n" +
                "    e.preventDefault();\n" +
                "  });\n" +
                " $('#noDataCheckbox_" + source + "').change(function(){\n" +
                "   if($(this).is(':checked')) {\n" +
                "       $('#table_" + source + "').hide(); \n" +
                "   } else{\n" +
                "       $('#table_" + source + "').show();\n" +
                "   }\n" +
                " });";

        html += makeJS(script);

        if (previousAudit != null)
            html += makeButton("Pokaż obecne wskazówki", "showReport" + source, previousAudit.getId() + "");
        html += makeButton("Zatwierdź", "save" + source);

        if (isEmpty) {
            html = "<h2>Brak danych do wyświetlenia</h2>";
        }

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

    public static String makeSourcesReport(Audit audit) {
        String html = "";
        html += prepareAuditResultHeader(audit);
        if (audit.getSources().size() == AuditMethods.getSources().size()) {
            html += "<h2>Przedsiębiorstwo posiada wszystkie źródła innowacji. Gratulacje!</h2>";
        } else {
            html += "<h2>W celu lepszego rozwoju firmy, zaleca się: </h2>";
            for (Source s : Common.getNotSelectedSource(audit.getSources())) {
                html += "<h3 class='pre'>" + s.getLongDescription() + "</h3>";
            }
        }
        html += makeButton("Powrót do źródeł", "generateSources");

        return html;
    }

    public static String makeImpedimentsReport(Audit audit) {
        String html = "";
        html += prepareAuditResultHeader(audit);
        if (audit.getImpediments().isEmpty()) {
            html += "<h2>Brak barier innowacyjności. Gratulacje!</h2>";
        } else {
            html += "<h2>W celu lepszego rozwoju firmy, zaleca się: </h2>";
            for (Impediment s : audit.getImpediments()) {
                for (ImpedimentAdvice advice : s.getAdvices()) {
                    html += "<h3>" + advice.getText() + "</h3>";
                }
            }
        }
        html += makeButton("Powrót do barier", "generateImpediments");

        return html;
    }

    public static String makeAdviceHtml(Impediment impediment) {
        String html = "<h3>" + impediment.getText() + ": </h3>";
        html += "<ul>";
        for (ImpedimentAdvice advice : impediment.getAdvices()) {
            html += "<li>" + advice.getText() + "</li>";
        }
        html += "</ul>";
        return html;
    }

    public static String makeOverviewContent(List<Audit> audits, List<Swot> swots, List<Audit> detailedAudits) {
        String html = "";
        if (audits.size() >= 2) {
            html += "<h1>Zestawienie wyników audytu ogólnego</h1>";
            html += makeOverviewChart(audits, "chartDivGeneral");
        } else {
            html += "<h1>Do zestawienia ogólnego są potrzebne co najmniej dwa audyty!</h1>";
        }
        if (detailedAudits.size() >= 2) {
            html += "<h1>Zestawienie wyników audytu szczegółowego</h1>";
            html += makeOverviewChart(detailedAudits, "chartDivDetailed");
        } else {
            html += "<h1>Do zestawienia szczegółowego potrzebne są co najmniej dwa audyty!</h1>";
        }
        if (swots.size() >= 2) {
            html += makeSwotOverview(swots);
        } else {
            html += "<h1>Do zestawienia SWOT są potrzebne co najmniej dwa audyty!</h1>";
        }

        return html;
    }

    private static String makeOverviewChart(List<Audit> audits, String divId) {
        String html = "";
        QuestionType type = audits.get(0).getAnswers().get(0).getQuestion().getType();

        Comparator<String> comparator = (s, t1) -> {
            try {
                Date d1 = sdf.parse(s);
                Date d2 = sdf.parse(t1);
                return d1.compareTo(d2);
            } catch (ParseException e) {
                return 0;
            }
        };

        TreeMap<String, Map<String, Integer>> data = new TreeMap<>(comparator);
        String title = "Końcowy wynik";
        for (Audit audit : audits) {
            Map<String, Integer> dataMap = new HashMap<>();
            dataMap.put("Końcowy wynik", Math.round(Common.getResultFromAnswers(audit.getAnswers(), true) * 100));
            data.put(sdf.format(audit.getAuditDate()), dataMap);

        }
        html += makeTimeChart(data, title, divId + "_1");

        data = new TreeMap<>(comparator);
        title = "Wyniki poszczególnych kategorii";
        for (Audit audit : audits) {
            Map<String, Integer> dataMap = new HashMap<>();
            for (QuestionCategory cat : QuestionCategory.values()) {
                if (cat.getType().equals(type)) {
                    if (cat.getType().equals(QuestionType.LICKERT))
                        dataMap.put(cat.toString(), Math.round(Common.getResultFromAnswersForLickert(audit.getAnswers(), cat, true) * 100));
                    else
                        dataMap.put(cat.toString(), Math.round(Common.getResultFromAnswersForDetailedLickert(audit.getAnswers(), cat, true) * 100));
                }
            }
            data.put(sdf.format(audit.getAuditDate()), dataMap);

        }

        html += makeTimeChart(data, title, divId + "_2");
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
                            shownAlternatives.add(sw.getId());
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

    private static String makeTimeChart(TreeMap<String, Map<String, Integer>> data, String title, String divId) {
        String html = "<h2>" + title + "</h2>";

        html += "<div class='chartDivClass' id='" + divId + "'></div>";

        String js = "var chartData = generateChartData();\n" +
                "\n" +
                "var chart = AmCharts.makeChart(\"" + divId + "\", {\n" +
                "    \"type\": \"serial\",\n" +
                "    \"theme\": \"light\",\n" +
                "    \"fontSize\": 16," +
                "    \"fontFamily\": \"Arial\"," +
                "    \"color\": \"#FFFFFF\"," +
                "    \"balloonDateFormat\": \"DD-MM-YYYY\",\n" +
                "    \"dataDateFormat\": \"DD-MM-YYYY\"," +
                "    \"language\": \"pl\"," +
                "    \"legend\": {\n" +
                "        \"useGraphSettings\": true,\n" +
                "        \"color\": \"#FFFFFF\"";
        int lineThickness = 3;
        if (data.get(data.firstKey()).size() == 1) {
            js += "        ,\"enabled\": false\n";
            lineThickness = 5;
        }
        js += "    },\n" +
                "    \"dataProvider\": chartData,\n" +
                "    \"synchronizeGrid\":true,\n" +
                "    \"valueAxes\": [{\n" +
                "        \"id\":\"v1\",\n" +
                "        \"axisColor\": \"#DADADA\",\n" +
                "        \"axisThickness\": 1,\n" +
                "        \"axisAlpha\": 1,\n" +
                "        \"maximum\": 100,\n" +
                "        \"strictMinMax\": true,\n" +
                "        \"position\": \"left\"\n" +
                "    }],\n" +
                "    \"graphs\": [\n";
        int iter = 0;
        for (Map.Entry<String, Integer> entry : data.get(data.firstKey()).entrySet()) {
            String color;
            String titleAndValue;
            if (data.get(data.firstKey()).size() == 1) {
                color = Constraints.colors[Constraints.colors.length - 1];
                titleAndValue = entry.getKey();
            } else {
                color = Constraints.colors[iter];
                titleAndValue = QuestionCategory.valueOf(entry.getKey()).getVisible();
            }
            js += "       {\"valueAxis\": \"v1\",\n" +
                    "        \"lineColor\": \"" + color + "\",\n" +
                    "        \"lineThickness\": " + lineThickness + ",\n" +
                    "        \"bullet\": \"round\",\n" +
                    "        \"bulletBorderThickness\": 1,\n" +
                    "        \"hideBulletsCount\": 30,\n" +
                    "        \"balloonText\": \"" + titleAndValue + ": [[value]] %\",\n" +
                    "        \"title\": \"" + titleAndValue + "\",\n" +
                    "        \"valueField\": \"" + titleAndValue + "\",\n" +
                    "\t\t\"fillAlphas\": 0\n" +
                    "    }";
            if (iter++ != data.get(data.firstKey()).size() - 1) {
                js += ", ";
            }
        }
        js += "],\n" +
                "    \"chartScrollbar\": {},\n" +
                "    \"chartCursor\": {\n" +
                "        \"categoryBalloonDateFormat\": \"DD-MM-YYYY\"," +
                "        \"cursorPosition\": \"mouse\"\n" +
                "    },\n" +
                "    \"categoryField\": \"date\",\n" +
                "    \"categoryAxis\": {\n" +
                "        \"parseDates\": true,\n" +
                "        \"axisColor\": \"#DADADA\",\n" +
                "        \"minorGridEnabled\": true}\n" +
                "    });\n" +
                "\n" +
                "chart.addListener(\"dataUpdated\", zoomChart);\n" +
                "zoomChart();\n" +
                "\n" +
                "\n" +
                "function generateChartData() {\n" +
                "    var chartData = [];\n";
        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            js += "        chartData.push({\n" +
                    "            date: \"" + entry.getKey() + "\",\n";
            iter = 0;
            for (Map.Entry<String, Integer> entryData : entry.getValue().entrySet()) {
                String titleAndValue = entryData.getKey();
                if (entry.getValue().size() != 1) {
                    titleAndValue = QuestionCategory.valueOf(entryData.getKey()).getVisible();
                }
                js += "            \"" + titleAndValue + "\": " + entryData.getValue() + "\n";
                if (entry.getValue().size() - 1 != iter++) {
                    js += ",";
                }
            }
            js += "        });\n";
        }
        js += "    return chartData;\n" +
                "}\n" +
                "\n" +
                "function zoomChart(){\n" +
                "    chart.zoomToIndexes(chart.dataProvider.length - 20, chart.dataProvider.length - 1);\n" +
                "    $(\".amcharts-chart-div\").find(\"a\").remove();\n" +
                "}\n";
        html += makeJS(js);

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

    public static String makeHelp(List<HelpDictionary> helpDictionary, LoginType type) {
        String html = "";
        if (type.equals(LoginType.ADMIN))
            html += makeButton("Dodaj nowe pojęcie", "addHelpWord");
        for (HelpDictionary help : helpDictionary) {
            html += "<div class='helpParagraph'>";
            html += "<div class='helpWord'>" + help.getWord() + "</div>";
            html += " - ";
            html += "<div class='helpContent'>" + help.getContent() + "</div>";
            html += "</div>";

        }
        return html;
    }

    public static String makeInnovationMenu(User loggedUser) {
        String html = HtmlContent.makeButton("Nowa innowacja", "newInnovation");
        switch (loggedUser.getRole()) {
            case EMPLOYEE:
                html += HtmlContent.makeButton("Przeglądaj swoje innowacje", "viewInnovations");
                break;
            case USER:
                html += HtmlContent.makeButton("Przeglądaj zgłoszone innowacje", "viewInnovations");
                break;
            case ADMIN:
                html += HtmlContent.makeButton("Zarządzaj zgłoszonymi innowacjami", "viewInnovations");
                break;
        }
        return html;
    }

    public static String makeNewInnovationForm(User loggedUser) {
        String html = makeInnovationMenu(loggedUser);
        List<InnovationQuestion> questions = QuestionMethods.getInnovationQuestions();
        html += "</br>";
        html += "<form id='innovationForm'>";
//        html += makeInputText("innovationName", "Nazwa innowacji");
//        html += makeInputText("innovationCompany", "Nazwa przedsiębiorstwa");
//        for (InnovationCategory category : InnovationCategory.values()) {
//            html += "<h2>" + category.getDisplay().toUpperCase() + "</h2>";
//            for (InnovationQuestion question : Common.getQuestionOfCategory(category, questions)) {
//                html += makeHtmlForType(question);
//            }
//        }
        int number = 1;
        String clas = "hideInnovation";
        for (InnovationQuestion q : questions) {
            if (q.getCategory().equals(InnovationCategory.OTHER) ||
                    q.getCategory().equals(InnovationCategory.USER)) {
                clas = "showInnovation";
            } else {
                clas = "hideInnovation";
                clas += " cat_" + q.getCategory().getNumber();
            }
            html += "<div class='" + clas + "'>" + makeHtmlForType(q) + "</div>";
            if (number++ == Constraints.INNOVATION_QUESTION_FIRST) {
                html += "<div class=" + clas + "'>" + makeInnovationSelect() + "</div>";
            }
        }
//        html += makeTextarea("innovationAttachments", "Linki do załączników");
//        html += makeLongQuestion("Każdy innowator oświadcza, że zapoznał się z treścią formularza oraz, że treść formularza jest zgodna ze stanem faktycznym.");
//        html += makeTextarea("innovationSigns", "", true);

        html += "</br></br></br></br>";
        html += makeButton("Zapisz", "saveInnovation");

        html += "</form>";

        html += makeINnovationJs();

        return html;
    }

    private static String makeInnovationSelect() {
        String html = "<br/><br/><div class='innovationDiv'><h2>Kategoria innowacji</h2><select id='innovationType'>";
        html += "<option value='-1'>Wybierz kategorię innowacji</option>";
        html += "<option value='" + InnovationCategory.PRODUCT.getNumber() + "'>" + InnovationCategory.PRODUCT.getDisplay() + "</option>";
        html += "<option value='" + InnovationCategory.PROCESS.getNumber() + "'>" + InnovationCategory.PROCESS.getDisplay() + "</option>";
        html += "<option value='" + InnovationCategory.ORGANIZATION.getNumber() + "'>" + InnovationCategory.ORGANIZATION.getDisplay() + "</option>";
        html += "<option value='" + InnovationCategory.MARKETING.getNumber() + "'>" + InnovationCategory.MARKETING.getDisplay() + "</option>";
        html += "</select></div><br/><br/><br/>";

        return html;
    }

    private static String makeINnovationJs() {
        String html = "";
        String js = "$(\".dateInput\").datepicker();" +
                "    addHideTextareaOnNo();" +
                "    replaceDollarsWithConjunction();" +
                "    $(\"#innovationType\").change(function(){" +
                "    $(\".hideInnovation\").hide();" +
                "       var clas=\"cat_\";" +
                "       $(\".\"+clas+$(this).val()).show();" +
                "       if($(this).val() != '-1') $(\".cat_2\").show();" +
                "});";
        html += makeJS(js);
        return html;
    }

    private static String makeHtmlForType(InnovationQuestion question) {
        String html = "";
        switch (question.getType()) {
            case TEXT:
                html = makeLongQuestion(question.getLabel());
                html += makeInputText("question_" + question.getId(), "", true);
                break;
            case TEXTAREA:
                html = makeLongQuestion(question.getLabel());
                html += makeTextarea("question_" + question.getId(), "", question.getPlaceholder());
                break;
            case YES_NO:
                html = makeLongQuestion(question.getLabel());
                String[] id = new String[2];
                String[] label = new String[2];
                String[] value = new String[2];
                for (int i = 0; i < 2; ++i) {
                    label[i] = Common.getTypeOptions(question.getType()).get(i);
                    id[i] = "questionRadio_" + question.getId() + "_" + i;
                    value[i] = i + "";
                }
                html += makeRadioButton(id, label, value, "question_" + question.getId());
                if (question.isAdditional()) {
                    html += makeTextarea("questionAdditional_" + question.getId(), "", question.getPlaceholder());
                }
                break;
            case JUST_LABEL:
                html = makeLongQuestion(question.getLabel());
                break;
            case RADIO:
                html = makeLongQuestion(question.getLabel());
                id = new String[6];
                label = new String[6];
                value = new String[6];
                for (int i = 0; i < 6; ++i) {
                    label[i] = Common.getTypeOptions(question.getType()).get(i);
                    id[i] = "questionRadio_" + question.getId() + "_" + i;
                    value[i] = i + "";
                }
                html += makeRadioButton(id, label, value, "question_" + question.getId());
                if (question.isAdditional()) {
                    html += makeTextarea("questionAdditional_" + question.getId(), "", question.getPlaceholder());
                }
                break;
            case RADIO2:
                html = makeLongQuestion(question.getLabel());
                id = new String[3];
                label = new String[3];
                value = new String[3];
                for (int i = 0; i < 3; ++i) {
                    label[i] = Common.getTypeOptions(question.getType()).get(i);
                    id[i] = "questionRadio_" + question.getId() + "_" + i;
                    value[i] = i + "";
                }
                html += makeRadioButton(id, label, value, "question_" + question.getId());
                if (question.isAdditional()) {
                    html += makeTextarea("questionAdditional_" + question.getId(), "", question.getPlaceholder());
                }
                break;
            case DATE:
                html = makeLongQuestion(question.getLabel());
                html += makeInputText("question_" + question.getId(), "", true, "dateInput");
                break;
        }

        return html;
    }

    private static String makeInputText(String id, String label, boolean wider) {
        String html = "<div class='innovationDiv'><div>" + label + "</div><input class='wider valueInput' type='text' id='" + id + "' name='" + id + "'/></div>";
        return html;
    }

    private static String makeInputText(String id, String label, boolean wider, String class_) {
        String html = "<div class='innovationDiv'><div>" + label + "</div><input class='wider valueInput " + class_ + "' type='text' id='" + id + "' name='" + id + "'/></div>";
        return html;
    }

    private static String makeTextarea(String id, String label, String placeholder) {
        if (placeholder == null) {
            placeholder = "";
        }
        String html = "<div class='innovationDiv'><div>" + label + "</div><textarea class='wider valueInput' id='" + id + "' name='" + id + "' placeholder='" + placeholder + "'></textarea></div>";

        return html;
    }

    private static String makeLongQuestion(String question) {
        String html = "<div class='innovationDiv labelInnovation'>" + question + "</div>";
        return html;
    }

    private static String makeRadioButton(String[] id, String[] label, String[] value, String name) {
        String html = "<div class='innovationDiv radioInnovation'>";
        for (int i = 0; i < id.length; ++i) {
            html += label[i] + "<input type='radio' id='" + id[i] + "' name='" + name + "' value='" + value[i] + "'/>";
        }
        html += "</div>";
        return html;
    }

    public static String makeInnovationTable(User loggedUser) {
        String html = makeInnovationMenu(loggedUser);
        List<Innovation> innovations = null;
        if (loggedUser.getRole().equals(LoginType.EMPLOYEE)) {
            innovations = QuestionMethods.getInnovations(loggedUser);
        } else {
            innovations = QuestionMethods.getInnovations();
        }
        if (innovations.isEmpty()) {
            html += "<h2>Brak wprowadzonych innowacji</h2>";
        } else {
            html += "</br></br><div id='tableWrapper'>";
            html += "<table class='myTable'><thead>";
            html += "<tr><th>Nazwa Innowacji</th><th>Data innowacji</th><th>Kategoria</th><th>Akcje</th></tr></thead><tbody>";
            for (Innovation innovation : innovations) {
                html += "<tr><td>" + innovation.getAnswers().get(0).getContent() + "</td><td>" + sdf.format(innovation.getDate()) + "</td><td>" + innovation.getCategory().getDisplay() + "</td><td>";
                html += "<img src='images/pdf.png' id='pdf_" + innovation.getId() + "' class='ideaOption'  onclick='generatePdf(" + innovation.getId() + ")' title='Generuj PDF'/>";
                if (loggedUser.getRole().equals(LoginType.ADMIN)) {
                    html += "<img src='images/reject.png' id='delete_" + innovation.getId() + "' class='ideaOption'  onclick='deleteInnovation(" + innovation.getId() + ")' title='Usuń innowację'/>";
                }
                html += "</td></tr>";
            }
            html += "</tbody></table></div>";
        }
        return html;
    }

    public static String generatePDFForInnovation(Innovation innovation, User loggedUser, HttpServletRequest request) {
        String filename = "Raport_" + innovation.getAnswers().get(0).getContent() + ".pdf";
        String path = request.getServletContext().getRealPath("/");
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont("ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font catFont = new Font(bf, 18,
                Font.BOLD);
        Font subFont = new Font(bf, 16,
                Font.BOLD);
        Font normalFont = new Font(bf, 12);
        Font smallBold = new Font(bf, 12,
                Font.BOLD);
        Font smallerBold = new Font(bf, 10,
                Font.BOLD);
        Font normalSmallFont = new Font(bf, 10);
        Font biggerBold = new Font(bf, 14,
                Font.BOLD);
        try {
            Document document = new Document();
            File output = new File(path + filename);
            PdfWriter.getInstance(document, new FileOutputStream(output));
            document.open();
            document.addTitle(innovation.getAnswers().get(0).getContent());
            document.addSubject("Identyfikacja innowacji");
            document.addKeywords("Innowacja, raport");
            document.addAuthor(Common.getAuthorOfInnovation(innovation));

            Paragraph firstPage = new Paragraph();
            firstPage.add(new Paragraph("Raport identyfikacji innowacji: " + innovation.getAnswers().get(0).getContent(), catFont));
            firstPage.add(new Paragraph(innovation.getCategory().getDisplay(), subFont));
            addEmptyLine(firstPage, 1);
            firstPage.add(new Paragraph("Dla: " + innovation.getAnswers().get(1).getContent(), subFont));
            addEmptyLine(firstPage, 1);
            firstPage.add(new Paragraph("Innowacja sporządzona przez:" + Common.getAuthorOfInnovation(innovation), biggerBold));
            firstPage.add(new Paragraph("Data innowacji: " + sdf.format(innovation.getDate()), biggerBold));
            addEmptyLine(firstPage, 2);
            firstPage.add(new Paragraph("Raport wygenerowany przez: " + loggedUser.getName() + " " + loggedUser.getSurname(), biggerBold));
            addEmptyLine(firstPage, 8);
            firstPage.add(new Paragraph("Raport został wygenerowany z użyciem aplikacji", smallBold));
            firstPage.add(new Paragraph("S Z I P  -  System Zarządzania Innowacjami w Przedsiębiorstwie", biggerBold));
            document.add(firstPage);
            document.newPage();
            Paragraph page = new Paragraph();
            for (InnovationAnswer answer : innovation.getAnswers()) {
                String question = answer.getQuestion().getLabel();
                if (question.contains("<")) {
                    String toReplace = question.substring(question.indexOf("<"), question.lastIndexOf(">") + 1);

                    if (toReplace.contains("first")) {
                        question = question.replace(toReplace, innovation.getCategory().getConjunction1());
                    } else {
                        question = question.replace(toReplace, innovation.getCategory().getConjunction2());
                    }
                }
                Paragraph ans = new Paragraph(question, smallBold);
                String ansToPrint = answer.getContent();
                if (answer.getQuestion().getType().equals(InnovationType.RADIO) ||
                        answer.getQuestion().getType().equals(InnovationType.RADIO2) ||
                        answer.getQuestion().getType().equals(InnovationType.YES_NO)) {
                    ansToPrint = Common.getTypeOptions(answer.getQuestion().getType()).get(Integer.parseInt(ansToPrint));
                }
                ans.add(new Paragraph(ansToPrint, normalFont));
                if (answer.getQuestion().isAdditional() && answer.getAdditionalAnswer() != null && !answer.getAdditionalAnswer().equals("")) {
                    ans.add(new Paragraph(answer.getQuestion().getPlaceholder() + ": ", smallerBold));
                    ans.add(new Paragraph(answer.getAdditionalAnswer(), normalSmallFont));
                }
                ans.setKeepTogether(true);
                page.add(ans);

            }
            document.add(page);
            document.newPage();
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filename;
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}