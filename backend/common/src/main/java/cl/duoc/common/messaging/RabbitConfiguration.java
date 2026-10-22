package cl.duoc.common.messaging;
import java.util.List;
import java.util.ArrayList;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
@Configuration
@ConditionalOnProperty(name="messaging.enabled",havingValue="true",matchIfMissing=true)
public class RabbitConfiguration {
  @Bean Jackson2JsonMessageConverter jsonConverter(){return new Jackson2JsonMessageConverter();}
  @Bean RabbitTemplate confirmedTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter converter){
    RabbitTemplate t=new RabbitTemplate(cf); t.setMessageConverter(converter); t.setMandatory(true); return t;
  }
  @Bean DirectExchange ordersDirect(MessagingProperties p){return new DirectExchange(p.getDirect(),true,false);}
  @Bean TopicExchange ordersTopic(MessagingProperties p){return new TopicExchange(p.getTopic(),true,false);}
  @Bean DirectExchange deadLetterExchange(MessagingProperties p){return new DirectExchange(p.getDlx(),true,false);}
  private Queue queue(String name,String dlx){return QueueBuilder.durable(name).withArgument("x-queue-type","quorum").deadLetterExchange(dlx).deadLetterRoutingKey(name+".dlq").build();}
  @Bean Queue notificationQueue(MessagingProperties p){return queue(p.getNotification(),p.getDlx());}
  @Bean Queue ticketQueue(MessagingProperties p){return queue(p.getTicket(),p.getDlx());}
  @Bean Queue auditQueue(MessagingProperties p){return queue(p.getAudit(),p.getDlx());}
  @Bean Declarables deadLetters(MessagingProperties p){
    List<Declarable> all=new ArrayList<>();
    for(String name:List.of(p.getNotification(),p.getTicket(),p.getAudit())){
      Queue q=QueueBuilder.durable(name+".dlq").withArgument("x-queue-type","quorum").build();
      all.add(q); all.add(new Binding(q.getName(),Binding.DestinationType.QUEUE,p.getDlx(),q.getName(),null));
    } return new Declarables(all);
  }
  @Bean Binding notificationBinding(MessagingProperties p){return new Binding(p.getNotification(),Binding.DestinationType.QUEUE,p.getDirect(),p.getNotificationKey(),null);}
  @Bean Binding ticketBinding(MessagingProperties p){return new Binding(p.getTicket(),Binding.DestinationType.QUEUE,p.getDirect(),p.getTicketKey(),null);}
  @Bean Binding auditBinding(MessagingProperties p){return new Binding(p.getAudit(),Binding.DestinationType.QUEUE,p.getTopic(),"order.#",null);}
}
