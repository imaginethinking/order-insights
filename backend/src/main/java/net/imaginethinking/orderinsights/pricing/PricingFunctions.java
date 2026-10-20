package net.imaginethinking.orderinsights.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.imaginethinking.orderinsights.order.Order;
import net.imaginethinking.orderinsights.order.OrderCalculations;

public class PricingFunctions {

  private PricingFunctions() {}

  public static Function<Order, BigDecimal> subtotal() {
    return OrderCalculations::subtotal;
  }

  public static UnaryOperator<BigDecimal> roundMoney() {
    return num -> num.setScale(2, RoundingMode.HALF_UP);
  }

  public static BiFunction<BigDecimal, BigDecimal, BigDecimal> percentageDiscountAmount() {
    return BigDecimal::multiply;
  }

  public static Function<Order, BigDecimal> roundedSubtotalWithAndThen() {
    return subtotal().andThen(roundMoney());
  }

  public static Function<Order, BigDecimal> roundedSubtotalWithCompose() {
    return roundMoney().compose(subtotal());
  }

}
