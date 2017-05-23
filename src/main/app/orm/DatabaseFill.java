package main.app.orm;

import main.app.enums.LoginType;
import org.hibernate.Session;


public class DatabaseFill {

    public static void main(String[] args) {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        s.beginTransaction();

        users(s);

        s.getTransaction().commit();
        s.close();

        System.exit(0);
    }

    private static void users(Session s) {
        User user = new User(LoginType.USER, "user", "user", "user", "user", "email", true);
        User user2 = new User(LoginType.ADMIN, "admin", "admin", "user", "user", "email", true);
        User user3 = new User(LoginType.USER, "inactive", "inactive", "user", "user", "email", false);
        User user4 = new User(LoginType.EMPLOYEE, "emp", "emp", "user", "user", "email", true);

        s.save(user);
        s.save(user2);
        s.save(user3);
        s.save(user4);
    }
}
