# Task 7: Final Report and Consumer Callback

> **Prerequisite:** complete Tasks 1–6. This task should assemble and reuse existing calculations rather than introduce duplicate report logic.

## Learning Objective

Combine the earlier Stream pipelines and functional behaviours into one reporting service, then use `Consumer<T>` for an action performed after the report calculation is complete.

## Concept Introduction

### `Consumer<T>`

A `Consumer<T>` accepts a value and returns no result:

```text
T -> void
```

Use it to act on a calculated value, for example by:

- publishing;
- exporting;
- logging;
- sending to another component.

The important distinction is that the Stream should **calculate** the result first. Side effects should not be hidden inside `map()`, `filter()`, or `peek()`.

## Part A: Complete `SalesReportDto`

Implement:

```http
GET /api/reports/sales
```

Reuse the calculations from earlier tasks.

The final report must contain:

```text
totalOrders
totalRevenue
ordersByTier
salesByCategory
highValueOrderIds
normalValueOrderIds
distinctSkuSummary
topCustomer
```

Rules:

- exclude `CANCELLED` orders;
- high value means `>= £500.00`;
- category output is deterministic;
- high/normal order IDs use the deterministic ordering defined in Task 3;
- top customer follows the rules for breaking ties from `README.md`;
- `topCustomer` is absent internally as an `Optional` when there are no qualifying orders and becomes `null` only when building the API DTO;
- when there are no qualifying orders, use the values for an empty report defined in `README.md`.

At least one substantial calculation should clearly resemble:

```text
collection
-> filter
-> flatMap or map
-> grouping / aggregation
-> response DTO
```

Break large calculations into focused helper methods.

## Part B: `Consumer<CustomerSummaryDto>`

Add a service method such as:

```text
publishCustomerSummaries(Consumer<CustomerSummaryDto> consumer)
```

The exact method name is your choice.

It should:

1. load all customers;
2. build a finished summary for each customer, including customers with zero qualifying orders;
3. sort the summaries by customer name ascending, then customer ID ascending;
4. complete all Stream calculations;
5. invoke the supplied consumer once for each resulting DTO.

This is a learning exercise only.

Do **not** add:

- another REST endpoint;
- file export;
- external integration;
- message queue.

## Avoid Hidden Side Effects

Do not use:

```java
peek(...)
```

to perform business actions.

Prefer:

```text
calculate DTO list
-> then pass each finished DTO to the Consumer
```

## When a Loop Is Better

If part of the report becomes highly stateful or difficult to read as a Stream, a conventional loop is acceptable.

The goal is clear Java, not maximum Stream usage.

## Definition of Done

- [ ] `SalesReportDto` is complete.
- [ ] Earlier rules/functions are reused.
- [ ] At least one Stream pipeline combines several steps to produce report data.
- [ ] No hidden mutation is required inside Stream operations.
- [ ] A `Consumer<CustomerSummaryDto>` callback is implemented.
- [ ] Consumer work happens after DTO calculation.
- [ ] No unnecessary extra endpoint or integration is added.

## Suggested Reading

Useful Baeldung topics:

- Java Consumer
- Java Streams
- Java Collectors
- side effects in Streams
