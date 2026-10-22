package cl.duoc.gateway;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import java.util.Map;
@RestController
public class ProxyController {
  private final RestClient client=RestClient.create();private final String orders,admin,workers;
  public ProxyController(@Value("${gateway.orders-url}")String o,@Value("${gateway.admin-url}")String a,@Value("${gateway.workers-url}")String w){orders=o;admin=a;workers=w;}
  @RequestMapping(value={"/api/orders","/api/orders/**","/api/admin/**","/api/workers/**"},method={RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE})
  public ResponseEntity<byte[]> proxy(HttpServletRequest request,@RequestBody(required=false)byte[] body){
    String path=request.getRequestURI();String target=path.startsWith("/api/orders")?orders:path.startsWith("/api/admin")?admin:workers;
    String query=request.getQueryString()==null?"":"?"+request.getQueryString();
    return client.method(HttpMethod.valueOf(request.getMethod())).uri(target+path+query)
      .header(HttpHeaders.AUTHORIZATION,request.getHeader(HttpHeaders.AUTHORIZATION))
      .contentType(MediaType.APPLICATION_JSON).body(body==null?new byte[0]:body)
      .exchange((req,res)->ResponseEntity.status(res.getStatusCode()).headers(h->{if(res.getHeaders().getContentType()!=null)h.setContentType(res.getHeaders().getContentType());}).body(res.getBody().readAllBytes()));
  }
  @GetMapping("/api/session")public Map<String,Object> session(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt principal){return Map.of("authenticated",true,"subject",principal.getSubject());}
}
