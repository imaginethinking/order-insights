# Task 2: Order Search, Filtering, and Sorting

> **Prerequisite:** complete Task 1. Reuse `OrderCalculations.subtotal(...)` whenever this task needs an order total.

## Learning Objective

Use `Predicate<T>`, Stream filtering, comparators, and `findFirst()` to build readable query logic over collections held in memory.

## Concept Introduction

### `Predicate<T>`

A `Predicate<T>` represents a yes/no rule:

```text
T -> boolean
```

For example:

```text
Order -> does this order match the requested status?
```

Predicates are useful because filtering behaviour can be stored, passed into methods, and later composed.

### `Comparator`

A `Comparator<T>` defines ordering. `Comparator.comparing(...)` is a concise way to sort objects by one of their properties.

## Goal

Implement:

```http
GET /api/orders/search
```

Response:

```text
List<OrderSummaryDto>
```

## Requirements

Each query parameter is optional. If a parameter is absent, it must not filter the result.

Support any combination of:

```text
status
tier
region
minTotal
placedWithinDays
```

Support:

```text
sort=date
sort=total
sort=customer
```

Rules:

```text
date      -> placedOn descending, then order ID ascending
total     -> total descending, then placedOn descending, then order ID ascending
customer  -> customer name ascending, then placedOn descending, then order ID ascending
```

Default:

```text
date
```

### Minimum total rule

A matching order satisfies:

```text
total >= minTotal
```

Reject negative `minTotal`.

### Date window rule

For now, obtain the current date once near the start of the search method:

```java
LocalDate businessDate = LocalDate.now();
```

Do not call `LocalDate.now()` repeatedly inside the Stream.

The reusable and testable `Supplier<LocalDate>` version will replace this in Task 4.

A matching order is placed between:

```text
businessDate.minusDays(placedWithinDays)
```

and:

```text
businessDate
```

inclusive.

Reject negative `placedWithinDays`.

Use Bean Validation for negative numeric query parameters where practical rather than putting validation logic in the controller body.

### Use Stream operations

Your search should use:

```java
filter(...)
sorted(...)
toList()
```

Avoid a large nested `if` block around the entire pipeline.

It is acceptable in this task to build local predicate variables.

Task 4 will refactor them into reusable rules.

## Additional Service Exercises

### 1. Fully allocatable order

Implement a method that returns whether an order contains **no** backordered lines.

Use:

```java
noneMatch(...)
```

### 2. Find the most recent matching order

Create a service method that accepts:

```java
Predicate<Order>
```

Then:

1. filter by the supplied predicate;
2. sort by `placedOn` descending;
3. use `findFirst()`.

Return:

```java
Optional<Order>
```

Sorting by date puts the most recent matching order first.

## APIs to Investigate

```java
Predicate<T>
filter(...)
sorted(...)
Comparator.comparing(...)
Comparator.thenComparing(...)
noneMatch(...)
findFirst()
toList()
```

## Definition of Done

- [ ] Combined search filters work.
- [ ] Search returns DTOs, not entities.
- [ ] All three sorts and their rules for breaking ties work.
- [ ] Default sorting is newest first.
- [ ] Fully allocatable logic uses `noneMatch`.
- [ ] Lookup of the most recent matching order uses a supplied `Predicate`.
- [ ] Invalid negative numeric filters return 400.

## Suggested Reading

Useful Baeldung topics:

- Java Predicate
- Java Streams filtering
- Java Comparator
- Java Optional
