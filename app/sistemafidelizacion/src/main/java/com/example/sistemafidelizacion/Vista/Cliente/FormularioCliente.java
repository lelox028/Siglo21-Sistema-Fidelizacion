package com.example.sistemafidelizacion.Vista.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;
import com.example.sistemafidelizacion.Vista.Cliente.*;
import com.example.sistemafidelizacion.Control.GestorClientes;

import java.awt.*;

public class FormularioCliente extends JPanel {

    private PantallaPrincipal pantallaPrincipal;
    private PanelTablaCliente panelIzquierdo;
    private GestorClientes gestorClientes;

    public FormularioCliente(PantallaPrincipal pantallaPrincipal) {
        this.pantallaPrincipal = pantallaPrincipal;
        this.gestorClientes = new GestorClientes();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del formulario
        JLabel titulo = new JLabel("Gestión de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // panel central para dividir en dos partes: buscador y formulario
        JSplitPane panelBody = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panelBody.setResizeWeight(0.30); // divide el espacio equitativamente

        // panel izquierdo para buscador y tabla de clientes
        panelIzquierdo = new PanelTablaCliente();
        // Panel derecho para detalles del cliente
        JPanel panelDerecho = createPanelFormularioCliente();

        panelBody.add(panelIzquierdo);
        panelBody.add(panelDerecho);

        add(panelBody, BorderLayout.CENTER);

        // Botón para regresar al menú principal
        JButton btnVolver = new JButton("← Volver al Menú");
        btnVolver.addActionListener(e -> pantallaPrincipal.mostrarPantalla("MENU"));

        // panel sur para el boton de volver
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSur.add(btnVolver);
        add(panelSur, BorderLayout.SOUTH);

        actualizarTabla();
    }

    private JPanel createPanelFormularioCliente() {
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Detalles del Cliente"));
        return panelFormulario;
    }

    public void actualizarTabla() {
        try {
            panelIzquierdo.cargarDatosTabla(gestorClientes.listarClientes());
        } catch (Exception e) {
            // Agregar mensaje de error para el usuario
            JOptionPane.showMessageDialog(this, "Error al cargar los clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public GestorClientes getGestorClientes() {
        return gestorClientes;
    }

}