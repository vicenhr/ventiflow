-- V4__crear_pedidos_estacion.sql
CREATE TABLE pedidos_estacion (
    id_pedido   BIGINT      NOT NULL,
    area_preparacion        VARCHAR(10) NOT NULL,
    estado      VARCHAR(12) NOT NULL DEFAULT 'PENDIENTE',
    iniciado_en TIMESTAMP,
    listo_en    TIMESTAMP,

    CONSTRAINT pk_pedidos_estacion PRIMARY KEY (id_pedido, area_preparacion),
    CONSTRAINT fk_pedidos_estacion_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    CONSTRAINT chk_pedidos_estacion_area_preparacion
        CHECK (area_preparacion IN ('COCINA', 'BARRA')),
    CONSTRAINT chk_pedidos_estacion_estado
        CHECK (estado IN ('PENDIENTE', 'PREPARANDO', 'LISTO')),
    CONSTRAINT chk_pedidos_estacion_tiempos
        CHECK (listo_en IS NULL OR iniciado_en IS NULL OR listo_en >= iniciado_en)
);

CREATE INDEX idx_pedidos_estacion_area_preparacion_estado
    ON pedidos_estacion (area_preparacion, estado);