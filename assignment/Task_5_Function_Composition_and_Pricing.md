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

For this exercise, keep the expected shape unambiguous by representing the three reusable behaviours in `PricingFunctions` as named functional-interface values. `static final` fields are a straightforward choice because the behaviours are stateless.

If you deliberately prefer static methods that return these functions, that is also valid, but choose one style. Do not create both a field and a method for the same behaviour.

### 1. Subtotal function

Represent:

```text
Order -> BigDecimal subtotal
```

as:

```java
Function<Order, BigDecimal>
```

Base it on `OrderCalculations.subtotal(...)`. Do not write another subtotal algorithm. A method reference is appropriate because the existing method already has the required shape.

### 2. Money rounding operator

Represent:

```text
BigDecimal -> BigDecimal
```

as:

```java
UnaryOperator<BigDecimal>
```

It must return a value at:

```text
2 decimal places
RoundingMode.HALF_UP
```

### 3. Percentage discount calculation

Represent:

```text
subtotal + decimal rate -> monetary discount amount
```

as:

```java
BiFunction<BigDecimal, BigDecimal, BigDecimal>
```

Example:

```text
subtotal = 100.00
rate     = 0.10
result   = 10.00
```

This function should perform the percentage calculation. Leave money rounding to the rounding operator so Task 6 can control exactly when rounding happens.

### 4. Compose functions

Create a reusable transformation for:

```text
Order
-> subtotal
-> round to 2 decimal places
```

Build it once with:

```java
andThen(...)
```

and once with:

```java
compose(...)
```

Both forms should represent the same execution sequence in this exercise. The purpose is to understand which side of the composition runs first.

Do not add a REST endpoint in Task 5. Task 6 will use these functions from `PricingService`.

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
