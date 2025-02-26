-- Create a new MySQL user and grant permissions
CREATE USER 'gameon_user'@'%' IDENTIFIED BY 'gameon_password';
GRANT ALL PRIVILEGES ON gameon_db.* TO 'gameon_user'@'%';
FLUSH PRIVILEGES;