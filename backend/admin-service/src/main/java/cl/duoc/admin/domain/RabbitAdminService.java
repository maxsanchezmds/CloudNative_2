package cl.duoc.admin.domain;
import cl.duoc.admin.api.AdminRequests.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
@Service
public class RabbitAdminService {
  private final AmqpAdmin admin;private final RestClient management;
  public RabbitAdminService(org.springframework.amqp.rabbit.connection.ConnectionFactory cf,
      @Value("${rabbit.management-url:http://rabbit1:15672/api}") String url,
      @Value("${spring.rabbitmq.username}") String user,@Value("${spring.rabbitmq.password}") String pass){
    admin=new RabbitAdmin(cf);management=RestClient.builder().baseUrl(url).defaultHeaders(h->h.setBasicAuth(user,pass)).build();
  }
  public void createQueue(QueueRequest r){admin.declareQueue(new Queue(r.name(),r.durable(),false,false));}
  public boolean deleteQueue(String name){checkName(name);protect(name);return admin.deleteQueue(name);}
  public void createExchange(ExchangeRequest r){Exchange e=switch(r.type()){case "topic"->new TopicExchange(r.name(),true,false);case "fanout"->new FanoutExchange(r.name(),true,false);default->new DirectExchange(r.name(),true,false);};admin.declareExchange(e);}
  public boolean deleteExchange(String name){checkName(name);protect(name);return admin.deleteExchange(name);}
  public void createBinding(BindingRequest r){admin.declareBinding(binding(r));}
  public void deleteBinding(BindingRequest r){admin.removeBinding(binding(r));}
  private Binding binding(BindingRequest r){return new Binding(r.queue(),Binding.DestinationType.QUEUE,r.exchange(),r.routingKey(),null);}
  private void checkName(String name){if(name==null || !name.matches(cl.duoc.admin.api.AdminRequests.NAME))throw new IllegalArgumentException("Invalid resource name");}
  private void protect(String name){if(name.startsWith("ea2."))throw new IllegalArgumentException("System topology cannot be deleted through the demo API");}
  public Object queues(){return management.get().uri("/queues/{vhost}","/").retrieve().body(Object.class);}
  public Object exchanges(){return management.get().uri("/exchanges/{vhost}","/").retrieve().body(Object.class);}
  public Object nodes(){return management.get().uri("/nodes").retrieve().body(Object.class);}
}
