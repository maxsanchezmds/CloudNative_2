package cl.duoc.admin.api;
import jakarta.validation.constraints.*;
public class AdminRequests {
  public static final String NAME="[a-zA-Z0-9][a-zA-Z0-9._-]{0,99}";
  public record QueueRequest(@NotBlank @Pattern(regexp=NAME) String name, boolean durable){}
  public record ExchangeRequest(@NotBlank @Pattern(regexp=NAME) String name,@NotBlank @Pattern(regexp="direct|topic|fanout") String type){}
  public record BindingRequest(@NotBlank @Pattern(regexp=NAME) String queue,@NotBlank @Pattern(regexp=NAME) String exchange,@NotNull @Size(max=100) String routingKey){}
}
