-- alter_add_ssn.sql
-- If you already have an `employee` table and only need to add SSN, run this.

USE employeeData;

ALTER TABLE employee
  ADD COLUMN IF NOT EXISTS ssn VARCHAR(9) AFTER last_name;

-- Make SSN unique (if desired). If duplicates exist, this will fail until duplicates are resolved.
ALTER TABLE employee
  ADD UNIQUE INDEX uq_employee_ssn (ssn);

-- To populate SSN for existing rows, use UPDATE statements or import in DBeaver.
