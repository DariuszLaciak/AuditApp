package main.app.orm.methods;

import main.app.enums.LoginType;
import main.app.orm.HibernateUtil;
import main.app.orm.User;
import org.hibernate.Session;

import java.util.List;


public class UserMethods {

    private static Session s;

    @SuppressWarnings("unchecked")
    public static List<User> getUsers() {
        List<User> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from User").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }

    public static List<User> getManagers() {
        List<User> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from User where role =:role").setParameter("role", LoginType.USER).list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }
}
