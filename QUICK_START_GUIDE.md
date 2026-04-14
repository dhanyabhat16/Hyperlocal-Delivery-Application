# Quick Start Guide - Customer Module

## Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher  
- **MySQL**: 8.0 or higher
- **Git**: For version control
- **IDE**: Visual Studio Code or IntelliJ IDEA (optional)

## Step 1: Clone/Setup Project

```bash
# Navigate to project directory
cd .\Hyperlocal-Delivery-Application

# Verify Maven installation
mvn --version

# Verify Java installation
java -version
```

## Step 2: Setup MySQL Database

### Option A: Using MySQL Command Line

```bash
# Start MySQL server
# Windows: Use MySQL Command Line Client or MySQL Server

# Login to MySQL
mysql -u root -p
# Enter password: root

# Create database (if not auto-created)
CREATE DATABASE hyperlocal_delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Exit MySQL
EXIT;
```

### Option B: Using MySQL Workbench
1. Open MySQL Workbench
2. Create new connection or use existing
3. Execute SQL commands to create database

## Step 3: Verify Database Configuration

Edit `src/main/resources/application.properties`:

```properties
# Check these settings match your MySQL setup
spring.datasource.url=jdbc:mysql://localhost:3306/hyperlocal_delivery_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

## Step 4: Install Dependencies

```bash
# From project root directory
mvn clean install

# This will:
# - Download all dependencies
# - Compile the code
# - Run tests (if any)
# - Create JAR file
```

## Step 5: Run the Application

### Option A: Using Maven

```bash
mvn spring-boot:run
```

### Option B: Using Java (fastest)

```bash
# Build first
mvn clean package

# Run the JAR
java -jar target/hyperlocaldelivery-0.0.1-SNAPSHOT.jar
```

### Option C: IDE (VS Code)

1. Install "Extension Pack for Java"
2. Press `Ctrl+Shift+D` (Debug)
3. Click "Run" on HyperlocaldeliveryApplication

## Step 6: Verify Application is Running

Check console for messages like:
```
Started HyperlocaldeliveryApplication in X seconds
Tomcat started on port(s): 8080
```

## Step 7: Test the API

### Test 1: Health Check
```bash
# Check if application is running
curl http://localhost:8080/hyperlocaldelivery/api/customers
```

### Test 2: Register Customer
```bash
curl -X POST http://localhost:8080/hyperlocaldelivery/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "address": "123 Test Street"
  }'
```

### Test 3: Get All Products
```bash
curl http://localhost:8080/hyperlocaldelivery/api/products
```

If getting JSON responses, the application is working! ✅

## Step 8: Load Sample Data (Optional)

```bash
# Login to MySQL
mysql -u root -p hyperlocal_delivery_db < sample_data.sql

# Or copy-paste SQL commands from sample_data.sql into MySQL Workbench
```

## Troubleshooting

### Issue: Port 8080 Already in Use

**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: MySQL Connection Refused

**Solutions**:
1. Verify MySQL is running
   ```bash
   # Windows
   net start MySQL80
   
   # Linux
   sudo service mysql start
   
   # Mac
   brew services start mysql
   ```

2. Verify credentials in `application.properties`
3. Check firewall settings

### Issue: Database Not Created

**Solution**: 
- Set `createDatabaseIfNotExist=true` in connection URL (already done)
- Or manually create database: `CREATE DATABASE hyperlocal_delivery_db;`

### Issue: Build Fails with Java Version Error

**Solution**: Update Java version
```bash
# Check Java version
java -version

# Download JDK 17 from https://www.oracle.com/java/technologies/downloads/
```

### Issue: Maven Not Found

**Solution**: Install Maven
- Download from https://maven.apache.org/download.cgi
- Add to system PATH

## IDE Setup

### VS Code

1. **Install Extensions**:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - MySQL

2. **Open Project**:
   - File → Open Folder → Select project

3. **Run Application**:
   - Press `F5` or Click Run

4. **Debug**:
   - Set breakpoints
   - Press `F5` to start debug mode

### IntelliJ IDEA

1. **Open Project**:
   - File → Open → Select project directory
   - Trust project when prompted

2. **Configure JDK**:
   - File → Project Structure → SDK
   - Ensure JDK 17+ is selected

3. **Run Application**:
   - Right-click `HyperlocaldeveryApplication.java`
   - Select "Run"

4. **View Database**:
   - View → Tool Windows → Database
   - Add MySQL connection

## Database Verification

### Check Tables Created Automatically

```bash
# Login to MySQL
mysql -u root -p hyperlocal_delivery_db

# Show all tables
SHOW TABLES;

# Expected tables:
# - users
# - customers
# - products
# - carts
# - cart_items
# - orders
# - order_items
# - payments

# View table structure
DESCRIBE users;
DESCRIBE customers;
```

## API Documentation

For complete API documentation, see:
- `API_TESTING_GUIDE.md` - Detailed endpoints with examples
- `CUSTOMER_MODULE_README.md` - Architecture and design details

## Common Operations

### Start Fresh (Reset Database)

```bash
# Delete database
mysql -u root -p -e "DROP DATABASE hyperlocal_delivery_db;"

# Restart application
# Tables and database will be recreated automatically
```

### View Application Logs

Look in console for:
- SQL queries (if debug enabled)
- Request/response logs
- Error messages

### Stop Application

```bash
# In terminal
Ctrl+C

# This gracefully shuts down the application
```

## Next Steps

1. ✅ Application is running
2. ✅ Database is created
3. ✅ API is accessible

### Now You Can:
- Test all endpoints using the `API_TESTING_GUIDE.md`
- Load sample data for testing
- Integrate with other modules
- Add your custom logic

## Performance Tips

1. **Disable Debug Logging** (for production):
   ```properties
   logging.level.org.hibernate.SQL=INFO
   ```

2. **Enable Query Caching**:
   ```properties
   spring.jpa.properties.hibernate.cache.use_second_level_cache=true
   ```

3. **Use Connection Pooling** (already enabled):
   - HikariCP is used by default

## Security Reminders

⚠️ **Before Production Deployment**:

- [ ] Change default passwords
- [ ] Enable HTTPS/SSL
- [ ] Implement JWT authentication
- [ ] Validate all inputs
- [ ] Use BCrypt for passwords
- [ ] Restrict CORS origins
- [ ] Enable rate limiting
- [ ] Add request validation

## Useful Commands

```bash
# Clean build
mvn clean

# Skip tests
mvn install -DskipTests

# Run specific test
mvn test -Dtest=TestClassName

# Create JAR
mvn package

# View dependencies
mvn dependency:tree

# Update dependencies
mvn versions:display-dependency-updates
```

## Contact & Support

For issues:
1. Check `Troubleshooting` section above
2. Review `CUSTOMER_MODULE_README.md`
3. Check MySQL/Java installations
4. Verify all ports are available

---

**Ready to go!** 🚀

Your Hyperlocal Delivery Application Customer Module should now be running on:
- **URL**: http://localhost:8080/hyperlocaldelivery
- **API Base**: http://localhost:8080/hyperlocaldelivery/api
- **Database**: hyperlocal_delivery_db

Start testing the APIs! 🎉

