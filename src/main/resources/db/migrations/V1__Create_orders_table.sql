-- V1__Create_orders_table.sql
CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY NOT NULL, -- UUIDs for primary keys
    user_id VARCHAR(36) NOT NULL,        -- ID of the user who placed the order
    status VARCHAR(50) NOT NULL,         -- e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, FAILED
    total_amount DECIMAL(10, 2) NOT NULL,
    street VARCHAR(255) NOT NULL,        -- Embedded Address fields
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);