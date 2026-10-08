-- ==========================================================
-- SCRIPT DE BASE DE DATOS - SISTEMA DE FIDELIZACIÓN
-- ==========================================================

-- 1. PREPARACIÓN DEL ENTORNO
CREATE DATABASE IF NOT EXISTS SistemaFidelizacion;
USE SistemaFidelizacion;

-- Limpieza previa (El borrado respeta el orden jerárquico inverso)
DROP TABLE IF EXISTS BeneficioAplicado;
DROP TABLE IF EXISTS Compra;
DROP TABLE IF EXISTS Beneficio;
DROP TABLE IF EXISTS Cliente;

-- ==========================================================
-- 2. LENGUAJE DE DEFINICIÓN DE DATOS (DDL)
-- ==========================================================

CREATE TABLE Cliente (
    dni INT NOT NULL PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    fecha_cumpleanos DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE Beneficio (
    id_beneficio INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE Compra (
    id_compra INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    dni_cliente INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (dni_cliente) REFERENCES Cliente(dni)
) ENGINE=InnoDB;

CREATE TABLE BeneficioAplicado (
    id_beneficio_aplicado INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_beneficio INT NOT NULL,
    fecha_aplicacion DATETIME NOT NULL,
    FOREIGN KEY (id_compra) REFERENCES Compra(id_compra),
    FOREIGN KEY (id_beneficio) REFERENCES Beneficio(id_beneficio)
) ENGINE=InnoDB;

-- ==========================================================
-- 3. LENGUAJE DE MANIPULACIÓN DE DATOS (DML) - POBLACIÓN
-- ==========================================================

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

-- ==========================================================
-- 4. CONSULTAS DE PRUEBA (DQL)
-- ==========================================================

-- Verificación del historial cruzado (Simulación de reporte)
SELECT 
    c.nombre_completo AS 'Cliente',
    co.id_compra AS 'Ticket',
    co.fecha_hora AS 'Fecha de Compra',
    co.monto AS 'Monto Ticket',
    IFNULL(b.nombre, 'Sin beneficio') AS 'Beneficio Utilizado'
FROM Cliente c
INNER JOIN Compra co ON c.dni = co.dni_cliente
LEFT JOIN BeneficioAplicado ba ON co.id_compra = ba.id_compra
LEFT JOIN Beneficio b ON ba.id_beneficio = b.id_beneficio
WHERE c.dni = 34567890;