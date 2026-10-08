package com.example.sistemafidelizacion.DAO;

import com.example.sistemafidelizacion.Modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ClienteDAO {

    // metodo para insertar un cliente en la base de datos.
    public boolean insertar(Cliente cliente) {

        // empezamos por preparar la sentencia SQL para insertar un cliente en la base
        // de datos.
        String sql = "INSERT INTO Cliente (dni, nombre_completo, telefono, email, fecha_cumpleanos) VALUES (?, ?, ?, ?, ?)";

        // try-with-resources para asegurar que la conexión y el PreparedStatement se
        // cierren automáticamente
        try (Connection conn = ConexionDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // creamos un PreparedStatement para ejecutar la sentencia SQL y asignamos los
            // valores de los parámetros usando los getters del objeto Cliente.
            stmt.setInt(1, cliente.getDni());
            stmt.setString(2, cliente.getNombreCompleto());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setObject(5, cliente.getFechaNacimiento());

            // Ejecutamos la sentencia y almacenamos el número de filas afectadas. Si es
            // mayor a 0, significa que la inserción fue exitosa.
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            // Manejo de error sql si ocurre un problema al insertar el cliente en la base
            // de datos imprime el mensaje de error en la consola y retorna false indicando
            // que la inserción falló.
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    public Cliente buscarPorDni(int dni) {
        // Preparamos la sentencia SQL para buscar un cliente por su DNI
        String sql = "SELECT * FROM Cliente WHERE dni = ?";
        // Inicializamos el objeto Cliente como null, que será retornado si no se
        // encuentra ningún cliente con el DNI proporcionado
        Cliente cliente = null;

        // Usamos try-with-resources para asegurar que la conexión, el PreparedStatement
        // y el ResultSet se cierren automáticamente
        try (Connection conn = ConexionDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                // Si el ResultSet tiene al menos una fila, significa que se encontró un cliente
                // con el DNI proporcionado. Extraemos los datos de esa fila y creamos un objeto
                // Cliente con ellos.
                if (rs.next()) {
                    String nombre = rs.getString("nombre_completo");
                    String tel = rs.getString("telefono");
                    String email = rs.getString("email");
                    LocalDate fechaNac = rs.getObject("fecha_cumpleanos", LocalDate.class);

                    // Inicialización del objeto con constructor
                    cliente = new Cliente(dni, nombre, tel, email, fechaNac);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por DNI: " + e.getMessage());
        }

        return cliente;
    }

    public boolean actualizar(Cliente cliente) {
        // preparamos la sentencia SQL para actualizar un cliente en la base de datos
        String sql = "UPDATE Cliente SET nombre_completo = ?, telefono = ?, email = ?, fecha_cumpleanos = ? WHERE dni = ?";
        // inicializamos la variable clienteExistente, que se usará para verificar si el
        // cliente que queremos actualizar ya existe en la base de datos.
        Cliente clienteExistente = buscarPorDni(cliente.getDni());

        if (clienteExistente == null) {
            // si el cliente no existe, imprimimos un mensaje de error y retornamos false
            // indicando que la actualización falló.
            System.err.println("Error: No se puede actualizar. El cliente con DNI " + cliente.getDni() + " no existe.");
            return false;
        }

        // si el cliente a modificar existe, procedemos a actualizarlo en la base de
        // datos.

        // try-with-resources para asegurar que la conexión y el PreparedStatement se
        // cierren automáticamente
        try (Connection conn = ConexionDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // creamos un PreparedStatement para ejecutar la sentencia SQL y asignamos los
            // valores de los parámetros usando los getters del objeto Cliente.
            stmt.setString(1, cliente.getNombreCompleto());
            stmt.setString(2, cliente.getTelefono());
            stmt.setString(3, cliente.getEmail());
            stmt.setObject(4, cliente.getFechaNacimiento());
            stmt.setInt(5, cliente.getDni());

            // ejecutamos la sentencia y almacenamos el número de filas afectadas. Si es
            // mayor a 0, significa que la actualización fue exitosa.
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            // manejo de error sql si ocurre un problema al actualizar el cliente en la base
            // de datos imprime el mensaje de error en la consola y retorna false indicando
            // que la actualización falló.
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }
}