package net.imaginethinking.orderinsights.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import net.imaginethinking.orderinsights.customer.CustomerTier;
import net.imaginethinking.orderinsights.customer.Region;
import net.imaginethinking.orderinsights.order.dtos.OrderSummaryDto;



@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderSummaryDto> getOrder(@PathVariable UUID orderId) {
    OrderSummaryDto response = orderService.getById(orderId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  public ResponseEntity<List<OrderSummaryDto>> searchOrders(
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) CustomerTier tier,
      @RequestParam(required = false) Region region,
      @RequestParam(required = false) @Min(0) BigDecimal minTotal,
      @RequestParam(required = false) @Min(0) Integer placedWithinDays,
      @RequestParam(defaultValue = "DATE") SortType sort) {
    List<OrderSummaryDto> response =
        orderService.search(status, tier, region, minTotal, placedWithinDays, sort);

    return ResponseEntity.ok(response);
  }


}
