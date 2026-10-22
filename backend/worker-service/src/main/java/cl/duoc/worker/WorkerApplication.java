package cl.duoc.worker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages="cl.duoc")
public class WorkerApplication { public static void main(String[] args) { SpringApplication.run(WorkerApplication.class, args); } }
