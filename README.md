# Library Management System - Server Components

## Overview
A Kotlin-based library management system server built with Ktor framework and Exposed SQL framework for database operations.

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

### Testing Suite (ApplicationTest.kt)
Comprehensive test coverage including:
- Root endpoint verification
- User registration with valid/invalid credentials
- User authentication and borrowing flow
- Item addition and search functionality
- Item borrowing and return processes
- Edge cases and error conditions

### Security Features
- Password storage in database
- User authentication check before operations
- Input validation for all endpoints
- SQL injection prevention through Exposed framework

## Technical Stack
- Ktor: Web framework for building asynchronous servers
- Exposed: Type-safe SQL framework
- SQLite: Lightweight, file-based database
- Kotlinx.serialization: JSON serialization/deserialization
- Netty: Asynchronous event-driven network application framework

## Server Configuration
- Default port: 8080
- Host: 0.0.0.0 (accessible from all network interfaces)
- Database: SQLite (library-database.sqlite)
- JVM Options: -Xmx64m -Xms64m (defined in gradlew.bat)

## About the Project
This is a Kotlin Multiplatform project targeting Android, Web, Desktop, Server.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/server` is for the Ktor server application.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.