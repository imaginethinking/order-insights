package net.imaginethinking.orderinsights.reporting.dtos;

import java.math.BigDecimal;
import net.imaginethinking.orderinsights.order.ProductCategory;

public record CategorySalesDto(ProductCategory category, int unitsSold, BigDecimal revenue) {

}
