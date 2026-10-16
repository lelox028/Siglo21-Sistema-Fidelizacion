package com.example.sistemafidelizacion.Vista.Compra;

import com.example.sistemafidelizacion.Control.GestorBeneficios;
import com.example.sistemafidelizacion.Control.GestorClientes;
import com.example.sistemafidelizacion.Control.GestorCompras;
import com.example.sistemafidelizacion.Modelo.Beneficio;
import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormularioCompra extends JPanel {
    private final GestorClientes gestorClientes = new GestorClientes();
    private final GestorCompras gestorCompras = new GestorCompras();
    private final GestorBeneficios gestorBeneficios = new GestorBeneficios();
    private final JTextField txtDni = new JTextField();
    private final JTextField txtMonto = new JTextField();
    private final JSpinner selectorFecha = new JSpinner(new SpinnerDateModel());
    private final JSpinner selectorHora = new JSpinner(new SpinnerDateModel());
    private final JTextField txtNombreCliente = crearCampoSoloLectura();
    private final JTextField txtDniCliente = crearCampoSoloLectura();
    private final JTextField txtEmailCliente = crearCampoSoloLectura();
    private final JTextField txtTelefonoCliente = crearCampoSoloLectura();
    private final JPanel beneficios = new JPanel();
    private final Map<JCheckBox, Beneficio> beneficiosDisponibles = new LinkedHashMap<>();

    public FormularioCompra(PantallaPrincipal pantallaPrincipal) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(new JLabel("Registrar Compra", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel izquierda = new JPanel(new GridBagLayout());
        izquierda.setBorder(BorderFactory.createTitledBorder("Cliente"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5); c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        c.gridx = 0; c.gridy = 0; izquierda.add(new JLabel("Buscar por DNI"), c);
        c.gridy++; izquierda.add(txtDni, c);
        JButton buscar = new JButton("Buscar cliente");
        buscar.addActionListener(e -> cargarCliente());
        c.gridy++; izquierda.add(buscar, c);
        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        izquierda.add(crearFichaCliente(), c);

        JPanel derecha = new JPanel(new GridBagLayout());
        derecha.setBorder(BorderFactory.createTitledBorder("Compra y beneficios aplicables"));
        GridBagConstraints d = new GridBagConstraints();
        d.insets = new Insets(5, 5, 5, 5); d.fill = GridBagConstraints.HORIZONTAL; d.weightx = 1;
        d.gridx = 0; d.gridy = 0; derecha.add(new JLabel("Monto ($)"), d);
        d.gridy++; derecha.add(txtMonto, d);
        d.gridy++; derecha.add(new JLabel("Fecha de la compra"), d);
        d.gridy++; derecha.add(crearSelectorFecha(), d);
        d.gridy++; derecha.add(new JLabel("Hora de la compra"), d);
        d.gridy++; derecha.add(crearSelectorHora(), d);
        beneficios.setLayout(new BoxLayout(beneficios, BoxLayout.Y_AXIS));
        beneficios.setBorder(BorderFactory.createTitledBorder("Beneficios aplicables"));
        d.gridy++; d.weighty = 1; d.fill = GridBagConstraints.BOTH; derecha.add(new JScrollPane(beneficios), d);
        JButton registrar = new JButton("Registrar compra");
        registrar.addActionListener(e -> registrarCompra());
        d.gridy++; d.weighty = 0; d.fill = GridBagConstraints.HORIZONTAL; derecha.add(registrar, d);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        split.setResizeWeight(0.4);
        add(split, BorderLayout.CENTER);
        JButton volver = new JButton("← Volver al Menú");
        volver.addActionListener(e -> pantallaPrincipal.mostrarPantalla("MENU"));
        add(volver, BorderLayout.SOUTH);
    }

    private void cargarCliente() {
        try {
            Cliente cliente = gestorClientes.buscarCliente(Integer.parseInt(txtDni.getText().trim()));
            if (cliente == null) {
                limpiarFichaCliente();
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            txtNombreCliente.setText(cliente.getNombreCompleto());
            txtDniCliente.setText(String.valueOf(cliente.getDni()));
            txtEmailCliente.setText(cliente.getEmail());
            txtTelefonoCliente.setText(cliente.getTelefono());
            beneficios.removeAll();
            beneficiosDisponibles.clear();
            List<Beneficio> lista = gestorBeneficios.consultarBeneficiosActivos();
            for (Beneficio beneficio : lista) {
                boolean beneficioCumpleanos = "Descuento Cumpleaños".equals(beneficio.getNombre());
                boolean beneficioYaUsado = beneficioCumpleanos
                        && gestorCompras.clienteUsoBeneficioCumpleanos(cliente.getDni());
                String nombre = beneficioYaUsado
                        ? beneficio.getNombre() + " (beneficio ya usado)"
                        : beneficio.getNombre();
                JCheckBox opcion = new JCheckBox(nombre);
                opcion.setEnabled(!beneficioYaUsado);
                beneficiosDisponibles.put(opcion, beneficio);
                beneficios.add(opcion);
            }
            beneficios.revalidate(); beneficios.repaint();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private JPanel crearFichaCliente() {
        JPanel ficha = new JPanel(new GridBagLayout());
        ficha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Datos del cliente"),
                BorderFactory.createEmptyBorder(50, 10, 50, 10)));

        agregarParCampo(ficha, 0, "Nombre Completo", txtNombreCliente);
        agregarParCampo(ficha, 1, "DNI", txtDniCliente);
        agregarParCampo(ficha, 2, "Email", txtEmailCliente);
        agregarParCampo(ficha, 3, "Teléfono", txtTelefonoCliente);
        return ficha;
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

    private void limpiarFichaCliente() {
        txtNombreCliente.setText("");
        txtDniCliente.setText("");
        txtEmailCliente.setText("");
        txtTelefonoCliente.setText("");
    }

    private void registrarCompra() {
        try {
            List<Beneficio> beneficiosSeleccionados = new ArrayList<>();
            for (Map.Entry<JCheckBox, Beneficio> entrada : beneficiosDisponibles.entrySet()) {
                if (entrada.getKey().isSelected()) {
                    beneficiosSeleccionados.add(entrada.getValue());
                }
            }
            gestorCompras.registrarCompra(Integer.parseInt(txtDni.getText().trim()),
                    Double.parseDouble(txtMonto.getText().trim()), obtenerFechaHora(),
                    beneficiosSeleccionados);
            JOptionPane.showMessageDialog(this, "Compra registrada correctamente.");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private JSpinner crearSelectorFecha() {
        selectorFecha.setValue(new Date());
        selectorFecha.setEditor(new JSpinner.DateEditor(selectorFecha, "dd/MM/yyyy"));
        return selectorFecha;
    }

    private JSpinner crearSelectorHora() {
        selectorHora.setValue(new Date());
        selectorHora.setEditor(new JSpinner.DateEditor(selectorHora, "HH:mm"));
        return selectorHora;
    }

    private LocalDateTime obtenerFechaHora() {
        Date fecha = (Date) selectorFecha.getValue();
        Date hora = (Date) selectorHora.getValue();
        LocalDateTime fechaSeleccionada = fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay();
        LocalDateTime horaSeleccionada = hora.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0)
                .withNano(0)
                .atDate(fechaSeleccionada.toLocalDate());
        return horaSeleccionada;
    }
}
