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
