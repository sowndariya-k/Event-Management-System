# Event Management System (EMS)

A comprehensive, multi-tiered Java application designed for robust event orchestration, registration, and management. This project serves as a prime example of **Clean Architecture**, emphasizing a strict separation of concerns, scalable design patterns, and enterprise-grade exception handling.

---

## 🏗️ Architecture & Separation of Concerns

The system follows a **Modular Tiered Architecture**, ensuring that each layer has a single, well-defined responsibility. This decoupling makes the codebase maintainable, testable, and easy to extend.

### 🧱 The 3-Tier Layered Approach

| Layer | Responsibility | Interaction Flow |
| :--- | :--- | :--- |
| **Action Layer** | **User Interaction & Flow Control**: Handles CLI menus, captures user input, and coordinates calls to the service layer. | `User` ↔ `Action` ↔ `Service` |
| **Service Layer** | **Business Logic & Rules**: The "heart" of the application. Processes data, applies business rules (e.g., availability checks), and coordinates multiple DAO calls. | `Action` ↔ `Service` ↔ `DAO` |
| **DAO Layer** | **Persistent Data Access**: Pure CRUD operations. Communicates directly with the MySQL database using JDBC. | `Service` ↔ `DAO` ↔ `Database` |

### 🛠️ Core Components
- **Models (POJOs)**: Plain Old Java Objects representing domain entities (User, Event, Ticket, etc.).
- **Enums**: Strongly-typed constants for Statuses, Roles, and Types, ensuring data integrity across the stack.
- **Utilities**: Cross-cutting concerns like Database connection, Date/Time formatting, and Input Validation.

---

## 🧬 Design Patterns & Professional Standards

### 1. Data Access Object (DAO) Pattern
Encapsulates all database-specific logic. By using interfaces (e.g., `EventDao`), business logic is insulated from the underlying database implementation, allowing for seamless shifts in storage technology.

### 2. Service Layer Pattern (Thin DAO, Fat Service)
We prioritize a "Thin DAO" approach where DAOs only execute queries, while the Service Layer handles the "Heavy Lifting" of business rules, validation, and complex data transformations.

### 3. Singleton & Factory Pattern
The `ApplicationUtil` acts as a simplified **Dependency Injection (DI)** container, ensuring that DAOs and Services are managed as Singletons. This reduces object overhead and provides a centralized registry for all core components.

### 4. Interface-Based Programming
Strict adherence to "Program to an Interface, not an Implementation." This core OOP principle ensures flexibility and allows for mocking components during unit testing.

### 5. Type-Safe Enum Logic
Eliminates "Magic Strings." The system uses Enums for all status comparisons and role-based checks, preventing runtime errors caused by typos and ensuring consistency from the SQL schema to the Java UI.

### 6. Centralized Exception Strategy
The application employs a unified `DataAccessException` for all database-related failures.
- **DAO**: Catches SQL exceptions and wraps them in a custom `DataAccessException`.
- **Service**: Propagates exceptions or handles them if a business-level fallback exists.
- **Action/Helper**: Provides graceful feedback to the user, ensuring the CLI never crashes due to unexpected DB states.

---

## 📂 Project Structure

```text
src/main/java/com/ems/
├── App.java                   # Application entry point & Main Menu
├── actions/                   # Workflow orchestrators (Admin, Organizer, Attendee)
├── dao/                       # Persistence interfaces
│   └── impl/                  # JDBC-based implementations
├── enums/                     # Type-safe constants (EventStatus, UserRole, etc.)
├── exception/                 # Custom exception definitions
├── menu/                      # Navigation and rendering logic
├── model/                     # Domain Entities (Data holders)
├── service/                   # Business Logic definitions
│   └── impl/                  # Business Rule implementations
└── util/                      # Reusable utilities
    ├── ApplicationUtil.java   # Singleton Registry (Service/DAO Factory)
    ├── DBConnectionUtil.java  # Connection pooling/management
    ├── DateTimeUtil.java      # Date and time formatting helpers
    ├── InputValidationUtil.java  # Centralized input validation
    ├── MenuHelper.java        # Shared display/formatting utilities
    ├── AdminMenuHelper.java   # Admin-specific display utilities
    └── PasswordUtil.java      # BCrypt password hashing utility
```

---

## 📏 Coding Standards & Best Practices

1. **Naming Conventions**:
   - Classes: `PascalCase` (e.g., `AdminService`)
   - Methods/Variables: `camelCase` (e.g., `processPayment`)
   - Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
2. **DRY (Don't Repeat Yourself)**: Reusable logic is extracted into static utilities or dedicated helper classes (e.g., `MenuHelper`, `AdminMenuHelper`).
3. **Input Integrity**: Every user input is validated via `InputValidationUtil` to prevent malformed data.
4. **Resource Management**: DB Connections and Scanners are handled carefully to prevent memory leaks.
5. **Password Security**: User passwords are securely hashed and verified using the **BCrypt** algorithm via `PasswordUtil`.

---

## 🚀 Setup & Execution

### Prerequisites

| Tool | Version |
| :--- | :--- |
| Java JDK | 25.0.2 |
| Eclipse IDE | 2024-12 (4.34) or later |
| MySQL Server | 8.0.45 |
| MySQL JDBC Connector | 8.0.33 |
| Maven | 3.6+ |

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/Event-Management-System.git
   ```

2. **Import into Eclipse**
   - Open Eclipse IDE.
   - Go to `File` → `Import` → `Existing Maven Projects`.
   - Browse to the cloned project folder and click `Finish`.

3. **Database Setup**
   - Open MySQL Workbench or any MySQL client.
   - Execute the SQL script located at:
     ```
     src/main/java/sql/ems.sql
     ```
   - This will create the schema, tables, and seed data.

4. **Configure Database Credentials**
   - Open `src/main/java/com/ems/util/DBConnectionUtil.java`.
   - Update the following constants with your MySQL environment credentials:
     ```java
     private static final String URL      = "jdbc:mysql://localhost:3306/ems";
     private static final String USER     = "your_mysql_username";
     private static final String PASSWORD = "your_mysql_password";
     ```

5. **Build the Project**
   - Right-click the project in Eclipse → `Run As` → `Maven Install`.
   - This will download all dependencies defined in `pom.xml`.

6. **Run the Application**
   - Navigate to `src/main/java/com/ems/App.java`.
   - Right-click → `Run As` → `Java Application`.
   - The console-based application will launch in the Eclipse Console.

---

## 📦 Maven Dependencies

| Dependency | Version | Purpose |
| :--- | :--- | :--- |
| `mysql-connector-java` | 8.0.33 | JDBC driver for MySQL database connectivity |
| `jbcrypt` | 0.4 | BCrypt password hashing and verification |
| `junit` | 3.8.1 | Unit testing framework |

---

## 👥 User Roles

| Role | Capabilities |
| :--- | :--- |
| **Admin** | Manage users, events, venues, categories, tickets, offers, notifications, and view system reports |
| **Organizer** | Create and manage own events, tickets, offers, view registrations and revenue reports |
| **Attendee** | Browse and search events, register, make payments, view bookings, submit feedback |
| **Guest** | Browse published events and register for a new account |

---
