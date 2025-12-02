USE employeeData;

-- Add `address` column if it doesn't already exist (idempotent)
SET @col_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'employeeData' AND TABLE_NAME = 'employee' AND COLUMN_NAME = 'address'
);

SET @sql = IF(@col_exists = 0,
  'ALTER TABLE employee ADD COLUMN address VARCHAR(255) AFTER last_name',
  'SELECT "address column already exists"'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
