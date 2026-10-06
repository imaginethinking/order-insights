package net.imaginethinking.orderinsights.order.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import net.imaginethinking.orderinsights.order.OrderStatus;

public record OrderSummaryDto(UUID orderId, String customerName, OrderStatus status,
    LocalDate placedOn, int lineCount, BigDecimal totalValue, boolean containsBackOrder) {

}
