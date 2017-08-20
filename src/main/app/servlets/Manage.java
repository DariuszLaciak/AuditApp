package main.app.servlets;

import main.app.Common;
import main.app.HtmlContent;
import main.app.enums.LoginType;
import main.app.orm.*;
import main.app.orm.methods.AuditMethods;
import main.app.orm.methods.OtherMethods;
import main.app.orm.methods.UserMethods;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
                    List<User> managers = UserMethods.getManagers();
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    editedUser = session.load(User.class, userId);
                    if (!loggedUser.getRole().equals(LoginType.ADMIN) && loggedUser.getId() != editedUser.getId()) {
                        responseMessage = "Brak uprawnień do edycji profilu";
                        success = false;
                        break;
                    } else {
                        data = HtmlContent.makeEditUserHtml(editedUser, loggedUser, managers);
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
                    managerId = Long.parseLong(request.getParameter("manager"));
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

                    if (managerId == -1) {
                        editedUser.setManager(null);
                    } else {
                        editedUser.setManager(session.load(User.class, managerId));
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
                case "getSources":
                    List<Source> sources = AuditMethods.getSources();
                    data = HtmlContent.makeSourcesTable(sources);
                    break;
                case "getImpediments":
                    List<Impediment> impediments = AuditMethods.getImpediments();
                    data = HtmlContent.makeImpedimentsTable(impediments);
                    break;
                case "saveSource":
                    String text = request.getParameter("text");
                    String description = request.getParameter("description");
                    String isInternal = request.getParameter("isInternal");
                    boolean internal = !isInternal.equals("0");
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    Source source = new Source(text, description, internal);
                    session.save(source);

                    session.getTransaction().commit();
                    session.close();

                    sources = AuditMethods.getSources();
                    data = HtmlContent.makeSourcesTable(sources);
                    responseMessage = "Pomyślnie dodano źródło";
                    break;
                case "saveImpediment":
                    String impedimentText = request.getParameter("text");
                    String[] advices = request.getParameterValues("advices[]");

                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    Impediment impediment = new Impediment(impedimentText);
                    session.save(impediment);
                    for (String advice : advices) {
                        ImpedimentAdvice adviceObj = new ImpedimentAdvice(advice);
                        adviceObj.setImpediment(impediment);
                        session.save(adviceObj);
                    }

                    session.getTransaction().commit();
                    session.close();

                    impediments = AuditMethods.getImpediments();
                    data = HtmlContent.makeImpedimentsTable(impediments);
                    responseMessage = "Pomyślnie dodano barierę";
                    break;
                case "getAdvices":
                    long impedimentId = Long.parseLong(request.getParameter("impedimentId"));
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    Impediment imp = session.load(Impediment.class, impedimentId);

                    session.getTransaction().commit();
                    session.close();

                    data = HtmlContent.makeAdviceHtml(imp);
                    break;
                case "deleteImpediment":
                    impedimentId = Long.parseLong(request.getParameter("impedimentId"));
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    Impediment impedimentToDelete = session.load(Impediment.class, impedimentId);
                    session.delete(impedimentToDelete);

                    session.getTransaction().commit();
                    session.close();

                    responseMessage = "Pomyślnie usunięto barierę";
                    data = HtmlContent.makeImpedimentsTable(AuditMethods.getImpediments());
                    break;
                case "deleteSource":
                    long sourceId = Long.parseLong(request.getParameter("sourceId"));
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    Source sourceToDelete = session.load(Source.class, sourceId);
                    session.delete(sourceToDelete);

                    session.getTransaction().commit();
                    session.close();

                    responseMessage = "Pomyślnie usunięto źródło";
                    data = HtmlContent.makeSourcesTable(AuditMethods.getSources());
                    break;
                case "getHelp":
                    data = HtmlContent.makeHelp(OtherMethods.getHelp(), loggedUser.getRole());
                    break;
                case "addHelp":
                    String word = request.getParameter("word");
                    String content = request.getParameter("content");

                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    HelpDictionary helpDictionary = new HelpDictionary(content, word);
                    session.save(helpDictionary);

                    session.getTransaction().commit();
                    session.close();

                    data = HtmlContent.makeHelp(OtherMethods.getHelp(), loggedUser.getRole());
                    responseMessage = "Pomyślnie dodano pojęcie";
                    break;
                case "innovationIdentification":
                    data = HtmlContent.makeInnovationMenu(loggedUser);
                    break;
                case "newInnovationForm":
                    data = HtmlContent.makeNewInnovationForm(loggedUser);
                    break;
                case "saveInnovation":
                    JSONArray answersJson = null, additionalJson = null;
                    JSONParser parser = new JSONParser();
                    String innovationName = request.getParameter("name");
                    String innovationCompany = request.getParameter("company");
                    String innovationAttachments = request.getParameter("attachments");
                    String innovationSigns = request.getParameter("signs");
                    String answers = request.getParameter("answers");
                    String additionAnswers = request.getParameter("additional");

                    Innovation innovation = new Innovation(innovationName, innovationCompany);
                    innovation.setAttachments(innovationAttachments);
                    innovation.setSigned(innovationSigns);
                    innovation.setLoggedUser(loggedUser);

                    try {
                        answersJson = (JSONArray) parser.parse(answers);
                        additionalJson = (JSONArray) parser.parse(additionAnswers);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    session.save(innovation);

                    if (answersJson != null) {
                        for (Object jo : answersJson) {
                            JSONObject ob = (JSONObject) jo;
                            JSONObject additional = Common.findAdditionalAnswer(additionalJson, (String) ob.get("id"));

                            InnovationQuestion question = session.load(InnovationQuestion.class, Long.parseLong((String) ob.get("id")));

                            InnovationAnswer innovationAnswer = new InnovationAnswer((String) ob.get("value"));
                            if (additional != null) {
                                innovationAnswer.setAdditionalAnswer((String) additional.get("value"));
                            }

                            innovationAnswer.setInnovation(innovation);
                            innovationAnswer.setQuestion(question);
                            session.save(innovationAnswer);
                        }
                    }
                    session.getTransaction().commit();
                    session.close();

                    responseMessage = "Pomyślnie zapisano innowację";
                    data = HtmlContent.makeInnovationTable(loggedUser);
                    break;
                case "viewInnovations":
                    data = HtmlContent.makeInnovationTable(loggedUser);
                    break;
                case "deleteInnovation":
                    String innovationId = request.getParameter("innovationId");
                    long id = Long.parseLong(innovationId);
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();

                    Innovation in = session.load(Innovation.class, id);
                    session.delete(in);

                    session.getTransaction().commit();
                    session.close();
                    responseMessage = "Pomyślnie usunięto innowację";
                    data = HtmlContent.makeInnovationTable(loggedUser);
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
