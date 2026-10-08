-- Active: 1748481882949@@127.0.0.1@3306@SistemaFidelizacion
-- ==========================================================
-- 4. CONSULTAS DE PRUEBA (DQL)
-- ==========================================================

USE SistemaFidelizacion;

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