package main.app;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import main.app.orm.HibernateUtil;
import main.app.orm.User;
import org.hibernate.Session;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.query.Query;
import org.json.simple.JSONObject;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Login extends HttpServlet {
    HttpSession s;
    Session session;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        s = request.getSession();
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JSONObject json = new JSONObject();
        PrintWriter out = response.getWriter();
        String html = "";
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        String responseMessage = "";
        boolean success = false;
        Object data = null;

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        if(!session.getTransaction().isActive())
            session.beginTransaction();

        Query q = session.createQuery("from User where username=:user AND password=:pass").setParameter("user", login).setParameter("pass",password);

        List<?> list = new ArrayList<>();

        try{
            list = q.list();
        }
        catch(JDBCConnectionException e){
            responseMessage = "Problem z serwerem. Spróbuj ponownie się zalogować.";
        }

        if(list.isEmpty()){
            responseMessage = "Zła nazwa użytkownika lub hasło!";
        }
        else {
            responseMessage = null;
            success = true;
            User loggedUser = (User)list.get(0);
            s.setAttribute("userData",loggedUser);
            s.setAttribute("userId",loggedUser.getId());

            data = HtmlContent.makeLoggedPanel(s);
        }

        session.getTransaction().commit();
        session.close();
        json.put("success",success);
        json.put("message",responseMessage);
        json.put("data",data);

        out.println(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
