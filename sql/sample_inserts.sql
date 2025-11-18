-- sample_inserts.sql
-- Adds sample employees and pay statements for testing reports and salary updates.

USE employeeData;

-- Sample employees
INSERT INTO employee (first_name, last_name, ssn, job_title, division, salary, is_full_time)
VALUES
('Alice', 'Johnson', '111223333', 'Software Engineer', 'Engineering', 75000.00, TRUE),
('Bob', 'Lee', '222334444', 'Software Engineer', 'Engineering', 92000.00, TRUE),
('Carol', 'Smith', '333445555', 'QA Analyst', 'Quality', 54000.00, TRUE),
('David', 'Brown', '444556666', 'Project Manager', 'Operations', 120000.00, TRUE),
('Eve', 'Davis', '555667777', 'HR Specialist', 'HR', 48000.00, TRUE);

-- Sample pay statements (Nov 2025 and Oct 2025)
INSERT INTO pay_statement (employee_id, pay_date, gross_pay, deductions, net_pay, notes)
VALUES
(1, '2025-11-15', 6250.00, 1250.00, 5000.00, 'Bi-weekly pay'),
(1, '2025-10-31', 6250.00, 1250.00, 5000.00, 'Bi-weekly pay'),
(2, '2025-11-15', 7666.67, 1533.34, 6133.33, 'Bi-weekly pay'),
(2, '2025-10-31', 7666.67, 1533.34, 6133.33, 'Bi-weekly pay'),
(3, '2025-11-15', 4500.00, 900.00, 3600.00, 'Bi-weekly pay'),
(4, '2025-11-15', 10000.00, 2000.00, 8000.00, 'Bi-weekly pay'),
(5, '2025-11-15', 4000.00, 800.00, 3200.00, 'Bi-weekly pay');

-- Verify inserted data with:
-- SELECT * FROM employee;
-- SELECT * FROM pay_statement ORDER BY pay_date DESC;
