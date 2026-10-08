package net.imaginethinking.orderinsights.order.dtos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;
import net.imaginethinking.orderinsights.order.Order;
import net.imaginethinking.orderinsights.order.OrderCalculations;
import net.imaginethinking.orderinsights.order.OrderLine;
import net.imaginethinking.orderinsights.order.OrderStatus;

public record OrderSummaryDto(
    UUID orderId,
    String customerName,
    OrderStatus status,
    LocalDate placedOn,
    int lineCount,
    BigDecimal totalValue,
    boolean containsBackOrder) {
  public static OrderSummaryDto from(Order order) {
    return new OrderSummaryDto(
        order.getId(),
        order.getCustomer().getName(),
        order.getStatus(),
        order.getPlacedOn(),
        order.getLines().size(),
        OrderCalculations.subtotal(order).setScale(2, RoundingMode.HALF_UP),
        order.getLines().stream()
            .anyMatch(OrderLine::isBackOrdered));
  }
}
