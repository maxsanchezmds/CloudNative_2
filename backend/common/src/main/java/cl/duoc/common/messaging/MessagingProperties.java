package cl.duoc.common.messaging;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration("messagingProperties")
@ConfigurationProperties(prefix="messaging")
public class MessagingProperties {
  private String direct="ea2.orders.direct", topic="ea2.orders.topic", dlx="ea2.orders.dlx";
  private String notification="ea2.notifications", ticket="ea2.tickets", audit="ea2.audit";
  private String notificationKey="notification", ticketKey="ticket", auditKey="order.created";
  public String getDirect(){return direct;} public void setDirect(String v){direct=v;}
  public String getTopic(){return topic;} public void setTopic(String v){topic=v;}
  public String getDlx(){return dlx;} public void setDlx(String v){dlx=v;}
  public String getNotification(){return notification;} public void setNotification(String v){notification=v;}
  public String getTicket(){return ticket;} public void setTicket(String v){ticket=v;}
  public String getAudit(){return audit;} public void setAudit(String v){audit=v;}
  public String getNotificationKey(){return notificationKey;} public void setNotificationKey(String v){notificationKey=v;}
  public String getTicketKey(){return ticketKey;} public void setTicketKey(String v){ticketKey=v;}
  public String getAuditKey(){return auditKey;} public void setAuditKey(String v){auditKey=v;}
}
