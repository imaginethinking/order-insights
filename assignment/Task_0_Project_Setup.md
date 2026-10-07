# Task 0: Project Setup

> **Before starting:** read `README.md`. It defines the domain, DTO contracts, API, and global business rules used throughout the assignment.

## Learning Objective

Set up a small Spring Boot application with packages grouped by feature. This gives you a starting point for the later tasks on Streams and functional interfaces.

## What You Are Building

Create the initial application, persistence model, repositories, sample data, controllers, DTOs, and basic error handling described in `README.md`.

Leave the business logic that uses Streams for the later tasks.

## Requirements

### 1. Create the project

Include:

- Spring Web
- Spring Data JPA
- H2
- Bean Validation
- Spring Boot Test

### 2. Create the domain model

Create:

- `Customer`
- `Order`
- `OrderLine`
- all enums listed in `README.md`

Use explicit table names:

```text
customers
orders
order_lines
```

Relationships:

```text
Customer 1 ─── * Order
Order    1 ─── * OrderLine
```

Use generated UUID identifiers.

`Order` should own the `OrderLine` collection.

For the JPA mapping:

- use a `customer_id` foreign key from `orders` to `customers`;
- use an `order_id` foreign key from `order_lines` to `orders`;
- avoid an extra join table for `Order.lines`;
- cascade persistence from `Order` to its lines;
- initialise `Order.lines` to an empty collection.

A reverse relationship from `Customer` or `OrderLine` is not required.

### 3. Create repositories

Create:

- `CustomerRepository`
- `OrderRepository`

Keep repositories concerned with persistence only.

### 4. Create the API DTOs

Create the DTO records defined in `README.md` with the required fields and types.

At this stage, the DTOs only need to represent the API contracts. You do **not** need to add all mapping or calculation logic yet. Later tasks tell you when each DTO should be populated.

Records are recommended.

### 5. Create controllers and service shells

Create:

- `OrderController`
- `OrderService`
- `ReportingController`
- `ReportingService`
- `PricingService`

Add method or endpoint shells for the API described in `README.md`, but do not implement the Stream-based business logic yet.

The project should compile and start. It is fine for later-task methods to remain incomplete until their task is reached.

### 6. Add consistent error handling

Create:

- `ResourceNotFoundException`;
- `ApiError`;
- `ApiExceptionHandler`.

Set up the common JSON error shape now. As later tasks introduce missing resources and invalid query values, route those failures through this handler using the status codes in `README.md`.

You do not need to manufacture errors for endpoints whose business logic has not been implemented yet.

### 7. Seed H2

Use any deterministic startup approach such as `CommandLineRunner`, `ApplicationRunner`, or SQL seed data.

Create roughly:

- 4-5 customers;
- 8-12 orders.

Your data must include:

- every customer tier represented by at least one customer;
- every customer tier represented by at least one order whose status is **not `CANCELLED`**;
- at least three regions;
- at least one cancelled order;
- at least one order with a backordered line;
- at least one order worth `>= £500`;
- at least one order worth `< £500`;
- at least one order with total quantity `>= 20`;
- repeated SKUs across different orders;
- every product category represented by at least one order line whose order is **not `CANCELLED`**;
- multiple orders for at least one customer;
- at least one line in every order.

Use realistic but simple values.

## Design for a Future Frontend

Although no frontend is being built, make the API suitable for one later:

- controllers return DTOs;
- entities stay inside the backend;
- JSON field names remain predictable;
- errors are returned as JSON;
- business logic stays in services or domain helpers.

## Definition of Done

- [ ] Application starts successfully.
- [ ] H2 schema is created.
- [ ] Sample data loads.
- [ ] All entities and enums exist.
- [ ] DTOs match `README.md`.
- [ ] Repositories exist.
- [ ] Controller/service shells exist.
- [ ] `Order` maps to the `orders` table.
- [ ] Error handling has a consistent JSON shape.

## Hint

Keep setup simple so you can spend most of your time on the Java exercises.
