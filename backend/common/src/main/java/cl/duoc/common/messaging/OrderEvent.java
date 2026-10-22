package cl.duoc.common.messaging;
public record OrderEvent(String eventId,String orderId,String product,int quantity,String email,String scenario,String createdAt) {}
