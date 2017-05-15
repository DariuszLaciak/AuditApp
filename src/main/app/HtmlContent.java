package main.app;

import com.mchange.io.FileUtils;
import main.app.enums.LoginType;
import main.app.enums.QuestionCategory;
import main.app.orm.Answer;
import main.app.orm.Audit;
import main.app.orm.Question;
import main.app.orm.User;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;

/**
 * Created by Darek on 2017-03-10.
 */
public class HtmlContent {
    public static String makeButton(String nameToDisplay, String onclick){
        String html = "";
        html += "<input class='userMenuButton' type='button' value='"+nameToDisplay+"' onclick='"+onclick+"()'/>";
        return html;
    }

    public static String makeLoginForm(){
        String html = "";

        html += "<form id='loginPanel' >";
        html += "<input type='text' id='user_id' name='user_id' placeholder='Login' />";
        html += "<input type='password' id='user_password' name='user_password' placeholder='Hasło'/>";
        html += "<input type='submit' value='Zaloguj się'/>";
        html += "<div id='registerUser' class='registerNew link'>Zarejestruj się</div>";
        html += "</form>";

        return html;
    }

    public static String makeLoggedPanel(HttpSession session){
        String html = "";

        User loggedUser = (User) session.getAttribute("userData");
        String username = loggedUser.getUsername();

        html += "<div id='loggedPanel'>";
        html += "<div class='loggedIn'>Witaj "+username+"! </div>";
        html += "<div class='link logoutUser'>Wyloguj</div>";
        html += "</div>";

        return html;
    }

    public static String makeUserMenu(LoginType userType){
        String html = "";
        html += "<div id='userMenu'>";
        html += makeButton("Nowy audyt","newAudit");
        html += makeButton("Historia audytów","auditHistory");
        if(userType != LoginType.USER)
            html += makeButton("Zarządzaj pytaniami","manageQuestions");
        html += "</div>";


        return html;
    }

    public static String makeQuestionTable(List<Question> questions){
        String html = "<table>";
        html += "<tr><th>Id</th><th>Treść</th><th>Typ</th><th>Wartość TAK</th><th>Kategoria</th></tr>";
        for(Question q : questions){
            html += "<tr><td>"+q.getId()+"</td><td>"+q.getContent()+"</td><td>"+q.getType()+"" +
                    "</td><td>"+q.getYesValue()+"</td><td>"+q.getCategory()+"</td></tr>";
        }
        html += "</table>";
        return html;
    }

    public static String makeQuestions(List<Question> questions, HttpSession session) {

        String html = "<h3>1- Zdecydowanie nie 2- Nie 3- Raczej nie 4- Nie wiem  5- Raczej tak 6- Tak 7-Zdecydowanie tak</h3>";
        String buttonValue = "Następne pytania";
        if (questions.size() <= Constraints.NUMBER_OF_QUESTIONS_PER_PAGE) {
            buttonValue = "Zakończ audyt";
        }
        html += "<ol>";
        for (Question q : Common.getRandomQuestionsAndRemoveAskedFromSession(questions, session)) {
            html += "<li><span>"+q.getContent()+"</span>";
            html += makeLickertScale(q.getId());
            html += "</li>";
        }
        html += makeButton(buttonValue,"nextQuestions");
        html += "</ol>";
        return html;
    }

    private static String makeLickertScale(long id){
        String html ="<p>1 <input class='lickertRadio' type='radio' name='"+id+"_lickert' id='"+id+"_lickert' value='"+Constraints.LICKERT_1+"'/>";
        html +="2 <input class='lickertRadio' type='radio' name='"+id+"_lickert' id='"+id+"_lickert' value='"+Constraints.LICKERT_2+"'/>" ;
        html +="3 <input class='lickertRadio' type='radio' name='"+id+"_lickert' id='"+id+"_lickert' value='"+Constraints.LICKERT_3+"'/>" ;
        html +="4 <input class='lickertRadio' type='radio' name='"+id+"_lickert' id='"+id+"_lickert' value='"+Constraints.LICKERT_4+"'/>" ;
        html +="5 <input class='lickertRadio' type='radio' name='"+id+"_lickert' id='"+id+"_lickert' value='"+Constraints.LICKERT_5+"'/>" ;
        html +="6 <input class='lickertRadio' type='radio' name='"+id+"_lickert' id='"+id+"_lickert' value='"+Constraints.LICKERT_6+"'/>" ;
        html +="7 <input class='lickertRadio' type='radio' name='"+id+"_lickert' id='"+id+"_lickert' value='"+Constraints.LICKERT_7+"'/></p>" ;
        return html;
    }

    private static String makeYesNo(long id){
        String html = "<p>Tak <input class='yesNoRadio' type='radio' name = '"+id+"_yesNo' id='"+id+"_yesNo' value='"+Constraints.YES_VAL+"'/>";
        html += "Nie <input class='yesNoRadio' type='radio' name = '"+id+"_yesNo' id='"+id+"_yesNo' value='"+Constraints.NO_VAL+"'/>";
        return html;
    }

    public static String prepareResults(List<Answer> answers, Audit audit) {
        StringBuilder html = new StringBuilder("<div class='auditResults'>");
        int resultTotal = Math.round(Common.getResultFromAnswers(answers)*100);
        html.append(prepareAuditResultHeader(audit));
        html.append("<div class='mainResult'>Całkowity wynik: ").append(resultTotal).append(" % </div>");
        html.append(prepareSpiderChart(answers));
        for(QuestionCategory category : QuestionCategory.values()){
            if (!category.equals(QuestionCategory.GENERAL)) {
                html.append("<div class='otherResult'>" +
                        "<div class='otherResultHeader'>" + category.getVisible().toUpperCase() + "</div>" +
                        "<div class='otherResultDesc'>" + category.getDesription() + "</div>" +
                        "<div class='otherResultPercent'>Spełnione w ").append(Math.round(Common.getResultFromAnswersForLickert(answers, category, true) * 100)).append(" % </div>" +
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
}