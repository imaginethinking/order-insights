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
import lombok.RequiredArgsConstructor;
import net.imaginethinking.orderinsights.common.exception.ResourceNotFoundException;
import net.imaginethinking.orderinsights.customer.CustomerTier;
import net.imaginethinking.orderinsights.customer.Region;
import net.imaginethinking.orderinsights.order.dtos.OrderSummaryDto;

@RequiredArgsConstructor
@Service
public class OrderService {
  private final OrderRepository orderRepository;

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

    Comparator<Order> compareByPlacedOn = Comparator.comparing(Order::getPlacedOn);
    Comparator<Order> compareByTotal = Comparator.comparing(OrderCalculations::subtotal);
    Comparator<Order> compareByCustomerName = Comparator.comparing(o -> o.getCustomer().getName());
    Comparator<Order> compareById = Comparator.comparing(Order::getId);

    Stream<Order> stream = orderRepository.findAll().stream()
        .filter(OrderRules.hasStatus(status))
        .filter(OrderRules.hasCustomerTier(tier))
        .filter(OrderRules.hasRegion(region))
        .filter(OrderRules.minimumValue(minTotal))
        .filter(OrderRules.placedWithinDays(placedWithinDays, LocalDate::now));

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

  public Optional<Order> getMostRecentMatchingOrder(Predicate<Order> predicate) {
    List<Order> orders = this.orderRepository.findAll();

    return orders.stream()
        .filter(predicate)
        .sorted(Comparator
            .comparing(Order::getPlacedOn).reversed()
            .thenComparing(Order::getId))
        .findFirst();
  }

  public boolean isAllocatable(Order order) {
    return OrderRules.isFullyAllocatable().test(order);
  }

}
