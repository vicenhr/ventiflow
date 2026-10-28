-- V7__preparar_usuarios_bcrypt.sql

ALTER TABLE usuarios RENAME COLUMN clave TO clave_hash;

ALTER TABLE usuarios
    ADD CONSTRAINT chk_usuarios_rol
    CHECK (rol IN ('ADMIN', 'CAJA', 'COCINA', 'BARRA'));