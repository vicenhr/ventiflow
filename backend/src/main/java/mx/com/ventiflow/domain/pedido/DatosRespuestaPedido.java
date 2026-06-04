package mx.com.ventiflow.domain.pedido;

public record DatosRespuestaPedido(
        Long idPedido,
        String turnoAsignado,
        String estadoGeneral,
        String mensaje
) {
}
