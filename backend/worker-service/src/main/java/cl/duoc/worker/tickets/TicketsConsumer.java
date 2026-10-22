package cl.duoc.worker.tickets;
import cl.duoc.worker.messaging.MessageHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
@Component
public class TicketsConsumer {
  private final MessageHandler handler;
  public TicketsConsumer(MessageHandler h){handler=h;}
  @RabbitListener(queues="#{@messagingProperties.getTicket()}")
  public void consume(Message message,Channel channel) throws java.io.IOException {handler.handle("tickets",message,channel);}
}
