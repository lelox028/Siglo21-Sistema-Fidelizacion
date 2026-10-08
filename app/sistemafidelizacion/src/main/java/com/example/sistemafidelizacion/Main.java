package com.example.sistemafidelizacion;

import java.sql.Connection;


import com.example.sistemafidelizacion.DAO.ConexionDB;

public class Main {
    public static void main(String[] args) {
        // 1. Configurar Look & Feel nativo para la interfaz Swing
        // try {
        //     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // } catch (Exception e) {
        //     System.err.println("No se pudo aplicar el Look & Feel nativo.");
        // }

        // 2. Verificación inicial de la Base de Datos
        System.out.println("Probando conexión a la Base de Datos...");
        
        // Intentar obtener la conexión
        Connection conn = ConexionDB.getConexion();
        
        if (conn != null) {
            System.out.println("¡Test exitoso! La Base de Datos respondió correctamente.");
            // Cerramos la conexión al terminar el test
            ConexionDB.cerrarConexion();
        } else {
            System.err.println("¡Test fallido! Revisa que MariaDB/MySQL esté corriendo y la contraseña sea correcta.");
        }

        // 3. Registrar cierre limpio de conexión al salir de la aplicación
        // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        //     System.out.println("Cerrando aplicación y liberando recursos...");
        //     ConexionDB.cerrarConexion();
        // }));

        // 4. Iniciar la interfaz gráfica en el Event Dispatch Thread (EDT) de Swing
        // SwingUtilities.invokeLater(() -> {
        //     PantallaPrincipal pantalla = new PantallaPrincipal();
        //     pantalla.setVisible(true); // Abre el Menú Principal del sistema
        // });
    }
}