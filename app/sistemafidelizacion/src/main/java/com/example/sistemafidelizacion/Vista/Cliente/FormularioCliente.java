package com.example.sistemafidelizacion.Vista.Cliente;

import javax.swing.*;
import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;
import com.example.sistemafidelizacion.Control.GestorClientes;

import java.awt.*;
import java.time.format.DateTimeParseException;

public class FormularioCliente extends JPanel {

    private PantallaPrincipal pantallaPrincipal;
    private PanelTablaCliente panelIzquierdo;
    private PanelFormularioCliente panelDerecho;
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
        panelBody.setResizeWeight(0.35);

        // panel izquierdo para buscador y tabla de clientes
        panelIzquierdo = new PanelTablaCliente();
        // Panel derecho para detalles del cliente
        panelDerecho = new PanelFormularioCliente(this);

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

        panelIzquierdo.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarClienteSeleccionado();
            }
        });
        actualizarTabla();
    }

    private void cargarClienteSeleccionado() {
        int dni = panelIzquierdo.getDniSeleccionado();
        if (dni < 0) {
            return;
        }
        try {
            Cliente cliente = gestorClientes.buscarCliente(dni);
            if (cliente != null) {
                panelDerecho.cargarCliente(cliente);
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    public void guardarCliente() {
        try {
            Cliente cliente = panelDerecho.obtenerCliente();
            int dni = cliente.getDni();
            if (gestorClientes.buscarCliente(dni) == null) {
                gestorClientes.registrarCliente(dni, cliente.getNombreCompleto(), cliente.getTelefono(),
                        cliente.getEmail(), cliente.getFechaNacimiento());
            } else {
                gestorClientes.modificarDatosCliente(cliente);
            }
            panelDerecho.mostrarEstado("Cliente guardado correctamente.");
            actualizarTabla();
        } catch (NumberFormatException | DateTimeParseException ex) {
            panelDerecho.mostrarEstado("DNI o fecha inválidos.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    public void verDetalles() {
        int dni = panelIzquierdo.getDniSeleccionado();
        if (dni >= 0) {
            pantallaPrincipal.mostrarCuentaCliente(dni);
        }
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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