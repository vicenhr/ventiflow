package mx.com.ventiflow.service;

import mx.com.ventiflow.domain.detalle.DetallePedidoRepository;
import mx.com.ventiflow.domain.pedido.MetricasResumen;
import mx.com.ventiflow.domain.pedido.PedidoCorto;
import mx.com.ventiflow.domain.pedido.PedidoRepository;
import mx.com.ventiflow.domain.pedido.VentaHora;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetricasService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public MetricasService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public MetricasResumen calcularResumen(LocalDate fechaInicio, LocalDate fechaFin) {
        // Convertir LocalDate a LocalDateTime cubriendo el día entero
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        Double ventasTotales = pedidoRepository.sumarVentasTotales(inicio, fin);
        Integer tickets = pedidoRepository.contarTickets(inicio, fin);
        String productoEstrella = detallePedidoRepository.encontrarProductoEstrella(inicio, fin);

        // Manejar posibles nulos si no hay ventas en el rango
        ventasTotales = ventasTotales != null ? ventasTotales : 0.0;
        productoEstrella = productoEstrella != null ? productoEstrella : "Sin datos";


        // 1. Mapeo del gráfico de barras actualizado
        List<Object[]> rawVentasHora = pedidoRepository.obtenerVentasPorHoraNativo(inicio, fin);
        List<VentaHora> graficoHoras = rawVentasHora.stream()
                .map(row -> new VentaHora(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).doubleValue() // Ahora se inyectará en el campo "total"
                ))
                .collect(Collectors.toList());

        // 2. Mapeo de los últimos 5 pedidos actualizado
        List<PedidoCorto> ultimosPedidos = pedidoRepository
                .findTop5ByFechaCreacionBetweenOrderByFechaCreacionDesc(inicio, fin)
                .stream()
                .map(p -> new PedidoCorto(
                        p.getIdPedido(),
                        p.getFechaCreacion(),
                        p.getTotal()
                ))
                .collect(Collectors.toList());

        return new MetricasResumen(
                ventasTotales,
                tickets,
                0.0,
                productoEstrella,
                graficoHoras,
                ultimosPedidos
        );
    }
}
