-- ==========================================================
-- 3. LENGUAJE DE MANIPULACIÓN DE DATOS (DML) - POBLACIÓN
-- ==========================================================

USE SistemaFidelizacion;


INSERT INTO Cliente (dni, nombre_completo, telefono, email, fecha_cumpleanos) 
VALUES 
(34567890, 'Giuliano Pascarelli', '2664123456', 'gpascarelli@email.com', '2000-05-15'),
(28123456, 'Ana Martinez', '2664654321', 'amartinez@email.com', '1980-10-20'),
(30987654, 'Carlos Rodriguez', '2664789012', 'crodriguez@email.com', '1995-02-08'),
(32765432, 'Lucia Fernandez', '2664890123', 'lfernandez@email.com', '1992-12-03');

INSERT INTO Beneficio (nombre, descripcion, activo) 
VALUES 
('Descuento Cumpleaños', '15% de descuento en el mes de nacimiento', TRUE),
('Puntos Dobles', 'Acumulación doble de puntos los días martes', TRUE),
('Envío Gratis', 'Envío sin cargo en compras superiores a $100', TRUE);

INSERT INTO Compra (dni_cliente, fecha_hora, monto) 
VALUES 
(34567890, '2026-05-15 10:30:00', 150.50),
(34567890, '2026-09-25 10:30:00', 85.00),
(28123456, '2026-10-20 11:15:00', 185.00),
(28123456, '2026-09-25 12:00:00', 72.00),
(30987654, '2026-02-08 13:45:00', 120.00),
(30987654, '2026-08-14 16:20:00', 95.75),
(32765432, '2026-12-03 09:10:00', 220.00),
(32765432, '2026-09-30 18:05:00', 68.00);

INSERT INTO BeneficioAplicado (id_compra, id_beneficio, fecha_aplicacion) 
VALUES 
(1, 1, '2026-05-15 10:30:00'),
(1, 2, '2026-05-15 10:30:00'),
(2, 2, '2026-09-25 10:30:00'),
(3, 1, '2026-10-20 11:15:00'),
(3, 3, '2026-10-20 11:15:00'),
(4, 2, '2026-09-25 12:00:00'),
(5, 1, '2026-02-08 13:45:00'),
(5, 3, '2026-02-08 13:45:00'),
(6, 2, '2026-08-14 16:20:00'),
(7, 1, '2026-12-03 09:10:00'),
(7, 3, '2026-12-03 09:10:00'),
(8, 2, '2026-09-30 18:05:00');