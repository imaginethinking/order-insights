package net.imaginethinking.orderinsights.order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.imaginethinking.orderinsights.customer.Customer;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Column(name = "placed_on")
  private LocalDate placedOn;

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "order_id")
  private List<OrderLine> lines = new ArrayList<>();
}
