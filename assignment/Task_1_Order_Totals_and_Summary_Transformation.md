# Task 1: Order Totals and Summary Transformation

> **Prerequisite:** complete Task 0 and keep `README.md` open for the DTO contract and API behaviour.

## Learning Objective

Use the Streams API for basic transformation and aggregation, and use `Function<T, R>` to represent reusable mapping behaviour.

## Concept Introduction

### Streams API

A Java `Stream` lets you describe a sequence of operations over data, such as:

```text
source -> transform -> filter -> aggregate
```

Streams are useful when collection processing can be expressed as a readable pipeline without mutating the source collection.

`reduce(...)` is a terminal Stream operation that combines many elements into one result, such as a monetary total.

### `Function<T, R>`

`Function<T, R>` represents behaviour that accepts one value of type `T` and returns a value of type `R`.

Example shape:

```text
Order -> OrderSummaryDto
```

This is useful when a transformation should be reusable or passed into another method.

### `Optional<T>`

`Optional<T>` represents a value that may or may not exist. Spring Data repository methods such as `findById(...)` return it so missing data can be handled explicitly rather than through `null`.

### Method references

A method reference such as `Type::method` is a concise alternative to a lambda when an existing method already matches the signature required by the functional interface.

## Goal

Implement the logic behind:

```http
GET /api/orders/{orderId}
```

## Requirements

### 1. Create reusable order calculations

Create:

```text
order/OrderCalculations.java
```

Keep pure calculations that are needed by several later features here. It should be stateless and does not need to be a Spring component.

Start with:

```text
lineValue(OrderLine)
subtotal(Order)
```

For each `OrderLine`:

```text
line value = unitPrice × quantity
```

Keep money as `BigDecimal`.

### 2. Calculate an order subtotal

Implement `subtotal(Order)` using a Stream pipeline that:

```text
order lines
-> map each line to its value
-> reduce to one BigDecimal total
```

Use `BigDecimal.ZERO` as an appropriate identity value.

### 3. Detect backorders

Use:

```java
anyMatch(...)
```

to determine whether any line has:

```text
backOrdered == true
```

### 4. Build `OrderSummaryDto`

Populate all fields defined in `README.md`.

Remember:

```text
lineCount = number of OrderLine objects
```

### 5. Make the mapping reusable

Represent the transformation as:

```java
Function<Order, OrderSummaryDto>
```

or return that function from a helper method.

Use a method reference where it improves readability.

### 6. Handle a missing order

Handle the repository's `Optional<Order>` result explicitly.

Unknown ID:

```text
404 Not Found
```

## APIs to Investigate

```java
stream()
map(...)
reduce(...)
anyMatch(...)
Function<T, R>
Optional
BigDecimal::add
```

## Avoid

- converting money to `double`;
- mutating the order inside Stream operations;
- returning a JPA entity from the controller.

## Definition of Done

- [ ] `OrderCalculations` contains reusable calculations for line values and subtotals.
- [ ] Order subtotal is correct.
- [ ] Backorder detection works.
- [ ] `OrderSummaryDto` is returned.
- [ ] Mapping uses reusable `Function` behaviour.
- [ ] A missing order returns 404.

## Suggested Reading

Useful Baeldung topics:

- Java Streams
- Java Function
- Java method references
- Java Optional
