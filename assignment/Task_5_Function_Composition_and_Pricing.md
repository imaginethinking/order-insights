# Task 5: Function Composition and Pricing

> **Prerequisite:** complete Task 1. Reuse the pure calculations in `OrderCalculations` rather than moving business logic into the pricing package.

## Learning Objective

Use `Function`, `UnaryOperator`, and `BiFunction` to model small price transformations and compose them into readable pipelines.

## Concept Introduction

### `Function<T, R>`

Represents:

```text
one input -> one output
```

Example:

```text
Order -> BigDecimal subtotal
```

### `UnaryOperator<T>`

A specialised function where input and output have the same type:

```text
BigDecimal -> BigDecimal
```

This fits operations such as monetary rounding.

### `BiFunction<T, U, R>`

Represents:

```text
two inputs -> one output
```

This is useful when a calculation needs two independent values, such as:

```text
subtotal + discount rate -> discount amount
```

### Function composition

`andThen()` and `compose()` connect compatible functions.

They differ in execution order, so you should understand both rather than memorising the names.

## Goal

Create:

```text
pricing/PricingFunctions.java
```

containing small, reusable pricing functions.

## Requirements

### 1. Subtotal function

Create reusable behaviour equivalent to:

```text
Order -> BigDecimal subtotal
```

Base this function on `OrderCalculations.subtotal(...)` rather than duplicating business logic. A method reference is appropriate if the signatures align.

### 2. Money rounding operator

Create:

```text
BigDecimal -> BigDecimal
```

that returns a value at:

```text
2 decimal places
RoundingMode.HALF_UP
```

Use:

```java
UnaryOperator<BigDecimal>
```

### 3. Percentage discount calculation

Create:

```java
BiFunction<BigDecimal, BigDecimal, BigDecimal>
```

where:

```text
first input  = subtotal
second input = decimal rate
output       = monetary discount amount
```

Example:

```text
subtotal = 100.00
rate     = 0.10
result   = 10.00
```

### 4. Compose functions

Build a transformation equivalent to:

```text
Order
-> subtotal
-> round to 2 decimal places
```

Demonstrate:

```java
andThen(...)
```

and:

```java
compose(...)
```

They may produce equivalent results in this simple example; the point is to understand execution order.

## Important

This task creates **generic pricing functions**.

Task 6 will use them to implement the discount policies.

Do not implement policy selection in this task.

## Definition of Done

- [ ] `PricingFunctions` exists.
- [ ] Subtotal is represented by reusable `Function` behaviour.
- [ ] Rounding uses `UnaryOperator`.
- [ ] Percentage discount amount uses `BiFunction`.
- [ ] Both `andThen()` and `compose()` are demonstrated.
- [ ] Money remains `BigDecimal`.

## Suggested Reading

Useful Baeldung topics:

- Java Function
- Java UnaryOperator
- Java BiFunction
- Function composition
