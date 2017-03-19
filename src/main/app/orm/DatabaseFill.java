package main.app.orm;

import main.app.enums.LoginType;
import org.hibernate.Session;

/**
 * Created by Darek on 2017-03-19.
 */
public class DatabaseFill {

    public static void main(String[] args){
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        s.beginTransaction();

        User user = new User(LoginType.USER,"user","user","user","user","email",true);
        User user2 = new User(LoginType.ADMIN,"admin","admin","user","user","email",true);
        User user3 = new User(LoginType.USER,"inactive","inactive","user","user","email",false);

        s.save(user);
        s.save(user2);
        s.save(user3);

        s.getTransaction().commit();
        s.close();

        System.exit(0);
    }
}
