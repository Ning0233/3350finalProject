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
