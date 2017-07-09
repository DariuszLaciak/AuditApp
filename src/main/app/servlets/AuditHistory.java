package main.app.servlets;

import main.app.Common;
import main.app.HtmlContent;
import main.app.enums.AuditType;
import main.app.enums.LoginType;
import main.app.orm.Audit;
import main.app.orm.HibernateUtil;
import main.app.orm.Swot;
import main.app.orm.User;
import main.app.orm.methods.AuditMethods;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        String data = null;
        boolean success = true;
        User loggedUser = (User) s.getAttribute("userData");
        List<Audit> allAudits = AuditMethods.getAudits();
        List<Swot> allSwots = AuditMethods.getSwots();
        List<Audit> generalAudits = Common.getAuditOfType(allAudits, AuditType.GENERAL);
        List<Audit> detailedAudits = Common.getAuditOfType(allAudits, AuditType.DETAILED);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if (!loggedUser.getRole().equals(LoginType.EMPLOYEE)) {
            switch (action) {
                case "table":
                    data = HtmlContent.getAuditHitory(generalAudits, true);
                    data += HtmlContent.getAuditHitory(detailedAudits, false);
                    data += HtmlContent.getSwotHistory(allSwots);
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
                case "overview":
                    String startDate = request.getParameter("startDate");
                    String endDate = request.getParameter("endDate");
                    Date start, end;
                    try {
                        start = sdf.parse(startDate);
                        end = sdf.parse(endDate);
                    } catch (ParseException e) {
                        success = false;
                        responseMessage = "Zły format daty!";
                        break;
                    }
                    Calendar c = Calendar.getInstance();
                    c.setTime(end);
                    c.add(Calendar.DATE, 1);

                    end = c.getTime();
                    List<Audit> auditsToMakeOverview = Common.getAuditsBetweendDates(generalAudits, start, end);
                    List<Swot> swotsToMakeOverview = Common.getSwotsBetweendDates(allSwots, start, end);
                    data = HtmlContent.makeOverviewContent(auditsToMakeOverview, swotsToMakeOverview);
                    break;
                case "swotReport":
                    long swotId = Long.parseLong(request.getParameter("swotId"));
                    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    Swot swot = session.load(Swot.class, swotId);

                    session.getTransaction().commit();
                    session.close();
                    data = HtmlContent.makeSwotResult(swot);
                    data += HtmlContent.makeButton("Wróć do historii", "getAuditHistory");
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
