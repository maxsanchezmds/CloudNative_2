package cl.duoc.order.domain;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<OrderRecord,String>{List<OrderRecord> findTop50ByOrderByCreatedAtDesc();}
