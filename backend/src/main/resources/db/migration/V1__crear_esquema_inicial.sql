-- V1__Crear_esquema_inicial.sql

SET client_encoding TO 'UTF8';

CREATE TABLE categorias (
    id_categoria BIGSERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL,
    area_preparacion VARCHAR(50) NOT NULL
);

CREATE TABLE productos (
    id_producto BIGSERIAL PRIMARY KEY,
    id_categoria BIGINT REFERENCES categorias(id_categoria),
    nombre_producto VARCHAR(150) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    estado_disponibilidad BOOLEAN DEFAULT TRUE
);

CREATE TABLE usuarios (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    clave VARCHAR(255) NOT NULL
);

CREATE TABLE clientes (
    id_cliente BIGSERIAL PRIMARY KEY,
    telefono VARCHAR(20) UNIQUE NOT NULL,
    nombre_alias VARCHAR(100),
    puntos_acumulados INT DEFAULT 0,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pedidos (
    id_pedido BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT REFERENCES usuarios(id_usuario),
    id_cliente BIGINT REFERENCES clientes(id_cliente),
    fecha_hora_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_general VARCHAR(50) NOT NULL,
    total_pagado DECIMAL(10,2) NOT NULL
);

CREATE TABLE detalle_pedidos (
    id_detalle BIGSERIAL PRIMARY KEY,
    id_pedido BIGINT REFERENCES pedidos(id_pedido),
    id_producto BIGINT REFERENCES productos(id_producto),
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

CREATE TABLE turnos (
    id_turno BIGSERIAL PRIMARY KEY,
    id_pedido BIGINT REFERENCES pedidos(id_pedido) UNIQUE,
    codigo_pantalla VARCHAR(10) NOT NULL,
    estado_visualizacion VARCHAR(50) NOT NULL,
    numero INT NOT NULL,
    fecha DATE NOT NULL
);

CREATE TABLE estado_trafico (
    id_metrica BIGSERIAL PRIMARY KEY,
    timestamp_medicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pedidos_activos_barra INT NOT NULL,
    pedidos_activos_cocina INT NOT NULL,
    recomendacion_sugerida VARCHAR(255)
);