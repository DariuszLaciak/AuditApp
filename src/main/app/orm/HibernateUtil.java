package main.app.orm;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();


    private static ServiceRegistry serviceRegistry;

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Audit.class);
        configuration.addAnnotatedClass(AuditResult.class);
        configuration.addAnnotatedClass(Question.class);
        configuration.addAnnotatedClass(Answer.class);
        configuration.configure();
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }

}