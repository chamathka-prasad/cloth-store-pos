-- ============================================================================
-- Batik POS System - Database Creation Script
-- ============================================================================
-- This script creates the database schema for the Batik POS System.
-- Execute this script before running the application for the first time.
--
-- Prerequisites:
-- 1. MySQL Server 8.0 or higher is installed and running
-- 2. You have administrative privileges to create databases
--
-- Usage:
-- mysql -u root -p < 01_create_database.sql
-- OR
-- Execute this script in MySQL Workbench or any MySQL client
--
-- As per SRS Section 1.6 (Assumptions and Dependencies)
-- ============================================================================

-- Drop database if it exists (WARNING: This will delete all data!)
-- Comment out the next line if you want to preserve existing data
DROP DATABASE IF EXISTS batik_pos_db;

-- Create the database with UTF-8 encoding
CREATE DATABASE batik_pos_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Display confirmation message
SELECT 'Database batik_pos_db created successfully!' AS Status;

-- Switch to the new database
USE batik_pos_db;

-- Display database information
SELECT DATABASE() AS 'Current Database';
SELECT VERSION() AS 'MySQL Version';
SELECT NOW() AS 'Creation Time';

-- ============================================================================
-- Note: The actual tables will be created automatically by Hibernate
-- when the application starts (hbm2ddl.auto=update in hibernate.cfg.xml)
--
-- However, you can also create tables manually by running the
-- 02_create_tables.sql script if you prefer explicit schema management.
-- ============================================================================
