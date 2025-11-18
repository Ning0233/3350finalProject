-- report_queries.sql
-- Useful reporting queries for DBeaver/MySQL. Replace YEAR()/MONTH() values as needed.

USE employeeData;

-- 1) Full-time employee information with pay statement history
-- Returns one row per pay statement for full-time employees
SELECT e.id, e.first_name, e.last_name, e.ssn, e.job_title, e.division, e.salary,
       p.pay_date, p.gross_pay, p.deductions, p.net_pay, p.notes
FROM employee e
JOIN pay_statement p ON e.id = p.employee_id
WHERE e.is_full_time = TRUE
ORDER BY e.last_name, p.pay_date DESC;

-- 2) Total pay for a month by job title (gross and net)
-- Change the YEAR and MONTH to the target period
SELECT e.job_title,
       SUM(p.gross_pay) AS total_gross_pay,
       SUM(p.net_pay) AS total_net_pay
FROM employee e
JOIN pay_statement p ON e.id = p.employee_id
WHERE YEAR(p.pay_date) = 2025 AND MONTH(p.pay_date) = 11
GROUP BY e.job_title
ORDER BY total_gross_pay DESC;

-- 3) Total pay for a month by division
SELECT e.division,
       SUM(p.gross_pay) AS total_gross_pay,
       SUM(p.net_pay) AS total_net_pay
FROM employee e
JOIN pay_statement p ON e.id = p.employee_id
WHERE YEAR(p.pay_date) = 2025 AND MONTH(p.pay_date) = 11
GROUP BY e.division
ORDER BY total_gross_pay DESC;

-- 4) Update salary by percentage for a salary range (example: 3.2% for >= 58000 and < 105000)
-- This is the same logic used by the Java DAO; test in a transaction or on a copy first.
START TRANSACTION;
UPDATE employee
SET salary = ROUND(salary * (1 + 3.2/100), 2)
WHERE salary >= 58000 AND salary < 105000;
-- SELECT * FROM employee WHERE salary >= 58000 AND salary < 105000;
COMMIT;

-- 5) Quick verification queries
-- Employees with current salary
SELECT id, first_name, last_name, job_title, division, salary FROM employee ORDER BY salary DESC;

-- Total payroll for the selected month across the company
SELECT SUM(gross_pay) AS total_gross_pay, SUM(net_pay) AS total_net_pay
FROM pay_statement p
WHERE YEAR(p.pay_date) = 2025 AND MONTH(p.pay_date) = 11;
