package cl.duoc.worker.messaging;
import cl.duoc.common.messaging.*;
import cl.duoc.worker.domain.ProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Component;
import org.slf4j.*;
@Component
public class MessageHandler {
  private static final Logger log=LoggerFactory.getLogger(MessageHandler.class);
  private final ProcessingService service;private final ConfirmedPublisher publisher;private final ObjectMapper json;
  public MessageHandler(ProcessingService s,ConfirmedPublisher p,ObjectMapper j){service=s;publisher=p;json=j;}
  public void handle(String domain,Message message,Channel channel) throws java.io.IOException {
    long tag=message.getMessageProperties().getDeliveryTag();
    Object header=message.getMessageProperties().getHeaders().getOrDefault("x-attempt",0);
    int attempt=header instanceof Number ? ((Number)header).intValue() : 0;
    try{
      OrderEvent event=json.readValue(message.getBody(),OrderEvent.class);
      boolean created=service.process(domain,event,attempt);
      channel.basicAck(tag,false);
      log.info("ACK domain={} event={} order={} attempt={} result={}",domain,event.eventId(),event.orderId(),attempt,created?"processed":"duplicate");
    }catch(ProcessingService.TransientFailure e){
      if(attempt>=2){channel.basicNack(tag,false,false);log.warn("DLQ_RETRIES_EXHAUSTED domain={} message={}",domain,message.getMessageProperties().getMessageId());return;}
      try{
        message.getMessageProperties().setHeader("x-attempt",attempt+1);
        publisher.send(message.getMessageProperties().getReceivedExchange(),message.getMessageProperties().getReceivedRoutingKey(),message);
        channel.basicAck(tag,false);log.info("RETRY_CONFIRMED domain={} attempt={}",domain,attempt+1);
      }catch(Exception publishFailure){channel.basicNack(tag,false,true);log.warn("NACK_REQUEUE domain={} reason={}",domain,publishFailure.getMessage());}
    }catch(IllegalArgumentException | com.fasterxml.jackson.core.JsonProcessingException e){
      channel.basicNack(tag,false,false);log.warn("NACK_DLQ domain={} reason={}",domain,e.getMessage());
    }catch(Exception e){
      // Unexpected infrastructure errors must not be acknowledged as success.
      channel.basicNack(tag,false,true);log.error("NACK_INFRASTRUCTURE domain={} reason={}",domain,e.getMessage());
    }
  }
}
