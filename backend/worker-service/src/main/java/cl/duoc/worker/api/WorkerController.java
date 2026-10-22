package cl.duoc.worker.api;
import cl.duoc.worker.domain.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/workers")
public class WorkerController {
  private final ReceiptRepository receipts;
  public WorkerController(ReceiptRepository r){receipts=r;}
  @GetMapping("/receipts") public List<ProcessingReceipt> receipts(){return receipts.findTop100ByOrderByProcessedAtDesc();}
}
