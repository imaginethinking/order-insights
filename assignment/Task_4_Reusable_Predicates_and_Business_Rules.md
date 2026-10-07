# Task 4: Reusable Predicates and Business Rules

> **Prerequisite:** complete Tasks 2-3. Refactor the search implementation from Task 2 in this task.

## Learning Objective

Refactor earlier filtering logic into small, reusable `Predicate<Order>` rules and compose them with `and()`, `or()`, and `negate()`.

## Concept Introduction

A predicate is more useful than a boolean flag when the caller needs to supply **behaviour**, not just data.

Predicates can also be composed:

```text
rule A AND rule B
rule A OR rule B
NOT rule A
```

This lets small rules form larger business rules without creating a large inheritance hierarchy.

### `Supplier<T>`

A `Supplier<T>` represents behaviour that produces a value without receiving an input.

For example:

```java
Supplier<LocalDate>
```

can provide the application's current business date.

Supplying the date makes the rule easier to test than calling `LocalDate.now()` inside it.

## Goal

Create:

```text
order/OrderRules.java
```

and refactor the Task 2 search logic to use reusable rules.

`OrderRules` can remain a stateless helper. Make it a Spring component only if your design needs one.

## Required Rules

Create named methods in `OrderRules` that return `Predicate<Order>`.

The goal is for Task 2 search filters to call these rules rather than keep their own copies of the same conditions.

### Exact status

Accept an `OrderStatus` and match:

```text
order.status == supplied status
```

### Exact customer tier

Accept a `CustomerTier` and match:

```text
order.customer.tier == supplied tier
```

### Active order

```text
status != CANCELLED
```

A natural way to express this is by reusing the exact-status rule and `negate()` rather than writing another status comparison.

### Preferred customer

```text
tier == SILVER OR tier == GOLD
```

A natural way to express this is by composing two exact-tier predicates with `or(...)`.

### Minimum value

```text
order total >= supplied threshold
```

Suggested shape:

```java
Predicate<Order> minimumValue(BigDecimal threshold)
```

Reuse `OrderCalculations.subtotal(...)` inside the rule.

### High value

Create a high value rule for:

```text
order total >= £500.00
```

Implement it by reusing `minimumValue(...)`, not by duplicating the comparison.

### Region

Accept a `Region` and match:

```text
order.customer.region == supplied region
```

### Fully allocatable

```text
no order line is backordered
```

Move or reuse the Task 2 `noneMatch(...)` behaviour here so there is only one implementation of this rule.

### Placed within N days

The rule method should accept:

```text
days + Supplier<LocalDate>
```

Production code can pass `LocalDate::now`; tests can pass a lambda that returns a fixed date.

For each call to the predicate, obtain the business date from the supplier and match orders from:

```text
businessDate.minusDays(days)
```

through:

```text
businessDate
```

inclusive.

## Composition Requirements

Use predicate composition for real rules rather than adding unused examples.

By the end of this task, your implementation should demonstrate:

- `negate()` through a meaningful rule such as active = NOT cancelled;
- `or(...)` through a meaningful rule such as preferred = SILVER OR GOLD;
- `and(...)` through at least one higher-level rule equivalent to:

```text
active AND preferred AND high value
```

The combined rule can be exposed as another named method in `OrderRules`. It does not need a new REST endpoint.

## Refactor Task 2

Update the search implementation from Task 2 so it reuses `OrderRules` for:

- status;
- tier;
- region;
- minimum total;
- placed-within-days.

Only apply a rule when its matching query parameter is present.

Keep the Task 2 search semantics and sort order unchanged. The purpose of this task is to move repeated conditions into reusable predicates, not to redesign the endpoint.

Also refactor the Task 2 fully allocatable method so it delegates to, or otherwise reuses, the `OrderRules` predicate instead of keeping a second `noneMatch(...)` implementation.

Task 2 was allowed to use local predicates and `LocalDate.now()`. Task 4 should remove that duplication and use the supplied date behaviour.

## Why Use Standard `Predicate`?

Do **not** create an `OrderPredicate` interface.

`Predicate<Order>` already:

- expresses the yes/no meaning clearly;
- supports composition;
- is widely understood by Java developers.

A custom interface would not add useful domain meaning here.

## Definition of Done

- [ ] `OrderRules` exists.
- [ ] Exact status, exact tier, region, minimum value, date, active, preferred, high value, and allocatable rules exist.
- [ ] `and()`, `or()`, and `negate()` are used in meaningful named rules.
- [ ] Date rules use `Supplier<LocalDate>`.
- [ ] Task 2 search reuses these rules.
- [ ] No unnecessary custom predicate interface exists.

## Suggested Reading

Useful Baeldung topics:

- Java Predicate
- Java Supplier
- Java Functional Interfaces
