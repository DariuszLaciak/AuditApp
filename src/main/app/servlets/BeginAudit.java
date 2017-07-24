package main.app.servlets;

import main.app.enums.AuditType;
import main.app.orm.Audit;
import main.app.orm.HibernateUtil;
import main.app.orm.User;
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
import java.util.Date;

/**
 * Created by Darek on 2017-04-08.
 */
@WebServlet(name = "BeginAudit", urlPatterns = {"/BeginAudit"})
public class BeginAudit extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession s = request.getSession();
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JSONObject json = new JSONObject();
        PrintWriter out = response.getWriter();
        User auditor = (User) s.getAttribute("userData");

        String type = request.getParameter("type");
        AuditType auditType = AuditType.valueOf(type.toUpperCase());
        String responseMessage = "";
        boolean success = false;
        Object data = null;
        if (auditor != null) {
            Audit newAudit = new Audit();
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            if (!session.getTransaction().isActive())
                session.beginTransaction();
            newAudit.setAuditor(auditor);
            newAudit.setAuditDate(new Date());
            newAudit.setType(auditType);
            long id = (long) session.save(newAudit);
            s.setAttribute("auditId", id);
            s.setAttribute("notAskedQuestions", null);
            s.setAttribute("startNumber", null);

            responseMessage = "";
            success = true;

            session.getTransaction().commit();
            session.close();
        }


        json.put("success", success);
        json.put("message", responseMessage);

        out.println(json);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
