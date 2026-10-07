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

Reuse the percentage-discount `BiFunction` from Task 5 when a percentage policy applies.

At least one policy must be represented directly by a lambda or method reference. You do not need a separate implementation class for each policy.

## 3. Create `DiscountPolicyResolver`

Create:

```text
pricing/DiscountPolicyResolver.java
```

Give it one clear responsibility:

```text
DiscountPolicyType -> DiscountPolicy
```

For example, a `resolve(DiscountPolicyType type)` method can return the corresponding policy behaviour.

Keep policy selection here. Do not put a `switch` over policy types inside the price-calculation method in `PricingService`.

Inject the resolver through the `PricingService` constructor so the service can be unit tested with a controlled policy.

## 4. Implement `PricingService`

Implement the quote flow in this order:

1. load the order;
2. calculate the raw subtotal with `OrderCalculations.subtotal(...)` or the Task 5 subtotal function;
3. resolve the requested `DiscountPolicy`;
4. pass the order and raw subtotal to the policy;
5. round the returned discount amount to 2 decimal places with the Task 5 rounding operator;
6. calculate `rawSubtotal - roundedDiscountAmount`;
7. round the final total to 2 decimal places;
8. round the subtotal to 2 decimal places for the DTO;
9. return `QuoteDto` with the requested policy type.

This gives one defined rounding sequence and avoids rounding the percentage calculation more than once.

Any existing order may be quoted, including a cancelled order.

For:

```http
GET /api/orders/{orderId}/quote?policy=LOYALTY
```

the `policy` query parameter is required.

Unknown order:

```text
404 Not Found
```

Missing or invalid policy value:

```text
400 Bad Request
```

Spring may reject an invalid enum query value before the resolver is called. That is fine as long as it is returned through the consistent API error handling.

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
