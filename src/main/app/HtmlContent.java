package main.app;

import main.app.enums.LoginType;
import main.app.enums.QuestionCategory;
import main.app.enums.QuestionType;
import main.app.orm.Answer;
import main.app.orm.Question;
import main.app.orm.User;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static String makeQuestions(List<Question> questions, QuestionCategory category, boolean isLast){
        String html = "<h1>"+category.getVisible()+"</h1>";
        if(!category.equals(QuestionCategory.GENERAL)){
            html += "<h3>1- Zdecydowanie nie 2- Nie 3- Raczej nie 4- Nie wiem  5- Raczej tak 6- Tak 7-Zdecydowanie tak</h3>";
        }
        html += "<ol>";
        for(Question q : questions){
            html += "<li><span>"+q.getContent()+"</span>";
            if(q.getType().equals(QuestionType.YES_NO)){
                html += makeYesNo(q.getId());
                if(q.getYesValue())
                    html += "<input class='yesValue' type='number' id='"+q.getId()+"_yesVal' placeholder='Ile'/>";
                html += "</p>";
            }
            else {
                html += makeLickertScale(q.getId());
            }
            html += "</li>";
        }
        String buttonValue = "Następna kategoria pytań";
        if(isLast){
            buttonValue = "Zakończ audyt";
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

    public static String prepareResults(List<Answer> answers){
        StringBuilder html = new StringBuilder("<div class='auditResults'>");
        int resultTotal = Math.round(Common.getResultFromAnswers(answers)*100);
        int yesNo_result = Math.round(Common.getResultFromAnswersForYesNo(answers)*100);

        html.append("<div class='mainResult'>Całkowity wynik: ").append(resultTotal).append(" % </div>");
        html.append("<div class='otherResult'>Wynik w grupie \"").append(QuestionCategory.GENERAL.getVisible()).append("\": ").append(yesNo_result).append(" % </div>");

        for(QuestionCategory category : QuestionCategory.values()){
            if(!category.equals(QuestionCategory.GENERAL))
                html.append("<div class='otherResult'>Wynik w grupie \"").append(category.getVisible()).append("\": ").append(Math.round(Common.getResultFromAnswersForLickert(answers, category) * 100)).append(" % </div>");
        }
        html.append("</div>");
        html.append(HtmlContent.makeButton("Rozpocznij nowy audyt", "nextAudit"));
        return html.toString();
    }
}
