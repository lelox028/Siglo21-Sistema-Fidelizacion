package com.example.sistemafidelizacion.Vista.Cliente;

import com.example.sistemafidelizacion.Modelo.Cliente;

import javax.swing.*;
import java.awt.*;

public class PanelFormularioCliente extends JPanel {

    private final JTextField txtDni = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtFechaNacimiento = new JTextField();
    private final JLabel lblEstado = new JLabel(" ");

    public PanelFormularioCliente(Runnable accionGuardar, Runnable accionVerDetalles) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Detalles del Cliente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        agregarCampo(gbc, 0, "DNI", txtDni);
        agregarCampo(gbc, 1, "Nombre Completo", txtNombre);
        agregarCampo(gbc, 2, "Email", txtEmail);
        agregarCampo(gbc, 3, "Teléfono", txtTelefono);
        agregarCampo(gbc, 4, "Fecha de Nacimiento (AAAA-MM-DD)", txtFechaNacimiento);

        JButton btnGuardar = new JButton("Guardar Cliente");
        btnGuardar.addActionListener(e -> accionGuardar.run());
        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> accionVerDetalles.run());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnGuardar);
        acciones.add(btnDetalles);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(acciones, gbc);

        gbc.gridy = 6;
        add(lblEstado, gbc);
    }

    private void agregarCampo(GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        add(campo, gbc);
    }

    public Cliente obtenerCliente() {
        return new Cliente(
                Integer.parseInt(txtDni.getText().trim()),
                txtNombre.getText().trim(),
                txtTelefono.getText().trim(),
                txtEmail.getText().trim(),
                java.time.LocalDate.parse(txtFechaNacimiento.getText().trim()));
    }

    public void cargarCliente(Cliente cliente) {
        txtDni.setText(String.valueOf(cliente.getDni()));
        txtNombre.setText(cliente.getNombreCompleto());
        txtEmail.setText(cliente.getEmail());
        txtTelefono.setText(cliente.getTelefono());
        txtFechaNacimiento.setText(cliente.getFechaNacimiento().toString());
    }

    public void mostrarEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }
}
