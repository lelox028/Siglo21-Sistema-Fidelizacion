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
