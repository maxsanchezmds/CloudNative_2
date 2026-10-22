package cl.duoc.gateway;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@Component @Order(1)
public class OriginFilter extends OncePerRequestFilter {
  private final String secret;
  public OriginFilter(@Value("${ORIGIN_KEY:}") String value){secret=value;}
  @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
    if(!secret.isBlank() && !"/actuator/health".equals(req.getRequestURI())){
      String provided=req.getHeader("X-Origin-Key");
      if(provided==null||!MessageDigest.isEqual(secret.getBytes(StandardCharsets.UTF_8),provided.getBytes(StandardCharsets.UTF_8))){res.sendError(403);return;}
    } chain.doFilter(req,res);
  }
}
