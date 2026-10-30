package com.example.sistemafidelizacion.Vista.Reportes;

import com.example.sistemafidelizacion.Control.GestorReportes;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PantallaReportes extends JPanel {
    private final GestorReportes gestor = new GestorReportes();
    private final JComboBox<String> comboMostrar = new JComboBox<>(
            new String[] {"Compras", "Clientes", "Beneficios"});
    private final JComboBox<String> comboAgrupar = new JComboBox<>();
    private final JSpinner spinnerDesde = crearSelectorFecha();
    private final JSpinner spinnerHasta = crearSelectorFecha();
    private final JCheckBox sinLimiteDesde = new JCheckBox("Sin límite");
    private final JCheckBox sinLimiteHasta = new JCheckBox("Sin límite");
    private final JSpinner spinnerMontoMin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10_000_000.0, 100.0));
    private final JSpinner spinnerMontoMax = new JSpinner(new SpinnerNumberModel(100_000.0, 0.0, 10_000_000.0, 100.0));
    private final JCheckBox sinMontoMin = new JCheckBox("Sin límite");
    private final JCheckBox sinMontoMax = new JCheckBox("Sin límite");
    private final JCheckBox soloBeneficiosActivos = new JCheckBox("Solo beneficios activos");
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea resumen = new JTextArea();

    public PantallaReportes(PantallaPrincipal pantallaPrincipal) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(new JLabel("Reportes e Históricos", SwingConstants.CENTER), BorderLayout.NORTH);
        comboMostrar.addActionListener(e -> actualizarAgrupaciones());
        sinLimiteDesde.setSelected(true);
        sinLimiteHasta.setSelected(true);
        sinMontoMin.setSelected(true);
        sinMontoMax.setSelected(true);
        actualizarAgrupaciones();

        JPanel filtros = crearPanelFiltros();
        JPanel resultados = new JPanel(new BorderLayout(8, 8));
        resultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        resumen.setEditable(false);
        resumen.setLineWrap(true);
        resumen.setWrapStyleWord(true);
        resultados.add(new JScrollPane(tabla), BorderLayout.CENTER);
        resultados.add(new JScrollPane(resumen), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filtros, resultados);
        split.setResizeWeight(0.30);
        add(split, BorderLayout.CENTER);
        JButton volver = new JButton("← Volver al Menú");
        volver.addActionListener(e -> pantallaPrincipal.mostrarPantalla("MENU"));
        add(volver, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Configuración y filtros"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int fila = 0;
        agregarFila(panel, c, fila++, "Mostrar:", comboMostrar);
        agregarFila(panel, c, fila++, "Agrupar por:", comboAgrupar);
        agregarFila(panel, c, fila++, "Desde:", crearControlFecha(spinnerDesde, sinLimiteDesde));
        agregarFila(panel, c, fila++, "Hasta:", crearControlFecha(spinnerHasta, sinLimiteHasta));
        agregarFila(panel, c, fila++, "Rango Min.:", crearControlMonto(spinnerMontoMin, sinMontoMin));
        agregarFila(panel, c, fila++, "Rango Max.:", crearControlMonto(spinnerMontoMax, sinMontoMax));
        c.gridx = 0; c.gridy = fila++; c.gridwidth = 2;
        panel.add(soloBeneficiosActivos, c);
        JButton generar = new JButton("Generar reporte");
        generar.addActionListener(e -> generarReporte());
        c.gridy = fila;
        panel.add(generar, c);
        return panel;
    }

    private void agregarFila(JPanel panel, GridBagConstraints c, int fila, String etiqueta, JComponent control) {
        c.gridy = fila; c.gridx = 0; c.gridwidth = 1;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        panel.add(control, c);
    }

    private JPanel crearControlFecha(JSpinner spinner, JCheckBox sinLimite) {
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.add(spinner, BorderLayout.CENTER);
        panel.add(sinLimite, BorderLayout.EAST);
        sinLimite.addActionListener(e -> spinner.setEnabled(!sinLimite.isSelected()));
        return panel;
    }

    private JPanel crearControlMonto(JSpinner spinner, JCheckBox sinLimite) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setColumns(7);
        }
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.add(spinner, BorderLayout.CENTER);
        panel.add(sinLimite, BorderLayout.EAST);
        sinLimite.addActionListener(e -> spinner.setEnabled(!sinLimite.isSelected()));
        return panel;
    }

    private void actualizarAgrupaciones() {
        comboAgrupar.removeAllItems();
        String reporte = (String) comboMostrar.getSelectedItem();
        if ("Compras".equals(reporte)) {
            comboAgrupar.addItem("Monto");
            comboAgrupar.addItem("Cantidad de beneficios");
            comboAgrupar.addItem("Horario");
        } else if ("Clientes".equals(reporte)) {
            comboAgrupar.addItem("Usaron beneficios");
            comboAgrupar.addItem("Nombre del cliente");
        } else {
            comboAgrupar.addItem("Activos vs Inactivos");
            comboAgrupar.addItem("Cantidad de usos");
        }
    }

    private void generarReporte() {
        try {
            LocalDate desde = sinLimiteDesde.isSelected() ? null : obtenerFecha(spinnerDesde);
            LocalDate hasta = sinLimiteHasta.isSelected() ? null : obtenerFecha(spinnerHasta);
            Double min = sinMontoMin.isSelected() ? null : ((Number) spinnerMontoMin.getValue()).doubleValue();
            Double max = sinMontoMax.isSelected() ? null : ((Number) spinnerMontoMax.getValue()).doubleValue();
            GestorReportes.Reporte reporte = gestor.generarReporte(
                    (String) comboMostrar.getSelectedItem(),
                    (String) comboAgrupar.getSelectedItem(),
                    desde, hasta, min, max, soloBeneficiosActivos.isSelected());
            modelo.setDataVector(reporte.getFilas().toArray(new Object[0][]), reporte.getColumnas());
            double total = reporte.getCompras().stream().mapToDouble(c -> c.getMonto()).sum();
            resumen.setText("Compras consideradas: " + reporte.getCompras().size()
                    + "\nMonto total: $" + String.format("%.2f", total)
                    + "\nAgrupación: " + comboAgrupar.getSelectedItem());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate obtenerFecha(JSpinner spinner) {
        Date fecha = (Date) spinner.getValue();
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private JSpinner crearSelectorFecha() {
        return new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
    }
}
