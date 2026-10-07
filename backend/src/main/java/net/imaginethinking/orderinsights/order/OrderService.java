package net.imaginethinking.orderinsights.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import net.imaginethinking.orderinsights.common.exception.ResourceNotFoundException;
import net.imaginethinking.orderinsights.customer.CustomerTier;
import net.imaginethinking.orderinsights.customer.Region;
import net.imaginethinking.orderinsights.order.dtos.OrderSummaryDto;

@AllArgsConstructor
@Service
public class OrderService {
  private OrderRepository orderRepository;

  public OrderSummaryDto getById(UUID orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

    Function<Order, OrderSummaryDto> mapper = OrderSummaryDto::from;

    return mapper.apply(order);
  }

  public List<OrderSummaryDto> search(
      OrderStatus status,
      CustomerTier tier,
      Region region,
      BigDecimal minTotal,
      Integer placedWithinDays,
      SortType sort) {
    LocalDate todayDate = LocalDate.now();

    Predicate<Order> matchesStatus = order -> status != null ? order.getStatus() == status : true;
    Predicate<Order> matchesTier = order -> tier != null ? order.getCustomer().getTier() == tier : true;
    Predicate<Order> matchesRegion = order -> region != null ? order.getCustomer().getRegion() == region : true;
    Predicate<Order> hasMinTotal = order -> minTotal != null
        ? OrderCalculations.subtotal(order).compareTo(minTotal) >= 0
        : true;
    Predicate<Order> isPlacedWithinDays = placedWithinDays != null
        ? order -> {
          LocalDate pastDate = todayDate.minusDays(placedWithinDays);
          return (order.getPlacedOn().isEqual(pastDate) || order.getPlacedOn().isAfter(pastDate))
              && (order.getPlacedOn().isEqual(todayDate) || order.getPlacedOn().isBefore(todayDate));
        }
        : order -> true;

    Comparator<Order> compareByPlacedOn = Comparator.comparing(Order::getPlacedOn);
    Comparator<Order> compareByTotal = Comparator.comparing(OrderCalculations::subtotal);
    Comparator<Order> compareByCustomerName = Comparator.comparing(o -> o.getCustomer().getName());
    Comparator<Order> compareById = Comparator.comparing(Order::getId);

    Stream<Order> stream = orderRepository.findAll().stream()
        .filter(matchesStatus)
        .filter(matchesTier)
        .filter(matchesRegion)
        .filter(hasMinTotal)
        .filter(isPlacedWithinDays);

    switch (sort) {
      case DATE:
        stream = stream.sorted(compareByPlacedOn.reversed()
            .thenComparing(compareById));
        break;

      case TOTAL: {
        stream = stream.sorted(compareByTotal.reversed()
            .thenComparing(compareByPlacedOn.reversed())
            .thenComparing(compareById));
      }
        break;

      case CUSTOMER: {
        stream = stream.sorted(compareByCustomerName
            .thenComparing(compareByPlacedOn.reversed())
            .thenComparing(compareById));
      }
    }

    Function<Order, OrderSummaryDto> mapper = OrderSummaryDto::from;

    List<OrderSummaryDto> response = stream.map(mapper).toList();

    return response;
  }

  public boolean isAllocatable(Order order) {
    return order.getLines().stream()
        .noneMatch(OrderLine::isBackOrdered);
  }

  public Optional<Order> getMostRecentMatchingOrder(Predicate<Order> predicate) {
    List<Order> orders = this.orderRepository.findAll();

    return orders.stream()
        .filter(predicate)
        .sorted(Comparator
            .comparing(Order::getPlacedOn).reversed()
            .thenComparing(Order::getId))
        .findFirst();
  }

}
