package main.app.servlets;

import main.app.HtmlContent;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Darek on 2017-04-08.
 */
@WebServlet(name = "Logout", urlPatterns = {"/Logout"})
public class Logout extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession s = request.getSession();
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JSONObject json = new JSONObject();
        PrintWriter out = response.getWriter();


        String responseMessage = null;
        boolean success = false;
        Object data = null;
        s.invalidate();
        if(request.getSession(false) == null) {
            data = HtmlContent.makeLoginForm();
            success = true;
        }
        else
            responseMessage = "Problem z serwerem. Skontaktuj się z administratorem.";

        json.put("success",success);
        json.put("message",responseMessage);
        json.put("data",data);

        out.println(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
