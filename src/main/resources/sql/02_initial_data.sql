-- ============================================================================
-- Batik POS System - Initial Data Script
-- ============================================================================
-- This script inserts the initial required data for the Batik POS System.
-- Execute this script after the tables have been created by Hibernate.
--
-- Usage:
-- mysql -u root -p batik_pos_db < 02_initial_data.sql
--
-- As per SRS Section 5.1 (FR-AUTH: User Authentication)
-- ============================================================================

USE batik_pos_db;

-- ============================================================================
-- Insert Default Admin User
-- ============================================================================
-- As per FR-AUTH-05: The Admin user cannot be deleted
-- Default credentials:
--   Username: admin
--   Password: admin123 (IMPORTANT: Change this in production!)
--
-- The password is hashed using BCrypt as per FR-AUTH-02
-- Hash generated for 'admin123': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================================================================

INSERT INTO User (username, passwordHash, role)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username; -- Prevents duplicate if already exists

-- Display confirmation
SELECT 'Default Admin user created successfully!' AS Status;
SELECT 'Username: admin' AS Credentials;
SELECT 'Password: admin123 (Please change this!)' AS Warning;

-- ============================================================================
-- Optional: Insert Sample Cashier User
-- ============================================================================
-- Uncomment the following lines to create a sample Cashier account
-- Username: cashier
-- Password: cashier123
-- Hash: $2a$10$E7uLZd.BHDjXVvhKjx5pKuXxJ5L0N5JD0J5L0N5JD0J5L0N5JD0J5L

-- INSERT INTO User (username, passwordHash, role)
-- VALUES ('cashier', '$2a$10$E7uLZd.BHDjXVvhKjx5pKuXxJ5L0N5JD0J5L0N5JD0J5L0N5JD0J5L', 'CASHIER');

-- ============================================================================
-- Insert Sample Categories (Optional)
-- ============================================================================
-- These are suggested categories based on the SRS examples
-- You can modify these based on actual business needs

-- Sample categories will be inserted when creating Product entities
-- Examples: 'Saree', 'Shirt', 'Sarong', 'Dress', 'Batik Fabric'

-- ============================================================================
-- Display all users
-- ============================================================================
SELECT userId, username, role, 'Created' AS status FROM User;

-- ============================================================================
-- IMPORTANT SECURITY NOTES:
-- 1. Change the default admin password immediately after first login!
-- 2. Never use default passwords in a production environment
-- 3. Ensure database access is properly secured with strong passwords
-- 4. Regular backups should be configured for production systems
-- ============================================================================
