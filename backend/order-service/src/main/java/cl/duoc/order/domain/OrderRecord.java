package cl.duoc.order.domain;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="customer_orders")
public class OrderRecord {
  @Id public String id;
  public String product;
  public int quantity;
  public String email;
  public String scenario;
  public Instant createdAt;
  public OrderRecord(){}
}
