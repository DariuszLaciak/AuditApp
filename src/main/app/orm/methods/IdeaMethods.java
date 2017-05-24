package main.app.orm.methods;

import main.app.orm.HibernateUtil;
import main.app.orm.Idea;
import main.app.orm.User;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by darek on 20.05.17.
 */
public class IdeaMethods {
    private static Session s;

    public static List<Idea> getIdeas() {
        List<Idea> ideas;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        ideas = s.createQuery("from Idea").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return ideas;
    }
}
