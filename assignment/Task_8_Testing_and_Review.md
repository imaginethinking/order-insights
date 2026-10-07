# Task 8: Testing and Review

> **Prerequisite:** complete Tasks 1-7. Test the behaviour you built rather than Spring framework internals.

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

## `OrderCalculations` Tests

Test the pure helper directly. Mockito is not needed here.

Cover:

- line value;
- subtotal with several lines;
- subtotal with one line;
- total quantity after Task 6.

## `OrderService` Tests

Mock `OrderRepository` where the service loads data.

Cover:

- order summary transformation;
- backorder flag in the returned DTO;
- one search filter;
- several combined filters;
- date sorting;
- total sorting;
- customer sorting;
- default sorting;
- empty search result;
- missing order ID;
- `findFirst()` returning the most recent matching order, including the same-date tie rule;
- fully allocatable behaviour.

Do not re-test the internal arithmetic of `OrderCalculations` in every service test. Verify that the service result is correct at the service boundary.

## `ReportingService` Tests

Cover:

- overall report totals;
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

## `OrderRules` Predicate Tests

Test the reusable rules directly with `Predicate.test(...)`.

Cover the individual rules used by search and the composed rules using:

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

Use a deterministic:

```java
Supplier<LocalDate>
```

for date-rule tests.

Also pass at least one `Predicate<Order>` directly into the Task 2 method that accepts behaviour and verify that the supplied predicate controls the result.

## `PricingFunctions` Tests

Test the functional-interface values directly with `apply(...)`.

Cover:

- subtotal `Function`;
- rounding `UnaryOperator`;
- percentage discount `BiFunction`;
- the `andThen(...)` subtotal-and-round composition;
- the equivalent `compose(...)` subtotal-and-round composition.

For the two composition forms, verify that both produce the expected result and be able to explain their execution order. You do not need to invent an unrelated example solely to make the two forms return different values.

## `PricingService` Tests

Mock the repository and `DiscountPolicyResolver` as appropriate.

Test:

- `NONE`;
- `LOYALTY` when the customer qualifies and when they do not;
- `BULK` when the order qualifies and when it does not;
- exact bulk boundary of quantity `20`;
- a mocked `DiscountPolicyResolver` returning a `DiscountPolicy` defined as a lambda;
- missing order;
- the defined rounding sequence and final total.

Verify that the service uses the `DiscountPolicy` returned by the resolver rather than calculating its own policy-specific discount.

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

to capture every DTO passed into the consumer.

Verify:

- the consumer is invoked once per customer;
- the captured DTO contents are correct;
- the zero-order customer is present;
- invocation order follows customer name ascending, then customer ID ascending.

This is the required `ArgumentCaptor` use case. Do not invent another collaborator purely for testing practice.

## Definition of Done

- [ ] Pure helpers are tested directly without unnecessary mocks.
- [ ] Service dependencies are mocked where appropriate.
- [ ] Spring framework classes are not unnecessarily mocked.
- [ ] Empty and boundary cases are covered.
- [ ] A supplied `Predicate<Order>` is tested as behaviour.
- [ ] Reusable pricing functions are tested directly and in composition.
- [ ] `ArgumentCaptor` verifies consumer output and order.
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
