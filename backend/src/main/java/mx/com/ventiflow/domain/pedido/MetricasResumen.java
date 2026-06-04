package mx.com.ventiflow.domain.pedido;

import java.util.List;

public record MetricasResumen(
        Double ventasTotales,
        Integer ticketsGenerados,
        Double tiempoPromedioSegundos,
        String productoEstrella,
        List<VentaHora> graficoHoras,
        List<PedidoCorto> ultimosPedidos
) {

}
