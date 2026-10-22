package cl.duoc.order.messaging;
import cl.duoc.order.domain.*;
import cl.duoc.common.messaging.ConfirmedPublisher;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;
import org.slf4j.*;
@Configuration @EnableScheduling
public class OutboxDispatcher {
  private static final Logger log=LoggerFactory.getLogger(OutboxDispatcher.class);
  private final OutboxRepository outbox;private final ConfirmedPublisher publisher;
  public OutboxDispatcher(OutboxRepository o,ConfirmedPublisher p){outbox=o;publisher=p;}
  @Scheduled(fixedDelay=1500)
  public synchronized void dispatch(){
    for(OutboxRecord item:outbox.findTop30BySentFalseOrderByCreatedAtAsc()){
      try{
        MessageProperties props=new MessageProperties();props.setContentType("application/json");props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);props.setMessageId(item.id);
        publisher.send(item.exchangeName,item.routingKey,new Message(item.payload.getBytes(java.nio.charset.StandardCharsets.UTF_8),props));item.sent=true;
        log.info("PUBLISH_CONFIRMED event={} order={} exchange={} key={}",item.id,item.orderId,item.exchangeName,item.routingKey);
      }catch(Exception e){log.warn("OUTBOX_RETRY event={} reason={}",item.id,e.getMessage());}
      item.attempts++;outbox.save(item);
    }
  }
}
