package cl.duoc.order.domain;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="outbox")
public class OutboxRecord {
  @Id public String id;
  public String orderId;
  public String exchangeName;
  public String routingKey;
  @Column(length=4000) public String payload;
  public boolean sent;
  public int attempts;
  public Instant createdAt;
  public OutboxRecord(){}
}
