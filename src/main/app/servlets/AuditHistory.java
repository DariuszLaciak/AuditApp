package main.app.servlets;

import main.app.Common;
import main.app.HtmlContent;
import main.app.enums.LoginType;
import main.app.orm.Audit;
import main.app.orm.User;
import main.app.orm.methods.AuditMethods;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by darek on 17.05.17.
 */
@WebServlet(name = "AuditHistory", urlPatterns = {"/AuditHistory"})
public class AuditHistory extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession s = request.getSession();
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JSONObject json = new JSONObject();
        PrintWriter out = response.getWriter();
        String responseMessage = "";
        Object data = null;
        boolean success = true;
        User loggedUser = (User) s.getAttribute("userData");
        List<Audit> allAudits = AuditMethods.getAudits();
        if (!loggedUser.getRole().equals(LoginType.EMPOLYEE)) {
            switch (action) {
                case "table":
                    data = HtmlContent.getAuditHitory(allAudits);
                    break;
                case "report":
                    String auditId = request.getParameter("auditId");
                    try {
                        data = HtmlContent.getReport(Common.getAudit(allAudits, Long.parseLong(auditId)));
                    } catch (NumberFormatException ex) {
                        success = false;
                        responseMessage = "Błąd serwera. Skontaktuj się z administratorem!";
                    }
                    break;
            }
        } else {
            success = false;
            responseMessage = "Nie masz uprawnień";
        }


        json.put("success", success);
        json.put("message", responseMessage);
        json.put("data", data);

        out.println(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
