package main.app.servlets;

import main.app.Common;
import main.app.HtmlContent;
import main.app.enums.LoginType;
import main.app.orm.HibernateUtil;
import main.app.orm.User;
import main.app.orm.methods.UserMethods;
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
import java.util.List;

/**
 * Created by darek on 29.05.17.
 */
@WebServlet(name = "Manage", urlPatterns = {"/Manage"})
public class Manage extends HttpServlet {
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
        List<User> allUsers = UserMethods.getUsers();
        if (loggedUser.getRole().equals(LoginType.ADMIN)) {
            switch (action) {
                case "getUsers":

                    data = HtmlContent.makeUsersForm(allUsers);
                    break;
                case "addUser":
                    String username = request.getParameter("username");
                    String name = request.getParameter("name");
                    String surname = request.getParameter("surname");
                    String mail = request.getParameter("mail");
                    String role = request.getParameter("role");
                    String active = request.getParameter("active");

                    boolean activeB = Boolean.valueOf(active);
                    LoginType userType = LoginType.valueOf(role.toUpperCase());

                    String password = Common.stripPolishCharacters(name.toLowerCase().charAt(0) + surname.toLowerCase());

                    User newUser = new User(userType, username, password, name, surname, mail, activeB);

                    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    session.save(newUser);

                    session.getTransaction().commit();
                    session.close();

                    responseMessage = "Pomyślnie dodano użytkownika";

                    data = HtmlContent.makeUsersForm(allUsers);

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
