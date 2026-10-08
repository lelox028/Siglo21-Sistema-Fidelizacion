package com.example.sistemafidelizacion.DAO;

import com.example.sistemafidelizacion.Modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ClienteDAO {

    // Decidi refactorizar estos metodos para que lancen ErrorAccesoDatosException
    // en lugar de imprimir errores en consola, para mantener la separacion de
    // responsabilidades y permitir que la capa de control maneje los errores de
    // manera adecuada.

    public boolean insertar(Cliente cliente) throws ErrorAccesoDatosException {
        String sql = "INSERT INTO Cliente (dni, nombre_completo, telefono, email, fecha_cumpleanos) "
                + "VALUES (?, ?, ?, ?, ?)";

        // Usamos try-with-resources para asegurar que la conexión y el
        // PreparedStatement se cierren automáticamente.
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Configuramos los parámetros del PreparedStatement con los datos del cliente.
            stmt.setInt(1, cliente.getDni());
            stmt.setString(2, cliente.getNombreCompleto());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setObject(5, cliente.getFechaNacimiento());
            // Ejecutamos la actualización y retornamos true si se insertó al menos una
            // fila.
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // En caso de error, lanzamos una excepción personalizada para que la capa de
            // control pueda manejarla.
            throw new ErrorAccesoDatosException("No se pudo insertar el cliente.", e);
        }
    }

    public Cliente buscarPorDni(int dni) throws ErrorAccesoDatosException {
        String sql = "SELECT dni, nombre_completo, telefono, email, fecha_cumpleanos "
                + "FROM Cliente WHERE dni = ?";
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            // Intentamos ejecutar la consulta y mapear el resultado a un objeto Cliente.
            try (ResultSet rs = stmt.executeQuery()) {
                // Si encontramos un resultado, lo mapeamos a un objeto Cliente y lo retornamos,
                // de lo contrario retornamos null.
                if (rs.next()) {
                    return mapearCliente(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo buscar el cliente.", e);
        }
    }

    public boolean actualizar(Cliente cliente) throws ErrorAccesoDatosException {
        String sql = "UPDATE Cliente SET nombre_completo = ?, telefono = ?, email = ?, "
                + "fecha_cumpleanos = ? WHERE dni = ?";
        // aca ya no es necesario validar que el cliente no sea nulo, ya que eso se hace
        // en la capa de control antes de llamar a este metodo.
        try (Connection conn = obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNombreCompleto());
            stmt.setString(2, cliente.getTelefono());
            stmt.setString(3, cliente.getEmail());
            stmt.setObject(4, cliente.getFechaNacimiento());
            stmt.setInt(5, cliente.getDni());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErrorAccesoDatosException("No se pudo actualizar el cliente.", e);
        }
    }

    // Metodos auxiliares privados.

    // Mapea un ResultSet a un objeto Cliente.
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        LocalDate fechaNacimiento = rs.getObject("fecha_cumpleanos", LocalDate.class);
        return new Cliente(
                rs.getInt("dni"),
                rs.getString("nombre_completo"),
                rs.getString("telefono"),
                rs.getString("email"),
                fechaNacimiento);
    }

    // Obtiene una conexión a la base de datos.
    private Connection obtenerConexion() throws ErrorAccesoDatosException {
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            throw new ErrorAccesoDatosException("No hay conexión disponible con la base de datos.", null);
        }
        return conn;
    }
}
