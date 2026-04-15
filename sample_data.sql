-- Sample Data Insert Script for Hyperlocal Delivery Application

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
INSERT INTO address (street, city, door_no, building_name, user_id) VALUES
('123 Maple Street', 'Bangalore', 'A-101', 'Green Apartments', 5),
('456 Oak Avenue', 'Mumbai', 'B-202', 'Blue Towers', 6),
('789 Pine Road', 'Chennai', 'C-303', 'Red Villas', 7),
('321 Elm Lane', 'Hyderabad', 'D-404', 'Yellow Complex', 8);

-- Insert Categories (assuming categories table exists)
INSERT INTO categories (name) VALUES
('Vegetables'),
('Dairy'),
('Bakery'),
('Meat'),
('Grains'),
('Oils & Condiments'),
('Staples'),
('Fruits');

-- Insert Products (assigned to warehouses)
INSERT INTO products (name, description, price, quantity, category_id, warehouse_id) VALUES
('Fresh Tomatoes', 'Organic fresh tomatoes from local farms', 50.00, 100, 1, 1),
('Whole Milk', 'Fresh pasteurized milk, 1 liter', 60.00, 150, 2, 1),
('Brown Bread', 'Whole wheat brown bread', 40.00, 80, 3, 1),
('Eggs (Half Dozen)', 'Farm fresh eggs', 35.00, 200, 2, 2),
('Chicken Breast', 'Fresh chicken breast, 500g', 250.00, 50, 4, 2),
('Rice (5kg)', 'Basmati rice, Premium quality', 300.00, 60, 5, 3),
('Cooking Oil (1L)', 'Refined vegetable oil', 120.00, 75, 6, 3),
('Sugar (1kg)', 'Refined granulated sugar', 45.00, 100, 7, 4),
('Salt (500g)', 'Iodized table salt', 20.00, 150, 7, 4),
('Apples (1kg)', 'Fresh red apples', 80.00, 40, 8, 1); 
LEFT JOIN customers c ON u.user_id = c.user_id WHERE user_id <= 4;

SELECT '' AS '';
SELECT 'Products' AS 'Section';
SELECT product_id, name, category, price, quantity FROM products LIMIT 10;
