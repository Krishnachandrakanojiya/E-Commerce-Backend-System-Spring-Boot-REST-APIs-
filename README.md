🛒 E-Commerce Backend System – Spring Boot REST APIs

A production-ready E-Commerce Backend application built using Spring Boot, following Microservices-inspired layered architecture, covering user management, product catalog, cart, order, and payment workflow with clean REST APIs.

This project is designed to demonstrate real-world backend engineering practices suitable for SDE-2 / Product-based company interviews.

🚀 Features
👤 User & Address Management

Create, update, and fetch users

Manage multiple addresses per user

Fetch user details by User ID

📦 Product Management

Add new products

Fetch product by Product ID

Update product details

Delete product

🛒 Cart Management

Add product to cart

Update cart item quantity

View cart by User ID

Delete cart / cart items

Auto create cart if not exists

📑 Order Management

Place order from cart

Order status lifecycle:

CREATED → CONFIRMED → SHIPPED → DELIVERED

Fetch order details

Update order status

💳 Payment (Design-Level)

Payment decision flow integrated during order placement

Designed to support:

Online Payment

COD

Extensible for real payment gateways

🏗️ Architecture
High Level Design (HLD)
Client (Postman / UI)
        |
        v
Controller Layer (REST APIs)
        |
        v
Service Layer (Business Logic)
        |
        v
Repository Layer (JPA / Hibernate)
        |
        v
PostgreSQL Database

Low Level Design (LLD)

Layered Architecture

DTO pattern for request/response

Exception handling using global exception handler

Clean separation of concerns

RESTful API design principles

🧰 Tech Stack
Technology	Usage
Java 17	Programming Language
Spring Boot	Backend Framework
Spring Web	REST APIs
Spring Data JPA	ORM
Hibernate	Persistence
PostgreSQL	Database
Maven	Build Tool
Postman	API Testing
Git & GitHub	Version Control
📌 API Endpoints Overview
User APIs

POST /users

GET /users/{userId}

PUT /users/{userId}

Product APIs

POST /products

GET /products/{productId}

PUT /products/{productId}

DELETE /products/{productId}

Cart APIs

POST /cart/add

PUT /cart/update

GET /cart?userId={id}

DELETE /cart/{userId}

Order APIs

POST /orders/place

PUT /orders/status

GET /orders/{orderId}

📌 Complete Postman Collection is included in the repository

🔄 End-to-End Flow (Real-World)

User registers and logs in

User browses products

Product added to cart

Cart updated (quantity)

Order placed from cart

Payment decision made

Order status updated step-by-step

🧪 Testing

APIs tested using Postman

Covers:

Positive scenarios

Edge cases

Invalid inputs

📁 Project Structure
src/main/java
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── exception
 └── config

🎯 Why This Project?

Designed like a real production backend

Covers complete E-Commerce domain

Demonstrates Spring Boot, REST, JPA, DB design

Suitable for:

SDE-1 / SDE-2 interviews

Backend Developer roles

Product-based companies

📌 Future Enhancements

JWT Authentication & Authorization

Role-based access (Admin/User)

Redis caching

Kafka for order events

Payment gateway integration

Docker & Kubernetes deployment

## 📘 API Documentation
Swagger UI is integrated for API exploration.

URL:
http://localhost:8080/swagger-ui.html

👨‍💻 Author

Krishna Kanojiya
Backend Engineer | Java | Spring Boot | Microservices | Azure

🔗 GitHub: https://github.com/Krishnachandrakanojiya
