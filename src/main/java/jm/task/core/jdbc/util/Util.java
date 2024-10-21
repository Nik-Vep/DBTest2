package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    private final static String URL = "jdbc:mysql://localhost:3306/mydbtest";
    private final static String USER_NAME = "root";
    private final static String PASSWORD = "root";
    private static volatile Connection connection;
    private static volatile SessionFactory sessionFactory;

    private Util() {
    }

    public static Connection getConnection() {
        Connection localConnection = connection;
        try {
            if (localConnection == null || localConnection.isClosed()) {
                synchronized (Util.class) {
                    localConnection = connection;
                    if (localConnection == null || localConnection.isClosed()) {
                        try {
                            Driver driver = new com.mysql.cj.jdbc.Driver();
                            DriverManager.registerDriver(driver);
                            localConnection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
                            connection = localConnection;
                        } catch (SQLException e) {
                            System.out.println("Connection failed: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check connection state: " + e.getMessage());
        }
        return localConnection;
    }

    public static SessionFactory getSessionFactory() {
        SessionFactory localSessionFactory = sessionFactory;
            if (localSessionFactory == null) {
                synchronized (Util.class) {
                    localSessionFactory = sessionFactory;
                    if (localSessionFactory == null) {
                        try {
                            Configuration configuration = new Configuration();
                            Properties settings = new Properties();
                            settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/mydbtest");
                            settings.put(Environment.USER, "root");
                            settings.put(Environment.PASS, "root");
                            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
                            settings.put(Environment.SHOW_SQL, "true");
                            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                            settings.put(Environment.HBM2DDL_AUTO, "");

                            configuration.setProperties(settings);
                            configuration.addAnnotatedClass(User.class);

                            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                                    .applySettings(configuration.getProperties()).build();

                            localSessionFactory = configuration.buildSessionFactory(serviceRegistry);
                            sessionFactory = localSessionFactory;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        return localSessionFactory;
    }
}


