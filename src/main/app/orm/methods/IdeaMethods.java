package main.app.orm.methods;

import main.app.orm.HibernateUtil;
import main.app.orm.Idea;
import main.app.orm.User;
import org.hibernate.Session;

import java.util.List;


public class IdeaMethods {
    private static Session s;

    public static List<Idea> getIdeas() {
        List<Idea> ideas;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        ideas = s.createQuery("from Idea order by actionDate desc, addedDate desc").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return ideas;
    }

    public static List<Idea> getIdeasForUser(User user) {
        List<Idea> ideas;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        ideas = s.createQuery("from Idea where employee = :user order by actionDate desc, addedDate desc").setParameter("user", user).list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return ideas;
    }
}
