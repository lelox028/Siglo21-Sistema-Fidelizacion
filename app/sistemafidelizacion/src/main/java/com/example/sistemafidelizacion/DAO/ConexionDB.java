package com.example.sistemafidelizacion.DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

// voy a utilizar el patron singleton para instanciar la conexion a la base de datos, de esta manera me aseguro que solo exista una instancia de la conexion a la base de datos en toda la aplicacion.
public class ConexionDB {

    //primero cargo las variables de entorno
    private static final Dotenv dotenv = Dotenv.load();

    //ahora configuramos los parametros de conexion a la base de datos
    private static final String URL = dotenv.get("DB_HOST");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASS");

    private static Connection conexion = null;

    // Constructor privado para evitar instanciación externa (Patrón Singleton)
    private ConexionDB() {}

    /**
     * Obtiene la conexión activa a la base de datos.
     * Si no existe o está cerrada, crea una nueva.
     */
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Registrar el driver JDBC (MySQL)
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a la Base de Datos.");
            }
        } catch (ClassNotFoundException e) {
            // Manejo de error si el driver JDBC no se encuentra
            System.err.println("Error: Driver JDBC no encontrado. " + e.getMessage());
        } catch (SQLException e) {
            // Manejo de error si ocurre un problema al conectar con la base de datos
            System.err.println("Error al conectar con la Base de Datos: " + e.getMessage());
        }
        return conexion;
    }

    /**
     * Cierra la conexión activa si está abierta.
     */
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                    System.out.println("Conexión a la Base de Datos cerrada.");
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}

