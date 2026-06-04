package mx.com.ventiflow.domain.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DatosRegistroCliente(
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\d{10}", message = "El teléfono debe contener exactamente 10 dígitos numéricos")
        String telefono,

        @NotBlank(message = "El nombre o alias es obligatorio")
        String nombreAlias
) {
}
