package cl.duoc.order.domain;
import cl.duoc.order.api.CreateOrder;
import cl.duoc.common.messaging.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class OrderService {
  private final OrderRepository orders; private final OutboxRepository outbox; private final MessagingProperties props; private final ObjectMapper json;
  public OrderService(OrderRepository o, OutboxRepository b, MessagingProperties p, ObjectMapper j){orders=o;outbox=b;props=p;json=j;}
  @Transactional
  public OrderRecord create(CreateOrder input) throws Exception {
    OrderRecord order=new OrderRecord();order.id=UUID.randomUUID().toString();order.product=input.product();order.quantity=input.quantity();order.email=input.email();order.scenario=input.scenario();order.createdAt=Instant.now();orders.save(order);
    for(String[] route:new String[][]{{props.getDirect(),props.getNotificationKey()},{props.getDirect(),props.getTicketKey()},{props.getTopic(),props.getAuditKey()}}){
      OutboxRecord item=new OutboxRecord();item.id=UUID.randomUUID().toString();item.orderId=order.id;item.exchangeName=route[0];item.routingKey=route[1];item.createdAt=order.createdAt;
      item.payload=json.writeValueAsString(new OrderEvent(item.id,order.id,order.product,order.quantity,order.email,order.scenario,order.createdAt.toString()));outbox.save(item);
    } return order;
  }
}
