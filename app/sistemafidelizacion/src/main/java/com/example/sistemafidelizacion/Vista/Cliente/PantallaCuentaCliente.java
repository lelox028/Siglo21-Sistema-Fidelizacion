package com.example.sistemafidelizacion.Vista.Cliente;

import com.example.sistemafidelizacion.Control.GestorClientes;
import com.example.sistemafidelizacion.DAO.CompraDAO;
import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Modelo.Compra;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PantallaCuentaCliente extends JPanel {
    private final GestorClientes gestorClientes = new GestorClientes();
    private final CompraDAO compraDAO = new CompraDAO();
    private final JLabel lblCliente = new JLabel("Cliente");
    private final JTextField txtNombre = crearCampoSoloLectura();
    private final JTextField txtDni = crearCampoSoloLectura();
    private final JTextField txtEmail = crearCampoSoloLectura();
    private final JTextField txtTelefono = crearCampoSoloLectura();
    private final JTextField txtFechaNacimiento = crearCampoSoloLectura();
    private final DefaultTableModel modeloHistorial = new DefaultTableModel(
            new Object[] {"Compra", "Fecha y hora", "Monto", "Datos"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JLabel lblDetalle = new JLabel("Seleccione una compra para ver sus detalles.");

    public PantallaCuentaCliente(PantallaPrincipal pantallaPrincipal) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        lblCliente.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblCliente, BorderLayout.NORTH);

        JPanel panelFicha = crearPanelFicha();
        JPanel contenedorFicha = new JPanel(new BorderLayout());
        contenedorFicha.setBorder(BorderFactory.createEmptyBorder(50, 12, 50, 12));
        contenedorFicha.add(panelFicha, BorderLayout.CENTER);

        JTable tabla = new JTable(modeloHistorial);
        tabla.removeColumn(tabla.getColumnModel().getColumn(3));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                Compra compra = (Compra) modeloHistorial.getValueAt(tabla.convertRowIndexToModel(tabla.getSelectedRow()), 3);
                mostrarDetalle(compra);
            }
        });
        JPanel historial = new JPanel(new BorderLayout(6, 6));
        historial.setBorder(BorderFactory.createTitledBorder("Historial de compras"));
        historial.add(new JScrollPane(tabla), BorderLayout.CENTER);
        historial.add(lblDetalle, BorderLayout.SOUTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contenedorFicha, historial);
        split.setResizeWeight(0.32);
        add(split, BorderLayout.CENTER);

        JButton volver = new JButton("← Volver");
        volver.addActionListener(e -> pantallaPrincipal.mostrarPantalla("CLIENTES"));
        add(volver, BorderLayout.SOUTH);
    }

    public void cargarCliente(int dni) {
        try {
            Cliente cliente = gestorClientes.buscarCliente(dni);
            if (cliente == null) {
                lblCliente.setText("Cliente no encontrado");
                limpiarFicha();
                modeloHistorial.setRowCount(0);
                return;
            }
            lblCliente.setText("Cliente: " + cliente.getNombreCompleto());
            txtNombre.setText(cliente.getNombreCompleto());
            txtDni.setText(String.valueOf(cliente.getDni()));
            txtEmail.setText(cliente.getEmail());
            txtTelefono.setText(cliente.getTelefono());
            txtFechaNacimiento.setText(String.valueOf(cliente.getFechaNacimiento()));
            modeloHistorial.setRowCount(0);
            List<Compra> compras = compraDAO.obtenerHistorialCliente(dni);
            for (Compra compra : compras) {
                modeloHistorial.addRow(new Object[] { compra.getIdCompra(),
                        compra.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        String.format("$ %.2f", compra.getMonto()), compra });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPanelFicha() {
        JPanel panelFicha = new JPanel(new GridBagLayout());
        panelFicha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Ficha del cliente"),
                BorderFactory.createEmptyBorder(50, 10, 50, 10)));

        agregarParCampo(panelFicha, 0, "Nombre Completo", txtNombre);
        agregarParCampo(panelFicha, 1, "DNI", txtDni);
        agregarParCampo(panelFicha, 2, "Email", txtEmail);
        agregarParCampo(panelFicha, 3, "Teléfono", txtTelefono);
        agregarParCampo(panelFicha, 4, "Fecha de Nacimiento", txtFechaNacimiento);

        return panelFicha;
    }

    private void agregarParCampo(JPanel panel, int fila, String nombreCampo, JTextField campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(crearParCampo(nombreCampo, campo), gbc);
    }

    private JPanel crearParCampo(String nombreCampo, JTextField campo) {
        JPanel parCampo = new JPanel();
        parCampo.setLayout(new BoxLayout(parCampo, BoxLayout.Y_AXIS));
        parCampo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel etiqueta = new JLabel(nombreCampo);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        parCampo.add(etiqueta);
        parCampo.add(Box.createVerticalStrut(4));
        parCampo.add(campo);
        return parCampo;
    }

    private JTextField crearCampoSoloLectura() {
        JTextField campo = new JTextField();
        campo.setEditable(false);
        campo.setPreferredSize(new Dimension(0, 30));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        campo.setBackground(UIManager.getColor("TextField.background"));
        return campo;
    }

    private void limpiarFicha() {
        txtNombre.setText("");
        txtDni.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        txtFechaNacimiento.setText("");
    }

    private void mostrarDetalle(Compra compra) {
        lblDetalle.setText(String.format("Compra #%d | Monto final: $ %.2f | Fecha: %s | Beneficios aplicados: %d",
                compra.getIdCompra(), compra.getMonto(), compra.getFechaHora(), compra.getBeneficiosAplicados().size()));
    }
}
