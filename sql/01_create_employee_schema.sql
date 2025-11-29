-- create_employee_schema.sql (renamed to ensure it runs before alter scripts)
CREATE DATABASE IF NOT EXISTS employeeData CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE employeeData;

-- Employee table (includes SSN column, no dashes, 9 digits stored as VARCHAR(9))
CREATE TABLE IF NOT EXISTS employee (
  id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  ssn VARCHAR(9) UNIQUE,
  job_title VARCHAR(100),
  division VARCHAR(100),
  salary DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  is_full_time BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_job_title(job_title),
  INDEX idx_division(division)
) ENGINE=InnoDB;

-- Pay statement / payroll history table
CREATE TABLE IF NOT EXISTS pay_statement (
  id INT AUTO_INCREMENT PRIMARY KEY,
  employee_id INT NOT NULL,
  pay_date DATE NOT NULL,
  gross_pay DECIMAL(12,2) NOT NULL,
  deductions DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  net_pay DECIMAL(12,2) NOT NULL,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
  INDEX idx_pay_date(pay_date)
) ENGINE=InnoDB;

-- Helpful view: latest pay per employee (optional)
CREATE OR REPLACE VIEW v_latest_pay AS
SELECT p.*
FROM pay_statement p
JOIN (
  SELECT employee_id, MAX(pay_date) AS max_date
  FROM pay_statement
  GROUP BY employee_id
) m ON p.employee_id = m.employee_id AND p.pay_date = m.max_date;
