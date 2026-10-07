package net.imaginethinking.orderinsights.order;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
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

}
