package cl.duoc.admin.api;
import cl.duoc.admin.api.AdminRequests.*;
import cl.duoc.admin.domain.RabbitAdminService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
  private final RabbitAdminService service;
  public AdminController(RabbitAdminService s){service=s;}
  @GetMapping("/queues")public Object queues(){return service.queues();}
  @GetMapping("/exchanges")public Object exchanges(){return service.exchanges();}
  @GetMapping("/nodes")public Object nodes(){return service.nodes();}
  @PostMapping("/queues")@ResponseStatus(HttpStatus.CREATED)public Map<String,String> queue(@Valid @RequestBody QueueRequest r){service.createQueue(r);return Map.of("name",r.name());}
  @DeleteMapping("/queues/{name}")public Map<String,Boolean> deleteQueue(@PathVariable String name){return Map.of("deleted",service.deleteQueue(name));}
  @PostMapping("/exchanges")@ResponseStatus(HttpStatus.CREATED)public Map<String,String> exchange(@Valid @RequestBody ExchangeRequest r){service.createExchange(r);return Map.of("name",r.name());}
  @DeleteMapping("/exchanges/{name}")public Map<String,Boolean> deleteExchange(@PathVariable String name){return Map.of("deleted",service.deleteExchange(name));}
  @PostMapping("/bindings")@ResponseStatus(HttpStatus.CREATED)public Map<String,String> bind(@Valid @RequestBody BindingRequest r){service.createBinding(r);return Map.of("result","created");}
  @DeleteMapping("/bindings")public Map<String,String> unbind(@Valid @RequestBody BindingRequest r){service.deleteBinding(r);return Map.of("result","deleted");}
}
