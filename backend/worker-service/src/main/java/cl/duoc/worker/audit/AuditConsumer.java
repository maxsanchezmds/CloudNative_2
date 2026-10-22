package cl.duoc.worker.audit;
import cl.duoc.worker.messaging.MessageHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
@Component
public class AuditConsumer {
  private final MessageHandler handler;
  public AuditConsumer(MessageHandler h){handler=h;}
  @RabbitListener(queues="#{@messagingProperties.getAudit()}")
  public void consume(Message message,Channel channel) throws java.io.IOException {handler.handle("audit",message,channel);}
}
