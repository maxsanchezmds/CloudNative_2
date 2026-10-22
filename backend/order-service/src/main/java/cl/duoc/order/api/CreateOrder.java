package cl.duoc.order.api;
import jakarta.validation.constraints.*;
public record CreateOrder(@NotBlank @Size(max=100) String product, @Min(1) @Max(100) int quantity,
    @NotBlank @Email @Size(max=200) String email, @NotBlank @Pattern(regexp="NORMAL|TRANSIENT|PERMANENT") String scenario){}
