-- ==========================================================
-- 3. LENGUAJE DE MANIPULACIÓN DE DATOS (DML) - POBLACIÓN
-- ==========================================================

USE SistemaFidelizacion;


INSERT INTO Cliente (dni, nombre_completo, telefono, email, fecha_cumpleanos) 
VALUES 
(34567890, 'Giuliano Pascarelli', '2664123456', 'gpascarelli@email.com', '2000-05-15'),
(28123456, 'Ana Martinez', '2664654321', 'amartinez@email.com', '1980-10-20');

INSERT INTO Beneficio (nombre, descripcion, activo) 
VALUES 
('Descuento Cumpleaños', '15% de descuento en el mes de nacimiento', TRUE),
('Puntos Dobles', 'Acumulación doble de puntos los días martes', TRUE);

INSERT INTO Compra (dni_cliente, fecha_hora, monto) 
VALUES 
(34567890, '2026-09-25 10:30:00', 15000.50),
(28123456, '2026-09-25 11:15:00', 8500.00);

INSERT INTO BeneficioAplicado (id_compra, id_beneficio, fecha_aplicacion) 
VALUES 
(1, 1, '2026-09-25 10:30:00');