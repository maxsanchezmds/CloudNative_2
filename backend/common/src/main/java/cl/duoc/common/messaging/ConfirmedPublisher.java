package cl.duoc.common.messaging;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
@Component
@ConditionalOnProperty(name="messaging.enabled",havingValue="true",matchIfMissing=true)
public class ConfirmedPublisher {
  private final RabbitTemplate template;
  public ConfirmedPublisher(RabbitTemplate template){this.template=template;}
  public void send(String exchange,String key,Message message) throws Exception {
    CorrelationData cd=new CorrelationData(UUID.randomUUID().toString());
    template.send(exchange,key,message,cd);
    var confirm=cd.getFuture().get(8,TimeUnit.SECONDS);
    if(!confirm.isAck() || cd.getReturned()!=null) throw new IllegalStateException("Broker rejected or returned publication");
  }
}
