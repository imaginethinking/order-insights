package net.imaginethinking.orderinsights.reporting.dtos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record CustomerSummaryDto(
    UUID customerId,
    String customerName,
    long orderCount,
    BigDecimal totalSpend,
    BigDecimal averageOrderValue) {
  public static CustomerSummaryDto of(
      UUID customerId,
      String customerName,
      long orderCount,
      BigDecimal totalSpend,
      BigDecimal averageOrder) {
    return new CustomerSummaryDto(
        customerId,
        customerName,
        orderCount,
        totalSpend.setScale(2, RoundingMode.HALF_UP),
        averageOrder.setScale(2, RoundingMode.HALF_UP));
  }
}
