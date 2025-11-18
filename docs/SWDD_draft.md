Software Design Document (Draft)

Table of Contents
- 1. Introduction
- 2. System Overview
- 3. Functional Requirements
- 4. Non-functional Requirements
- 5. Data Model and Schema
- 6. Programming Tasks (a, b, c)
- 7. Test Cases
- 8. Code Organization / Class Summary
- 9. Reports
- 10. Deployment & Run Instructions
- 11. Appendices (SQL, sample data, tests)

---

1. Introduction

This document describes the design for a minimal Employee Management system for Company '2'. It includes the database schema, code organization, test cases, and report queries. The system provides a console UX for data insertion, search, update, salary adjustments, and reports.

2. System Overview

- Actors: Administrator (data entry and report consumer)
- Primary capabilities:
  - Insert/update/delete employee records
  - Search employees by ID, name fragment, or SSN
  - Bulk salary updates by percentage for a specified salary range
  - Reports: full-time employees with pay history, total monthly pay by job title, total monthly pay by division

3. Functional Requirements

- Add `SSN` column to the `employee` table (9 digits, no dashes).
- Search employees by `empid`, `SSN`, `name` (partial match).
- Update any employee field.
- Increase salary for employees whose salary falls in a given range by a percentage.
- Generate 3 reports (see Section 9).

4. Non-functional Requirements

- Use MySQL for data persistence; DBeaver used for manual data entry and query running.
- Console UX for quick minimal working solution (Java, JDBC).
- No authentication/authorization required.

5. Data Model and Schema

Primary tables (files provided under `sql/`):
- `employee` (id, first_name, last_name, ssn, job_title, division, salary, is_full_time, created_at)
  - `ssn` stored as `VARCHAR(9)` (no dashes), unique index `uq_employee_ssn`.
- `pay_statement` (id, employee_id, pay_date, gross_pay, deductions, net_pay, notes, created_at)

Reference SQL files:
- `sql/create_employee_schema.sql` — creates `employeeData` DB, `employee` and `pay_statement` tables, and view `v_latest_pay`.
- `sql/alter_add_ssn.sql` — ALTER script to add `ssn` column to an existing table and create a unique index.
- `sql/sample_inserts.sql` — sample employees and pay statements for testing.
- `sql/report_queries.sql` — SQL queries for the required reports and the example bulk salary update.

Notes about SSN: input validation must ensure exactly 9 digits (client-side or via import process in DBeaver).

6. Programming Tasks (a, b, c)

Below are the three programming tasks derived from the user story and the mapping to the implemented code.

Task A — Update employee data (general update for all fields)
- Description: Implement a general update path for any employee field using `Employee` model and `EmployeeDAO.updateEmployee(Employee)`.
- File(s): `src/main/java/com/company/employee/Employee.java`, `EmployeeDAO.java`, `App.java` (update flow).
- Acceptance: The DAO updates the DB; retrieving the record shows the updated values.

Task B — Search for employee
- Description: Implement search by ID, SSN, and name fragment.
- File(s): `EmployeeDAO.findById`, `findBySSN`, `findByName`, `App.java` (search menu).
- Acceptance: Search returns correct records for each search type.

Task C — Update salary for employees within a salary range
- Description: Apply a percentage increase to salaries where salary >= min and < max.
- File(s): `EmployeeDAO.updateSalaryByRange`, `App.java` (menu to call this).
- Example: `3.2%` increase for `salary >= 58000 AND salary < 105000`.

7. Test Cases

The test harness is implemented with JUnit 5 in `src/test/java/com/company/employee/EmployeeDAOTest.java`.

Test case snapshots (pass/fail):

- Test: Insert, find, update employee (testInsertFindUpdate)
  - Setup: create test DB (`employeeData_test`), create tables, insert a few records.
  - Steps: insert new employee, assert id assigned, find by id, update last name and salary, assert updated values.
  - Expected: pass when DAO correctly inserts and updates rows.

- Test: Search by SSN and name fragment (testFindBySSNAndName)
  - Steps: find by SSN '111223333' -> expect 'Alice'; find by name fragment 'Lee' -> expect 'Bob'.
  - Expected: pass when DAO search works.

- Test: Salary range update (testUpdateSalaryByRange)
  - Steps: run `updateSalaryByRange(3.2, 50000.00, 90000.00)`; validate affected rows >= 1 and Alice's salary updated accordingly.
  - Expected: pass when DAO updates salaries and persisted values reflect change.

- Test: Reports (testReports)
  - Steps: call three report methods and assert non-empty results based on sample data.
  - Expected: pass when report SQL executes and returns rows.

Running tests
- Update `src/test/resources/db-test.properties` with your MySQL credentials.
- Run `mvn test`; the tests will create and drop `employeeData_test` (ensure the user has create/drop privileges).

8. Code Organization / Class Summary

Main packages and classes:
- `com.company.employee`
  - `App` — console UI and menu
  - `Employee` — data model for employees
  - `EmployeeDAO` — JDBC data access object (insert, update, search, reports)
  - `CombinedPay` — DTO for employee + pay history row
  - `JobTitlePayTotal` — DTO for job-title totals
  - `DivisionPayTotal` — DTO for division totals

9. Reports

Implemented report methods (in `EmployeeDAO`):
- `getFullTimeEmployeePayHistory()` — returns `List<CombinedPay>`: rows join employee + pay_statement filtered by `is_full_time`.
- `getTotalPayByJobTitle(int year, int month)` — returns gross/net totals per job title.
- `getTotalPayByDivision(int year, int month)` — returns gross/net totals per division.

SQL equivalents are in `sql/report_queries.sql` — you can run these directly in DBeaver or call DAO methods from the console app.

10. Deployment & Run Instructions

- Edit `src/main/resources/db.properties` to point to your MySQL `employeeData` database.
- Build:

```bash
mvn -DskipTests package
```

- Run the console app:

```bash
mvn exec:java -Dexec.mainClass="com.company.employee.App"
```

- To run unit/integration tests (they create/drop `employeeData_test`):

```bash
mvn test
```

11. Appendices

- SQL files: `sql/create_employee_schema.sql`, `sql/alter_add_ssn.sql`, `sql/sample_inserts.sql`, `sql/report_queries.sql`.
- Tests: `src/test/java/com/company/employee/EmployeeDAOTest.java` and `src/test/resources/db-test.properties`.
- Notes: UML diagrams intentionally skipped per request — if you later want them, I can generate PlantUML sources and PNGs.


End of draft. Paste this into your SWDD template and edit the narrative/formatting as needed.
