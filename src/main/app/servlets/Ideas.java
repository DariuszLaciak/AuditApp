package main.app.servlets;

import main.app.HtmlContent;
import main.app.enums.IdeaTypes;
import main.app.enums.LoginType;
import main.app.enums.Status;
import main.app.orm.HibernateUtil;
import main.app.orm.Idea;
import main.app.orm.Opinion;
import main.app.orm.User;
import main.app.orm.methods.IdeaMethods;
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
import java.util.List;

@WebServlet(name = "Ideas", urlPatterns = {"/Ideas"})
public class Ideas extends HttpServlet {
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
        Session session;

        User user = (User) s.getAttribute("userData");

        List<Idea> ideas;
        if (user.getRole().equals(LoginType.EMPLOYEE)) {
            ideas = IdeaMethods.getIdeasForUser(user);
        } else {
            ideas = IdeaMethods.getIdeas();
        }

        switch (action) {
            case "newIdea":
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                String content = request.getParameter("content");
                String type = request.getParameter("type");
                String name = request.getParameter("name");

                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Idea idea = new Idea(name, content, IdeaTypes.valueOf(type));
                idea.setEmployee(user);

                session.save(idea);

                session.getTransaction().commit();
                session.close();

                responseMessage = "Pomyślnie dodano nowy pomysł";
                break;
            case "listIdeas":
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();
                String html = "<h1>Zgłoszone pomysły: </h1>";
                html += HtmlContent.displayIdeas(ideas, user);
                data = html;

                session.getTransaction().commit();
                session.close();
                break;
            case "saveOpinion":
                String opinionContent = request.getParameter("value");

                long ideaId = Integer.parseInt(request.getParameter("ideaId"));

                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Idea ideaToSave = session.load(Idea.class, ideaId);

                session.refresh(user);

                Opinion opinion = new Opinion(opinionContent);
                opinion.setIdea(ideaToSave);
                opinion.setAuthor(user);
                ideaToSave.setActionDate(new Date());

                session.save(opinion);
                session.update(ideaToSave);

                responseMessage = "Pomyślnie zapisano opinię";

                session.getTransaction().commit();
                session.close();

                if (user.getRole().equals(LoginType.EMPLOYEE)) {
                    ideas = IdeaMethods.getIdeasForUser(user);
                } else {
                    ideas = IdeaMethods.getIdeas();
                }

                JSONObject dataInner = new JSONObject();
                dataInner.put("opinion", HtmlContent.makeMoreInfoIdea(ideaToSave));
                dataInner.put("ideas", HtmlContent.displayIdeas(ideas, user));
                data = dataInner;

                break;
            case "changeIdeaStatus":
                boolean isOpen = Boolean.parseBoolean(request.getParameter("isOpen"));
                long id = Long.parseLong(request.getParameter("ideaId"));
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Idea ideaTochange = session.load(Idea.class, id);
                if (isOpen) {
                    ideaTochange.setStatus(Status.OPEN);
                } else {
                    ideaTochange.setStatus(Status.CLOSED);
                }

                session.update(ideaTochange);

                session.getTransaction().commit();
                session.close();

                responseMessage = "Pomyślnie zmieniono status pomysłu";

                data = HtmlContent.displayIdeas(IdeaMethods.getIdeas(), user);
                break;
            case "moreInfoIdea":
                ideaId = Long.parseLong(request.getParameter("ideaId"));
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Idea i = session.load(Idea.class, ideaId);
                data = HtmlContent.makeMoreInfoIdea(i);

                session.getTransaction().commit();
                session.close();

                break;
            default:
                responseMessage = "Złe zapytanie";
                success = false;
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
