package cl.duoc.worker.messaging;
import cl.duoc.common.messaging.*;
import cl.duoc.worker.domain.ProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.*;
import org.springframework.amqp.core.*;
import static org.mockito.Mockito.*;
class MessageHandlerTest {
  ProcessingService service; ConfirmedPublisher publisher; Channel channel;MessageHandler handler;
  Message message;
  @BeforeEach void setup() throws Exception {
    service=mock(ProcessingService.class);publisher=mock(ConfirmedPublisher.class);channel=mock(Channel.class);
    ObjectMapper json=new ObjectMapper();handler=new MessageHandler(service,publisher,json);
    MessageProperties props=new MessageProperties();props.setDeliveryTag(7);props.setReceivedExchange("exchange");props.setReceivedRoutingKey("route");props.setMessageId("event-1");
    message=new Message(json.writeValueAsBytes(new OrderEvent("event-1","order-123","Item",1,"a@example.test","NORMAL","2026-10-19")),props);
  }
  @Test void acknowledgesAfterSuccessfulPersistence() throws Exception {
    when(service.process(anyString(),any(),anyInt())).thenReturn(true);
    handler.handle("tickets",message,channel);
    var order=inOrder(service,channel);order.verify(service).process(eq("tickets"),any(),eq(0));order.verify(channel).basicAck(7,false);
  }
  @Test void rejectsPermanentFailureIntoDeadLetterRoute() throws Exception {
    when(service.process(anyString(),any(),anyInt())).thenThrow(new IllegalArgumentException("bad"));
    handler.handle("tickets",message,channel);verify(channel).basicNack(7,false,false);verify(channel,never()).basicAck(anyLong(),anyBoolean());
  }
  @Test void acknowledgesOriginalOnlyAfterConfirmedRetry() throws Exception {
    when(service.process(anyString(),any(),anyInt())).thenThrow(new ProcessingService.TransientFailure());
    handler.handle("tickets",message,channel);
    var order=inOrder(publisher,channel);order.verify(publisher).send("exchange","route",message);order.verify(channel).basicAck(7,false);
    Assertions.assertEquals(1,((Number)message.getMessageProperties().getHeader("x-attempt")).intValue());
  }
  @Test void failedRetryPublicationRequeuesOriginalWithoutAck() throws Exception {
    when(service.process(anyString(),any(),anyInt())).thenThrow(new ProcessingService.TransientFailure());
    doThrow(new IllegalStateException("unavailable")).when(publisher).send(anyString(),anyString(),any());
    handler.handle("tickets",message,channel);verify(channel).basicNack(7,false,true);verify(channel,never()).basicAck(anyLong(),anyBoolean());
  }
  @Test void malformedJsonGoesToDlq() throws Exception {
    handler.handle("audit",new Message("{bad".getBytes(),message.getMessageProperties()),channel);
    verify(channel).basicNack(7,false,false);verifyNoInteractions(service);
  }
  @Test void exhaustedRetriesGoToDlq() throws Exception {
    message.getMessageProperties().setHeader("x-attempt",2);
    when(service.process(anyString(),any(),anyInt())).thenThrow(new ProcessingService.TransientFailure());
    handler.handle("audit",message,channel);verify(channel).basicNack(7,false,false);verifyNoInteractions(publisher);
  }
}
