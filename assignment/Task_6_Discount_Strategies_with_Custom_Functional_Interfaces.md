# Task 6: Discount Strategies with a Custom Functional Interface

> **Prerequisite:** complete Task 5. Use the pricing functions from Task 5 to implement discount policies.

## Learning Objective

Create a custom `@FunctionalInterface` so discount policies can be supplied as lambdas or method references.

## Concept Introduction

A custom functional interface is useful when:

- the behaviour has a clear domain meaning;
- a generic JDK interface would hide that meaning;
- you still want to supply the behaviour with lambdas or method references.

This task uses a custom `DiscountPolicy` because "discount policy" is a business concept.

`@FunctionalInterface` tells the compiler that the interface is intended to have exactly one abstract method, which keeps it compatible with lambdas and method references.

## Goal

Implement the quote endpoint:

```http
GET /api/orders/{orderId}/quote?policy=LOYALTY
```

## 1. Create `DiscountPolicy`

Create a custom interface similar to:

```java
@FunctionalInterface
public interface DiscountPolicy {
    BigDecimal discountFor(Order order, BigDecimal subtotal);
}
```

The method returns the **monetary discount amount**.

Although this has the same basic shape as a `BiFunction`, the custom name makes the business intent clearer.

## 2. Implement the policies

Use the rules from `README.md`.

### `NONE`

```text
discount = £0.00
```

### `LOYALTY`

```text
GOLD customer -> 10%
otherwise     -> 0%
```

### `BULK`

```text
total item quantity >= 20 -> 7.5%
otherwise                 -> 0%
```

Add a reusable `totalQuantity(Order)` calculation to `OrderCalculations` and use a Stream to sum line quantities.

At least one policy must be created with a lambda or method reference rather than a concrete implementation class.

Reuse the percentage discount `BiFunction` from Task 5.

## 3. Create `DiscountPolicyResolver`

Create:

```text
pricing/DiscountPolicyResolver.java
```

It should translate:

```java
DiscountPolicyType
```

into the corresponding:

```java
DiscountPolicy
```

Example flow:

```text
LOYALTY
-> resolver
-> loyalty DiscountPolicy
```

Keep the switch that selects a policy out of the method that calculates the price.

Inject the resolver through the `PricingService` constructor. This keeps policy selection separate from price calculation and makes the interaction easy to unit test.

## 4. Implement `PricingService`

The service should:

1. load the order;
2. calculate subtotal;
3. resolve the requested policy;
4. calculate the raw discount amount;
5. round the discount amount to 2 decimal places;
6. calculate:
   ```text
   finalTotal = subtotal - roundedDiscountAmount
   ```
7. return `subtotal`, `discountAmount`, and `finalTotal` at 2 decimal places using `RoundingMode.HALF_UP`;
8. return `QuoteDto`.

Any existing order may be quoted, including a cancelled order.

Unknown order:

```text
404 Not Found
```

Unknown policy:

```text
400 Bad Request
```

## Why a Custom Interface Here?

This:

```java
DiscountPolicy policy
```

communicates more business meaning than:

```java
BiFunction<Order, BigDecimal, BigDecimal> function
```

Do not create custom wrappers where `Predicate`, `Supplier`, or `UnaryOperator` already express the meaning clearly.

## Definition of Done

- [ ] `DiscountPolicy` is annotated with `@FunctionalInterface`.
- [ ] `DiscountPolicyType` contains `NONE`, `LOYALTY`, and `BULK`.
- [ ] `DiscountPolicyResolver` maps types to behaviour.
- [ ] Exact discount rules match `README.md`.
- [ ] At least one policy is a lambda/method reference.
- [ ] Task 5 pricing functions are reused.
- [ ] Quote endpoint returns `QuoteDto`.

## Suggested Reading

Useful Baeldung topics:

- Java `@FunctionalInterface`
- Java Functional Interfaces
- Strategy pattern with lambdas
