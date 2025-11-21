package com.chamathka.batikpos.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HibernateUtil.
 *
 * This class tests the database connection and Hibernate configuration.
 * Before running these tests, ensure:
 * 1. MySQL server is running
 * 2. Database 'batik_pos_db' exists
 * 3. Credentials in hibernate.cfg.xml are correct
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HibernateUtilTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUp() {
        System.out.println("=".repeat(60));
        System.out.println("Testing Hibernate Configuration for Batik POS System");
        System.out.println("=".repeat(60));
    }

    @Test
    @Order(1)
    @DisplayName("Test SessionFactory Initialization")
    void testSessionFactoryInitialization() {
        System.out.println("\n[Test 1] Testing SessionFactory initialization...");

        assertDoesNotThrow(() -> {
            sessionFactory = HibernateUtil.getSessionFactory();
        }, "SessionFactory initialization should not throw exception");

        assertNotNull(sessionFactory, "SessionFactory should not be null");
        System.out.println("✓ SessionFactory initialized successfully");
    }

    @Test
    @Order(2)
    @DisplayName("Test SessionFactory is Singleton")
    void testSessionFactorySingleton() {
        System.out.println("\n[Test 2] Testing SessionFactory singleton pattern...");

        SessionFactory factory1 = HibernateUtil.getSessionFactory();
        SessionFactory factory2 = HibernateUtil.getSessionFactory();

        assertSame(factory1, factory2,
            "SessionFactory should return the same instance (Singleton pattern)");

        System.out.println("✓ SessionFactory follows singleton pattern correctly");
    }

    @Test
    @Order(3)
    @DisplayName("Test Database Connection")
    void testDatabaseConnection() {
        System.out.println("\n[Test 3] Testing database connection...");

        SessionFactory factory = HibernateUtil.getSessionFactory();
        assertNotNull(factory, "SessionFactory should be initialized");

        Session session = null;
        try {
            session = factory.openSession();
            assertNotNull(session, "Session should not be null");
            assertTrue(session.isConnected(), "Session should be connected to database");

            System.out.println("✓ Database connection established successfully");

        } catch (Exception e) {
            fail("Failed to open database session: " + e.getMessage());

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("✓ Session closed properly");
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test Session Open and Close")
    void testSessionOpenAndClose() {
        System.out.println("\n[Test 4] Testing session lifecycle...");

        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        assertTrue(session.isOpen(), "Newly opened session should be open");
        assertTrue(session.isConnected(), "Newly opened session should be connected");

        session.close();

        assertFalse(session.isOpen(), "Closed session should not be open");

        System.out.println("✓ Session lifecycle works correctly");
    }

    @Test
    @Order(5)
    @DisplayName("Test Transaction Support")
    void testTransactionSupport() {
        System.out.println("\n[Test 5] Testing transaction support...");

        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        try {
            // Begin transaction
            session.beginTransaction();
            assertNotNull(session.getTransaction(), "Transaction should not be null");
            assertTrue(session.getTransaction().isActive(), "Transaction should be active");

            System.out.println("✓ Transaction started successfully");

            // Rollback (no actual changes)
            session.getTransaction().rollback();
            assertFalse(session.getTransaction().isActive(),
                "Transaction should not be active after rollback");

            System.out.println("✓ Transaction rollback works correctly");

        } catch (Exception e) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            fail("Transaction test failed: " + e.getMessage());

        } finally {
            session.close();
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test HibernateUtil isInitialized()")
    void testIsInitialized() {
        System.out.println("\n[Test 6] Testing isInitialized() method...");

        // Ensure SessionFactory is initialized
        HibernateUtil.getSessionFactory();

        assertTrue(HibernateUtil.isInitialized(),
            "isInitialized() should return true after SessionFactory creation");

        System.out.println("✓ isInitialized() returns correct status");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("All tests completed");
        System.out.println("=".repeat(60));

        // Note: We don't shut down SessionFactory here as other tests might need it
        // In a real application, shutdown would be called on application exit
        System.out.println("\nNote: SessionFactory is still active for other tests");
        System.out.println("Call HibernateUtil.shutdown() on application exit");
    }
}
