package main.app.orm.methods;

import main.app.enums.QuestionCategory;
import main.app.enums.QuestionType;
import main.app.orm.*;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Darek on 2017-04-22.
 */
public class QuestionMethods {
    private static Session s;

    public static List<InnovationQuestion> getInnovationQuestions() {
        List<InnovationQuestion> lista;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from InnovationQuestion").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

    public static List<Innovation> getInnovations() {
        List<Innovation> lista;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Innovation").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

    public static List<Innovation> getInnovations(User user) {
        List<Innovation> lista;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Innovation where loggedUser=:user").setParameter("user", user).list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

    public static List<Question> getQuestions() {
        List<Question> lista;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Question").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

    public static List<Question> getQuestions(QuestionType type) {
        List<Question> lista;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Question where type=:type").setParameter("type", type).list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

    public static List<Question> getQuestions(QuestionCategory category) {
        List<Question> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Question where category=:cat").setParameter("cat", category).list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

    public static Question getQuestionById(long id) {
        Question q;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        q = (Question) s.createQuery("from Question where id=:id").setParameter("id", id).list().get(0);
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return q;
    }

    public static List<SwotAlternatives> getSwot() {
        List<SwotAlternatives> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from SwotAlternatives").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }
}
