# Library Management System - Server Components

## Overview
A Kotlin-based library management system built with Ktor framework and Exposed SQL framework for database operations.

## Core Components

### Database Structure
- `DatabaseFactory.kt`: Handles SQLite database initialization and schema creation
- Tables:
  - `Users`
    - username (PK, varchar-50)
    - password (varchar-64)
  - `Items`
    - id (PK, auto-increment)
    - title (varchar-100)
    - ISBN (varchar-50)
    - type (varchar-4)
    - isBorrowed (boolean)
    - borrower (varchar-50, nullable)
    - borrowedDate (varchar-50, nullable)
    - dueDate (varchar-50, nullable)

### API Endpoints

#### User Management
- `POST /users/add`: Register new users
  - Parameters: username, password
  - Returns: "User added" or error message
  - Status codes: 200 OK, 400 Bad Request, 409 Conflict
- `POST /users/login`: Authenticate users and retrieve borrowed items
  - Parameters: username, password
  - Returns: List of borrowed items in JSON format
  - Status codes: 200 OK, 401 Unauthorized

#### Item Management
- `POST /items/add`: Add new items to library
  - Parameters: title, ISBN, type
  - Returns: "Item added" or error message
  - Status codes: 200 OK, 400 Bad Request
- `GET /items/search`: Search items by title or ISBN
  - Parameters: query
  - Returns: JSON array of matching items
  - Status codes: 200 OK, 404 Not Found
- `POST /items/borrow`: Borrow an item
  - Parameters: id, username
  - Updates: borrower, borrowedDate, dueDate, isBorrowed
  - Status codes: 200 OK, 400 Bad Request
- `POST /items/return`: Return a borrowed item
  - Parameters: id
  - Resets: borrower, borrowedDate, dueDate, isBorrowed to null/false
  - Status codes: 200 OK, 400 Bad Request

### Data Models
- `ItemJson`: Serializable data class containing:
  - id: Int
  - title: String
  - ISBN: String
  - type: String
  - borrower: String?
  - borrowedDate: String?
  - dueDate: String?
  - isBorrowed: Boolean
- `Item`: Entity class for client side operations:
  - id: Int
  - title: String
  - ISBN: String
  - type: String
- `BorrowedItem`: Entity class for client side operations:
  - Inherits from Item
  - borrower: String
  - borrowedDate: String
  - dueDate: String

### Testing Suite (ApplicationTest.kt)
Comprehensive test coverage including:
- Root endpoint verification
- User registration with valid/invalid credentials
- User authentication and borrowing flow
- Item addition and search functionality
- Item borrowing and return processes
- Edge cases and error conditions

### Security Features
- SHA256 password storage in database
- User authentication check before operations
- Input validation for all endpoints
- SQL injection prevention through Exposed framework

## Technical Stack
- Ktor: Web framework for building asynchronous servers
- Exposed: Type-safe SQL framework
- SQLite: Lightweight, file-based database
- Kotlinx.serialization: JSON serialization/deserialization
- Netty: Asynchronous event-driven network application framework
- Compose Multiplatform/Kotlin Multiplatform: multiplatform support for client components

## Server Configuration
- Default port: 8080
- Host: 0.0.0.0 (accessible from all network interfaces)
- Database: SQLite (library-database.sqlite)
- JVM Options: -Xmx64m -Xms64m (defined in gradlew.bat)