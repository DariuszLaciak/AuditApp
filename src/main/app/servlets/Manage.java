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
        Session session;
            switch (action) {
                case "getUsers":

                    data = HtmlContent.makeUsersForm(allUsers);
                    break;
                case "getManagers":
                    data = HtmlContent.getManagersSelect(UserMethods.getManagers());
                    break;
                case "addUser":
                    String username = request.getParameter("username");
                    String name = request.getParameter("name");
                    String surname = request.getParameter("surname");
                    String mail = request.getParameter("mail");
                    String role = request.getParameter("role");
                    String active = request.getParameter("active");
                    String manager = request.getParameter("manager");

                    long managerId = Long.parseLong(manager);
                    boolean activeB = Boolean.valueOf(active);
                    LoginType userType = LoginType.valueOf(role.toUpperCase());

                    String password = Common.stripPolishCharacters(name.toLowerCase().charAt(0) + surname.toLowerCase());

                    User newUser = new User(userType, username, password, name, surname, mail, activeB);


                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    User managerO = session.load(User.class, managerId);
                    newUser.setManager(managerO);

                    session.save(newUser);

                    session.getTransaction().commit();
                    session.close();

                    responseMessage = "Pomyślnie dodano użytkownika";

                    data = HtmlContent.makeUsersForm(allUsers);

                    break;
                case "changePass":
                    long userId = Long.parseLong(request.getParameter("userId"));
                    String newPassword = request.getParameter("password");
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    User editedUser = session.load(User.class, userId);
                    editedUser.setPassword(newPassword);

                    session.update(editedUser);

                    session.getTransaction().commit();
                    session.close();

                    responseMessage = "Hasło użytkownika zostało zmienione.";
                    break;
                case "editUser":
                    userId = Long.parseLong(request.getParameter("userId"));

                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    editedUser = session.load(User.class, userId);
                    if (!loggedUser.getRole().equals(LoginType.ADMIN) && loggedUser.getId() != editedUser.getId()) {
                        responseMessage = "Brak uprawnień do edycji profilu";
                        success = false;
                        break;
                    } else {
                        data = HtmlContent.makeEditUserHtml(editedUser, loggedUser);
                    }
                    session.getTransaction().commit();
                    session.close();
                    break;
                case "confirmEditUser":
                    userId = Long.parseLong(request.getParameter("userId"));
                    name = request.getParameter("name");
                    surname = request.getParameter("surname");
                    mail = request.getParameter("email");
                    role = request.getParameter("type");
                    active = request.getParameter("active");
                    password = request.getParameter("password");
                    boolean changePass = Boolean.valueOf(request.getParameter("changePass"));
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    editedUser = session.load(User.class, userId);
                    editedUser.setName(name);
                    editedUser.setSurname(surname);
                    editedUser.setEmail(mail);
                    if (loggedUser.getRole().equals(LoginType.ADMIN)) {
                        activeB = Boolean.valueOf(active);
                        userType = LoginType.valueOf(role.toUpperCase());
                        editedUser.setRole(userType);
                        editedUser.setActive(activeB);
                    }
                    if (changePass) {
                        editedUser.setPassword(password);
                    }

                    session.update(editedUser);

                    session.getTransaction().commit();
                    session.close();

                    if (loggedUser.getRole().equals(LoginType.ADMIN)) {
                        data = HtmlContent.makeUsersForm(UserMethods.getUsers());
                    }

                    responseMessage = "Pomyślnie zmieniono profil";
                    break;
                case "deleteUser":
                    userId = Long.parseLong(request.getParameter("userId"));
                    if (loggedUser.getRole().equals(LoginType.ADMIN)) {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        if (!session.getTransaction().isActive())
                            session.beginTransaction();

                        editedUser = session.load(User.class, userId);
                        editedUser.getEmployees().clear();
                        editedUser.getAudits().clear();
                        session.delete(editedUser);

                        session.getTransaction().commit();
                        session.close();
                        data = HtmlContent.makeUsersForm(UserMethods.getUsers());
                        responseMessage = "Pomyślnie usunięto użytkownika z bazy";
                    } else {
                        responseMessage = "Brak uprawnień";
                        success = false;
                    }
                    break;
            }

        json.put("success", success);
        json.put("message", responseMessage);
        json.put("data", data);

        out.println(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
