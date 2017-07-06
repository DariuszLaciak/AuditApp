package main.app.orm;

import main.app.enums.LoginType;
import main.app.enums.SwotCategory;
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

    private static void swot(Session s) {
        SwotAlternatives swot = new SwotAlternatives(SwotCategory.STRENGHTS, "Znacząca pozycja");
        SwotAlternatives swot2 = new SwotAlternatives(SwotCategory.WEAKNESSES, "Brak jasno wytyczonej strategii");
        SwotAlternatives swot3 = new SwotAlternatives(SwotCategory.OPPORTUNITES, "Pojawienie się nowych grup klientów");
        SwotAlternatives swot4 = new SwotAlternatives(SwotCategory.THREATS, "Możliwość pojawienia się nowych konkurentów");

        s.save(swot);
        s.save(swot2);
        s.save(swot3);
        s.save(swot4);
    }
}
