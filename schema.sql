-- Optional: run this manually if you prefer to create the schema/table yourself
-- instead of relying on spring.jpa.hibernate.ddl-auto=update

CREATE DATABASE IF NOT EXISTS productdb;

USE productdb;

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    productsku VARCHAR(50) NOT NULL UNIQUE
);

-- Sample data (optional)
INSERT INTO product (name, productsku) VALUES ('Wireless Mouse', 'WM-BLK-001');
INSERT INTO product (name, productsku) VALUES ('Mechanical Keyboard', 'MK-RGB-002');
