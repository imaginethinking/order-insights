# Task 8: Testing and Review

> **Prerequisite:** complete Tasks 1–7. Test the behaviour you built rather than Spring framework internals.

## Learning Objective

Test business logic that uses Streams and functions with JUnit 5 and Mockito, including behaviour passed into the unit under test.

## Concept Introduction

### Mockito

Mockito lets a unit test replace dependencies such as repositories with controlled test doubles.

For this assignment, use it to isolate your own service logic rather than mock Spring framework internals.

### `ArgumentCaptor`

`ArgumentCaptor<T>` records an argument passed to a mocked collaborator so the test can inspect the exact value.

It is useful here for verifying the DTOs sent to the Task 7 `Consumer<CustomerSummaryDto>`.

## Required Tools

Use these tools in the tests below:

```java
@Mock
@InjectMocks
when(...).thenReturn(...)
verify(...)
ArgumentCaptor
```

## `OrderService` Tests

Cover:

- order subtotal;
- DTO transformation;
- backorder detection;
- one search filter;
- several combined filters;
- date sorting;
- total sorting;
- customer sorting;
- default sorting;
- empty search result;
- missing order ID;
- `findFirst()` returning the most recent matching order;
- fully allocatable `noneMatch()` behaviour.

## `ReportingService` Tests

Cover:

- grouping by customer tier;
- `flatMap` category aggregation;
- category revenue;
- high/normal partitioning;
- `toMap()` lookup of order totals;
- distinct SKU summary;
- customer summary calculation;
- customer with zero qualifying orders;
- missing customer ID;
- top customer;
- rules for breaking ties when choosing the top customer;
- empty order collection;
- all orders cancelled;
- all fields of an empty report.

## Predicate Tests

Test rules individually and in composition:

```text
A AND B
A OR B
NOT A
```

Include boundaries:

```text
order total exactly equals minimum threshold
order total exactly equals £500 report threshold
placedOn exactly equals earliest allowed date
```

Pass at least one:

```java
Predicate<Order>
```

directly into the unit under test.

Use a deterministic:

```java
Supplier<LocalDate>
```

for tests of date rules.

## Function Tests

Test:

- subtotal `Function`;
- rounding `UnaryOperator`;
- percentage discount `BiFunction`;
- composed `Function`;
- the difference in execution order between:
  - `compose()`
  - `andThen()`

Test the reusable `Function` behaviour directly with `apply(...)`, and test composed functions as behaviour rather than reimplementing their calculations inside the test.

## `PricingService` Tests

Test:

- `NONE`;
- `LOYALTY` when the customer qualifies and when they do not;
- `BULK` when the order qualifies and when it does not;
- exact bulk boundary of quantity `20`;
- a mocked `DiscountPolicyResolver` returning a `DiscountPolicy` defined as a lambda;
- missing order;
- correct final rounding.

Verify that the service uses the policy returned by the resolver rather than calculating its own discount.

## Consumer + `ArgumentCaptor`

Mock:

```java
Consumer<CustomerSummaryDto>
```

Call the Task 7 publishing method with several customers, including one with zero qualifying orders.

Use:

```java
ArgumentCaptor<CustomerSummaryDto>
```

to capture the DTOs passed into the consumer and verify their contents.

This is the required `ArgumentCaptor` use case. Do not invent another collaborator purely for testing practice.

## Definition of Done

- [ ] JUnit 5 tests cover the main business logic.
- [ ] Repositories are mocked where appropriate.
- [ ] Spring framework classes are not unnecessarily mocked.
- [ ] Empty and boundary cases are covered.
- [ ] Predicate behaviour is passed into the method that accepts it.
- [ ] Reusable functions are tested directly and in composition.
- [ ] `ArgumentCaptor` verifies consumer output.
- [ ] Tests remain readable and focused.

---

# Final Review Questions

You should be able to answer these without looking them up first.

1. Why is a `Predicate<Order>` more flexible than several boolean flags?
2. What is the difference between `map()` and `flatMap()`?
3. Why are side effects inside Stream operations usually undesirable?
4. Why is `DiscountPolicy` a reasonable custom functional interface?
5. Why would a custom `OrderPredicate` add little value?
6. What is the difference between `compose()` and `andThen()`?
7. Why is `Supplier<LocalDate>` useful for testing?
8. When would a loop be clearer than a Stream?
9. What does Stream laziness mean?
10. Why is `BigDecimal` preferable to `double` for money?

# Optional Stretch Goals

Choose at most two.

## 1. Nested collector report

Produce:

```java
Map<CustomerTier, Map<ProductCategory, Integer>>
```

showing units sold by category within each customer tier.

## 2. Combine discount policies

Allow several `DiscountPolicy` instances to be evaluated and apply:

```text
the highest monetary discount
```

Keep the behaviour explicit and testable.

## 3. Stream vs loop comparison

Implement one small aggregation twice:

- once with collectors;
- once with a conventional loop.

Write a short note explaining which version you would keep in production and why.
