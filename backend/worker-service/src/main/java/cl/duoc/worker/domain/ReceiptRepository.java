package cl.duoc.worker.domain;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReceiptRepository extends JpaRepository<ProcessingReceipt,String>{List<ProcessingReceipt> findTop100ByOrderByProcessedAtDesc();}
