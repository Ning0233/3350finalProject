-- 00_create_users.sql
-- Create application and test users (will run on container initialization)

CREATE USER IF NOT EXISTS 'proj_user'@'%' IDENTIFIED BY 'proj_pass';
GRANT SELECT, INSERT, UPDATE, DELETE ON employeeData.* TO 'proj_user'@'%';

CREATE USER IF NOT EXISTS 'test_user'@'%' IDENTIFIED BY 'secret123';
-- Give test_user the privileges needed to create/drop/alter databases and create foreign keys
GRANT CREATE, DROP, ALTER, INSERT, UPDATE, DELETE, SELECT, REFERENCES ON *.* TO 'test_user'@'%';

-- Optionally grant proj_user limited references if your app creates FKs (not typical)
GRANT REFERENCES ON employeeData.* TO 'proj_user'@'%';

FLUSH PRIVILEGES;
