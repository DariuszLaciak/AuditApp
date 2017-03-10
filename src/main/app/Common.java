package main.app;

import javax.servlet.http.HttpSession;

/**
 * Created by Darek on 2017-03-05.
 */
public class Common {
    public static boolean isSessionActive(HttpSession session){
        return session.getAttribute("name") != null;
    }
}
