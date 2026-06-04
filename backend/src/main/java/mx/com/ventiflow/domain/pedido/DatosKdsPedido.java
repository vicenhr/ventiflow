package mx.com.ventiflow.domain.pedido;

import com.fasterxml.jackson.annotation.JsonFormat;
import mx.com.ventiflow.domain.detalle.DatosDetalleKds;

import java.time.LocalDateTime;
import java.util.List;

public record DatosKdsPedido(
        Long idPedido,
        String turnoAsignado,
        String estadoGeneral,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaHoraCreacion,
        List<DatosDetalleKds> detalles
) {
}
