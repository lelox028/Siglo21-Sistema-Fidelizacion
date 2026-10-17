-- ==========================================================
-- 4. CONSULTAS DE PRUEBA (DQL)
-- ==========================================================

USE SistemaFidelizacion;

-- Una consulta independiente para verificar cada tabla cargada.
SELECT dni, nombre_completo, telefono, email, fecha_cumpleanos
FROM Cliente;

SELECT id_beneficio, nombre, descripcion, activo
FROM Beneficio;

SELECT id_compra, dni_cliente, fecha_hora, monto
FROM Compra;

SELECT id_beneficio_aplicado, id_compra, id_beneficio, fecha_aplicacion
FROM BeneficioAplicado;