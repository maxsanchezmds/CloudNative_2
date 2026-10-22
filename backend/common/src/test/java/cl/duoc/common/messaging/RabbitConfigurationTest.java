package cl.duoc.common.messaging;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RabbitConfigurationTest {
  @Test void topologyHasDurableQuorumQueuesAndExplicitDeadLetterRouting(){
    var config=new RabbitConfiguration();var p=new MessagingProperties();
    for(var q:java.util.List.of(config.notificationQueue(p),config.ticketQueue(p),config.auditQueue(p))){
      assertTrue(q.isDurable());assertEquals("quorum",q.getArguments().get("x-queue-type"));
      assertEquals(p.getDlx(),q.getArguments().get("x-dead-letter-exchange"));assertEquals(q.getName()+".dlq",q.getArguments().get("x-dead-letter-routing-key"));
    }
    assertEquals(6,config.deadLetters(p).getDeclarables().size());
    assertEquals("direct",config.ordersDirect(p).getType());assertEquals("topic",config.ordersTopic(p).getType());
    assertEquals("order.#",config.auditBinding(p).getRoutingKey());
  }
}
