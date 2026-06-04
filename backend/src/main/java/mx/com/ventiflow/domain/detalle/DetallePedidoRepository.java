package mx.com.ventiflow.domain.detalle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    // Esta consulta agrupa los productos activos por su área de preparación (Barra o Cocina)
    @Query("SELECT p.categoria.areaPreparacion, SUM(dp.cantidad) " +
            "FROM DetallePedido dp " +
            "JOIN dp.producto p " +
            "JOIN dp.pedido ped " +
            "WHERE ped.estado IN ('PENDIENTE', 'PREPARANDO') " +
            "GROUP BY p.categoria.areaPreparacion")
    List<Object[]> contarCargaActivaPorArea();


    // Consulta el producto más vendido del mes
    @Query(value = "SELECT pr.nombre_producto " +
            "FROM detalle_pedidos dp " +
            "JOIN productos pr ON dp.id_producto = pr.id_producto " +
            "JOIN pedidos p ON dp.id_pedido = p.id_pedido " +
            "WHERE p.fecha_hora_creacion BETWEEN :inicio AND :fin " +
            "GROUP BY pr.nombre_producto " +
            "ORDER BY SUM(dp.cantidad) DESC LIMIT 1", nativeQuery = true)
    String encontrarProductoEstrella(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
