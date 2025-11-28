-- alter_add_ssn.sql
-- If you already have an `employee` table and only need to add SSN, run this.

USE employeeData;

-- Add `ssn` column if it doesn't already exist (idempotent)
SET @col_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'employeeData' AND TABLE_NAME = 'employee' AND COLUMN_NAME = 'ssn'
);

SET @sql = IF(@col_exists = 0,
  'ALTER TABLE employee ADD COLUMN ssn VARCHAR(9) AFTER last_name',
  'SELECT "ssn column already exists"'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add unique index on ssn if it doesn't already exist (idempotent)
SET @idx_exists = (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = 'employeeData' AND TABLE_NAME = 'employee' AND INDEX_NAME = 'uq_employee_ssn'
);

SET @sql2 = IF(@idx_exists = 0,
  'ALTER TABLE employee ADD UNIQUE INDEX uq_employee_ssn (ssn)',
  'SELECT "uq_employee_ssn already exists"'
);
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- To populate SSN for existing rows, use UPDATE statements or import in DBeaver.
