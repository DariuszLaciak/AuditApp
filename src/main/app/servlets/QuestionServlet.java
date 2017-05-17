package main.app.servlets;

import main.app.Common;
import main.app.HtmlContent;
import main.app.enums.QuestionCategory;
import main.app.enums.QuestionType;
import main.app.orm.*;
import main.app.orm.methods.QuestionMethods;
import main.app.orm.methods.ResultMethods;
import org.hibernate.HibernateException;
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
 * Created by Darek on 2017-04-22.
 */
@WebServlet(name = "Question", urlPatterns = {"/Question"})
public class QuestionServlet extends HttpServlet {
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
        switch (action) {
            case "new": {
                responseMessage = "Pomyślnie dodano pytanie";
                String questionContent = request.getParameter("content");
                String questionType = request.getParameter("type");
                String additional = request.getParameter("additional");
                String category = request.getParameter("category");
                boolean additionalYes = false;
                QuestionType type = QuestionType.valueOf(questionType.toUpperCase());
                QuestionCategory questionCategory = QuestionCategory.valueOf(category.toUpperCase());
                if (type.equals(QuestionType.YES_NO)) {
                    additionalYes = Boolean.valueOf(additional);
                }
                Question question = new Question(questionContent, type, additionalYes, questionCategory);

                try {
                    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();
                    session.persist(question);

                    session.getTransaction().commit();
                    session.close();

                } catch (HibernateException e) {
                    responseMessage = "Problem z serwerem. Stontaktuj się z administratorem";
                    success = false;
                }
            }
            break;
            case "list": {
                List<Question> all = QuestionMethods.getQuestions();
                data = HtmlContent.makeQuestionTable(all);
            }
            break;
            case "getQuestions": {
                if (s.getAttribute("notAskedQuestions") == null) {
                    s.setAttribute("notAskedQuestions", QuestionMethods.getQuestions());
                }
                List<Question> allQuestions = (List<Question>) s.getAttribute("notAskedQuestions");

                int numberOfQuestions = allQuestions.size();
                String toSave = request.getParameter("toSave");


                JSONArray toSaveJson = null;
                JSONParser parser = new JSONParser();
                try {
                    toSaveJson = (JSONArray) parser.parse(toSave);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();
                if (toSaveJson != null) {
                    for (Object jo : toSaveJson) {

                        JSONObject ob = (JSONObject) jo;
                        Answer ans = new Answer();
                        ans.setAnswer(Float.parseFloat((String) ob.get("answer")));
                        if (ob.containsKey("yesVal") && !ob.get("yesVal").equals("")) {
                            ans.setYesValueAnswer(Long.parseLong((String) ob.get("yesVal")));
                        }
                        Question q = session.load(Question.class, Long.parseLong((String) ob.get("id")));
                        ans.setQuestion(q);

                        Audit audit = session.load(Audit.class, (Long) s.getAttribute("auditId"));

                        ans.setAudit(audit);

                        session.saveOrUpdate(ans);
                        q.getAnswers().add(ans);
                        audit.getAnswers().add(ans);
                        session.merge(q);

                    }
                }
                session.getTransaction().commit();
                session.close();
                if (numberOfQuestions == 0) {
                    List<Answer> answers = ResultMethods.getAnswersForAudit((Long) s.getAttribute("auditId"));
                    float result = Common.getResultFromAnswers(answers);
                    result *= 100;
                    int percent = Math.round(result);

                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();
                    AuditResult auditResult = new AuditResult();
                    Audit audit = session.load(Audit.class, (Long) s.getAttribute("auditId"));
                    auditResult.setAudit(audit);
                    auditResult.setResultValue(percent);
                    session.persist(auditResult);
                    audit.setResult(auditResult);
                    session.merge(audit);

                    session.getTransaction().commit();
                    session.close();
                    s.removeAttribute("notAskedQuestions");

                    data = HtmlContent.prepareResults(answers, audit); // + wynik punktowy
                } else {
                    data = HtmlContent.makeQuestions(allQuestions, s);
                }

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
