package net.imaginethinking.orderinsights.order;

import java.math.BigDecimal;

public class OrderCalculations {

  private OrderCalculations() {}

  public static BigDecimal lineValue(OrderLine orderLine) {
    return orderLine.getUnitPrice().multiply(BigDecimal.valueOf(orderLine.getQuantity()));
  }

  public static BigDecimal subtotal(Order order) {
    return order.getLines().stream()
        .map(line -> lineValue(line))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
