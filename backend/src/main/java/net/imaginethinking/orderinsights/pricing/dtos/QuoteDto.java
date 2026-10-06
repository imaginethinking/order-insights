package net.imaginethinking.orderinsights.pricing.dtos;

import java.math.BigDecimal;
import java.util.UUID;
import net.imaginethinking.orderinsights.pricing.DiscountPolicyType;

public record QuoteDto(UUID orderId, BigDecimal subtotal, BigDecimal discountAmount,
    BigDecimal finalTotal, DiscountPolicyType policy) {

}
