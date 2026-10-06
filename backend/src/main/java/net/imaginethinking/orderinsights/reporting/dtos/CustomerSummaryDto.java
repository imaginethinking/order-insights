package net.imaginethinking.orderinsights.reporting.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomerSummaryDto(UUID customerId, String customerName, long orderCount,
    BigDecimal totalSpend, BigDecimal averageOrderValue) {

}
