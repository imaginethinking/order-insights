package net.imaginethinking.orderinsights.order;

import java.util.UUID;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import net.imaginethinking.orderinsights.common.exception.ResourceNotFoundException;
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

}
