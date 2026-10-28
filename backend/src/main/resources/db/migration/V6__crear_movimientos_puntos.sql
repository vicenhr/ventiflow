-- V6__crear_movimiento_puntos.sql

CREATE TABLE movimiento_puntos (
    id_movimiento BIGSERIAL,
    id_cliente    BIGINT      NOT NULL,
    id_pedido     BIGINT,
    tipo          VARCHAR(10) NOT NULL,
    puntos        INTEGER     NOT NULL,
    creado_en     TIMESTAMP   NOT NULL DEFAULT now(),

    CONSTRAINT pk_movimiento_puntos PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_movimiento_puntos_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    CONSTRAINT fk_movimiento_puntos_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    CONSTRAINT chk_movimiento_puntos_tipo
        CHECK (tipo IN ('GANADO', 'CANJEADO', 'AJUSTE')),
    CONSTRAINT chk_movimiento_puntos_signo
        CHECK (
            (tipo = 'GANADO'   AND puntos > 0) OR
            (tipo = 'CANJEADO' AND puntos < 0) OR
            (tipo = 'AJUSTE'   AND puntos <> 0)
        )
);

CREATE INDEX idx_movimiento_puntos_cliente
    ON movimiento_puntos (id_cliente);