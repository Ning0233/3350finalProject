package com.company.employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

public class EmployeeDAO {
    private String url;
    private String user;
    private String password;

    public EmployeeDAO() throws Exception {
        this("db.properties");
    }

    /**
     * Load DB connection properties from a specific resource on the classpath.
     * Useful for tests which can provide `db-test.properties`.
     */
    public EmployeeDAO(String resourceName) throws Exception {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalStateException(resourceName + " not found in resources");
            }
            props.load(in);
        }
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password");
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    // REPORT: Full-time employee information with pay statement history
    public List<CombinedPay> getFullTimeEmployeePayHistory() throws SQLException {
        String sql = "SELECT e.id, e.first_name, e.last_name, e.ssn, e.job_title, e.division, e.salary, "
                + "p.pay_date, p.gross_pay, p.deductions, p.net_pay, p.notes "
                + "FROM employee e JOIN pay_statement p ON e.id = p.employee_id "
                + "WHERE e.is_full_time = TRUE "
                + "ORDER BY e.last_name, p.pay_date DESC";
        List<CombinedPay> out = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CombinedPay cp = new CombinedPay(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("ssn"),
                        rs.getString("job_title"),
                        rs.getString("division"),
                        rs.getDouble("salary"),
                        rs.getDate("pay_date"),
                        rs.getDouble("gross_pay"),
                        rs.getDouble("deductions"),
                        rs.getDouble("net_pay"),
                        rs.getString("notes")
                );
                out.add(cp);
            }
        }
        return out;
    }

    // REPORT: Total pay for a month by job title
    public List<JobTitlePayTotal> getTotalPayByJobTitle(int year, int month) throws SQLException {
        String sql = "SELECT e.job_title, SUM(p.gross_pay) AS total_gross_pay, SUM(p.net_pay) AS total_net_pay "
                + "FROM employee e JOIN pay_statement p ON e.id = p.employee_id "
                + "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? "
                + "GROUP BY e.job_title ORDER BY total_gross_pay DESC";
        List<JobTitlePayTotal> out = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new JobTitlePayTotal(
                            rs.getString("job_title"),
                            rs.getDouble("total_gross_pay"),
                            rs.getDouble("total_net_pay")
                    ));
                }
            }
        }
        return out;
    }

    // REPORT: Total pay for a month by division
    public List<DivisionPayTotal> getTotalPayByDivision(int year, int month) throws SQLException {
        String sql = "SELECT e.division, SUM(p.gross_pay) AS total_gross_pay, SUM(p.net_pay) AS total_net_pay "
                + "FROM employee e JOIN pay_statement p ON e.id = p.employee_id "
                + "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? "
                + "GROUP BY e.division ORDER BY total_gross_pay DESC";
        List<DivisionPayTotal> out = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new DivisionPayTotal(
                            rs.getString("division"),
                            rs.getDouble("total_gross_pay"),
                            rs.getDouble("total_net_pay")
                    ));
                }
            }
        }
        return out;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void insertEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employee (first_name, last_name, address, ssn, job_title, division, salary, is_full_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getFirstName());
            ps.setString(2, e.getLastName());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getSsn());
            ps.setString(5, e.getJobTitle());
            ps.setString(6, e.getDivision());
            ps.setDouble(7, e.getSalary());
            ps.setBoolean(8, e.isFullTime());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }
        }
    }

    public Employee findById(int id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, address, ssn, job_title, division, salary, is_full_time FROM employee WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Employee findBySSN(String ssn) throws SQLException {
        String sql = "SELECT id, first_name, last_name, address, ssn, job_title, division, salary, is_full_time FROM employee WHERE ssn = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ssn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Employee> findByName(String nameFragment) throws SQLException {
        String sql = "SELECT id, first_name, last_name, address, ssn, job_title, division, salary, is_full_time FROM employee WHERE first_name LIKE ? OR last_name LIKE ?";
        List<Employee> out = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + nameFragment + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }

    // list all employees
    public List<Employee> listAll() throws SQLException {
        String sql = "SELECT id, first_name, last_name, address, ssn, job_title, division, salary, is_full_time FROM employee ORDER BY last_name, first_name";
        List<Employee> out = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }

    public boolean updateEmployee(Employee e) throws SQLException {
        String sql = "UPDATE employee SET first_name = ?, last_name = ?, address = ?, ssn = ?, job_title = ?, division = ?, salary = ?, is_full_time = ? WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getFirstName());
            ps.setString(2, e.getLastName());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getSsn());
            ps.setString(5, e.getJobTitle());
            ps.setString(6, e.getDivision());
            ps.setDouble(7, e.getSalary());
            ps.setBoolean(8, e.isFullTime());
            ps.setInt(9, e.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public int updateSalaryByRange(double percent, double minInclusive, double maxExclusive) throws SQLException {
        if (percent <= 0) throw new IllegalArgumentException("Percentage must be positive.");
        String sql = "UPDATE employee SET salary = salary * (1 + ?/100) WHERE salary >= ? AND salary < ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, percent);
            ps.setDouble(2, minInclusive);
            ps.setDouble(3, maxExclusive);
            return ps.executeUpdate();
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("ssn"),
            rs.getString("job_title"),
            rs.getString("division"),
            rs.getString("address"),
            rs.getDouble("salary"),
            rs.getBoolean("is_full_time")
        );
    }
}
