# Batik POS System - Database Setup Guide

This guide covers the complete database configuration for the Batik POS System (Phase 1, Step 2).

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Detailed Setup](#detailed-setup)
- [Configuration Files](#configuration-files)
- [Testing the Connection](#testing-the-connection)
- [Troubleshooting](#troubleshooting)
- [Production Deployment](#production-deployment)

---

## Prerequisites

Before setting up the database, ensure you have:

- ✅ **MySQL Server 8.0+** installed and running
- ✅ **Java 17+** installed
- ✅ **Maven 3.6+** installed
- ✅ Administrative access to MySQL
- ✅ Completed Phase 1, Step 1 (pom.xml setup)

---

## Quick Start

### 1. Start MySQL Server

**Windows:**
```powershell
# Check if MySQL is running
Get-Service MySQL80

# Start MySQL if not running
Start-Service MySQL80
```

**Linux:**
```bash
# Start MySQL
sudo systemctl start mysql

# Check status
sudo systemctl status mysql
```

**macOS:**
```bash
# Start MySQL
mysql.server start

# Check status
mysql.server status
```

### 2. Create Database

```bash
# Navigate to project root
cd cloth-store-pos

# Create database
mysql -u root -p < src/main/resources/sql/01_create_database.sql
```

When prompted, enter your MySQL root password.

### 3. Configure Database Credentials

Edit `src/main/resources/hibernate.cfg.xml`:

```xml
<!-- Update these lines with your MySQL credentials -->
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">YOUR_PASSWORD_HERE</property>
```

### 4. Test the Connection

```bash
# Compile the project
mvn clean compile

# Run the database connection test
mvn exec:java -Dexec.mainClass="com.chamathka.batikpos.util.DatabaseConnectionTest"
```

If all tests pass, you're ready to proceed! ✅

---

## Detailed Setup

### Step 1: Verify MySQL Installation

```bash
# Check MySQL version
mysql --version

# Expected output: mysql  Ver 8.0.x or higher
```

### Step 2: Create Database Schema

The project includes a SQL script to create the database:

```bash
mysql -u root -p < src/main/resources/sql/01_create_database.sql
```

This creates:
- Database: `batik_pos_db`
- Character set: `utf8mb4` (full Unicode support)
- Collation: `utf8mb4_unicode_ci`

**Verify database creation:**

```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'batik_pos_db';"
```

### Step 3: Configure Hibernate

The main configuration file is located at:
```
src/main/resources/hibernate.cfg.xml
```

**Key Configuration Properties:**

| Property | Default Value | Description |
|----------|---------------|-------------|
| `hibernate.connection.url` | `jdbc:mysql://localhost:3306/batik_pos_db` | Database URL |
| `hibernate.connection.username` | `root` | MySQL username |
| `hibernate.connection.password` | `root` | MySQL password |
| `hibernate.dialect` | `MySQL8Dialect` | MySQL version |
| `hibernate.hbm2ddl.auto` | `update` | Auto-create tables |
| `hibernate.show_sql` | `true` | Log SQL statements |

### Step 4: Initialize Tables

When you first run the application, Hibernate will automatically create all 9 tables:

1. `User` - System users
2. `Customer` - Customer records
3. `Supplier` - Supplier information
4. `Product` - Master products
5. `ProductVariant` - Product variants (SKUs)
6. `GRN` - Goods Received Notes
7. `GRNItem` - GRN line items
8. `Sale` - Sales transactions
9. `SaleItem` - Sale line items

This is controlled by: `<property name="hibernate.hbm2ddl.auto">update</property>`

### Step 5: Insert Initial Data

After tables are created, insert the default Admin user:

```bash
mysql -u root -p batik_pos_db < src/main/resources/sql/02_initial_data.sql
```

**Default Admin Credentials:**
- Username: `admin`
- Password: `admin123`

⚠️ **Change this password immediately in production!**

---

## Configuration Files

### 1. hibernate.cfg.xml
**Location:** `src/main/resources/hibernate.cfg.xml`

**Purpose:** Main Hibernate configuration

**Key sections:**
- Database connection settings
- Connection pool configuration (HikariCP)
- Schema generation strategy
- SQL logging settings
- Entity class mappings

### 2. HibernateUtil.java
**Location:** `src/main/java/com/chamathka/batikpos/util/HibernateUtil.java`

**Purpose:** Singleton SessionFactory manager

**Key methods:**
- `getSessionFactory()` - Get the SessionFactory instance
- `shutdown()` - Clean shutdown
- `isInitialized()` - Check if initialized

**Usage example:**
```java
Session session = HibernateUtil.getSessionFactory().openSession();
Transaction tx = session.beginTransaction();
try {
    // Database operations here
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    throw e;
} finally {
    session.close();
}
```

### 3. SQL Scripts
**Location:** `src/main/resources/sql/`

- `01_create_database.sql` - Creates the database
- `02_initial_data.sql` - Inserts default admin user
- `README.md` - SQL scripts documentation

---

## Testing the Connection

### Method 1: Run Standalone Test

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.chamathka.batikpos.util.DatabaseConnectionTest"
```

**Expected output:**
```
╔════════════════════════════════════════════════════════════════════╗
║               Batik POS - Database Connection Test                 ║
╚════════════════════════════════════════════════════════════════════╝

[1/5] Testing SessionFactory initialization...
      ✓ SessionFactory initialized successfully

[2/5] Testing database session creation...
      ✓ Database session opened successfully

[3/5] Testing database connection status...
      ✓ Connected to database successfully

[4/5] Testing transaction support...
      ✓ Transaction started successfully
      ✓ Transaction rollback successful

[5/5] Testing session cleanup...
      ✓ Session closed successfully

══════════════════════════════════════════════════════════════════════
  ✓✓✓ ALL TESTS PASSED ✓✓✓

  Database connection is properly configured!
  You can now proceed with running the Batik POS application.
══════════════════════════════════════════════════════════════════════
```

### Method 2: Run JUnit Tests

```bash
mvn test -Dtest=HibernateUtilTest
```

---

## Troubleshooting

### ❌ Error: "Access denied for user 'root'@'localhost'"

**Problem:** Incorrect database credentials

**Solution:**
1. Verify your MySQL credentials:
   ```bash
   mysql -u root -p
   ```

2. Update `hibernate.cfg.xml` with correct username/password

### ❌ Error: "Unknown database 'batik_pos_db'"

**Problem:** Database doesn't exist

**Solution:**
```bash
mysql -u root -p < src/main/resources/sql/01_create_database.sql
```

### ❌ Error: "Communications link failure"

**Problem:** MySQL server is not running

**Solution:**

**Windows:**
```powershell
Start-Service MySQL80
```

**Linux:**
```bash
sudo systemctl start mysql
```

**macOS:**
```bash
mysql.server start
```

### ❌ Error: "Could not create connection to database server"

**Problem:** Wrong host/port or firewall blocking

**Solution:**
1. Verify MySQL is listening on port 3306:
   ```bash
   netstat -an | grep 3306
   ```

2. Check firewall settings

3. If MySQL is on a different port, update `hibernate.cfg.xml`:
   ```xml
   <property name="hibernate.connection.url">
       jdbc:mysql://localhost:YOUR_PORT/batik_pos_db?...
   </property>
   ```

### ❌ Error: "Table 'User' doesn't exist"

**Problem:** Schema generation is disabled

**Solution:**
Change `hibernate.cfg.xml`:
```xml
<!-- Change from 'validate' to 'update' -->
<property name="hibernate.hbm2ddl.auto">update</property>
```

---

## Production Deployment

When deploying to production, follow these best practices:

### 1. Use Validate Mode

```xml
<!-- Prevent auto-schema changes in production -->
<property name="hibernate.hbm2ddl.auto">validate</property>
```

### 2. Disable SQL Logging

```xml
<property name="hibernate.show_sql">false</property>
<property name="hibernate.format_sql">false</property>
```

### 3. Create Dedicated Database User

```sql
CREATE USER 'batikpos_app'@'localhost' IDENTIFIED BY 'STRONG_PASSWORD';
GRANT SELECT, INSERT, UPDATE, DELETE ON batik_pos_db.* TO 'batikpos_app'@'localhost';
FLUSH PRIVILEGES;
```

Update `hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.username">batikpos_app</property>
<property name="hibernate.connection.password">STRONG_PASSWORD</property>
```

### 4. Configure Connection Pool

```xml
<!-- Production connection pool settings -->
<property name="hibernate.hikari.minimumIdle">10</property>
<property name="hibernate.hikari.maximumPoolSize">50</property>
<property name="hibernate.hikari.connectionTimeout">30000</property>
<property name="hibernate.hikari.idleTimeout">600000</property>
<property name="hibernate.hikari.maxLifetime">1800000</property>
```

### 5. Set Up Regular Backups

```bash
#!/bin/bash
# Daily backup script
BACKUP_DIR="/var/backups/batik-pos"
DATE=$(date +%Y%m%d_%H%M%S)

mysqldump -u root -p batik_pos_db > "$BACKUP_DIR/batik_pos_db_$DATE.sql"

# Keep only last 30 days
find "$BACKUP_DIR" -name "*.sql" -mtime +30 -delete
```

### 6. Enable SSL Connection

```xml
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3306/batik_pos_db?useSSL=true&amp;requireSSL=true
</property>
```

---

## Alternative: PostgreSQL Setup

If you prefer PostgreSQL over MySQL:

### 1. Uncomment PostgreSQL Dependency

In `pom.xml`, uncomment:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

### 2. Update hibernate.cfg.xml

```xml
<property name="hibernate.connection.url">
    jdbc:postgresql://localhost:5432/batik_pos_db
</property>

<property name="hibernate.connection.driver_class">
    org.postgresql.Driver
</property>

<property name="hibernate.dialect">
    org.hibernate.dialect.PostgreSQLDialect
</property>
```

### 3. Create Database in PostgreSQL

```bash
psql -U postgres
CREATE DATABASE batik_pos_db;
\q
```

---

## Next Steps

Once database setup is complete:

✅ **Phase 1, Step 2** - COMPLETE

**Next:** Phase 1, Step 3 - Create all 9 Hibernate Entity classes

---

## Support & References

- **SRS Section 1.6:** Assumptions and Dependencies
- **SRS Section 3.2:** Data Persistence
- **SRS Section 6:** Database Requirements (Data Dictionary)
- **SQL Scripts:** `src/main/resources/sql/README.md`

For issues, check the troubleshooting section or review the Hibernate logs.
