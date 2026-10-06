package net.imaginethinking.orderinsights.order;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_lines")
public class OrderLine {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String sku;

  @Enumerated(EnumType.STRING)
  private ProductCategory category;

  private int quantity;

  @Column(name = "unit_price")
  private BigDecimal unitPrice;

  @Column(name = "back_ordered")
  private boolean backOrdered;
}
