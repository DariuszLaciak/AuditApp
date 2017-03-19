package main.app;

import main.app.orm.User;

import javax.servlet.http.HttpSession;

/**
 * Created by Darek on 2017-03-10.
 */
public class HtmlContent {
    public static String makeButton(String nameToDisplay){
        return "Prtzycisk" + nameToDisplay;
    }

    public static String makeLoginForm(){
        String html = "";

        html += "<form id='loginPanel'>";
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
        html += "<div class='link registerNew'>Wyloguj</div>";
        html += "</div>";

        return html;
    }
}
