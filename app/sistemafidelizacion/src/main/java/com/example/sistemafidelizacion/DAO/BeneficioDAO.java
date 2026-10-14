package com.example.sistemafidelizacion.DAO;

import com.example.sistemafidelizacion.Modelo.Beneficio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BeneficioDAO {
    public boolean insertar(Beneficio beneficio) throws ErrorAccesoDatosException {
        String sql = "INSERT INTO Beneficio (nombre, descripcion, activo) VALUES (?, ?, ?)";
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, beneficio.getNombre());
            stmt.setString(2, beneficio.getDescripcion());
            stmt.setBoolean(3, beneficio.isActivo());
            if (stmt.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    beneficio.setIdBeneficio(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo insertar el beneficio.", e);
        }
    }

    public Beneficio buscarPorId(int idBeneficio) throws ErrorAccesoDatosException {
        String sql = "SELECT id_beneficio, nombre, descripcion, activo FROM Beneficio "
                + "WHERE id_beneficio = ?";
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBeneficio);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearBeneficio(rs) : null;
            }
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo buscar el beneficio.", e);
        }
    }

    public boolean actualizar(Beneficio beneficio) throws ErrorAccesoDatosException {
        String sql = "UPDATE Beneficio SET nombre = ?, descripcion = ?, activo = ? "
                + "WHERE id_beneficio = ?";
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, beneficio.getNombre());
            stmt.setString(2, beneficio.getDescripcion());
            stmt.setBoolean(3, beneficio.isActivo());
            stmt.setInt(4, beneficio.getIdBeneficio());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo actualizar el beneficio.", e);
        }
    }

    public List<Beneficio> listarTodos() throws ErrorAccesoDatosException {
        return listarConFiltro(null);
    }

    public List<Beneficio> listarActivos() throws ErrorAccesoDatosException {
        return listarConFiltro(Boolean.TRUE);
    }

    private List<Beneficio> listarConFiltro(Boolean activo) throws ErrorAccesoDatosException {
        String sql = "SELECT id_beneficio, nombre, descripcion, activo FROM Beneficio"
                + (activo == null ? "" : " WHERE activo = ?")
                + " ORDER BY id_beneficio";
        List<Beneficio> beneficios = new ArrayList<>();
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (activo != null) {
                stmt.setBoolean(1, activo);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    beneficios.add(mapearBeneficio(rs));
                }
            }
            return beneficios;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo listar los beneficios.", e);
        }
    }

    private Beneficio mapearBeneficio(ResultSet rs) throws SQLException {
        return new Beneficio(
                rs.getInt("id_beneficio"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("activo"));
    }

    private Connection obtenerConexion() throws ErrorAccesoDatosException {
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            throw new ErrorAccesoDatosException("No hay conexión disponible con la base de datos.", null);
        }
        return conn;
    }
}
