package com.chamathka.batikpos.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate Utility Class for Batik POS System
 *
 * This class implements the Singleton pattern to manage the Hibernate SessionFactory.
 * The SessionFactory is a thread-safe, immutable object that is expensive to create,
 * so we create it only once and reuse it throughout the application lifecycle.
 *
 * As per SRS Section 3.2 (Data Persistence), this utility provides centralized
 * access to database sessions for all DAO/Repository classes.
 *
 * Usage Example:
 * <pre>
 * Session session = HibernateUtil.getSessionFactory().openSession();
 * Transaction transaction = session.beginTransaction();
 * try {
 *     // Perform database operations
 *     transaction.commit();
 * } catch (Exception e) {
 *     transaction.rollback();
 *     throw e;
 * } finally {
 *     session.close();
 * }
 * </pre>
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    /**
     * Singleton instance of SessionFactory.
     * Marked as volatile to ensure thread-safety with double-checked locking.
     */
    private static volatile SessionFactory sessionFactory;

    /**
     * Private constructor to prevent instantiation.
     * This class should only be accessed via static methods.
     */
    private HibernateUtil() {
        throw new AssertionError("HibernateUtil is a utility class and should not be instantiated");
    }

    /**
     * Gets the Hibernate SessionFactory instance.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * This method reads the configuration from hibernate.cfg.xml located in
     * src/main/resources/ as per SRS Section 7.5 (Maintainability).
     *
     * @return The SessionFactory instance
     * @throws ExceptionInInitializerError if SessionFactory creation fails
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    try {
                        logger.info("Initializing Hibernate SessionFactory...");

                        // Create SessionFactory from hibernate.cfg.xml
                        Configuration configuration = new Configuration();
                        configuration.configure("hibernate.cfg.xml");

                        sessionFactory = configuration.buildSessionFactory();

                        logger.info("Hibernate SessionFactory initialized successfully");
                        logger.info("Database URL: {}",
                            configuration.getProperty("hibernate.connection.url"));

                    } catch (Exception e) {
                        logger.error("Failed to initialize Hibernate SessionFactory", e);
                        logger.error("Please ensure:");
                        logger.error("1. MySQL server is running");
                        logger.error("2. Database 'batik_pos_db' exists");
                        logger.error("3. hibernate.cfg.xml contains correct credentials");

                        throw new ExceptionInInitializerError(
                            "Could not initialize Hibernate SessionFactory. " +
                            "Check logs for details. Error: " + e.getMessage()
                        );
                    }
                }
            }
        }
        return sessionFactory;
    }

    /**
     * Shuts down the SessionFactory and releases all resources.
     *
     * This method should be called when the application is shutting down
     * to ensure proper cleanup of database connections and resources.
     *
     * As per SRS Section 7.4 (Reliability), this ensures graceful shutdown
     * and prevents connection leaks.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Shutting down Hibernate SessionFactory...");
            try {
                sessionFactory.close();
                logger.info("Hibernate SessionFactory shut down successfully");
            } catch (Exception e) {
                logger.error("Error while shutting down Hibernate SessionFactory", e);
            }
        }
    }

    /**
     * Checks if the SessionFactory is initialized and open.
     *
     * @return true if SessionFactory is initialized and not closed, false otherwise
     */
    public static boolean isInitialized() {
        return sessionFactory != null && !sessionFactory.isClosed();
    }

    /**
     * Gets the current Hibernate Configuration.
     * Useful for debugging and testing purposes.
     *
     * @return A new Configuration instance loaded from hibernate.cfg.xml
     */
    public static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        return configuration;
    }
}
