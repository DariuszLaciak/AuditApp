package main.app.orm.methods;

import main.app.enums.QuestionCategory;
import main.app.orm.HibernateUtil;
import main.app.orm.Question;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Darek on 2017-04-22.
 */
public class QuestionMethods {
    private static Session s;

    public static List<Question> getQuestions() {
        List<Question> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Question").list();
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
}
