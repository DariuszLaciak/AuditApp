package main.app;

/**
 * Created by Darek on 2017-03-10.
 */
public class HtmlContent {
    public static String makeButton(String nameToDisplay){
        return "Prtzycisk" + nameToDisplay;
    }

    public static String makeLoginForm(){
        String html = "";

        html += "<form id='loginPanel' action='Login'>";
        html += "<input type='text' id='user_id' name='user_id' placeholder='Your login' />";
        html += "<input type='password' id='user_password' name='user_password' placeholder='Your password'/>";
        html += "<input type='submit' value='Log in'/>";
        html += "</div>";

        return html;
    }
}
