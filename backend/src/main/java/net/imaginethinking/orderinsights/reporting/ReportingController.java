package net.imaginethinking.orderinsights.reporting;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import net.imaginethinking.orderinsights.reporting.dtos.CustomerSummaryDto;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class ReportingController {
  private final ReportingService reportingService;

  @GetMapping("/customers/{customerId}")
  public ResponseEntity<CustomerSummaryDto> getCustomerSummary(@PathVariable UUID customerId) {
    CustomerSummaryDto response = reportingService.getCustomerSummary(customerId);

    return ResponseEntity.ok(response);
  }

}
