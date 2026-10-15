package com.example.sistemafidelizacion.Vista.Cliente;

import com.example.sistemafidelizacion.Control.GestorClientes;
import com.example.sistemafidelizacion.DAO.CompraDAO;
import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Modelo.Compra;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    private final JTable tablaCompras = new JTable(modeloHistorial);
    private final JSpinner selectorFecha = new JSpinner(new SpinnerDateModel());
    private final JPanel panelBeneficios = new JPanel();
    private final JTextField txtMontoCompra = crearCampoSoloLectura();
    private final JTextField txtFechaCompra = crearCampoSoloLectura();
    private List<Compra> comprasCliente = new ArrayList<>();
    private Compra compraSeleccionada;

    public PantallaCuentaCliente(PantallaPrincipal pantallaPrincipal) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        lblCliente.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblCliente, BorderLayout.NORTH);

        JPanel panelFicha = crearPanelFicha();
        JPanel contenedorFicha = new JPanel(new BorderLayout());
        contenedorFicha.setBorder(BorderFactory.createEmptyBorder(50, 12, 50, 12));
        contenedorFicha.add(panelFicha, BorderLayout.CENTER);

        tablaCompras.removeColumn(tablaCompras.getColumnModel().getColumn(3));
        tablaCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCompras.getSelectionModel().addListSelectionListener(e -> seleccionarCompra());
        JPanel panelHistorial = crearPanelHistorial();
        JPanel panelDetalleCompra = crearPanelDetalleCompra();
        JSplitPane splitHistorial = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, panelHistorial, panelDetalleCompra);
        splitHistorial.setResizeWeight(0.55);
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panelDerecho.add(splitHistorial, BorderLayout.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contenedorFicha, panelDerecho);
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
                comprasCliente = new ArrayList<>();
                modeloHistorial.setRowCount(0);
                limpiarDetalleCompra();
                return;
            }
            lblCliente.setText("Cliente: " + cliente.getNombreCompleto());
            txtNombre.setText(cliente.getNombreCompleto());
            txtDni.setText(String.valueOf(cliente.getDni()));
            txtEmail.setText(cliente.getEmail());
            txtTelefono.setText(cliente.getTelefono());
            txtFechaNacimiento.setText(String.valueOf(cliente.getFechaNacimiento()));
            comprasCliente = compraDAO.obtenerHistorialCliente(dni);
            mostrarCompras(comprasCliente);
            limpiarDetalleCompra();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPanelHistorial() {
            JPanel panel = new JPanel(new BorderLayout(6, 6));
            panel.setBorder(BorderFactory.createTitledBorder("Compras del cliente"));

            JPanel buscador = new JPanel(new BorderLayout(6, 6));
            buscador.add(new JLabel("Buscar por fecha:"), BorderLayout.WEST);
            selectorFecha.setEditor(new JSpinner.DateEditor(selectorFecha, "dd/MM/yyyy"));
            buscador.add(selectorFecha, BorderLayout.CENTER);
            JButton btnFiltrar = new JButton("Filtrar");
            btnFiltrar.addActionListener(e -> aplicarFiltroFecha());
            buscador.add(btnFiltrar, BorderLayout.EAST);
            JButton btnLimpiar = new JButton("Todas");
            btnLimpiar.addActionListener(e -> {
                mostrarCompras(comprasCliente);
            });
            buscador.add(btnLimpiar, BorderLayout.SOUTH);

            panel.add(buscador, BorderLayout.NORTH);
            panel.add(new JScrollPane(tablaCompras), BorderLayout.CENTER);
            return panel;
    }

    private JPanel crearPanelDetalleCompra() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Detalles de la compra"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 8, 6, 8);

            gbc.gridy = 0;
            panel.add(new JLabel("Monto final"), gbc);
            gbc.gridy = 1;
            panel.add(txtMontoCompra, gbc);
            gbc.gridy = 2;
            panel.add(new JLabel("Fecha y hora"), gbc);
            gbc.gridy = 3;
            panel.add(txtFechaCompra, gbc);
            gbc.gridy = 4;
            panel.add(new JLabel("Beneficios aplicados"), gbc);
            gbc.gridy = 5;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            panelBeneficios.setLayout(new BoxLayout(panelBeneficios, BoxLayout.Y_AXIS));
            panelBeneficios.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            panel.add(new JScrollPane(panelBeneficios), gbc);
            limpiarDetalleCompra();
            return panel;
    }

    private void aplicarFiltroFecha() {
            Date fechaSeleccionada = (Date) selectorFecha.getValue();
            LocalDate dia = fechaSeleccionada.toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            List<Compra> comprasFiltradas = comprasCliente.stream()
                    .filter(compra -> compra.getFechaHora().toLocalDate().equals(dia))
                    .toList();
            mostrarCompras(comprasFiltradas);
    }

    private void mostrarCompras(List<Compra> compras) {
            modeloHistorial.setRowCount(0);
            tablaCompras.clearSelection();
            limpiarDetalleCompra();
            for (Compra compra : compras) {
                modeloHistorial.addRow(new Object[] {
                        compra.getIdCompra(),
                        compra.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        String.format("$ %.2f", compra.getMonto()),
                        compra
                });
            }
    }

    private void seleccionarCompra() {
            int fila = tablaCompras.getSelectedRow();
            if (fila < 0) {
                return;
            }
            compraSeleccionada = (Compra) modeloHistorial.getValueAt(
                    tablaCompras.convertRowIndexToModel(fila), 3);
            mostrarDetalle(compraSeleccionada);
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
        txtMontoCompra.setText(String.format("$ %.2f", compra.getMonto()));
        txtFechaCompra.setText(compra.getFechaHora()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        panelBeneficios.removeAll();
        if (compra.getBeneficiosAplicados().isEmpty()) {
            panelBeneficios.add(new JLabel("No se aplicaron beneficios."));
        } else {
            compra.getBeneficiosAplicados().forEach(aplicado -> {
                JLabel beneficio = new JLabel("• " + aplicado.getBeneficio().getNombre());
                beneficio.setAlignmentX(Component.LEFT_ALIGNMENT);
                panelBeneficios.add(beneficio);
                panelBeneficios.add(Box.createVerticalStrut(6));
            });
        }
        panelBeneficios.revalidate();
        panelBeneficios.repaint();
    }

    private void limpiarDetalleCompra() {
        txtMontoCompra.setText("");
        txtFechaCompra.setText("");
        panelBeneficios.removeAll();
        panelBeneficios.add(new JLabel("Seleccione una compra."));
    }
}
