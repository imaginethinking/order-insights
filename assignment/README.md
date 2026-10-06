# Order Insights Service

A short Spring Boot assignment focused on **Java Streams**, **functional interfaces**, and clear service design.

The project should take roughly **one working day** for someone already comfortable with basic Spring Boot, REST controllers, JPA, DTOs, validation, exception handling, JUnit, and Mockito.

## Scenario

Build a small backend for managing internal wholesale orders.

The system stores customers, orders, and order lines. You will implement:

- order summaries;
- order search and sorting;
- sales reporting;
- reusable business rules;
- functional pricing transformations;
- configurable discount strategies;
- focused service tests.

The aim is to practise modern Java rather than build a large CRUD application.

## Intended Learning Outcomes

By the end of the assignment, you should be able to:

- build readable Stream pipelines for filtering, transformation, flattening, sorting, grouping, partitioning, and aggregation;
- use `Predicate`, `Function`, `Consumer`, `Supplier`, `UnaryOperator`, and `BiFunction` for realistic backend behaviour;
- compose predicates and functions instead of duplicating business logic;
- create a custom `@FunctionalInterface` when its name makes the business purpose clearer;
- use method references and `Optional` appropriately;
- keep Stream operations free from hidden mutation and side effects;
- recognise when a conventional loop is clearer than a Stream;
- unit test service logic that uses Streams and functions with JUnit 5 and Mockito.

## Technical Requirements

Use:

- Java 17 or newer;
- Spring Boot;
- Maven;
- Spring Web;
- Spring Data JPA;
- H2;
- Bean Validation;
- JUnit 5;
- Mockito.

Do **not** add authentication, Kafka, Docker, cloud deployment, external services, or a frontend.

## Implementation Standards

Follow these Spring Boot practices:

- package by feature;
- constructor dependency injection;
- thin controllers;
- business logic in services or focused domain helpers;
- repositories concerned with persistence only;
- DTOs at the API boundary;
- small methods and clear names;
- no unnecessary inheritance or abstractions.

This assignment intentionally performs some filtering and aggregation in Java so you can practise Streams. With a large production dataset, it would often be better to filter and aggregate in database queries.

## Future Frontend Compatibility

No frontend is required now, but the backend should be easy to consume from a future Angular, React, or similar client.

Keep that future use in mind by:

- returning DTOs rather than JPA entities;
- keeping controllers thin;
- exposing predictable JSON responses;
- using stable REST paths and enum values;
- returning consistent JSON errors;
- keeping business logic out of controllers;
- avoiding HTML rendered by the server.

Leave frontend code and CORS configuration until you build a frontend.

## Domain Model

### `Customer`

| Field | Type |
|---|---|
| `id` | `UUID` |
| `name` | `String` |
| `tier` | `CustomerTier` |
| `region` | `Region` |

### `Order`

| Field | Type |
|---|---|
| `id` | `UUID` |
| `customer` | `Customer` |
| `status` | `OrderStatus` |
| `placedOn` | `LocalDate` |
| `lines` | `List<OrderLine>` |

### `OrderLine`

| Field | Type |
|---|---|
| `id` | `UUID` |
| `sku` | `String` |
| `category` | `ProductCategory` |
| `quantity` | `int` |
| `unitPrice` | `BigDecimal` |
| `backOrdered` | `boolean` |

### Enums

```text
CustomerTier: STANDARD, SILVER, GOLD
Region: NORTH, SOUTH, EAST, WEST
OrderStatus: PENDING, CONFIRMED, DISPATCHED, CANCELLED
ProductCategory: HARDWARE, OFFICE, ELECTRONICS, OTHER
DiscountPolicyType: NONE, LOYALTY, BULK
```

## Entity Relationships

Use these relationships:

```text
Customer 1 ─── * Order
Order    1 ─── * OrderLine
```

Required mapping rules:

- use generated `UUID` identifiers for all three entities;
- `Order` has a `ManyToOne` relationship to `Customer` using a `customer_id` foreign key;
- `Order` owns a unidirectional `OneToMany` collection of `OrderLine`;
- map that collection with an `order_id` foreign key in `order_lines`, rather than introducing a join table;
- cascade persistence from `Order` to its lines so an order and its lines can be seeded together;
- initialise the collection of order lines to an empty collection;
- a reverse `Customer.orders` collection is **not required**;
- a reverse `OrderLine.order` reference is **not required**;
- map the entity tables explicitly as:
  - `customers`
  - `orders`
  - `order_lines`

> `Order` should map to `orders` because `ORDER` is a SQL keyword in many databases.

## DTO Contracts

Use records where practical.

### `OrderSummaryDto`

```text
UUID orderId
String customerName
OrderStatus status
LocalDate placedOn
int lineCount
BigDecimal totalValue
boolean containsBackOrder
```

`lineCount` means the number of `OrderLine` objects, not the total product quantity.

### `CustomerSummaryDto`

```text
UUID customerId
String customerName
long orderCount
BigDecimal totalSpend
BigDecimal averageOrderValue
```

### `CategorySalesDto`

```text
ProductCategory category
int unitsSold
BigDecimal revenue
```

### `SalesReportDto`

```text
long totalOrders
BigDecimal totalRevenue
Map<CustomerTier, Long> ordersByTier
List<CategorySalesDto> salesByCategory
List<UUID> highValueOrderIds
List<UUID> normalValueOrderIds
String distinctSkuSummary
CustomerSummaryDto topCustomer
```

`topCustomer` may be `null` in the JSON response when no qualifying orders exist. Internally, model the absence of a result with `Optional<CustomerSummaryDto>`.

### `QuoteDto`

```text
UUID orderId
BigDecimal subtotal
BigDecimal discountAmount
BigDecimal finalTotal
DiscountPolicyType policy
```

## Global Business Rules

Unless a task states otherwise:

- reporting excludes `CANCELLED` orders;
- an **active** order means any order whose status is not `CANCELLED`;
- a **preferred customer** is `SILVER` or `GOLD`;
- minimum value checks are inclusive: `total >= threshold`;
- the threshold for high value orders in the report is **£500.00**, inclusive;
- monetary API values use **2 decimal places** with `RoundingMode.HALF_UP`;
- a customer with no qualifying orders has:
  - `orderCount = 0`
  - `totalSpend = 0.00`
  - `averageOrderValue = 0.00`
- backordered lines still contribute to totals and reporting; `backOrdered` is an allocation flag only;
- only customers with at least one order whose status is not `CANCELLED` are eligible to be `topCustomer`;
- ranking for the top customer is:
  1. highest total spend;
  2. if tied, customer name ascending;
  3. if still tied, customer ID ascending;
- date window checks are inclusive:
  - from `businessDate.minusDays(days)`
  - through `businessDate`;
- any existing order may be quoted, regardless of order status;
- grouped report data contains only tiers and categories present in qualifying data; do not add groups with zero values.

For an empty sales report, return:

```text
totalOrders = 0
totalRevenue = 0.00
ordersByTier = {}
salesByCategory = []
highValueOrderIds = []
normalValueOrderIds = []
distinctSkuSummary = ""
topCustomer = null
```

## Discount Rules

Use these exact policies:

### `NONE`

- discount amount: `£0.00`

### `LOYALTY`

- `GOLD` customer: **10%**
- otherwise: `0%`

### `BULK`

- if total item quantity across all order lines is **20 or more**: **7.5%**
- otherwise: `0%`

The custom discount policy returns a **monetary discount amount**, not a percentage rate.

## API

### Order summary

```http
GET /api/orders/{orderId}
```

Response:

```text
OrderSummaryDto
```

### Search orders

```http
GET /api/orders/search
```

Optional query parameters:

```text
status
tier
region
minTotal
placedWithinDays
sort
```

Response:

```text
List<OrderSummaryDto>
```

Sort rules:

```text
date      -> placedOn descending, then order ID ascending
total     -> total descending, then placedOn descending, then order ID ascending
customer  -> customer name ascending, then placedOn descending, then order ID ascending
```

Default sort:

```text
date
```

### Sales report

```http
GET /api/reports/sales
```

Response:

```text
SalesReportDto
```

### Customer summary

```http
GET /api/reports/customers/{customerId}
```

Response:

```text
CustomerSummaryDto
```

### Quote an order

```http
GET /api/orders/{orderId}/quote?policy=LOYALTY
```

Response:

```text
QuoteDto
```

## Error Behaviour

Return predictable JSON errors.

| Condition | HTTP result |
|---|---|
| unknown order ID | `404 Not Found` |
| unknown customer ID | `404 Not Found` |
| invalid enum/query value | `400 Bad Request` |
| negative `minTotal` | `400 Bad Request` |
| negative `placedWithinDays` | `400 Bad Request` |
| unknown discount policy | `400 Bad Request` |

Use a small, consistent `ApiError` DTO containing at least:

```text
int status
String error
String message
String path
```

A central `@RestControllerAdvice` is sufficient.

## Suggested Package Structure

```text
com.example.orderinsights
├── customer
│   ├── Customer.java
│   ├── CustomerRepository.java
│   ├── CustomerTier.java
│   └── Region.java
├── order
│   ├── Order.java
│   ├── OrderLine.java
│   ├── OrderCalculations.java
│   ├── OrderController.java
│   ├── OrderRepository.java
│   ├── OrderService.java
│   ├── OrderRules.java
│   ├── OrderStatus.java
│   ├── ProductCategory.java
│   └── dto
│       └── OrderSummaryDto.java
├── pricing
│   ├── DiscountPolicy.java
│   ├── DiscountPolicyResolver.java
│   ├── DiscountPolicyType.java
│   ├── PricingFunctions.java
│   ├── PricingService.java
│   └── dto
│       └── QuoteDto.java
├── reporting
│   ├── ReportingController.java
│   ├── ReportingService.java
│   └── dto
│       ├── CustomerSummaryDto.java
│       ├── CategorySalesDto.java
│       └── SalesReportDto.java
└── common
    ├── api
    │   ├── ApiError.java
    │   └── ApiExceptionHandler.java
    └── exception
        └── ResourceNotFoundException.java
```

This shows the **completed project structure**. Later tasks introduce some of these classes, so do not create every file during Task 0 unless that task asks you to.

You may adjust file placement slightly, but keep classes grouped into packages by feature.

## Task Order

Complete the files in this order:

1. `Task_0_Project_Setup.md`
2. `Task_1_Order_Totals_and_Summary_Transformation.md`
3. `Task_2_Order_Search_Filtering_and_Sorting.md`
4. `Task_3_Sales_Reporting_and_Aggregation.md`
5. `Task_4_Reusable_Predicates_and_Business_Rules.md`
6. `Task_5_Function_Composition_and_Pricing.md`
7. `Task_6_Discount_Strategies_with_Custom_Functional_Interfaces.md`
8. `Task_7_Final_Report_and_Consumer_Callback.md`
9. `Task_8_Testing_and_Review.md`

Each task builds on the previous ones. Avoid jumping ahead unless you are already comfortable with the earlier concepts.
