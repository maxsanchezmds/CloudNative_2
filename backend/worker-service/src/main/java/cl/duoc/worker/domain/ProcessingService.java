package cl.duoc.worker.domain;
import cl.duoc.common.messaging.OrderEvent;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ProcessingService {
  private final ReceiptRepository receipts;
  public ProcessingService(ReceiptRepository r){receipts=r;}
  @Transactional
  public boolean process(String domain,OrderEvent event,int attempt){
    String id=domain+":"+event.eventId();
    if(receipts.existsById(id))return false;
    if(event.orderId()==null || event.quantity()<1 || "PERMANENT".equals(event.scenario()))throw new IllegalArgumentException("Permanent validation failure");
    if("TRANSIENT".equals(event.scenario()) && attempt==0)throw new TransientFailure();
    ProcessingReceipt item=new ProcessingReceipt();item.id=id;item.eventId=event.eventId();item.orderId=event.orderId();item.domain=domain;item.attempt=attempt;item.processedAt=Instant.now();
    item.result=switch(domain){case "notifications"->"Notificación registrada para "+event.email();case "tickets"->"Ticket T-"+event.orderId().substring(0,8);default->"Auditoría order.created persistida";};
    receipts.saveAndFlush(item);return true;
  }
  public static class TransientFailure extends RuntimeException {public TransientFailure(){super("Recoverable dependency failure");}}
}
