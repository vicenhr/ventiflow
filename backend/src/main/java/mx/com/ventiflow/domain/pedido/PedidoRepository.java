package mx.com.ventiflow.domain.pedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Consulta para el GET /api/v1/pedidos/activos
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.detalles d JOIN d.producto pr JOIN pr.categoria c " +
            "WHERE p.estado IN ('PENDIENTE', 'PREPARANDO') " +
            "AND c.areaPreparacion = :area ORDER BY p.fechaCreacion ASC")
    List<Pedido> findPedidosActivosPorArea(@Param("area") String area);

    // 1. Para el KDS (Barra/Cocina): Pedidos pendientes/en preparación de HOY por área
    @Query("""
        SELECT DISTINCT p FROM Pedido p 
        JOIN p.detalles d 
        WHERE p.estado IN ('PENDIENTE', 'PREPARANDO', 'LISTO') 
        AND p.fechaCreacion >= CURRENT_DATE 
        AND d.producto.categoria.areaPreparacion = :area
    """)
    List<Pedido> findPedidosActivosDeHoy(@Param("area") String area);

    // 2. Para la Pantalla Pública (CDS): Todo lo de hoy que no esté entregado ni cancelado
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'PREPARANDO', 'LISTO') " +
            "AND p.fechaCreacion >= :inicioDia AND p.fechaCreacion <= :finDia " +
            "ORDER BY p.fechaCreacion ASC")
    List<Pedido> findPedidosParaPantallaPublica(
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia
    );

    // Para evitar cruzar lógicas, dividiremos las responsabilidades en dos interfaces.

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado != 'CANCELADO' AND p.fechaCreacion BETWEEN :inicio AND :fin")
    Double sumarVentasTotales(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.fechaCreacion BETWEEN :inicio AND :fin")
    Integer contarTickets(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Agrupación por hora nativa en PostgreSQL
    @Query(value = "SELECT EXTRACT(HOUR FROM fecha_hora_creacion) AS hora, SUM(total_pagado) AS totalVentas " +
            "FROM pedidos " +
            "WHERE fecha_hora_creacion BETWEEN :inicio AND :fin AND estado_general != 'CANCELADO' " +
            "GROUP BY EXTRACT(HOUR FROM fecha_hora_creacion) " +
            "ORDER BY hora", nativeQuery = true)
    List<Object[]> obtenerVentasPorHoraNativo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Obtener los últimos N pedidos (Método nativo de Spring Data)
    List<Pedido> findTop5ByFechaCreacionBetweenOrderByFechaCreacionDesc(LocalDateTime inicio, LocalDateTime fin);
}
