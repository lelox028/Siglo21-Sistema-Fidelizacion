package com.example.sistemafidelizacion.Vista;

import javax.swing.*;
import java.awt.*;

public class FormularioCliente extends JPanel {

    private PantallaPrincipal pantallaPrincipal;

    public FormularioCliente(PantallaPrincipal pantallaPrincipal) {
        this.pantallaPrincipal = pantallaPrincipal;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del formulario
        JLabel titulo = new JLabel("Gestión de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // panel central para dividir en dos partes: buscador y formulario
        JPanel panelBody = new JPanel(new BorderLayout(15, 0)); 

        // panel izquierdo para buscador y tabla de clientes
        JPanel panelIzquierdo = createPanelBuscador(); 
        // Panel derecho para detalles del cliente
        JPanel panelDerecho = createPanelFormularioCliente();

        panelBody.add(panelIzquierdo, BorderLayout.WEST);
        panelBody.add(panelDerecho);

        add(panelBody, BorderLayout.CENTER);

        // Botón para regresar al menú principal
        JButton btnVolver = new JButton("← Volver al Menú");
        btnVolver.addActionListener(e -> pantallaPrincipal.mostrarPantalla("MENU"));

        // panel sur para el boton de volver
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSur.add(btnVolver);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel createPanelBuscador() {
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5,5));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Buscador de Clientes"));
        panelIzquierdo.setPreferredSize(new Dimension(400, 0)); // ancho fijo para el panel izquierdo
        // agregar panel de buscador anclado a norte
        //agregar panel de tabla de clientes anclado a centro
        return panelIzquierdo;
    }

    private JPanel createPanelFormularioCliente(){
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Detalles del Cliente"));
        return panelFormulario;
    }
}