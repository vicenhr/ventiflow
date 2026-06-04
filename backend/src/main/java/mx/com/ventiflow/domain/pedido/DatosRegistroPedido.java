package mx.com.ventiflow.domain.pedido;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import mx.com.ventiflow.domain.detalle.DatosDetallePedido;

import java.math.BigDecimal;
import java.util.List;

public record DatosRegistroPedido(
        @NotNull(message = "El idUsuario es obligatorio")
        Long idUsuario,

        Long idCliente, // Este puede ser null si es cliente casual

        @NotNull(message = "El total es obligatorio")
        BigDecimal totalPagado,

        @NotEmpty(message = "El pedido debe contener al menos un producto")
        List<DatosDetallePedido> detalles // @NotEmpty asegura que no sea null y que tamaño sea > 0
) {
}
