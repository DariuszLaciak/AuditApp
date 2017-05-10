package main.app.servlets;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        String responseMessage = "";
        boolean success = false;
        Object data = null;

        String companyName = request.getParameter("companyName");
        String companyREGON = request.getParameter("companyREGON");
        String companyKRS = request.getParameter("companyKRS");
        String companyYear = request.getParameter("companyYear");
        String companyEmployees = request.getParameter("companyEmployees");

        Date companyEstrabished = null;
        SimpleDateFormat sdl = new SimpleDateFormat("YYYY");
        try {
            companyEstrabished = sdl.parse(companyYear);
        } catch (ParseException e) {
            responseMessage = "Zły format roku założenia firmy (RRRR)";
        }

        int employees = 0;
        try {
            employees = Integer.parseInt(companyEmployees);
        }
        catch (NumberFormatException e){
            responseMessage = "Liczba pracowników musi być liczbą!";
        }



        if(employees > 0 && companyEstrabished!= null && auditor != null) {
            Audit newAudit = new Audit(companyName, companyREGON, companyKRS, companyEstrabished, employees);
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            if(!session.getTransaction().isActive())
                session.beginTransaction();
            newAudit.setAuditor(auditor);
            long id = (long) session.save(newAudit);
            s.setAttribute("auditId",id);

            responseMessage = "";
            success = true;

            session.getTransaction().commit();
            session.close();
        }


        json.put("success",success);
        json.put("message",responseMessage);

        out.println(json);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
