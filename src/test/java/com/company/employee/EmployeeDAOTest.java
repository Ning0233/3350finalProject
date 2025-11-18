package com.company.employee;

import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeeDAOTest {
    private Properties testProps = new Properties();
    private String serverUrl;
    private String testDbUrl;
    private String user;
    private String password;

    @BeforeAll
    public void setupDatabase() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db-test.properties")) {
            if (in == null) throw new IllegalStateException("db-test.properties not found in test resources");
            testProps.load(in);
        }
        serverUrl = testProps.getProperty("db.serverUrl");
        testDbUrl = testProps.getProperty("db.url");
        user = testProps.getProperty("db.user");
        password = testProps.getProperty("db.password");

        // Create fresh test database
        try (Connection c = DriverManager.getConnection(serverUrl, user, password); Statement s = c.createStatement()) {
            s.executeUpdate("DROP DATABASE IF EXISTS employeeData_test");
            s.executeUpdate("CREATE DATABASE employeeData_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        // Create tables and insert sample data
        try (Connection c = DriverManager.getConnection(testDbUrl, user, password); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE employee (id INT AUTO_INCREMENT PRIMARY KEY, first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL, ssn VARCHAR(9) UNIQUE, job_title VARCHAR(100), division VARCHAR(100), salary DECIMAL(12,2) NOT NULL DEFAULT 0.00, is_full_time BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            s.executeUpdate("CREATE TABLE pay_statement (id INT AUTO_INCREMENT PRIMARY KEY, employee_id INT NOT NULL, pay_date DATE NOT NULL, gross_pay DECIMAL(12,2) NOT NULL, deductions DECIMAL(12,2) NOT NULL DEFAULT 0.00, net_pay DECIMAL(12,2) NOT NULL, notes TEXT, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE)");

            // Insert sample employees
            s.executeUpdate("INSERT INTO employee (first_name, last_name, ssn, job_title, division, salary, is_full_time) VALUES ('Alice','Johnson','111223333','Software Engineer','Engineering',75000.00,TRUE)");
            s.executeUpdate("INSERT INTO employee (first_name, last_name, ssn, job_title, division, salary, is_full_time) VALUES ('Bob','Lee','222334444','Software Engineer','Engineering',92000.00,TRUE)");
            s.executeUpdate("INSERT INTO employee (first_name, last_name, ssn, job_title, division, salary, is_full_time) VALUES ('Carol','Smith','333445555','QA Analyst','Quality',54000.00,TRUE)");

            // Insert pay statements
            s.executeUpdate("INSERT INTO pay_statement (employee_id, pay_date, gross_pay, deductions, net_pay, notes) VALUES (1,'2025-11-15',6250.00,1250.00,5000.00,'Bi-weekly')");
            s.executeUpdate("INSERT INTO pay_statement (employee_id, pay_date, gross_pay, deductions, net_pay, notes) VALUES (2,'2025-11-15',7666.67,1533.34,6133.33,'Bi-weekly')");
            s.executeUpdate("INSERT INTO pay_statement (employee_id, pay_date, gross_pay, deductions, net_pay, notes) VALUES (3,'2025-11-15',4500.00,900.00,3600.00,'Bi-weekly')");
        }
    }

    @AfterAll
    public void teardownDatabase() throws Exception {
        try (Connection c = DriverManager.getConnection(serverUrl, user, password); Statement s = c.createStatement()) {
            s.executeUpdate("DROP DATABASE IF EXISTS employeeData_test");
        }
    }

    @Test
    public void testInsertFindUpdate() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee e = new Employee(0, "Dana", "White", "444556777", "HR", "HR", 48000.00);
        dao.insertEmployee(e);
        assertTrue(e.getId() > 0, "Inserted employee should have id");

        Employee found = dao.findById(e.getId());
        assertNotNull(found);
        assertEquals("Dana", found.getFirstName());

        found.setLastName("Black");
        found.setSalary(50000.00);
        dao.updateEmployee(found);

        Employee updated = dao.findById(found.getId());
        assertEquals("Black", updated.getLastName());
        assertEquals(50000.00, updated.getSalary());
    }

    @Test
    public void testFindBySSNAndName() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee a = dao.findBySSN("111223333");
        assertNotNull(a);
        assertEquals("Alice", a.getFirstName());

        var list = dao.findByName("Lee");
        assertFalse(list.isEmpty());
        assertEquals("Bob", list.get(0).getFirstName());
    }

    @Test
    public void testUpdateSalaryByRange() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        // increase 3.2% for salaries >= 50000 and < 90000
        int affected = dao.updateSalaryByRange(3.2, 50000.00, 90000.00);
        assertTrue(affected >= 1);

        // verify Alice's salary changed (was 75000)
        Employee alice = dao.findBySSN("111223333");
        assertNotNull(alice);
        assertEquals(75000.00 * 1.032, alice.getSalary(), 0.01);
    }

    @Test
    public void testReports() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        var combined = dao.getFullTimeEmployeePayHistory();
        assertFalse(combined.isEmpty());

        var byJob = dao.getTotalPayByJobTitle(2025, 11);
        assertFalse(byJob.isEmpty());

        var byDiv = dao.getTotalPayByDivision(2025, 11);
        assertFalse(byDiv.isEmpty());
    }
}
