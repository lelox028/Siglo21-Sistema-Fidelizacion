package com.example.sistemafidelizacion.DAO;

import com.example.sistemafidelizacion.Modelo.BeneficioAplicado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BeneficioAplicadoDAO {
    public boolean insertar(BeneficioAplicado beneficioAplicado) throws ErrorAccesoDatosException {
        String sql = "INSERT INTO BeneficioAplicado "
                + "(id_compra, id_beneficio, fecha_aplicacion) VALUES (?, ?, ?)";
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, beneficioAplicado.getCompra().getIdCompra());
            stmt.setInt(2, beneficioAplicado.getBeneficio().getIdBeneficio());
            stmt.setObject(3, beneficioAplicado.getFechaAplicacion());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo registrar el beneficio aplicado.", e);
        }
    }

    public boolean verificarUsoCumpleanos(int dni, int anio) throws ErrorAccesoDatosException {
        String sql = "SELECT COUNT(*) FROM BeneficioAplicado ba "
                + "INNER JOIN Compra co ON co.id_compra = ba.id_compra "
                + "INNER JOIN Cliente c ON c.dni = co.dni_cliente "
                + "INNER JOIN Beneficio b ON b.id_beneficio = ba.id_beneficio "
                + "WHERE c.dni = ? AND YEAR(ba.fecha_aplicacion) = ? "
                + "AND b.nombre = 'Descuento Cumpleaños'";
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            stmt.setInt(2, anio);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo verificar el uso del beneficio de cumpleaños.", e);
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
