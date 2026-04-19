-- Sample Data Insert Script for Hyperlocal Delivery Application

-- Reset seed data (order matters due to foreign keys)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE address;
TRUNCATE TABLE users;
TRUNCATE TABLE warehouses;
SET FOREIGN_KEY_CHECKS = 1;

-- Insert Warehouses
INSERT INTO warehouses (name, city) VALUES
('Bangalore Warehouse', 'Bangalore'),
('Chennai Warehouse', 'Chennai'),
('Hyderabad Warehouse', 'Hyderabad'),
('Mumbai Warehouse', 'Mumbai');

-- Insert Warehouse Managers
INSERT INTO users (name, email, password, role, warehouse_id) VALUES
('Manager Bangalore', 'manager.bangalore@example.com', '$2a$10$examplehashedpassword', 'WAREHOUSE', 1),
('Manager Chennai', 'manager.chennai@example.com', '$2a$10$examplehashedpassword', 'WAREHOUSE', 2),
('Manager Hyderabad', 'manager.hyderabad@example.com', '$2a$10$examplehashedpassword', 'WAREHOUSE', 3),
('Manager Mumbai', 'manager.mumbai@example.com', '$2a$10$examplehashedpassword', 'WAREHOUSE', 4);

-- Update warehouses with manager_id
UPDATE warehouses SET manager_id = 1 WHERE warehouse_id = 1;
UPDATE warehouses SET manager_id = 2 WHERE warehouse_id = 2;
UPDATE warehouses SET manager_id = 3 WHERE warehouse_id = 3;
UPDATE warehouses SET manager_id = 4 WHERE warehouse_id = 4;

-- Insert Customers
INSERT INTO users (name, email, password, role) VALUES
('Rajesh Kumar', 'rajesh@example.com', '$2a$10$examplehashedpassword', 'CUSTOMER'),
('Priya Singh', 'priya@example.com', '$2a$10$examplehashedpassword', 'CUSTOMER'),
('Arun Patel', 'arun@example.com', '$2a$10$examplehashedpassword', 'CUSTOMER'),
('Deepika Gupta', 'deepika@example.com', '$2a$10$examplehashedpassword', 'CUSTOMER');

-- Insert Addresses
INSERT INTO address (street, city, door_no, building_name, is_default_address, user_id) VALUES
('123 Maple Street', 'Bangalore', 'A-101', 'Green Apartments', false, 5),
('456 Oak Avenue', 'Mumbai', 'B-202', 'Blue Towers', false, 6),
('789 Pine Road', 'Chennai', 'C-303', 'Red Villas', false, 7),
('321 Elm Lane', 'Hyderabad', 'D-404', 'Yellow Complex', false, 8);

-- Insert Categories
INSERT INTO categories (category_name) VALUES
('Vegetables'),
('Dairy'),
('Bakery'),
('Meat'),
('Grains'),
('Oils & Condiments'),
('Staples'),
('Fruits');

-- Insert Products (assigned to warehouses)
INSERT INTO products (name, category, description, price, quantity, available, category_id, warehouse_id) VALUES
('Fresh Tomatoes', 'Vegetables', 'Organic fresh tomatoes from local farms', 50.00, 100, true, 1, 1),
('Whole Milk', 'Dairy', 'Fresh pasteurized milk, 1 liter', 60.00, 150, true, 2, 1),
('Brown Bread', 'Bakery', 'Whole wheat brown bread', 40.00, 80, true, 3, 1),
('Eggs (Half Dozen)', 'Dairy', 'Farm fresh eggs', 35.00, 200, true, 2, 2),
('Chicken Breast', 'Meat', 'Fresh chicken breast, 500g', 250.00, 50, true, 4, 2),
('Rice (5kg)', 'Grains', 'Basmati rice, Premium quality', 300.00, 60, true, 5, 3),
('Cooking Oil (1L)', 'Oils & Condiments', 'Refined vegetable oil', 120.00, 75, true, 6, 3),
('Sugar (1kg)', 'Staples', 'Refined granulated sugar', 45.00, 100, true, 7, 4),
('Salt (500g)', 'Staples', 'Iodized table salt', 20.00, 150, true, 7, 4),
('Apples (1kg)', 'Fruits', 'Fresh red apples', 80.00, 40, true, 8, 1);
