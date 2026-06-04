-- V2__Insertar_catalogos.sql

INSERT INTO usuarios (nombre, rol, clave) VALUES
('Admin', 'Administrador', '1234'),
('Caja 1', 'Cajero', '1234'),
('Mesero 1', 'Mesero', '1234');

INSERT INTO categorias (nombre_categoria, area_preparacion) VALUES
('Cafés Clásicos', 'Barra'),
('Bebidas Heladas y Frappés', 'Barra'),
('Tés e Infusiones', 'Barra'),
('Panadería Dulce', 'Cocina'),
('Alimentos Salados', 'Cocina'),
('Postres y Rebanadas', 'Cocina');

INSERT INTO productos (id_categoria, nombre_producto, precio_unitario, estado_disponibilidad) VALUES
(1, 'Café Americano 12oz', 45.00, TRUE),
(1, 'Latte Vainilla Caliente', 65.00, TRUE),
(2, 'Frappé de Caramelo', 85.00, TRUE),
(3, 'Té Verde Helado', 55.00, TRUE),
(4, 'Croissant de Mantequilla', 40.00, TRUE),
(5, 'Sándwich de Pavo y Queso', 95.00, TRUE),
(6, 'Rebanada de Pastel de Zanahoria', 75.00, TRUE);