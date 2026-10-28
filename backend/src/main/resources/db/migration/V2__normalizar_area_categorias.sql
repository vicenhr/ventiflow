-- V2_normalizar_area_categorias.sql

UPDATE categorias SET area_preparacion = UPPER(area_preparacion);

ALTER TABLE categorias
    ADD CONSTRAINT chk_categorias_area
    CHECK (area_preparacion IN ('COCINA', 'BARRA'));