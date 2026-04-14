-- Sample Data Insert Script for Hyperlocal Delivery Application
-- This script populates sample data for testing the Customer Module

-- Insert Customers
INSERT INTO users (dtype, name, email, password, role) VALUES 
('Customer', 'Rajesh Kumar', 'rajesh@example.com', 'password123', 'CUSTOMER'),
('Customer', 'Priya Singh', 'priya@example.com', 'password123', 'CUSTOMER'),
('Customer', 'Arun Patel', 'arun@example.com', 'password123', 'CUSTOMER'),
('Customer', 'Deepika Gupta', 'deepika@example.com', 'password123', 'CUSTOMER');

INSERT INTO customers (address, user_id) VALUES 
('123 Maple Street, Bangalore', 1),
('456 Oak Avenue, Mumbai', 2),
('789 Pine Road, Delhi', 3),
('321 Elm Lane, Pune', 4);

-- Insert Products
INSERT INTO products (name, description, category, price, quantity) VALUES 
('Fresh Tomatoes', 'Organic fresh tomatoes from local farms', 'Vegetables', 50.00, 100),
('Whole Milk', 'Fresh pasteurized milk, 1 liter', 'Dairy', 60.00, 150),
('Brown Bread', 'Whole wheat brown bread', 'Bakery', 40.00, 80),
('Eggs (Half Dozen)', 'Farm fresh eggs', 'Dairy', 35.00, 200),
('Chicken Breast', 'Fresh chicken breast, 500g', 'Meat', 250.00, 50),
('Rice (5kg)', 'Basmati rice, Premium quality', 'Grains', 300.00, 60),
('Cooking Oil (1L)', 'Refined vegetable oil', 'Oils & Condiments', 120.00, 75),
('Sugar (1kg)', 'Refined granulated sugar', 'Staples', 45.00, 100),
('Salt (500g)', 'Iodized table salt', 'Staples', 20.00, 150),
('Apples (1kg)', 'Fresh red apples', 'Fruits', 80.00, 40);

-- Insert Carts (Optional - will be created dynamically)
-- INSERT INTO carts (customer_id) VALUES (1), (2), (3), (4);

-- Insert Sample Cart Items (Optional - for demonstration)
-- INSERT INTO cart_items (cart_id, product_id, quantity, price) VALUES 
-- (1, 1, 2, 50.00),
-- (1, 2, 1, 60.00),
-- (2, 5, 1, 250.00),
-- (2, 6, 1, 300.00);

-- Insert Sample Order (Optional - for demonstration)
-- INSERT INTO orders (customer_id, status, total_amount, created_at, updated_at) VALUES 
-- (1, 'PLACED', 160.00, NOW(), NOW());

-- INSERT INTO order_items (order_id, product_id, quantity, price) VALUES 
-- (1, 1, 2, 50.00),
-- (1, 2, 1, 60.00);

-- Display Sample Records
SELECT 'Customers' AS 'Section';
SELECT user_id AS 'ID', name, email, address FROM users u 
LEFT JOIN customers c ON u.user_id = c.user_id WHERE user_id <= 4;

SELECT '' AS '';
SELECT 'Products' AS 'Section';
SELECT product_id, name, category, price, quantity FROM products LIMIT 10;
