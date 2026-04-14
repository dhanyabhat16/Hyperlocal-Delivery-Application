# API Testing Guide - Customer Module

This guide provides example requests to test all Customer Module endpoints. Use Postman, curl, or any REST client to test these endpoints.

**Base URL**: `http://localhost:8080/hyperlocaldelivery`

---

## 1. Customer Registration

### Register New Customer
```bash
curl -X POST http://localhost:8080/hyperlocaldelivery/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "address": "123 Main Street, City"
  }'
```

**Response (201 Created):**
```json
{
  "statusCode": 201,
  "message": "Customer registered successfully",
  "data": {
    "userId": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Main Street, City"
  },
  "success": true
}
```

---

## 2. Customer Login

### Login Customer
```bash
curl -X POST "http://localhost:8080/hyperlocaldelivery/api/customers/login?email=john@example.com&password=password123" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Main Street, City"
  },
  "success": true
}
```

---

## 3. Get Customer Information

### Get Customer by ID
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/customers/1 \
  -H "Content-Type: application/json"
```

### Get Customer by Email
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/customers/email/john@example.com \
  -H "Content-Type: application/json"
```

### Get All Customers
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/customers \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Customers retrieved successfully",
  "data": [
    {
      "userId": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "address": "123 Main Street, City"
    }
  ],
  "success": true
}
```

---

## 4. Update Customer Profile

### Update Customer
```bash
curl -X PUT http://localhost:8080/hyperlocaldelivery/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Updated",
    "address": "456 New Street, City"
  }'
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Customer updated successfully",
  "data": {
    "userId": 1,
    "name": "John Updated",
    "email": "john@example.com",
    "address": "456 New Street, City"
  },
  "success": true
}
```

---

## 5. Product Browsing & Search

### Get All Products
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/products \
  -H "Content-Type: application/json"
```

### Get Product by ID
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/products/1 \
  -H "Content-Type: application/json"
```

### Search Products by Category
```bash
curl -X GET "http://localhost:8080/hyperlocaldelivery/api/products/search/category?category=Vegetables" \
  -H "Content-Type: application/json"
```

### Search Products by Name
```bash
curl -X GET "http://localhost:8080/hyperlocaldelivery/api/products/search/name?name=milk" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Products retrieved successfully",
  "data": [
    {
      "productId": 1,
      "name": "Fresh Tomatoes",
      "description": "Organic fresh tomatoes from local farms",
      "category": "Vegetables",
      "price": 50.00,
      "quantity": 100
    }
  ],
  "success": true
}
```

---

## 6. Shopping Cart Operations

### Get or Create Cart
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/carts/1 \
  -H "Content-Type: application/json"
```

### Add Item to Cart
```bash
curl -X POST http://localhost:8080/hyperlocaldelivery/api/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

**Response (201 Created):**
```json
{
  "statusCode": 201,
  "message": "Product added to cart successfully",
  "data": {
    "cartId": 1,
    "items": [
      {
        "cartItemId": 1,
        "product": {
          "productId": 1,
          "name": "Fresh Tomatoes",
          "description": "Organic fresh tomatoes from local farms",
          "category": "Vegetables",
          "price": 50.00,
          "quantity": 100
        },
        "quantity": 2,
        "price": 50.00
      }
    ],
    "totalAmount": 100.00
  },
  "success": true
}
```

### Add Another Item to Cart
```bash
curl -X POST http://localhost:8080/hyperlocaldelivery/api/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 2,
    "quantity": 1
  }'
```

### Update Cart Item Quantity
```bash
curl -X PUT http://localhost:8080/hyperlocaldelivery/api/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "cartItemId": 1,
    "quantity": 3
  }'
```

### Remove Item from Cart
```bash
curl -X DELETE http://localhost:8080/hyperlocaldelivery/api/carts/1/items/1 \
  -H "Content-Type: application/json"
```

### Clear Entire Cart
```bash
curl -X DELETE http://localhost:8080/hyperlocaldelivery/api/carts/1 \
  -H "Content-Type: application/json"
```

---

## 7. Order Management

### Place Order
```bash
curl -X POST http://localhost:8080/hyperlocaldelivery/api/orders/1/place-order \
  -H "Content-Type: application/json"
```

**Response (201 Created):**
```json
{
  "statusCode": 201,
  "message": "Order placed successfully",
  "data": {
    "orderId": 1,
    "status": "PLACED",
    "totalAmount": 160.00,
    "items": [
      {
        "orderItemId": 1,
        "product": {
          "productId": 1,
          "name": "Fresh Tomatoes",
          "description": "Organic fresh tomatoes from local farms",
          "category": "Vegetables",
          "price": 50.00,
          "quantity": 100
        },
        "quantity": 2,
        "price": 50.00
      },
      {
        "orderItemId": 2,
        "product": {
          "productId": 2,
          "name": "Whole Milk",
          "description": "Fresh pasteurized milk, 1 liter",
          "category": "Dairy",
          "price": 60.00,
          "quantity": 150
        },
        "quantity": 1,
        "price": 60.00
      }
    ],
    "createdAt": "2024-01-15T10:30:45",
    "updatedAt": "2024-01-15T10:30:45"
  },
  "success": true
}
```

### Get Order by ID
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/orders/1 \
  -H "Content-Type: application/json"
```

### Get Customer's Orders
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/orders/customer/1 \
  -H "Content-Type: application/json"
```

### Track Order
```bash
curl -X GET http://localhost:8080/hyperlocaldelivery/api/orders/1/track \
  -H "Content-Type: application/json"
```

### Cancel Order
```bash
curl -X DELETE http://localhost:8080/hyperlocaldelivery/api/orders/1/orders/1/cancel \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Order cancelled successfully",
  "data": {
    "orderId": 1,
    "status": "CANCELLED",
    "totalAmount": 160.00,
    "items": [...],
    "createdAt": "2024-01-15T10:30:45",
    "updatedAt": "2024-01-15T10:35:20"
  },
  "success": true
}
```

---

## 8. Error Response Examples

### Insufficient Stock
```json
{
  "statusCode": 400,
  "message": "Insufficient stock for product: Fresh Tomatoes. Available: 5, Requested: 10",
  "success": false
}
```

### Resource Not Found
```json
{
  "statusCode": 404,
  "message": "Customer not found with ID: 999",
  "success": false
}
```

### Duplicate Email
```json
{
  "statusCode": 409,
  "message": "Email already registered: john@example.com",
  "success": false
}
```

### Invalid Operation
```json
{
  "statusCode": 400,
  "message": "Order cannot be cancelled. Current status: DELIVERED",
  "success": false
}
```

---

## Testing Workflow

### Complete Flow Example:

1. **Register Customer**
   ```bash
   # Customer registers
   ```

2. **Login & Get ID**
   ```bash
   # Customer logs in, receives customerId
   ```

3. **Browse Products**
   ```bash
   # Fetch all products or search
   ```

4. **Add to Cart**
   ```bash
   # Add multiple products with desired quantities
   ```

5. **Update Cart**
   ```bash
   # Modify quantities or remove items
   ```

6. **View Cart**
   ```bash
   # Get current cart with total
   ```

7. **Place Order**
   ```bash
   # Create order from cart
   ```

8. **Track Order**
   ```bash
   # Monitor order status
   ```

9. **View Order History**
   ```bash
   # Get all customer orders
   ```

---

## Testing Tips

1. **Use Postman**: Import requests for easier management
2. **Keep Response Headers**: Check for content-type: application/json
3. **Validate IDs**: Ensure customer/product IDs exist before operations
4. **Test Error Cases**: Try invalid IDs, quantities, etc.
5. **Check Timestamps**: Verify order timestamps for correct sequencing
6. **Database Verification**: Query MySQL directly to verify data

---

## Performance Considerations

- Products returned: All available products (paginate for production)
- Order history: Sorted by latest first (DESC)
- Cart operations: Atomic transactions
- Stock validation: Real-time checking

---

## Notes

- All timestamps are in ISO 8601 format
- Prices are in rupees (can be modified)
- Password stored as plain text (use BCrypt in production)
- CORS enabled for all origins (restrict in production)

