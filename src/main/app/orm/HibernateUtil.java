package main.app.orm;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Audit.class);
        configuration.addAnnotatedClass(AuditResult.class);
        configuration.addAnnotatedClass(Question.class);
        configuration.addAnnotatedClass(Answer.class);
        configuration.addAnnotatedClass(Idea.class);
        configuration.addAnnotatedClass(Opinion.class);
        configuration.addAnnotatedClass(SwotAlternatives.class);
        configuration.addAnnotatedClass(SwotRelations.class);
        configuration.addAnnotatedClass(Swot.class);
        configuration.addAnnotatedClass(Source.class);
        configuration.addAnnotatedClass(Impediment.class);
        configuration.addAnnotatedClass(ImpedimentAdvice.class);
        configuration.addAnnotatedClass(HelpDictionary.class);
        configuration.addAnnotatedClass(Innovation.class);
        configuration.addAnnotatedClass(InnovationQuestion.class);
        configuration.addAnnotatedClass(InnovationAnswer.class);
        configuration.configure();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
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