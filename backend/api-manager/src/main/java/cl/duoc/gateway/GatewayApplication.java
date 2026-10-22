package cl.duoc.gateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages="cl.duoc")
public class GatewayApplication { public static void main(String[] args) { SpringApplication.run(GatewayApplication.class, args); } }
