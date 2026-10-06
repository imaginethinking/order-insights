# Task 3: Sales Reporting and Aggregation

> **Prerequisite:** complete Task 2. Reuse `OrderCalculations.subtotal(...)`; do not create a second implementation of the order total calculation.

## Learning Objective

Use `flatMap`, collectors, grouping, partitioning, and aggregation to turn nested order data into report DTOs.

## Concept Introduction

### `flatMap`

`map` transforms one element into one result.

`flatMap` is useful when each input contains another collection and you want one combined stream.

For this project:

```text
Stream<Order>
-> each Order contains List<OrderLine>
-> flatMap
-> Stream<OrderLine>
```

### Collectors

Collectors finish a Stream by combining elements into another structure, such as a list, map, grouped result, partition, or joined string.

## Goal

Implement the reusable calculations required by:

```http
GET /api/reports/sales
```

and:

```http
GET /api/reports/customers/{customerId}
```

Unless stated otherwise, exclude `CANCELLED` orders.

## Part A: Sales Calculations

### 1. Overall totals

Calculate:

```text
totalOrders
totalRevenue
```

where both values consider orders whose status is not `CANCELLED`.

### 2. Order count by customer tier

Produce:

```java
Map<CustomerTier, Long>
```

Use:

```java
Collectors.groupingBy(...)
Collectors.counting()
```

### 3. Units sold by category

Use:

```text
orders
-> flatMap to lines
-> group by ProductCategory
-> sum quantities
```

A result such as this is suitable internally:

```java
Map<ProductCategory, Integer>
```

### 4. Revenue by category

For each category, calculate:

```text
sum(unitPrice × quantity)
```

Keep values as `BigDecimal`.

Combine the units and revenue calculations into one:

```text
CategorySalesDto
```

per qualifying category, producing:

```text
List<CategorySalesDto>
```

Sort the DTO list by category name so API output is deterministic.

### 5. Order total lookup

Create:

```java
Map<UUID, BigDecimal>
```

containing:

```text
order ID -> order total
```

Use:

```java
Collectors.toMap(...)
```

Order IDs are unique, so handling duplicate keys is not required.

### 6. Partition high value and normal orders

Use the order total lookup from the previous step.

High value means:

```text
total >= £500.00
```

Use:

```java
Collectors.partitioningBy(...)
```

Convert both partitions into:

```text
highValueOrderIds
normalValueOrderIds
```

Sort both ID lists by:

1. `placedOn` descending;
2. order ID ascending when dates tie.

### 7. Distinct SKU summary

From all qualifying lines:

1. obtain SKUs;
2. remove duplicates;
3. sort alphabetically;
4. join into one comma-separated string.

Example:

```text
ABC-100, CAB-200, XYZ-300
```

Use:

```java
distinct()
sorted()
Collectors.joining(...)
```

## Part B: Customer Summary

Implement the calculation used by:

```http
GET /api/reports/customers/{customerId}
```

For the requested customer, using orders whose status is not `CANCELLED`, calculate:

```text
customerId
customerName
orderCount
totalSpend
averageOrderValue
```

Use `OrderRepository` data and filter by the order's customer ID. Do not add a reverse `Customer.orders` relationship solely to make this report easier.

Use `CustomerRepository.findById(...)` to distinguish:

```text
existing customer with no qualifying orders
```

from:

```text
unknown customer
```

Unknown customer:

```text
404 Not Found
```

Existing customer with no qualifying orders:

```text
orderCount = 0
totalSpend = 0.00
averageOrderValue = 0.00
```

When dividing:

```text
averageOrderValue = totalSpend / orderCount
```

use:

```java
scale = 2
RoundingMode.HALF_UP
```

### Top customer helper

Only customers with at least one order whose status is not `CANCELLED` are eligible.

Create a helper that can determine the top customer by:

1. highest total spend;
2. if tied, customer name ascending;
3. if still tied, customer ID ascending.

If there are no qualifying orders, there is no top customer.

Represent absence internally as:

```java
Optional<CustomerSummaryDto>
```

## Suggested Pipeline Shape

At least one calculation should naturally resemble:

```text
orders
-> filter
-> flatMap
-> map
-> grouping / aggregation
-> DTO
```

## APIs to Investigate

```java
flatMap(...)
distinct()
reduce(...)
Collectors.groupingBy(...)
Collectors.partitioningBy(...)
Collectors.toMap(...)
Collectors.joining(...)
Collectors.counting()
Collectors.summingInt(...)
```

## Definition of Done

- [ ] Overall order count and total revenue are calculated.
- [ ] Order counts by tier are calculated.
- [ ] Units and revenue by category are calculated.
- [ ] `toMap()` is used for order totals.
- [ ] High/normal partitions are both used.
- [ ] Distinct SKU summary is produced.
- [ ] Customer summary endpoint works.
- [ ] Customers with no qualifying orders receive a summary with zero values.
- [ ] Top customer logic is deterministic.
- [ ] Money division uses explicit rounding.

## Suggested Reading

Useful Baeldung topics:

- Java `flatMap`
- Java `groupingBy`
- Java `partitioningBy`
- Java Collectors
