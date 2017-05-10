package main.app.orm.methods;

import main.app.orm.Answer;
import main.app.orm.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Darek on 2017-04-23.
 */
public class ResultMethods {
    private static Session s;

    public static List<Answer> getAnswersForAudit(long audit) {
        List<Answer> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Answer where auditId=:audit").setParameter("audit", audit).list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }
}
