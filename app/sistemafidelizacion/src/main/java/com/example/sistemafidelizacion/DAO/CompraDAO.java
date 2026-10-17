package com.example.sistemafidelizacion.DAO;

import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Modelo.Beneficio;
import com.example.sistemafidelizacion.Modelo.BeneficioAplicado;
import com.example.sistemafidelizacion.Modelo.Compra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompraDAO {
    public boolean insertar(Compra compra) throws ErrorAccesoDatosException {
        String sql = "INSERT INTO Compra (dni_cliente, fecha_hora, monto) VALUES (?, ?, ?)";
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, compra.getCliente().getDni());
            stmt.setObject(2, compra.getFechaHora());
            stmt.setDouble(3, compra.getMonto());
            if (stmt.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    compra.setIdCompra(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo insertar la compra.", e);
        }
    }

    public List<Compra> obtenerHistorialCliente(int dni) throws ErrorAccesoDatosException {
        String sql = "SELECT co.id_compra, co.fecha_hora, co.monto, "
                + "c.dni, c.nombre_completo, c.telefono, c.email, c.fecha_cumpleanos, "
                + "ba.id_beneficio_aplicado, ba.fecha_aplicacion, "
                + "b.id_beneficio, b.nombre AS beneficio_nombre, b.descripcion, b.activo "
                + "FROM Compra co INNER JOIN Cliente c ON c.dni = co.dni_cliente "
                + "LEFT JOIN BeneficioAplicado ba ON ba.id_compra = co.id_compra "
                + "LEFT JOIN Beneficio b ON b.id_beneficio = ba.id_beneficio "
                + "WHERE co.dni_cliente = ? ORDER BY co.fecha_hora";
        Map<Integer, Compra> compras = new LinkedHashMap<>();
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapearCompraConBeneficio(rs, compras);
                }
            }
            return new ArrayList<>(compras.values());
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo obtener el historial del cliente.", e);
        }
    }

    public List<Compra> obtenerEntre(LocalDateTime desde, LocalDateTime hasta) throws ErrorAccesoDatosException {
        String sql = "SELECT co.id_compra, co.fecha_hora, co.monto, "
                + "c.dni, c.nombre_completo, c.telefono, c.email, c.fecha_cumpleanos, "
                + "ba.id_beneficio_aplicado, ba.fecha_aplicacion, "
                + "b.id_beneficio, b.nombre AS beneficio_nombre, b.descripcion, b.activo "
                + "FROM Compra co INNER JOIN Cliente c ON c.dni = co.dni_cliente "
                + "LEFT JOIN BeneficioAplicado ba ON ba.id_compra = co.id_compra "
                + "LEFT JOIN Beneficio b ON b.id_beneficio = ba.id_beneficio "
                + "WHERE co.fecha_hora BETWEEN ? AND ? ORDER BY co.fecha_hora";
        Map<Integer, Compra> compras = new LinkedHashMap<>();
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, desde);
            stmt.setObject(2, hasta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapearCompraConBeneficio(rs, compras);
                }
            }
            return new ArrayList<>(compras.values());
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo obtener el historial del período.", e);
        }
    }

    public List<Compra> obtenerParaReporte(LocalDateTime desde, LocalDateTime hasta)
            throws ErrorAccesoDatosException {
        // Query GIGANTE para obtener todas las compras, los clientes que las realizaron
        // y sus beneficios aplicados.
        return obtenerParaReporte(desde, hasta, null, null, false);
    }

    public List<Compra> obtenerParaReporte(LocalDateTime desde, LocalDateTime hasta,
            Double montoMinimo, Double montoMaximo, boolean soloBeneficiosActivos)
            throws ErrorAccesoDatosException {
        String sql = "SELECT co.id_compra, co.fecha_hora, co.monto, "
                + "c.dni, c.nombre_completo, c.telefono, c.email, c.fecha_cumpleanos, "
                + "ba.id_beneficio_aplicado, ba.fecha_aplicacion, "
                + "b.id_beneficio, b.nombre AS beneficio_nombre, b.descripcion, b.activo "
                // Inner Join con cliente para obtener los datos del cliente que realizó la
                // compra (todas las compras tienen exactamente un cliente asociado)
                + "FROM Compra co INNER JOIN Cliente c ON c.dni = co.dni_cliente "
                // Left join con beneficio aplicado y beneficio para obtener los beneficios
                // aplicados a cada compra (si los hay)
                + "LEFT JOIN BeneficioAplicado ba ON ba.id_compra = co.id_compra "
                + "LEFT JOIN Beneficio b ON b.id_beneficio = ba.id_beneficio "
                // Filtro magico que permite que si desde o hasta son null, no se aplique el
                // filtro de fecha correspondiente, si no son null, se filtra por el rango de
                // fecha solicitado.
                + "WHERE (? IS NULL OR co.fecha_hora >= ?) "
                + "AND (? IS NULL OR co.fecha_hora <= ?) "
                // lo mismo para el monto minimo y maximo, si son null no se filtra, si no lo
                // son, se filtra por el rango de monto solicitado.
                + "AND (? IS NULL OR co.monto >= ?) "
                + "AND (? IS NULL OR co.monto <= ?) "
                // Filtro para que si soloBeneficiosActivos es true, se muestren solo las compras que tengan al menos un beneficio activo aplicado, si es false, se muestran todas las compras.
                + "AND (? = FALSE OR EXISTS ("
                + "SELECT 1 FROM BeneficioAplicado ba_act "
                + "INNER JOIN Beneficio b_act ON b_act.id_beneficio = ba_act.id_beneficio "
                + "WHERE ba_act.id_compra = co.id_compra AND b_act.activo = TRUE)) "
                + "ORDER BY co.fecha_hora";
        Map<Integer, Compra> compras = new LinkedHashMap<>();
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, desde);
            stmt.setObject(2, desde);
            stmt.setObject(3, hasta);
            stmt.setObject(4, hasta);
            stmt.setObject(5, montoMinimo);
            stmt.setObject(6, montoMinimo);
            stmt.setObject(7, montoMaximo);
            stmt.setObject(8, montoMaximo);
            stmt.setBoolean(9, soloBeneficiosActivos);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapearCompraConBeneficio(rs, compras);
                }
            }
            return new ArrayList<>(compras.values());
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo obtener la información para el reporte.", e);
        }
    }

    private void mapearCompraConBeneficio(ResultSet rs, Map<Integer, Compra> compras) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("dni"),
                rs.getString("nombre_completo"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getObject("fecha_cumpleanos", java.time.LocalDate.class));
        int idCompra = rs.getInt("id_compra");
        Compra compra = compras.get(idCompra);
        if (compra == null) {
            LocalDateTime fechaHora = rs.getObject("fecha_hora", LocalDateTime.class);
            compra = new Compra(idCompra, fechaHora, rs.getDouble("monto"), cliente, null);
            compras.put(idCompra, compra);
        }

        if (rs.getObject("id_beneficio_aplicado") != null) {
            Beneficio beneficio = new Beneficio(
                    rs.getInt("id_beneficio"),
                    rs.getString("beneficio_nombre"),
                    rs.getString("descripcion"),
                    rs.getBoolean("activo"));
            compra.agregarBeneficio(new BeneficioAplicado(
                    rs.getInt("id_beneficio_aplicado"),
                    beneficio,
                    compra,
                    rs.getObject("fecha_aplicacion", LocalDateTime.class)));
        }
    }

    private Connection obtenerConexion() throws ErrorAccesoDatosException {
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            throw new ErrorAccesoDatosException("No hay conexión disponible con la base de datos.", null);
        }
        return conn;
    }
}
