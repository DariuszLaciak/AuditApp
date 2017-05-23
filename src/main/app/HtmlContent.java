package main.app;

import com.mchange.io.FileUtils;
import main.app.enums.LoginType;
import main.app.enums.QuestionCategory;
import main.app.enums.Status;
import main.app.orm.*;
import main.app.servlets.Login;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;

public class HtmlContent {
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
        html += "<div id='registerUser' class='registerNew link'>Zarejestruj się</div>";
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

        StringBuilder html = new StringBuilder("<h3>1- Zdecydowanie nie 2- Nie 3- Raczej nie 4- Nie wiem  5- Raczej tak 6- Tak 7-Zdecydowanie tak</h3>");
        String buttonValue = "Następne pytania";
        if (questions.size() <= Constraints.NUMBER_OF_QUESTIONS_PER_PAGE) {
            buttonValue = "Zakończ audyt";
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
            if (!category.equals(QuestionCategory.GENERAL)) {
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
        }
        html.append("</div>");
        html.append(HtmlContent.makeButton("Rozpocznij nowy audyt", "nextAudit"));
        return html.toString();
    }

    private static String prepareAuditResultHeader(Audit audit) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String html = "<div class='auditResultHeader'>";
        html += "<div class='auditedCompany'>" +
                "<span class='importantSpan'>Audyt innowacji firmy: " + audit.getName() + "</span>" +
                "<span>Data przeprowadzenia audytu: " + sdf.format(audit.getAuditDate()) + "</span></div>";
        html += "<div class='auditorData'>Audytor: " + audit.getAuditor().getName() + " " + audit.getAuditor().getSurname() + "</div>";
        html += "</div>";

        return html;
    }

    private static String prepareSpiderChart(List<Answer> answers) {
        String html = "";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (QuestionCategory qc : QuestionCategory.values()) {
            if (!qc.equals(QuestionCategory.GENERAL)) {
                int result = Math.round(Common.getResultFromAnswersForLickert(answers, qc, false) / 3);
                dataset.addValue(result, "wynik", qc.getVisible());
                dataset.addValue(10, "max10", qc.getVisible());
                dataset.addValue(8, "max8", qc.getVisible());
                dataset.addValue(6, "max6", qc.getVisible());
                dataset.addValue(4, "max4", qc.getVisible());
                dataset.addValue(2, "max2", qc.getVisible());
            }
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

    public static String getAuditHitory(List<Audit> audits) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        StringBuilder html = new StringBuilder("<div id='tableWrapper'><table class='myTable'>");
        html.append("<thead><tr><th>Data audytu</th><th>Wynik</th><th></th></tr></thead><tbody>");
        for (Audit audit : audits) {
            html.append("<tr>");
            html.append("<td>").append(sdf.format(audit.getAuditDate())).append("</td>");
            html.append("<td>").append(audit.getResult().getResultValue()).append("</td>");
            html.append("<td>").append(makeButton("Raport", "makeReport", String.valueOf(audit.getId()))).append("</td>");
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

    public static String displayIdeas(List<Idea> ideas, LoginType userType) {
        String html = "";
        if (!ideas.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
            int numberOfTDs = 5;
            html += "<div id='tableWrapper'>";
            html += "<table class='myTable'><thead>";
            html += "<tr><th class='widestCol'>Tutuł pomysłu</th><th>Kategoria</th><th>Status</th><th>Data dodania</th>";
            if (!userType.equals(LoginType.EMPLOYEE)) {
                html += "<th>Autor</th>";
                numberOfTDs++;
            }
            html += "<th>Data decyzji</th>";
            if (!userType.equals(LoginType.EMPLOYEE)) {
                html += "<th>Akcje</th>";
                numberOfTDs++;
            }
            html += "</tr></thead><tbody>";
            for (Idea i : ideas) {
                html += "<tr class='tableTR'><td onclick='moreInfoIdea(" + i.getId() + ")' title='Szczegółowy opis pomysłu' class='widestCol moreInfo'>" + i.getName() + "</td><td  class='widestCol'>" + i.getType().getValue() + "</td><td>" + i.getStatus().getValue() + "</td><td>" + sdf.format(i.getAddedDate()) + "</td>";
                if (!userType.equals(LoginType.EMPLOYEE)) {
                    html += "<td>" + i.getEmployee().getName() + " " + i.getEmployee().getName() + "</td>";
                }
                html += "<td>";
                if (i.getActionDate() != null) {
                    html += sdf.format(i.getActionDate());
                } else {
                    html += "BRAK";
                }
                html += "</td>";
                if (!userType.equals(LoginType.EMPLOYEE)) {
                    html += "<td>";
                    if (i.getStatus().equals(Status.PENDING)) {
                        html += "<img src='images/accept.png' id='accept_" + i.getId() + "' class='ideaOption' onclick='acceptIdea(" + i.getId() + ")' title='Zaakceptuj'/>" +
                                "<img src='images/reject.png' id='reject_" + i.getId() + "' class='ideaOption'  onclick='rejectIdea(" + i.getId() + ")' title='Odrzuć'/>";
                    } else {
                        html += "<img src='images/more.png' id='decisionIdea_" + i.getId() + "' class='ideaOption' onclick='decisionIdea(" + i.getId() + ")' title='Pokaż komentarz decyzji'/>";
                    }
                    html += "</td>";
                }
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
}