package com.example.sistemafidelizacion.DAO;

import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Modelo.Compra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                + "c.dni, c.nombre_completo, c.telefono, c.email, c.fecha_cumpleanos "
                + "FROM Compra co INNER JOIN Cliente c ON c.dni = co.dni_cliente "
                + "WHERE co.dni_cliente = ? ORDER BY co.fecha_hora";
        List<Compra> compras = new ArrayList<>();
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    compras.add(mapearCompra(rs));
                }
            }
            return compras;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo obtener el historial del cliente.", e);
        }
    }

    public List<Compra> obtenerEntre(LocalDateTime desde, LocalDateTime hasta) throws ErrorAccesoDatosException {
        String sql = "SELECT co.id_compra, co.fecha_hora, co.monto, "
                + "c.dni, c.nombre_completo, c.telefono, c.email, c.fecha_cumpleanos "
                + "FROM Compra co INNER JOIN Cliente c ON c.dni = co.dni_cliente "
                + "WHERE co.fecha_hora BETWEEN ? AND ? ORDER BY co.fecha_hora";
        List<Compra> compras = new ArrayList<>();
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, desde);
            stmt.setObject(2, hasta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    compras.add(mapearCompra(rs));
                }
            }
            return compras;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo obtener el historial del período.", e);
        }
    }

    private Compra mapearCompra(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("dni"),
                rs.getString("nombre_completo"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getObject("fecha_cumpleanos", java.time.LocalDate.class));
        LocalDateTime fechaHora = rs.getObject("fecha_hora", LocalDateTime.class);
        return new Compra(rs.getInt("id_compra"), fechaHora, rs.getDouble("monto"), cliente, null);
    }

    private Connection obtenerConexion() throws ErrorAccesoDatosException {
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            throw new ErrorAccesoDatosException("No hay conexión disponible con la base de datos.", null);
        }
        return conn;
    }
}
