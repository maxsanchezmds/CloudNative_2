package cl.duoc.order.domain;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OutboxRepository extends JpaRepository<OutboxRecord,String>{List<OutboxRecord> findTop30BySentFalseOrderByCreatedAtAsc(); long countBySentFalse();}
