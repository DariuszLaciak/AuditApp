package main.app;

import com.mchange.io.FileUtils;
import main.app.enums.*;
import main.app.orm.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
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

    public static String makeLoginForm() {
        String html = "";

        html += "<form id='loginPanel' >";
        html += "<input type='text' id='user_id' name='user_id' placeholder='Login' />";
        html += "<input type='password' id='user_password' name='user_password' placeholder='Hasło'/>";
        html += "<input type='submit' value='Zaloguj się'/>";
        html += "<div id='registerUser' class='registerNew link'>Zapomniałem hasło się</div>";
        html += "</form>";

        return html;
    }

    public static String makeLoggedPanel(HttpSession session) {
        String html = "";

        User loggedUser = (User) session.getAttribute("userData");
        String username = loggedUser.getUsername();

        html += "<div id='loggedPanel'>";
        html += "<div class='loggedIn'>Witaj " + username + "! </div>";
        html += "<div class='link logoutUser'>Wyloguj</div>";
        html += "</div>";

        return html;
    }

    public static String makeUserMenu(LoginType userType) {
        String html = "";
        html += "<div id='userMenu'>";
        if (!userType.equals(LoginType.EMPLOYEE)) {
            html += makeButton("Nowy audyt", "newAudit");
            html += makeButton("Historia audytów", "auditHistory");
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

    public static String makeQuestionTable(List<Question> questions) {
        StringBuilder html = new StringBuilder("<div id='tableWrapper'><table class='myTable'>");
        html.append("<thead><tr><th>Id</th><th class='widestCol'>Treść</th><th>Typ</th><th>Wartość TAK</th><th>Kategoria</th></tr></thead><tbody>");
        for (Question q : questions) {
            html.append("<tr><td>").append(q.getId()).append("</td><td class='widestCol'>").append(q.getContent()).append("</td><td>").append(q.getType()).append("").append("</td><td>").append(q.getYesValue()).append("</td><td>").append(q.getCategory()).append("</td></tr>");
        }
        html.append("</tbody></table></div>");
        return html.toString();
    }

    public static String makeQuestions(List<Question> questions, HttpSession session) {

        StringBuilder html = new StringBuilder("");
        String buttonValue = "Następne pytania";
        if (questions.size() <= Constraints.NUMBER_OF_QUESTIONS_PER_PAGE) {
            buttonValue = "Analiza SWOT";
        }
        html.append("<ol>");
        for (Question q : Common.getRandomQuestionsAndRemoveAskedFromSession(questions, session)) {
            html.append("<li><span class='questionLabel'>").append(q.getContent()).append("</span>");
            html.append(makeLickertScale(q.getId()));
            html.append("</li>");
        }
        html.append(makeButton(buttonValue, "nextQuestions"));
        html.append("</ol>");
        return html.toString();
    }

    //<input type="radio" id="option-one" name="selector"><label for="option-one">Tak</label><input type="radio" id="option-two" name="selector"><label for="option-two">Raczej tak</label><input type="radio" id="option-three" name="selector"><label for="option-three">Zdecydowanie tak</label>
    private static String makeLickertScale(long id) {
        String html = "<div class='radio-group'><input class='lickertRadio' type='radio' id='" + id + "_lickert_1' name='" + id + "_lickert' value='" + Constraints.LICKERT_1 + "'><label for='" + id + "_lickert_1'>Zdecydowanie nie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_2' name='" + id + "_lickert' value='" + Constraints.LICKERT_2 + "'><label for='" + id + "_lickert_2'>Raczej nie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_3' name='" + id + "_lickert' value='" + Constraints.LICKERT_3 + "'><label for='" + id + "_lickert_3'>Nie</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_4' name='" + id + "_lickert' value='" + Constraints.LICKERT_4 + "'><label for='" + id + "_lickert_4'>Obojętne</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_5' name='" + id + "_lickert' value='" + Constraints.LICKERT_5 + "'><label for='" + id + "_lickert_5'>Tak</label>";
        html += "<input class='lickertRadio' type='radio' id='" + id + "_lickert_6' name='" + id + "_lickert' value='" + Constraints.LICKERT_6 + "'><label for='" + id + "_lickert_6'>Raczej tak</label>";
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

        html.append(makeSwotResult(audit));
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
        spiderWebPlot.setLabelGenerator(new StandardCategoryItemLabelGenerator());
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

    private static String makeSwotResult(Audit audit) {
        String html = "<h2>Wynik analizy SWOT</h2>";
        SwotResult result = Common.getSwotResultDescription(audit.getSwot());
        html += "<div class='otherResult bold'>Zalecana strategia: " + result.getStrategy() + "</div>";
        html += "<div class='otherResultDesc'>" + result.getDescription() + "</div>";
        return html;
    }

    public static String getAuditHitory(List<Audit> audits) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        StringBuilder html = new StringBuilder("<div id='tableWrapper'><table class='myTable'>");
        html.append("<thead><tr><th>Data audytu</th><th>Wynik</th><th></th></tr></thead><tbody>");
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
                "<th>Typ</th><th>Aktywny</th><th>Akcje</th>";
        html += "</tr></thead><tbody>";
        for (User user : users) {
            html += "<tr class='tableTR'><td>" + user.getUsername() + "</td><td>" + user.getName() + "</td><td>" + user.getSurname() + "</td>" +
                    "<td>" + user.getEmail() + "</td><td>" + sdf.format(user.getAccountCreated()) + "</td><td>" + user.getRole().getDisplayName() + "</td>" +
                    "<td>" + (user.isActive() ? "tak" : "nie") + "</td><td>";
            html += "<img src='images/edit.png' title='Edytuj użytkownika' class='ideaOption' onclick='editUser(" + user.getId() + ")' />";
            html += "<img src='images/reject.png' title='Usuń użytkownika' class='ideaOption' onclick='deleteUser(" + user.getId() + ")' />";
            html += "</td></tr>";
        }
        html += "</tbody></table></div>";

        return html;
    }

    public static String displayIdeas(List<Idea> ideas, User user) {
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
            html += "<th>Data decyzji</th>";
            html += "<th>Akcje</th>";
            html += "</tr></thead><tbody>";
            for (Idea i : ideas) {
                html += "<tr class='tableTR'><td onclick='moreInfoIdea(" + i.getId() + ")' title='Szczegółowy opis pomysłu' class='widestCol moreInfo'>" + i.getName() + "</td><td  class='widestCol'>" + i.getType().getValue() + "</td><td>" + i.getStatus().getValue() + "</td><td>" + sdf.format(i.getAddedDate()) + "</td>";
                if (!user.getRole().equals(LoginType.EMPLOYEE)) {
                    html += "<td>" + i.getEmployee().getName() + " " + i.getEmployee().getName() + "</td>";
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
                    if (i.getStatus().equals(Status.PENDING)) {
                        html += "<img src='images/accept.png' id='accept_" + i.getId() + "' class='ideaOption' onclick='acceptIdea(" + i.getId() + ")' title='Zaakceptuj'/>" +
                                "<img src='images/reject.png' id='reject_" + i.getId() + "' class='ideaOption'  onclick='rejectIdea(" + i.getId() + ")' title='Odrzuć'/>";
                    }
                }
                if (!i.getStatus().equals(Status.PENDING)) {
                    html += "<img src='images/more.png' id='decisionIdea_" + i.getId() + "' class='ideaOption' onclick='decisionIdea(" + i.getId() + ")' title='Pokaż komentarz decyzji'/>";
                }
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

    public static String makeMoreInfoIdea(Idea i) {
        String html = "";
        html += "<div id='ideaOpinionAuthor'>";
        html += "Autor decyzji: " + i.getOpinion().getAuthor().getName() + " " + i.getOpinion().getAuthor().getSurname();
        html += "</div><div id='ideaOpinionContent'>";
        html += i.getOpinion().getContent();
        html += "</div>";
        return html;
    }

    public static String makeSwotTables(List<SwotAlternatives> list, long auditId) {
        String html = "<h2>Analiza SWOT</h2>";
        for (SwotCategory category : SwotCategory.values()) {
            html += "<div class='row style-select'>";
            html += "<div class='tableHeader'>" + category.getValue() + "</div>";
            html += "<div class='col-md-12'>";
            html += "<div class='subject-info-box-1'>";
            html += "<label>Dostępne</label>";
            html += "<select multiple id='source_" + category.toString().toLowerCase() + "'>";
            for (SwotAlternatives sw : list) {
                if (sw.getCategory().equals(category)) {
                    html += "<option value='swot_" + sw.getId() + "'>" + sw.getText() + "</option>";
                }
            }
            html += "</select>";
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

        html += makeButton("Zapisz", "saveSwot", String.valueOf(auditId));


        return html;
    }

    private static String makeJS(String script) {
        String html = "<script type='application/javascript'>$(function($) {";
        html += script;
        html += "});</script>";

        return html;
    }

    public static String makeOverviewContent(List<Audit> audits) {
        String html = "";
        if (audits.size() >= 2) {
            html += makeOverviewChart(audits);
            html += makeSwotOverview(audits);
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

    private static String makeTimeChart(TimeSeriesCollection collection, Stroke lineStroke, String title, boolean showLegend) {
        String html = "";
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Data", "Wynik [%]", collection, showLegend, false, false);
        chart.setBackgroundPaint(null);
        if (showLegend) {
            chart.getLegend().setBackgroundPaint(Color.lightGray);
        }
        chart.getTitle().setPaint(Color.WHITE);
        chart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.WHITE);
        chart.getXYPlot().getDomainAxis().setLabelPaint(Color.WHITE);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < chart.getXYPlot().getSeriesCount(); ++i) {
            renderer.setSeriesStroke(i, lineStroke);
            renderer.setSeriesShapesFilled(i, true);
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

    private static String makeSwotOverview(List<Audit> audits) {
        String html = "<h1>Zestawienie wyników analizy SWOT</h1>";
        boolean areSwotChanges = false;
        Audit firstAudit = audits.get(0);
        Audit lastAudit = audits.get(audits.size() - 1);

        if (firstAudit.getSwot().isEmpty() && lastAudit.getSwot().isEmpty()) {
            html += "<h2>Brak danych SWOT dla podanego okresu</h2>";
        } else
            for (SwotCategory cat : SwotCategory.values()) {
                html += "<h2>" + cat.getValue() + "</h2><ul>";

                for (SwotAlternatives sw : firstAudit.getSwot()) {
                    if (sw.getCategory().equals(cat)) {
                        areSwotChanges = true;
                        if (lastAudit.getSwot().contains(sw)) {
                            html += "<li>" + sw.getText() + "</li>";
                        } else {
                            if (cat.equals(SwotCategory.OPPORTUNITES) || cat.equals(SwotCategory.STRENGHTS)) {
                                html += "<li>" + printIconOverview(false, false) + sw.getText() + "</li>";
                            } else {
                                html += "<li>" + printIconOverview(false, true) + sw.getText() + "</li>";
                            }
                        }
                    }
                }
                for (SwotAlternatives sw : lastAudit.getSwot()) {
                    if (sw.getCategory().equals(cat)) {
                        areSwotChanges = true;
                        if (!firstAudit.getSwot().contains(sw)) {
                            if (cat.equals(SwotCategory.OPPORTUNITES) || cat.equals(SwotCategory.STRENGHTS)) {
                                html += "<li>" + printIconOverview(true, true) + sw.getText() + "</li>";
                            } else {
                                html += "<li>" + printIconOverview(true, false) + sw.getText() + "</li>";
                            }
                        }
                    }
                }
                if (!areSwotChanges) {
                    html += "<li>BRAK ZMIAN</li>";
                }
                html += "</ul>";
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
}