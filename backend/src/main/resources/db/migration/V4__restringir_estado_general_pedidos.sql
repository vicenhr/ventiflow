-- V4__restringir_estado_general_pedidos.sql
ALTER TABLE pedidos
    ADD CONSTRAINT chk_pedidos_estado_general
    CHECK (estado_general IN ('PENDIENTE', 'PREPARANDO', 'LISTO', 'ENTREGADO'));