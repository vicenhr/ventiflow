package mx.com.ventiflow.domain.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoCorto(
        Long turno,
        LocalDateTime fechaHora,
        BigDecimal total
) {
}
