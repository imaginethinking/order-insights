package net.imaginethinking.orderinsights.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.imaginethinking.orderinsights.customer.CustomerTier;
import net.imaginethinking.orderinsights.customer.Region;

public class OrderRules {

  private OrderRules() {}

  public static Predicate<Order> hasStatus(OrderStatus status) {
    return order -> status != null ? order.getStatus() == status : true;
  }

  public static Predicate<Order> hasCustomerTier(CustomerTier tier) {
    return order -> tier != null ? order.getCustomer().getTier() == tier : true;
  }

  public static Predicate<Order> isActive() {
    return hasStatus(OrderStatus.CANCELLED).negate();
  }

  public static Predicate<Order> minimumValue(BigDecimal threshold) {
    return order -> threshold != null
        ? OrderCalculations.subtotal(order).compareTo(threshold) >= 0
        : true;
  }

  public static Predicate<Order> hasRegion(Region region) {
    return order -> region != null ? order.getCustomer().getRegion() == region : true;
  }

  public static Predicate<Order> placedWithinDays(Integer days, Supplier<LocalDate> fromDateSupplier) {
    if (days == null || fromDateSupplier == null) {
      return order -> true;
    }

    return order -> {
      LocalDate fromDate = fromDateSupplier.get();
      LocalDate pastDate = fromDate.minusDays(days);

      return (order.getPlacedOn().isEqual(pastDate) || order.getPlacedOn().isAfter(pastDate))
          && (order.getPlacedOn().isEqual(fromDate) || order.getPlacedOn().isBefore(fromDate));
    };
  }

  public static Predicate<Order> isFullyAllocatable() {
    return order -> order.getLines().stream()
        .noneMatch(OrderLine::isBackOrdered);
  }

  public static Predicate<Order> isPreferredCustomer() {
    return hasCustomerTier(CustomerTier.SILVER).or(hasCustomerTier(CustomerTier.GOLD));
  }

  public static Predicate<Order> isHighValue() {
    return minimumValue(BigDecimal.valueOf(500));
  }

  public static Predicate<Order> isActiveAndPreferredCustomerAndHighValue() {
    return isActive()
        .and(isPreferredCustomer())
        .and(isHighValue());
  }
}
