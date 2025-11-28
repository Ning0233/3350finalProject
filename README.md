# Employee Management — Final Project

This repository contains a minimal employee management application (console, JDBC DAO, and a small Spring Boot web UI) used for the course final project. It includes SQL schema, a Docker Compose setup for a local MySQL instance, JUnit tests, and a lightweight Thymeleaf UI with HTTPS support for local development.

**Quick facts**
- **Main app:** `src/main/java/com/company/employee` (DAO + models)
- **Web UI:** Spring Boot + Thymeleaf (`WebApp`, `WebController`, templates in `src/main/resources/templates`)
- **Reports:** REST endpoints under `/api/reports` and Thymeleaf UI for employee list
- **DB init SQL:** `sql/` (schema and init scripts)

**Prerequisites**
- Java 17+ (Java 21 tested)
- Maven 3.8+
- Docker (for local MySQL) or a local MySQL server
- `openssl` (optional; used by the keystore script)

**1) Start the local MySQL (Docker)**
The project includes `docker-compose.yml` which creates a MySQL container and runs the SQL files in `./sql` at initialization.

From the project root:
```bash
docker compose up -d
docker logs -f fp_mysql --tail 200
```

If you reconfigured SQL or users and the container was already initialized, reinitialize the volume to run init scripts again:
```bash
docker compose down
docker volume rm fp_mysql_data
docker compose up -d
```

Credentials created by the compose/init scripts (for local development):
- root: `rootpass`
- app user: `proj_user` / `proj_pass` (has privileges on `employeeData`)
- test user: `test_user` / `secret123` (used by tests)

If you prefer to manage the DB with DBeaver / Workbench, connect to `127.0.0.1:3306` using the above credentials and run `SHOW TABLES IN employeeData;`.

**2) Generate local HTTPS keystore (development only)**
The app is configured to serve HTTPS on port `8443` and redirect HTTP `8080` to HTTPS. A script creates a self-signed PKCS12 keystore used by Spring Boot.

Run once from project root:
```bash
chmod +x scripts/generate-keystore.sh
./scripts/generate-keystore.sh
```
This creates `src/main/resources/keystore.p12` (password `changeit`) — DO NOT use this in production.

**3) Build and run the web app**
```bash
# Build (skips tests as needed)
mvn -DskipTests package

# Run with Maven (recommended during development)
mvn spring-boot:run

# Or run the packaged JAR
java -jar target/employee-mgmt-1.0-SNAPSHOT.jar
```

The server listens on HTTPS `https://localhost:8443/` (self-signed certificate — accept the warning in your browser). HTTP `http://localhost:8080` will redirect to HTTPS by configuration.

If port `8080` is already in use you can either stop the other process or override the HTTP redirect port:
```bash
# temporary override to use 8081 for HTTP redirect
mvn -Dspring-boot.run.arguments="--server.http.port=8081" spring-boot:run
```

**4) Connect to the web UI**
- Open: `https://localhost:8443/` and accept the browser warning for the self-signed cert.
- The index page lists employees; use "New Employee" to add records.

Example curl calls (ignore cert warning with `-k`):
```bash
# Full-time pay report (JSON)
curl -k https://127.0.0.1:8443/api/reports/fulltime-pay

# Create an employee (JSON POST)
curl -k -X POST -H "Content-Type: application/json" \
  -d '{"firstName":"Alice","lastName":"Smith","ssn":"123456789","jobTitle":"Engineer","division":"R&D","salary":90000}' \
  https://127.0.0.1:8443/api/employees
```

**5) Run tests**
JUnit tests use the `db-test.properties` resource (see `src/test/resources/db-test.properties`) and will connect to the test DB at the URL configured there. To run tests:
```bash
mvn -U test
```

If tests fail due to DB initialization or user/privilege mismatch, ensure the Docker container was initialized after `sql/00_create_users.sql` was added and that the test user exists with sufficient grants. To reinitialize the DB (destructive):
```bash
docker compose down
docker volume rm fp_mysql_data
docker compose up -d
```

**6) Connecting with DBeaver / Workbench**
- Host: `127.0.0.1`
- Port: `3306`
- Database: `employeeData`
- User: `proj_user` / `proj_pass` (or `root` / `rootpass`)

If you encounter the JDBC error "Public Key Retrieval is not allowed", add driver property `allowPublicKeyRetrieval=true` and `useSSL=false` in the driver properties or in the JDBC URL (the project `db.properties` is already configured for this).

**7) Troubleshooting**
- Address in use on port 8080: find and stop the process:
  ```bash
  lsof -iTCP:8080 -sTCP:LISTEN -n -P
  kill <PID>
  ```
- Empty reports: confirm there are `pay_statement` rows in `employeeData`:
  ```bash
  docker exec -i fp_mysql mysql -u root -prootpass -e "USE employeeData; SELECT COUNT(*) FROM pay_statement;"
  ```
- If init SQL failed during `docker compose up`, check `docker logs fp_mysql --tail 200` and fix the SQL scripts in `./sql`.

**8) Project notes & next steps**
- `EmployeeDAO` contains the core JDBC operations.
- Web UI is intentionally minimal (no authentication) for the assignment.
- Optional improvements: add server-rendered reports, form validation, authentication, and a production-ready TLS certificate.

If you want, I can add a short Postman collection, export a DBeaver connection profile, or add server-rendered reports to the UI next.

---
Files of interest:
- `src/main/resources/db.properties` — runtime DB properties
- `src/test/resources/db-test.properties` — test DB properties
- `sql/create_employee_schema.sql` — schema
- `sql/00_create_users.sql` — user/grant initialization
- `scripts/generate-keystore.sh` — create dev PKCS12 keystore

Happy to update this README with screenshots or a short video guide if you'd like.

**Dependencies (Maven)**
- **Spring Boot Parent:** `org.springframework.boot:spring-boot-starter-parent:3.2.12` (project parent)
- **Spring Web:** `org.springframework.boot:spring-boot-starter-web` (managed by Spring Boot parent)
- **Thymeleaf:** `org.springframework.boot:spring-boot-starter-thymeleaf` (managed by Spring Boot parent)
- **MySQL Connector/J:** `mysql:mysql-connector-java:8.0.33`
- **JUnit 5:** `org.junit.jupiter:junit-jupiter-api:5.10.0` and `org.junit.jupiter:junit-jupiter-engine:5.10.0` (test scope)

Key build plugins (in `pom.xml`):
- `org.springframework.boot:spring-boot-maven-plugin` (Spring Boot packaging/run)
- `org.apache.maven.plugins:maven-compiler-plugin:3.11.0` (compiler)
- `org.codehaus.mojo:exec-maven-plugin:3.1.0` (main class execution during development)

Java version: project targets **Java 17** (see `pom.xml` properties `maven.compiler.source` / `java.version`).

If you want a short `pom.xml` snippet or a Gradle equivalent, tell me which format you prefer and I can add it.
# Employee Management - Final Project Scaffold

This is a minimal scaffold for the employee management final project.

Quick start (macOS / zsh):

1. Edit `src/main/resources/db.properties` with your MySQL connection details (database `employeeData`).
2. Build:

```bash
mvn -q -DskipTests package
```

3. Run (uses exec plugin):

```bash
mvn -q exec:java -Dexec.mainClass="com.company.employee.App"
```

Notes:
- The app is a console UX prototype. You can switch to JavaFX later if desired.
- The `EmployeeDAO` uses JDBC; ensure MySQL is running and `employeeData` exists.

Running tests
1. Edit `src/test/resources/db-test.properties` with your MySQL credentials if necessary.
	- `db.serverUrl` should point to the server without a database path (used to create/drop the test DB).
	- `db.url` should include the test database name (the test harness will create/drop `employeeData_test`).
2. Run the tests with Maven:

```bash
mvn test
```

The tests create `employeeData_test` on your MySQL server and drop it when finished. Make sure your MySQL user in the test properties has privileges to create and drop databases.

Using Docker (recommended for local development)

1) Start the MySQL container (from project root):

```bash
docker compose up -d
```

This will start a MySQL 8.0 container that initializes the `employeeData` database and creates the local users `proj_user`/`proj_pass` and `test_user`/`secret123`. The SQL under `./sql` is mounted into the container and executed on first startup.

2) Verify the container is running:

```bash
docker ps
```

3) Run tests (they will use `src/test/resources/db-test.properties` which is preconfigured to use `test_user`):

```bash
mvn -U test
```

4) Run the app (it will use `src/main/resources/db.properties` which is preconfigured for `proj_user`):

```bash
mvn -DskipTests package
mvn exec:java -Dexec.mainClass="com.company.employee.App"
```

5) Stop and remove containers and volumes when done:

```bash
docker compose down -v
```

Notes:
- If you change credentials in `docker-compose.yml`, update `src/main/resources/db.properties` and `src/test/resources/db-test.properties` accordingly.
- For macOS / Docker Desktop the MySQL host is `localhost` and port `3306` is forwarded to the container.
