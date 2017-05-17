package main.app.orm.methods;

import main.app.enums.QuestionCategory;
import main.app.orm.Audit;
import main.app.orm.HibernateUtil;
import main.app.orm.Question;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Darek on 2017-04-22.
 */
public class AuditMethods {
    private static Session s;

    public static List<Audit> getAudits() {
        List<Audit> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from Audit").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

}
