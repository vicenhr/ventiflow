package mx.com.ventiflow.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DatosUsuario(
        @NotBlank String usuario,
        @NotBlank String clave
) {
}
