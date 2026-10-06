# Task 4: Reusable Predicates and Business Rules

> **Prerequisite:** complete Tasks 2–3. Refactor the search implementation from Task 2 in this task.

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

Implement named methods that return `Predicate<Order>`.

### Active order

```text
status != CANCELLED
```

### Preferred customer

```text
tier == SILVER OR tier == GOLD
```

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

```text
order.customer.region == supplied region
```

### Fully allocatable

```text
no order line is backordered
```

### Placed within N days

Give the rule method a supplied:

```java
Supplier<LocalDate>
```

The method should accept:

```text
days + Supplier<LocalDate>
```

Production code can pass `LocalDate::now`; tests can pass a lambda that returns a fixed date.

Match orders from:

```text
businessDate.minusDays(days)
```

through:

```text
businessDate
```

inclusive.

## Composition Requirements

Create at least one combined rule equivalent to:

```text
active AND preferred AND high value
```

Also use each of these to combine or invert rules:

```java
or(...)
negate(...)
```

Example ideas:

```text
preferred OR high value
NOT cancelled
```

Use combinations that express useful business rules.

## Refactor Task 2

Update the search implementation from Task 2 so its filters reuse methods from `OrderRules`.

Also refactor the fully allocatable check from Task 2 to reuse the corresponding rule.

Task 2 was allowed to use local predicates and `LocalDate.now()`.

Task 4 should remove that duplication and use the supplied date instead.

## Why Use Standard `Predicate`?

Do **not** create an `OrderPredicate` interface.

`Predicate<Order>` already:

- expresses the yes/no meaning clearly;
- supports composition;
- is widely understood by Java developers.

A custom interface would not add useful domain meaning here.

## Definition of Done

- [ ] `OrderRules` exists.
- [ ] Rules have precise meanings matching this file.
- [ ] At least one rule is returned from a method.
- [ ] `and()`, `or()`, and `negate()` are demonstrated.
- [ ] Date rules use `Supplier<LocalDate>`.
- [ ] Task 2 search reuses these rules.
- [ ] No unnecessary custom predicate interface exists.

## Suggested Reading

Useful Baeldung topics:

- Java Predicate
- Java Supplier
- Java Functional Interfaces
