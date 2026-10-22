package cl.duoc.order.api;
import cl.duoc.order.domain.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/orders")
public class OrderController {
  private final OrderService service;private final OrderRepository orders;private final OutboxRepository outbox;
  public OrderController(OrderService s,OrderRepository r,OutboxRepository b){service=s;orders=r;outbox=b;}
  @GetMapping public List<OrderRecord> list(){return orders.findTop50ByOrderByCreatedAtDesc();}
  @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderRecord create(@Valid @RequestBody CreateOrder input) throws Exception {return service.create(input);}
  @GetMapping("/status") public Map<String,Object> status(){return Map.of("orders",orders.count(),"pendingPublications",outbox.countBySentFalse(),"database","PostgreSQL");}
}
