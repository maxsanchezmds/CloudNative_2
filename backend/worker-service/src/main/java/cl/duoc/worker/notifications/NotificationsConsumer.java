package cl.duoc.worker.notifications;
import cl.duoc.worker.messaging.MessageHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
@Component
public class NotificationsConsumer {
  private final MessageHandler handler;
  public NotificationsConsumer(MessageHandler h){handler=h;}
  @RabbitListener(queues="#{@messagingProperties.getNotification()}")
  public void consume(Message message,Channel channel) throws java.io.IOException {handler.handle("notifications",message,channel);}
}
