package cl.duoc.worker.domain;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="processing_receipts")
public class ProcessingReceipt {
  @Id public String id;
  public String eventId;
  public String orderId;
  public String domain;
  public String result;
  public int attempt;
  public Instant processedAt;
  public ProcessingReceipt(){}
}
