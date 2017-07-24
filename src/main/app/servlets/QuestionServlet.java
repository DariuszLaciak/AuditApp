package main.app.servlets;

import main.app.Common;
import main.app.HtmlContent;
import main.app.enums.AuditType;
import main.app.enums.QuestionType;
import main.app.enums.SwotCategory;
import main.app.orm.*;
import main.app.orm.methods.AuditMethods;
import main.app.orm.methods.QuestionMethods;
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
import java.util.ArrayList;
import java.util.Date;
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

        User auditor = (User) s.getAttribute("userData");
        switch (action) {
            case "list": {
                List<Question> all = QuestionMethods.getQuestions();
                data = HtmlContent.makeQuestionTable(all);
            }
            break;
            case "getQuestions": {
                String questionType = request.getParameter("isGeneral");
                QuestionType type = QuestionType.LICKERT;
                if (!Boolean.parseBoolean(questionType)) {
                    type = QuestionType.DETAILED;
                }
                if (s.getAttribute("notAskedQuestions") == null) {
                    s.setAttribute("notAskedQuestions", QuestionMethods.getQuestions(type));
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


                if (toSaveJson != null) {
                    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();
                    for (Object jo : toSaveJson) {

                        JSONObject ob = (JSONObject) jo;
                        Answer ans = new Answer();
                        ans.setAnswer(Float.parseFloat((String) ob.get("answer")));
                        Question q = session.load(Question.class, Long.parseLong((String) ob.get("id")));
                        ans.setQuestion(q);

                        Audit audit = session.load(Audit.class, (Long) s.getAttribute("auditId"));

                        ans.setAudit(audit);

                        session.saveOrUpdate(ans);
                        q.getAnswers().add(ans);
                        audit.getAnswers().add(ans);
                        session.merge(q);

                    }
                    session.getTransaction().commit();
                    session.close();
                }

                if (numberOfQuestions == 0) {
                    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                    if (!session.getTransaction().isActive())
                        session.beginTransaction();
                    AuditResult auditResult = new AuditResult();
                    Audit audit = session.load(Audit.class, (Long) s.getAttribute("auditId"));

                    List<Answer> answers = audit.getAnswers();
                    float result = Common.getResultFromAnswers(answers);
                    result *= 100;
                    int percent = Math.round(result);

                    auditResult.setAudit(audit);
                    auditResult.setResultValue(percent);
                    session.persist(auditResult);
                    audit.setResult(auditResult);
                    session.merge(audit);

                    session.getTransaction().commit();
                    session.close();
                    s.removeAttribute("notAskedQuestions");

                    data = HtmlContent.prepareResults(audit);
                } else {
                    data = HtmlContent.makeQuestions(allQuestions, s);
                }

            }
            break;
            case "newSwot": {
                data = HtmlContent.makeSwotTables(QuestionMethods.getSwot());
            }
            break;
            case "saveSwot": {
                String[] alternatives = request.getParameterValues("selected[]");
                long swotId = Long.parseLong(request.getParameter("swotId"));

                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Swot swot = new Swot(new Date());
                swot.setAuditorId(auditor);
                session.save(swot);

                session.getTransaction().commit();
                session.close();

                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                swot = session.load(Swot.class, swotId);
                List<SwotAlternatives> alternativesObjects = new ArrayList<>();
                for (String alt : alternatives) {
                    alternativesObjects.add(session.load(SwotAlternatives.class, Long.parseLong(alt)));
                }
                swot.getAlternatives().addAll(alternativesObjects);

                session.update(swot);

                session.getTransaction().commit();
                session.close();

                data = HtmlContent.makeSwotRelations(swot);
            }
            break;
            case "newSwotPosition": {
                String category = request.getParameter("category");
                String value = request.getParameter("value");
                SwotCategory cat = SwotCategory.valueOf(category);

                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                SwotAlternatives alt = new SwotAlternatives(cat, value);
                long altId = (long) session.save(alt);

                responseMessage = "Pomyślnie dodano cechę.";

                session.getTransaction().commit();
                session.close();

                data = HtmlContent.getSingleAlternativeOption(altId, value);
            }
            break;
            case "swotRelations": {
                long swotId = Long.parseLong(request.getParameter("swotId"));
                String toSave = request.getParameter("relations");


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

                Swot swotToAnalyse = session.load(Swot.class, swotId);

                for (Object jo : toSaveJson) {
                    JSONObject ob = (JSONObject) jo;
                    //value, id1, id2
                    SwotAlternatives alt1 = session.load(SwotAlternatives.class, Long.parseLong(ob.get("id1").toString()));
                    SwotAlternatives alt2 = session.load(SwotAlternatives.class, Long.parseLong(ob.get("id2").toString()));
                    int value = Integer.parseInt(ob.get("value").toString());

                    SwotRelations relation = new SwotRelations(value, swotToAnalyse, alt1, alt2);

                    session.save(relation);
                }

                data = HtmlContent.makeSwotResult(swotToAnalyse);

                session.getTransaction().commit();
                session.close();

            }
            break;
            case "newSources": {
                data = HtmlContent.makeSourceOrImpedimentTable(AuditMethods.getSources(), true);
            }
            break;
            case "newImpediments": {
                data = HtmlContent.makeSourceOrImpedimentTable(AuditMethods.getImpediments(), false);
            }
            break;
            case "sourceAudit": {
                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Audit audit = new Audit();
                audit.setType(AuditType.SOURCES);
                audit.setAuditor(auditor);
                audit.setAuditDate(new Date());
                long auditId = (long) session.save(audit);

                session.getTransaction().commit();
                session.close();

                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                audit = session.load(Audit.class, auditId);

                String[] sources = request.getParameterValues("sources[]");
                boolean noData = Boolean.parseBoolean(request.getParameter("noData"));
                if (!noData) {
                    for (String src : sources) {
                        Source source = session.load(Source.class, Long.parseLong(src));
                        audit.getSources().add(source);
                    }
                }
                session.getTransaction().commit();
                session.close();

                data = HtmlContent.makeSourcesReport(audit);
            }
            break;
            case "impedimentAudit": {
                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Audit audit = new Audit();
                audit.setType(AuditType.IMPEDIMENTS);
                audit.setAuditor(auditor);
                audit.setAuditDate(new Date());

                session.save(audit);

                long auditId = (long) session.save(audit);

                session.getTransaction().commit();
                session.close();

                session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                audit = session.load(Audit.class, auditId);
                String[] impediments = request.getParameterValues("impediments[]");
                boolean noData = Boolean.parseBoolean(request.getParameter("noData"));
                if (!noData) {
                    for (String src : impediments) {
                        Impediment impediment = session.load(Impediment.class, Long.parseLong(src));
                        audit.getImpediments().add(impediment);
                    }
                }
                session.getTransaction().commit();
                session.close();

                data = HtmlContent.makeImpedimentsReport(audit);
            }
            break;
            case "showSources": {
                long auditId = Long.parseLong(request.getParameter("auditId"));
                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Audit audit = session.load(Audit.class, auditId);

                session.getTransaction().commit();
                session.close();

                data = HtmlContent.makeSourcesReport(audit);
            }
            break;
            case "showImpediments": {
                long auditId = Long.parseLong(request.getParameter("auditId"));
                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                if (!session.getTransaction().isActive())
                    session.beginTransaction();

                Audit audit = session.load(Audit.class, auditId);

                session.getTransaction().commit();
                session.close();

                data = HtmlContent.makeImpedimentsReport(audit);
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
