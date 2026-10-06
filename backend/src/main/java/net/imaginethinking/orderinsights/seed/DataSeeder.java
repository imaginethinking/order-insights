package net.imaginethinking.orderinsights.seed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import net.imaginethinking.orderinsights.customer.Customer;
import net.imaginethinking.orderinsights.customer.CustomerRepository;
import net.imaginethinking.orderinsights.customer.CustomerTier;
import net.imaginethinking.orderinsights.customer.Region;
import net.imaginethinking.orderinsights.order.Order;
import net.imaginethinking.orderinsights.order.OrderLine;
import net.imaginethinking.orderinsights.order.OrderRepository;
import net.imaginethinking.orderinsights.order.OrderStatus;
import net.imaginethinking.orderinsights.order.ProductCategory;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

  private static final LocalDate SAMPLE_DATE = LocalDate.of(2026, 10, 6);

  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (customerRepository.count() > 0 || orderRepository.count() > 0) {
      return;
    }

    // Customers: all three tiers and all four regions.
    Customer northgate = customer("Northgate Supplies", CustomerTier.GOLD, Region.NORTH);
    Customer southbank = customer("Southbank Trading", CustomerTier.SILVER, Region.SOUTH);
    Customer eastwood = customer("Eastwood Office", CustomerTier.STANDARD, Region.EAST);
    Customer westbrook = customer("Westbrook Wholesale", CustomerTier.GOLD, Region.WEST);
    customer("Riverside Retail", CustomerTier.STANDARD, Region.NORTH);

    // Order 1: GOLD, OFFICE, £500.00, 20 items.
    order(northgate, OrderStatus.CONFIRMED, 0,
        line("OFFICE-100", ProductCategory.OFFICE, 20, "25.00", false));

    // Order 2: GOLD, ELECTRONICS, £600.00, 2 items.
    order(northgate, OrderStatus.DISPATCHED, 2,
        line("ELEC-100", ProductCategory.ELECTRONICS, 2, "300.00", false));

    // Order 3: cancelled order, £1,200.00, 4 items; repeats ELEC-100.
    order(northgate, OrderStatus.CANCELLED, 12,
        line("ELEC-100", ProductCategory.ELECTRONICS, 4, "300.00", false));

    // Order 4: SILVER, HARDWARE and OFFICE, £225.00, 7 items; backordered office line.
    order(southbank, OrderStatus.PENDING, 1,
        line("HARD-100", ProductCategory.HARDWARE, 5, "40.00", false),
        line("OFFICE-200", ProductCategory.OFFICE, 2, "12.50", true));

    // Order 5: SILVER, OTHER, £200.00, 20 items.
    order(southbank, OrderStatus.CONFIRMED, 4,
        line("OTHER-100", ProductCategory.OTHER, 20, "10.00", false));

    // Order 6: STANDARD, ELECTRONICS, £499.99, 1 item.
    order(eastwood, OrderStatus.CONFIRMED, 0,
        line("ELEC-200", ProductCategory.ELECTRONICS, 1, "499.99", false));

    // Order 7: STANDARD, HARDWARE and OFFICE, £500.00, 20 items; repeats OFFICE-100.
    order(eastwood, OrderStatus.DISPATCHED, 7,
        line("HARD-200", ProductCategory.HARDWARE, 10, "25.00", false),
        line("OFFICE-100", ProductCategory.OFFICE, 10, "25.00", false));

    // Order 8: GOLD, OTHER, £750.00, 1 item.
    order(westbrook, OrderStatus.DISPATCHED, 30,
        line("OTHER-200", ProductCategory.OTHER, 1, "750.00", false));

    // Order 9: GOLD, OFFICE, £37.50, 3 items; backordered line; repeats OFFICE-200.
    order(westbrook, OrderStatus.PENDING, 3,
        line("OFFICE-200", ProductCategory.OFFICE, 3, "12.50", true));

    // Order 10: SILVER, OTHER, £190.00, 19 items; repeats OTHER-100.
    order(southbank, OrderStatus.CONFIRMED, 31,
        line("OTHER-100", ProductCategory.OTHER, 19, "10.00", false));
  }

  private Customer customer(String name, CustomerTier tier, Region region) {
    Customer customer = new Customer();
    customer.setName(name);
    customer.setTier(tier);
    customer.setRegion(region);
    return customerRepository.save(customer);
  }

  private OrderLine line(String sku, ProductCategory category, int quantity, String unitPrice,
      boolean backOrdered) {
    OrderLine line = new OrderLine();
    line.setSku(sku);
    line.setCategory(category);
    line.setQuantity(quantity);
    line.setUnitPrice(new BigDecimal(unitPrice));
    line.setBackOrdered(backOrdered);
    return line;
  }

  private void order(Customer customer, OrderStatus status, int daysAgo, OrderLine... lines) {
    Order order = new Order();
    order.setCustomer(customer);
    order.setStatus(status);
    order.setPlacedOn(SAMPLE_DATE.minusDays(daysAgo));
    Collections.addAll(order.getLines(), lines);
    orderRepository.save(order);
  }
}
