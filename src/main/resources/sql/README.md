# Database Setup Guide for Batik POS System

This directory contains SQL scripts for setting up and managing the Batik POS System database.

## Prerequisites

- MySQL Server 8.0 or higher installed and running
- MySQL Client or MySQL Workbench
- Administrative access to create databases

## Setup Instructions

### Step 1: Create the Database

Run the database creation script:

```bash
mysql -u root -p < 01_create_database.sql
```

Or in MySQL client:
```sql
source 01_create_database.sql
```

This will:
- Create the `batik_pos_db` database
- Set UTF-8 encoding (utf8mb4)

### Step 2: Configure Hibernate

Update the database credentials in `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">YOUR_PASSWORD</property>
```

### Step 3: Run the Application (First Time)

When you run the application for the first time, Hibernate will automatically create all tables based on the Entity classes. This is controlled by:

```xml
<property name="hibernate.hbm2ddl.auto">update</property>
```

### Step 4: Insert Initial Data

After the tables are created, insert the default Admin user:

```bash
mysql -u root -p batik_pos_db < 02_initial_data.sql
```

## Default Credentials

### Admin Account
- **Username:** `admin`
- **Password:** `admin123`

⚠️ **IMPORTANT:** Change the default password immediately after first login!

## Database Configuration

### Connection Settings

| Setting | Default Value | Description |
|---------|---------------|-------------|
| Host | localhost | MySQL server hostname |
| Port | 3306 | MySQL server port |
| Database | batik_pos_db | Database name |
| Username | root | Database user |
| Password | root | Database password (CHANGE THIS!) |
| Encoding | UTF-8 (utf8mb4) | Character encoding |

### Hibernate Settings

| Setting | Value | Purpose |
|---------|-------|---------|
| `hbm2ddl.auto` | update | Auto-create/update tables |
| `show_sql` | true | Log SQL statements (development) |
| `format_sql` | true | Format SQL for readability |
| `dialect` | MySQL8Dialect | MySQL 8.x compatibility |

## Tables Created by Hibernate

When you run the application, Hibernate will create the following 9 tables:

1. **User** - System users (Admin, Cashier)
2. **Customer** - Customer information
3. **Supplier** - Supplier details
4. **Product** - Master product catalog
5. **ProductVariant** - Product variants (SKU, size, color, stock)
6. **GRN** - Goods Received Notes (stock intake)
7. **GRNItem** - GRN line items
8. **Sale** - Sales transactions
9. **SaleItem** - Sale line items

## Troubleshooting

### Error: "Access denied for user 'root'@'localhost'"

**Solution:** Check your MySQL username and password in `hibernate.cfg.xml`

```bash
# Test MySQL connection
mysql -u root -p
```

### Error: "Unknown database 'batik_pos_db'"

**Solution:** Run the database creation script:

```bash
mysql -u root -p < 01_create_database.sql
```

### Error: "Communications link failure"

**Solutions:**
1. Ensure MySQL server is running:
   ```bash
   # Linux
   sudo systemctl status mysql

   # Windows
   # Check Services for "MySQL80"

   # macOS
   mysql.server status
   ```

2. Check the port (default: 3306):
   ```bash
   netstat -an | grep 3306
   ```

### Error: "Table doesn't exist"

**Solution:** This happens if `hbm2ddl.auto` is set to `validate` before tables are created. Change it to `update`:

```xml
<property name="hibernate.hbm2ddl.auto">update</property>
```

## For Production Deployment

When deploying to production:

1. **Change the schema generation strategy:**
   ```xml
   <property name="hibernate.hbm2ddl.auto">validate</property>
   ```

2. **Disable SQL logging:**
   ```xml
   <property name="hibernate.show_sql">false</property>
   ```

3. **Use strong passwords** for database access

4. **Configure regular backups:**
   ```bash
   # Create backup
   mysqldump -u root -p batik_pos_db > backup_$(date +%Y%m%d).sql

   # Restore backup
   mysql -u root -p batik_pos_db < backup_20250121.sql
   ```

5. **Set up proper user permissions:**
   ```sql
   CREATE USER 'batikpos_user'@'localhost' IDENTIFIED BY 'strong_password';
   GRANT ALL PRIVILEGES ON batik_pos_db.* TO 'batikpos_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

## Alternative: Using PostgreSQL

If you prefer PostgreSQL over MySQL:

1. Uncomment PostgreSQL dependency in `pom.xml`
2. Update `hibernate.cfg.xml`:
   ```xml
   <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/batik_pos_db</property>
   <property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
   <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>
   ```

## Support

For database-related issues, check:
- SRS Section 1.6: Assumptions and Dependencies
- SRS Section 3.2: Data Persistence
- SRS Section 6: Database Requirements
