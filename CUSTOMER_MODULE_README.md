# Hyperlocal Delivery Application - Customer Module Documentation

## Overview
This document provides comprehensive documentation for the **Customer Module** of the Hyperlocal Delivery Application, which is part of the UE23CS352B Object Oriented Analysis & Design mini-project.

## Implementation Details

### Architecture & Patterns Used

#### MVC Architecture
- **Model**: Entity classes (User, Customer, Product, Cart, Order, Payment)
- **View**: REST API Endpoints
- **Controller**: Spring REST Controllers handling HTTP requests
- **Service**: Business logic layer with transaction management

#### Design Patterns Applied

1. **Singleton Pattern** - Spring Beans (Services, Repositories)
2. **Factory Pattern** - UserRepository creates User instances
3. **Builder Pattern** - DTOs and Entity construction
4. **Observer Pattern** - Spring Data JPA listeners (PrePersist, PreUpdate)
5. **Strategy Pattern** - Different order status transitions

#### Design Principles Applied

1. **Single Responsibility Principle (SRP)** - Each service handles one business domain
2. **Open/Closed Principle** - Extendable entity model with inheritance
3. **Liskov Substitution Principle** - Customer extends User while maintaining substitutability
4. **Dependency Inversion Principle** - Dependency injection via Spring
5. **Interface Segregation Principle** - Focused DTOs for specific operations

### Project Structure

```
src/main/java/com/ecommerce/hyperlocaldelivery/
├── entity/                          # JPA Entity Classes
│   ├── Role.java                   # Enum for user roles
│   ├── OrderStatus.java            # Enum for order statuses
│   ├── PaymentStatus.java          # Enum for payment statuses
│   ├── User.java                   # Base user entity
│   ├── Customer.java               # Customer entity (extends User)
│   ├── Product.java                # Product entity
│   ├── Cart.java                   # Shopping cart entity
│   ├── CartItem.java               # Cart item entity
│   ├── Order.java                  # Order entity
│   ├── OrderItem.java              # Order item entity
│   └── Payment.java                # Payment entity
├── repository/                      # Spring Data JPA Repositories
│   ├── UserRepository.java
│   ├── CustomerRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── CartItemRepository.java
│   ├── OrderRepository.java
│   └── PaymentRepository.java
├── service/                         # Business Logic Layer
│   ├── CustomerService.java        # Customer management
│   ├── ProductService.java         # Product browsing & search
│   ├── CartService.java            # Cart operations
│   └── OrderService.java           # Order processing
├── controller/                      # REST Controllers
│   ├── CustomerController.java     # Customer endpoints
│   ├── ProductController.java      # Product endpoints
│   ├── CartController.java         # Cart endpoints
│   └── OrderController.java        # Order endpoints
├── dto/                            # Data Transfer Objects
│   ├── CustomerRegistrationDTO.java
│   ├── CustomerDTO.java
│   ├── ProductDTO.java
│   ├── AddToCartDTO.java
│   ├── CartItemDTO.java
│   ├── CartDTO.java
│   ├── OrderItemDTO.java
│   ├── OrderDTO.java
│   ├── UpdateQuantityDTO.java
│   └── ApiResponseDTO.java
├── exception/                       # Custom Exception Classes
│   ├── ResourceNotFoundException.java
│   ├── InsufficientStockException.java
│   ├── InvalidOperationException.java
│   └── DuplicateEmailException.java
├── config/                         # Configuration Classes
│   └── GlobalExceptionHandler.java # Centralized exception handling
├── util/                           # Utility Classes
│   └── Constants.java              # Application constants
└── HyperlocaldeliveryApplication.java # Main Spring Boot Application
```

## Database Configuration

### MySQL Setup

1. **Create Database:**
```sql
CREATE DATABASE hyperlocal_delivery_db;
```

2. **Connection Details:**
   - **Host**: localhost
   - **Port**: 3306
   - **Database**: hyperlocal_delivery_db
   - **Username**: root
   - **Password**: root

3. **Update `application.properties`:**
   - Database URL, username, and password are configured for auto-creation
   - Hibernate will automatically create tables on application startup

### Database Tables

| Table | Purpose |
|-------|---------|
| `users` | Base user information (inherited by customers) |
| `customers` | Customer-specific data |
| `products` | Product catalog |
| `carts` | Shopping carts |
| `cart_items` | Items in carts |
| `orders` | Customer orders |
| `order_items` | Items in orders |
| `payments` | Payment records |

## API Endpoints

### Customer Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/customers/register` | Register new customer |
| POST | `/api/customers/login` | Customer login |
| GET | `/api/customers/{customerId}` | Get customer details |
| GET | `/api/customers/email/{email}` | Get customer by email |
| PUT | `/api/customers/{customerId}` | Update customer profile |
| GET | `/api/customers` | Get all customers |
| DELETE | `/api/customers/{customerId}` | Delete customer |

### Product Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{productId}` | Get product by ID |
| GET | `/api/products/search/category` | Search by category |
| GET | `/api/products/search/name` | Search by name |

### Cart Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/carts/{customerId}` | Get customer's cart |
| POST | `/api/carts/{customerId}/items` | Add item to cart |
| PUT | `/api/carts/{customerId}/items` | Update item quantity |
| DELETE | `/api/carts/{customerId}/items/{cartItemId}` | Remove item from cart |
| DELETE | `/api/carts/{customerId}` | Clear entire cart |

### Order Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders/{customerId}/place-order` | Place order |
| GET | `/api/orders/{orderId}` | Get order details |
| GET | `/api/orders/customer/{customerId}` | Get customer's orders |
| GET | `/api/orders/{orderId}/track` | Track order |
| DELETE | `/api/orders/{customerId}/orders/{orderId}/cancel` | Cancel order |

## Entity Relationships

### Class Diagram Summary

```
User (Base Class)
├── role: Role
├── userId: Integer
├── name: String
├── email: String
└── password: String

Customer (extends User)
├── address: String
├── carts: List<Cart>
└── orders: List<Order>

Cart
├── cartId: Integer
├── customer: Customer (ManyToOne)
├── items: List<CartItem> (OneToMany)
└── getTotal(): Double

CartItem
├── cartItemId: Integer
├── cart: Cart (ManyToOne)
├── product: Product (ManyToOne)
├── quantity: Integer
└── price: Double

Order
├── orderId: Integer
├── customer: Customer (ManyToOne)
├── status: OrderStatus
├── totalAmount: Double
├── items: List<OrderItem> (OneToMany)
├── createdAt: LocalDateTime
└── updatedAt: LocalDateTime

OrderItem
├── orderItemId: Integer
├── order: Order (ManyToOne)
├── product: Product (ManyToOne)
├── quantity: Integer
└── price: Double

Product
├── productId: Integer
├── name: String
├── description: String
├── category: String
├── price: Double
└── quantity: Integer

Payment
├── paymentId: Integer
├── order: Order (OneToOne)
├── amount: Double
├── status: PaymentStatus
└── paymentDate: LocalDateTime
```

## Activity Flow

### Customer Usage Flow

1. **Registration/Login**
   - Customer registers with email and address
   - System validates email uniqueness
   - Customer can login with email/password

2. **Product Browsing**
   - Customer can view all products
   - Search by product name
   - Search by category

3. **Shopping Cart**
   - Browse products and view details
   - Add items to cart with desired quantity
   - Cart validates product availability
   - Update quantities for cart items
   - Remove items from cart
   - View cart total

4. **Order Placement**
   - Place order from cart items
   - System creates order and order items
   - Cart is cleared after order
   - Order gets PLACED status

5. **Order Management**
   - Track order status
   - View all orders with timeline
   - Cancel orders (only in PLACED status)

## Key Features

### Customer Service Features
- ✅ Registration with validation
- ✅ Login authentication
- ✅ Profile management (name, address)
- ✅ View customer details

### Product Service Features
- ✅ Browse all products
- ✅ Search by category
- ✅ Search by name (case-insensitive)
- ✅ View product details

### Cart Service Features
- ✅ Create/retrieve shopping cart
- ✅ Add items with quantity validation
- ✅ Stock availability checking
- ✅ Update item quantities
- ✅ Remove items from cart
- ✅ Clear entire cart
- ✅ Calculate cart total

### Order Service Features
- ✅ Place orders from cart
- ✅ Generate order items from cart
- ✅ Order tracking
- ✅ Order cancellation (PLACED status only)
- ✅ Order history with timestamps
- ✅ Status management

## Exception Handling

The application includes custom exceptions with global exception handler:

| Exception | HTTP Status | Use Case |
|-----------|------------|----------|
| ResourceNotFoundException | 404 | Resource not found |
| InsufficientStockException | 400 | Not enough product stock |
| InvalidOperationException | 400 | Invalid business operation |
| DuplicateEmailException | 409 | Email already registered |

All exceptions return consistent ApiResponseDTO format.

## Data Transfer Objects (DTOs)

All API communications use DTOs for data encapsulation:

- **CustomerRegistrationDTO** - For registration requests
- **CustomerDTO** - For customer responses
- **ProductDTO** - For product information
- **AddToCartDTO** - For adding items to cart
- **CartItemDTO** - For cart item information
- **CartDTO** - For complete cart information
- **OrderItemDTO** - For order item information
- **OrderDTO** - For complete order information
- **ApiResponseDTO** - Standardized API response wrapper

## Running the Application

1. **Prerequisites:**
   - Java 17 or later
   - MySQL Server running
   - Maven 3.6+

2. **Setup Database:**
   - Start MySQL server
   - Database will be created automatically on application startup

3. **Run Application:**
   ```bash
   cd Hyperlocal-Delivery-Application
   mvn spring-boot:run
   ```

4. **Access API:**
   - Base URL: http://localhost:8080/hyperlocaldelivery/api
   - Example: http://localhost:8080/hyperlocaldelivery/api/customers

## Testing the APIs

### Example Requests

**Register Customer:**
```json
POST /api/customers/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "address": "123 Main Street, City"
}
```

**Login:**
```
POST /api/customers/login?email=john@example.com&password=password123
```

**Add to Cart:**
```json
POST /api/carts/1/items
{
  "productId": 1,
  "quantity": 2
}
```

**Place Order:**
```
POST /api/orders/1/place-order
```

## Response Format

All successful responses follow this format:
```json
{
  "statusCode": 200,
  "message": "Operation successful",
  "data": { ... },
  "success": true
}
```

Error responses:
```json
{
  "statusCode": 400,
  "message": "Error description",
  "success": false
}
```

## Implementation Notes

- **Transactions**: All write operations are transactional
- **Validation**: Stock availability is validated before cart operations
- **Timestamps**: Orders track creation and update timestamps
- **Relationships**: Proper JPA relationships with cascade operations
- **Security**: Note: Password handling should use BCrypt in production
- **CORS**: Enabled for all origins (update for production)

## Next Steps for Team Members

The customer module is complete and ready for integration with:
1. **Warehouse Manager Module** - Manage products and stock
2. **Delivery Partner Module** - Handle delivery operations
3. **Admin Module** - System administration and reporting

Each module can work independently on their use cases while implementing their design patterns and principles.

## Team Member Contribution Example

As the Customer Module owner:
- ✅ Implemented all customer use cases
- ✅ Applied 5 design patterns (Singleton, Factory, Builder, Observer, Strategy)
- ✅ Applied 5 design principles (SRP, OCP, LSP, DIP, ISP)
- ✅ Used MVC architecture pattern
- ✅ Implemented complete REST API
- ✅ Database integration with MySQL
- ✅ Exception handling and validation
- ✅ Entity relationships and transactions

---

**Created for**: UE23CS352B - Object Oriented Analysis & Design Mini-Project
**Domain**: e-Commerce (Hyperlocal Delivery)
**Technology Stack**: Spring Boot 4.0.5, Spring Data JPA, MySQL, Lombok
