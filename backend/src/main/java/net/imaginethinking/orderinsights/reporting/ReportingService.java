package net.imaginethinking.orderinsights.reporting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import net.imaginethinking.orderinsights.common.exception.ResourceNotFoundException;
import net.imaginethinking.orderinsights.customer.Customer;
import net.imaginethinking.orderinsights.customer.CustomerRepository;
import net.imaginethinking.orderinsights.customer.CustomerTier;
import net.imaginethinking.orderinsights.order.Order;
import net.imaginethinking.orderinsights.order.OrderCalculations;
import net.imaginethinking.orderinsights.order.OrderLine;
import net.imaginethinking.orderinsights.order.OrderRepository;
import net.imaginethinking.orderinsights.order.OrderStatus;
import net.imaginethinking.orderinsights.order.ProductCategory;
import net.imaginethinking.orderinsights.reporting.dtos.CategorySalesDto;
import net.imaginethinking.orderinsights.reporting.dtos.CustomerSummaryDto;

@RequiredArgsConstructor
@Service
public class ReportingService {

  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;

  public CustomerSummaryDto getCustomerSummary(UUID customerId) {
    Customer customer = this.customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId.toString()));

    return getCustomerSummaryDto(customer, getQualifyingOrders());
  }

  private long getTotalOrders(List<Order> orders) {
    return orders.stream()
        .count();
  }

  private BigDecimal getTotalRevenue(List<Order> orders) {
    return orders.stream()
        .map(OrderCalculations::subtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private Map<CustomerTier, Long> getCountsByCustomerTier(List<Order> orders) {
    return orders.stream()
        .collect(Collectors.groupingBy(o -> o.getCustomer().getTier(), Collectors.counting()));
  }

  private Map<ProductCategory, Integer> getUnitsSoldByCategory(List<Order> orders) {
    return orders.stream()
        .flatMap(o -> o.getLines().stream())
        .collect(Collectors.groupingBy(l -> l.getCategory(), Collectors.summingInt(l -> l.getQuantity())));
  }

  private List<CategorySalesDto> getRevenueByCategoriesDto(List<Order> orders) {
    Map<ProductCategory, Integer> unitsSoldByCategory = getUnitsSoldByCategory(orders);

    Map<ProductCategory, BigDecimal> revenueByCategory = orders.stream()
        .flatMap(o -> o.getLines().stream())
        .collect(
            Collectors.groupingBy(OrderLine::getCategory,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    OrderCalculations::lineValue,
                    BigDecimal::add)));

    Function<ProductCategory, CategorySalesDto> mapper =
        c -> new CategorySalesDto(c, unitsSoldByCategory.get(c), revenueByCategory.get(c));

    return unitsSoldByCategory.keySet().stream()
        .sorted(Comparator.comparing(ProductCategory::name))
        .map(mapper)
        .toList();
  }

  private Map<UUID, BigDecimal> getTotalLookup(List<Order> orders) {
    return orders.stream()
        .collect(Collectors.toMap(Order::getId, OrderCalculations::subtotal));
  }

  private Map<Boolean, List<UUID>> partitionOrderIdsByValue(List<Order> orders) {
    Map<UUID, BigDecimal> totalLookup = getTotalLookup(orders);

    BigDecimal highValueAmount = BigDecimal.valueOf(500);

    Map<Boolean, List<Order>> unsorted = orders.stream()
        .collect(Collectors.partitioningBy(o -> totalLookup.get(o.getId()).compareTo(highValueAmount) >= 0));

    Comparator<Order> compareByPlacedOn = Comparator.comparing(Order::getPlacedOn);
    Comparator<Order> compareById = Comparator.comparing(Order::getId);

    List<UUID> highValueSorted = unsorted.get(true).stream()
        .sorted(compareByPlacedOn.reversed().thenComparing(compareById))
        .map(Order::getId)
        .toList();

    List<UUID> normalValueSorted = unsorted.get(false).stream()
        .sorted(compareByPlacedOn.reversed().thenComparing(compareById))
        .map(Order::getId)
        .toList();

    return Map.of(
        true, highValueSorted,
        false, normalValueSorted);
  }

  private String getDistinctSkuString(List<Order> orders) {
    return orders.stream()
        .flatMap(o -> o.getLines().stream())
        .map(OrderLine::getSku)
        .distinct()
        .sorted(Comparator.comparing(s -> s))
        .collect(Collectors.joining(", "));
  }

  private CustomerSummaryDto getCustomerSummaryDto(Customer customer, List<Order> orders) {
    List<Order> customerOrders = orders.stream()
        .filter(o -> o.getCustomer().getId().equals(customer.getId()))
        .toList();

    long orderCount = getTotalOrders(customerOrders);
    BigDecimal totalSpend = getTotalRevenue(customerOrders);
    BigDecimal averageOrderValue = orderCount != 0L
        ? totalSpend.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP)
        : BigDecimal.valueOf(0.00);

    return CustomerSummaryDto.of(customer.getId(), customer.getName(), orderCount, totalSpend, averageOrderValue);
  }

  private Optional<CustomerSummaryDto> getTopCustomerSummaryDto(List<Order> orders) {
    Map<Customer, BigDecimal> customerRevenue = orders.stream()
        .collect(Collectors.groupingBy(
            Order::getCustomer,
            Collectors.reducing(
                BigDecimal.ZERO,
                OrderCalculations::subtotal,
                BigDecimal::add)));

    Comparator<Customer> compareTotalSpend = Comparator.comparing(customerRevenue::get);
    Comparator<Customer> compareName = Comparator.comparing(Customer::getName);
    Comparator<Customer> compareId = Comparator.comparing(Customer::getId);

    return customerRevenue.keySet().stream()
        .sorted(compareTotalSpend.reversed()
            .thenComparing(compareName)
            .thenComparing(compareId))
        .findFirst()
        .map(c -> getCustomerSummaryDto(c, orders));
  }

  private List<Order> getQualifyingOrders() {
    return this.orderRepository.findAll()
        .stream()
        .filter(isNotCancelled())
        .toList();
  }

  private Predicate<Order> isNotCancelled() {
    return o -> o.getStatus() != OrderStatus.CANCELLED;
  }

}
