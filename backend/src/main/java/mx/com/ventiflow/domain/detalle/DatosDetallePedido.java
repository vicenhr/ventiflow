package mx.com.ventiflow.domain.detalle;

import java.math.BigDecimal;

public record DatosDetallePedido(
        Long idProducto,
        Integer cantidad,
        BigDecimal subtotal
) {
}
