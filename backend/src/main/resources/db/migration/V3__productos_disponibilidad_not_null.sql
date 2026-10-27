-- V3__productos_disponibilidad_not_null.sql
UPDATE productos SET estado_disponibilidad = TRUE WHERE estado_disponibilidad IS NULL;
ALTER TABLE productos ALTER COLUMN estado_disponibilidad SET NOT NULL;