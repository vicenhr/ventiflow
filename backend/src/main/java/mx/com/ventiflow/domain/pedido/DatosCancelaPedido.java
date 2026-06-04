package mx.com.ventiflow.domain.pedido;

public record DatosCancelaPedido(
        Long idPedido,
        String tipoAlerta,
        String mensaje
) {
}
