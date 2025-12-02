package com.company.employee;

import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
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

    @BeforeEach
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
            s.executeUpdate("CREATE TABLE employee (id INT AUTO_INCREMENT PRIMARY KEY, first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL, address VARCHAR(255), ssn VARCHAR(9) UNIQUE, job_title VARCHAR(100), division VARCHAR(100), salary DECIMAL(12,2) NOT NULL DEFAULT 0.00, is_full_time BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            s.executeUpdate("CREATE TABLE pay_statement (id INT AUTO_INCREMENT PRIMARY KEY, employee_id INT NOT NULL, pay_date DATE NOT NULL, gross_pay DECIMAL(12,2) NOT NULL, deductions DECIMAL(12,2) NOT NULL DEFAULT 0.00, net_pay DECIMAL(12,2) NOT NULL, notes TEXT, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE)");

            // Insert sample employees
            s.executeUpdate("INSERT INTO employee (first_name, last_name, address, ssn, job_title, division, salary, is_full_time) VALUES ('Alice','Johnson','123 Main St','111223333','Software Engineer','Engineering',75000.00,TRUE)");
            s.executeUpdate("INSERT INTO employee (first_name, last_name, address, ssn, job_title, division, salary, is_full_time) VALUES ('Bob','Lee','234 Oak Ave','222334444','Software Engineer','Engineering',92000.00,TRUE)");
            s.executeUpdate("INSERT INTO employee (first_name, last_name, address, ssn, job_title, division, salary, is_full_time) VALUES ('Carol','Smith','345 Pine Rd','333445555','QA Analyst','Quality',54000.00,TRUE)");
            // extra Smith for TC-B3
            s.executeUpdate("INSERT INTO employee (first_name, last_name, address, ssn, job_title, division, salary, is_full_time) VALUES ('Eve','Smith','456 Elm St','555667777','Developer','Engineering',60000.00,TRUE)");

            // Insert pay statements
            s.executeUpdate("INSERT INTO pay_statement (employee_id, pay_date, gross_pay, deductions, net_pay, notes) VALUES (1,'2025-11-15',6250.00,1250.00,5000.00,'Bi-weekly')");
            s.executeUpdate("INSERT INTO pay_statement (employee_id, pay_date, gross_pay, deductions, net_pay, notes) VALUES (2,'2025-11-15',7666.67,1533.34,6133.33,'Bi-weekly')");
            s.executeUpdate("INSERT INTO pay_statement (employee_id, pay_date, gross_pay, deductions, net_pay, notes) VALUES (3,'2025-11-15',4500.00,900.00,3600.00,'Bi-weekly')");
        }
    }

    @AfterEach
    public void teardownDatabase() throws Exception {
        try (Connection c = DriverManager.getConnection(serverUrl, user, password); Statement s = c.createStatement()) {
            s.executeUpdate("DROP DATABASE IF EXISTS employeeData_test");
        }
    }

    @Test
    public void testInsertFindUpdate() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee e = new Employee(0, "Dana", "White", "444556777", "HR", "HR", "789 Old Rd", 48000.00);
        dao.insertEmployee(e);
        assertTrue(e.getId() > 0, "Inserted employee should have id");

        Employee found = dao.findById(e.getId());
        assertNotNull(found);
        assertEquals("Dana", found.getFirstName());

        found.setLastName("Black");
        found.setSalary(50000.00);
        boolean ok = dao.updateEmployee(found);
        assertTrue(ok);

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

    // TC-A1: Update address successfully
    @Test
    public void tc_A1_updateAddress() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee e = dao.findById(1);
        assertNotNull(e);
        e.setAddress("456 New Ave");
        boolean ok = dao.updateEmployee(e);
        assertTrue(ok);
        Employee updated = dao.findById(1);
        assertEquals("456 New Ave", updated.getAddress());
    }

    // TC-A2: Update non-existing employee
    @Test
    public void tc_A2_updateNonExisting() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee e = new Employee();
        e.setId(9999);
        e.setFirstName("Ghost");
        boolean ok = dao.updateEmployee(e);
        assertFalse(ok);
    }

    // TC-A3: Update multiple fields
    @Test
    public void tc_A3_updateMultipleFields() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee e = dao.findById(2);
        assertNotNull(e);
        e.setJobTitle("Senior Dev");
        e.setDivision("Engineering");
        boolean ok = dao.updateEmployee(e);
        assertTrue(ok);
        Employee updated = dao.findById(2);
        assertEquals("Senior Dev", updated.getJobTitle());
        assertEquals("Engineering", updated.getDivision());
    }

    // TC-B1: Search by valid empId
    @Test
    public void tc_B1_searchById() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee e = dao.findById(1);
        assertNotNull(e);
    }

    // TC-B2: Search by SSN not in DB
    @Test
    public void tc_B2_searchBySsnNotFound() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        Employee e = dao.findBySSN("999999999");
        assertNull(e);
    }

    // TC-B3: Search by name with multiple matches
    @Test
    public void tc_B3_searchByNameMultiple() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        var list = dao.findByName("Smith");
        assertTrue(list.size() >= 2, "Expect at least two Smiths");
    }

    @Test
    public void testUpdateSalaryByRange() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        // increase 3.2% for salaries >= 50000 and < 90000
        int affected = dao.updateSalaryByRange(3.2, 58000.00, 105000.00);
        assertTrue(affected >= 1);

        // verify Alice's salary changed (was 75000)
        Employee alice = dao.findBySSN("111223333");
        assertNotNull(alice);
        assertEquals(75000.00 * 1.032, alice.getSalary(), 0.01);
    }

    // TC-C1: Apply increase to valid range and verify multiple employees
    @Test
    public void tc_C1_salaryIncreaseValidRange() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        // employees in range 58000..105000: Alice(75000), Bob(92000), Eve(60000)
        int affected = dao.updateSalaryByRange(3.2, 58000.00, 105000.00);
        assertEquals(3, affected);
        Employee a = dao.findBySSN("111223333");
        Employee b = dao.findBySSN("222334444");
        Employee e = dao.findBySSN("555667777");
        assertEquals(75000.00 * 1.032, a.getSalary(), 0.01);
        assertEquals(92000.00 * 1.032, b.getSalary(), 0.01);
        assertEquals(60000.00 * 1.032, e.getSalary(), 0.01);
    }

    // TC-C2: Range with no employees
    @Test
    public void tc_C2_rangeNoEmployees() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        int affected = dao.updateSalaryByRange(3.0, 300000.00, 400000.00);
        assertEquals(0, affected);
    }

    // TC-C3: Invalid (negative) percentage
    @Test
    public void tc_C3_negativePercentage() throws Exception {
        EmployeeDAO dao = new EmployeeDAO("db-test.properties");
        var ex = assertThrows(IllegalArgumentException.class, () -> dao.updateSalaryByRange(-5.0, 50000.00, 100000.00));
        assertTrue(ex.getMessage().contains("positive"));
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
