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

Use `BigDecimal.ZERO` as the identity value.

### 3. Derive the backorder flag for the summary

When you build an `OrderSummaryDto`, derive `containsBackOrder` from the order's lines using:

```java
anyMatch(...)
```

The result should be `true` when at least one `OrderLine` has:

```text
backOrdered == true
```

`OrderLine.backOrdered` is the stored state. `OrderSummaryDto.containsBackOrder` is a derived summary value.

Do **not** add a separate `containsBackOrder` field to the `Order` entity. You may calculate the value directly in the mapping code or in a small helper if that makes the mapping easier to read.

### 4. Create one `Order -> OrderSummaryDto` mapping

Populate every field in `OrderSummaryDto` from the supplied `Order`.

Remember:

```text
lineCount = number of OrderLine objects
```

Use `OrderCalculations.subtotal(order)` for `totalValue` and the `anyMatch(...)` result from the previous step for `containsBackOrder`.

Create **one** implementation of this mapping. A static factory such as `OrderSummaryDto.from(Order)`, a mapper helper, or a service helper are all acceptable. Do not create several versions of the same mapping.

### 5. Represent that existing mapping as a `Function`

This step is about treating the mapping from Part 4 as reusable behaviour. It is **not** asking you to write the mapping again.

Where `OrderService` needs to turn an `Order` into an `OrderSummaryDto`, represent the existing mapping as:

```java
Function<Order, OrderSummaryDto>
```

If your Part 4 mapping is a normal method whose signature is already:

```text
Order -> OrderSummaryDto
```

use a method reference when it reads clearly. Then actually use the `Function`, for example as the mapper passed to a Stream or by calling `apply(...)`.

If you instead choose a helper method that returns a `Function<Order, OrderSummaryDto>`, use that approach consistently. Do **not** implement both approaches just to satisfy the task.

### 6. Implement the order summary endpoint flow

For:

```http
GET /api/orders/{orderId}
```

use this flow:

```text
repository lookup
-> handle Optional<Order>
-> map the Order to OrderSummaryDto
-> return the DTO
```

Unknown ID:

```text
404 Not Found
```

Do not return the JPA entity from the controller.

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
- [ ] `containsBackOrder` is derived from `OrderLine.backOrdered` with `anyMatch(...)`.
- [ ] `OrderSummaryDto` is returned.
- [ ] There is one Order-to-summary mapping implementation.
- [ ] That mapping is actually used as `Function<Order, OrderSummaryDto>` behaviour.
- [ ] A missing order returns 404.

## Suggested Reading

Useful Baeldung topics:

- Java Streams
- Java Function
- Java method references
- Java Optional
