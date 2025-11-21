package com.chamathka.batikpos.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Standalone Database Connection Test for Batik POS System.
 *
 * This class can be run independently to verify that the database
 * connection is properly configured before running the main application.
 *
 * Usage:
 * java com.chamathka.batikpos.util.DatabaseConnectionTest
 *
 * What this test verifies:
 * 1. hibernate.cfg.xml is correctly configured
 * 2. MySQL server is running and accessible
 * 3. Database 'batik_pos_db' exists
 * 4. Database credentials are correct
 * 5. Hibernate can establish a connection
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
public class DatabaseConnectionTest {

    public static void main(String[] args) {
        System.out.println("╔" + "═".repeat(68) + "╗");
        System.out.println("║" + " ".repeat(15) + "Batik POS - Database Connection Test" + " ".repeat(16) + "║");
        System.out.println("╚" + "═".repeat(68) + "╝");
        System.out.println();

        boolean allTestsPassed = true;

        // Test 1: SessionFactory Initialization
        System.out.println("[1/5] Testing SessionFactory initialization...");
        SessionFactory sessionFactory = null;
        try {
            sessionFactory = HibernateUtil.getSessionFactory();
            if (sessionFactory != null) {
                System.out.println("      ✓ SessionFactory initialized successfully");
            } else {
                System.out.println("      ✗ FAILED: SessionFactory is null");
                allTestsPassed = false;
            }
        } catch (Exception e) {
            System.out.println("      ✗ FAILED: " + e.getMessage());
            System.out.println();
            printTroubleshootingGuide();
            System.exit(1);
        }

        // Test 2: Session Creation
        System.out.println("\n[2/5] Testing database session creation...");
        Session session = null;
        try {
            session = sessionFactory.openSession();
            if (session != null && session.isOpen()) {
                System.out.println("      ✓ Database session opened successfully");
            } else {
                System.out.println("      ✗ FAILED: Could not open session");
                allTestsPassed = false;
            }
        } catch (Exception e) {
            System.out.println("      ✗ FAILED: " + e.getMessage());
            allTestsPassed = false;
        }

        // Test 3: Connection Status
        System.out.println("\n[3/5] Testing database connection status...");
        try {
            if (session != null && session.isConnected()) {
                System.out.println("      ✓ Connected to database successfully");
            } else {
                System.out.println("      ✗ FAILED: Not connected to database");
                allTestsPassed = false;
            }
        } catch (Exception e) {
            System.out.println("      ✗ FAILED: " + e.getMessage());
            allTestsPassed = false;
        }

        // Test 4: Transaction Support
        System.out.println("\n[4/5] Testing transaction support...");
        try {
            if (session != null) {
                session.beginTransaction();
                if (session.getTransaction().isActive()) {
                    System.out.println("      ✓ Transaction started successfully");
                    session.getTransaction().rollback();
                    System.out.println("      ✓ Transaction rollback successful");
                } else {
                    System.out.println("      ✗ FAILED: Transaction not active");
                    allTestsPassed = false;
                }
            }
        } catch (Exception e) {
            System.out.println("      ✗ FAILED: " + e.getMessage());
            allTestsPassed = false;
            if (session != null && session.getTransaction() != null &&
                session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }

        // Test 5: Session Cleanup
        System.out.println("\n[5/5] Testing session cleanup...");
        try {
            if (session != null && session.isOpen()) {
                session.close();
                if (!session.isOpen()) {
                    System.out.println("      ✓ Session closed successfully");
                } else {
                    System.out.println("      ✗ FAILED: Session still open");
                    allTestsPassed = false;
                }
            }
        } catch (Exception e) {
            System.out.println("      ✗ FAILED: " + e.getMessage());
            allTestsPassed = false;
        }

        // Display configuration information
        System.out.println("\n" + "─".repeat(70));
        System.out.println("Database Configuration:");
        System.out.println("─".repeat(70));
        try {
            org.hibernate.cfg.Configuration config = HibernateUtil.getConfiguration();
            System.out.println("Database URL: " +
                config.getProperty("hibernate.connection.url"));
            System.out.println("Username:     " +
                config.getProperty("hibernate.connection.username"));
            System.out.println("Dialect:      " +
                config.getProperty("hibernate.dialect"));
            System.out.println("Schema Mode:  " +
                config.getProperty("hibernate.hbm2ddl.auto"));
        } catch (Exception e) {
            System.out.println("Could not retrieve configuration details");
        }

        // Final Results
        System.out.println("\n" + "═".repeat(70));
        if (allTestsPassed) {
            System.out.println("  ✓✓✓ ALL TESTS PASSED ✓✓✓");
            System.out.println();
            System.out.println("  Database connection is properly configured!");
            System.out.println("  You can now proceed with running the Batik POS application.");
        } else {
            System.out.println("  ✗✗✗ SOME TESTS FAILED ✗✗✗");
            System.out.println();
            System.out.println("  Please fix the issues above before running the application.");
            printTroubleshootingGuide();
        }
        System.out.println("═".repeat(70));

        // Cleanup
        try {
            HibernateUtil.shutdown();
            System.out.println("\nSessionFactory shut down successfully.");
        } catch (Exception e) {
            System.out.println("\nWarning: Error during shutdown: " + e.getMessage());
        }

        // Exit with appropriate code
        System.exit(allTestsPassed ? 0 : 1);
    }

    /**
     * Prints a troubleshooting guide for common database connection issues.
     */
    private static void printTroubleshootingGuide() {
        System.out.println("\n" + "─".repeat(70));
        System.out.println("Troubleshooting Guide:");
        System.out.println("─".repeat(70));
        System.out.println("1. Ensure MySQL server is running:");
        System.out.println("   • Windows: Check Services for 'MySQL80'");
        System.out.println("   • Linux: sudo systemctl status mysql");
        System.out.println("   • macOS: mysql.server status");
        System.out.println();
        System.out.println("2. Verify database exists:");
        System.out.println("   mysql -u root -p");
        System.out.println("   SHOW DATABASES;");
        System.out.println();
        System.out.println("3. Create database if needed:");
        System.out.println("   mysql -u root -p < src/main/resources/sql/01_create_database.sql");
        System.out.println();
        System.out.println("4. Check credentials in:");
        System.out.println("   src/main/resources/hibernate.cfg.xml");
        System.out.println();
        System.out.println("5. Verify MySQL is listening on port 3306:");
        System.out.println("   netstat -an | grep 3306");
        System.out.println("─".repeat(70));
    }
}
