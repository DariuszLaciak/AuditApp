package main.app.orm.methods;


import main.app.orm.HelpDictionary;
import main.app.orm.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class OtherMethods {
    private static Session s;

    public static List<HelpDictionary> getHelp() {
        List<HelpDictionary> lista = null;
        s = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!s.getTransaction().isActive())
            s.beginTransaction();
        lista = s.createQuery("from HelpDictionary order by Word").list();
        s.getTransaction().commit();
        if (s.isOpen())
            s.close();
        return lista;
    }
}
