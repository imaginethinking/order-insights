package net.imaginethinking.orderinsights.reporting.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.imaginethinking.orderinsights.customer.CustomerTier;

public record SalesReportDto(long totalOrder, BigDecimal totalRevenue,
    Map<CustomerTier, Long> ordersByTier, List<CategorySalesDto> salesByCategory,
    List<UUID> highValueOrderIds, List<UUID> normalValueOrderIds, String distinctSkuSummary,
    CustomerSummaryDto topCustomer) {

}
