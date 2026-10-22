package cl.duoc.admin.api;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class AdminValidationTest {
  private final jakarta.validation.Validator validator=Validation.buildDefaultValidatorFactory().getValidator();
  @Test void blankQueueNameIsRejected(){assertFalse(validator.validate(new AdminRequests.QueueRequest("",true)).isEmpty());}
  @Test void invalidCharactersAreRejected(){assertFalse(validator.validate(new AdminRequests.QueueRequest("../queue",true)).isEmpty());}
  @Test void unknownExchangeTypeIsRejected(){assertFalse(validator.validate(new AdminRequests.ExchangeRequest("demo.x","invalid")).isEmpty());}
  @Test void correctBindingIsAccepted(){assertTrue(validator.validate(new AdminRequests.BindingRequest("demo.q","demo.x","order.#")).isEmpty());}
}
