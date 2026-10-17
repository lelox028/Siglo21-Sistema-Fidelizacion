package com.example.sistemafidelizacion.Vista.Reportes;

import com.example.sistemafidelizacion.Control.GestorReportes;
import com.example.sistemafidelizacion.Modelo.BeneficioAplicado;
import com.example.sistemafidelizacion.Modelo.Compra;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PantallaReportes extends JPanel {
    private final GestorReportes gestor = new GestorReportes();
    private final JComboBox<String> mostrar = new JComboBox<>(new String[] {"Compras", "Clientes", "Beneficios"});
    private final JComboBox<String> agruparPor = new JComboBox<>();
    private final JTextField desde = new JTextField();
    private final JTextField hasta = new JTextField();
    private final JTextField montoMinimo = new JTextField();
    private final JTextField montoMaximo = new JTextField();
    private final JCheckBox soloActivos = new JCheckBox("Solo compras con beneficios activos actualmente");
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public PantallaReportes(PantallaPrincipal pantallaPrincipal) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(new JLabel("Reportes de compras", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel opciones = new JPanel(new BorderLayout(8, 8));
        opciones.setBorder(BorderFactory.createTitledBorder("Filtros y opciones"));
        JPanel tipo = new JPanel(new GridLayout(0, 1, 4, 4));
        tipo.add(new JLabel("Mostrar"));
        tipo.add(mostrar);
        tipo.add(new JLabel("Agrupar por"));
        tipo.add(agruparPor);
        mostrar.addActionListener(e -> actualizarAgrupaciones());
        opciones.add(tipo, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new BorderLayout(6, 6));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        JPanel grilla = new JPanel(new GridLayout(2, 2, 6, 4));
        grilla.add(crearCampo("Desde (AAAA-MM-DD)", desde));
        grilla.add(crearCampo("Hasta (AAAA-MM-DD)", hasta));
        grilla.add(crearCampo("Monto mínimo", montoMinimo));
        grilla.add(crearCampo("Monto máximo", montoMaximo));
        filtros.add(grilla, BorderLayout.NORTH);
        filtros.add(soloActivos, BorderLayout.CENTER);
        JButton generar = new JButton("Generar reporte");
        generar.addActionListener(e -> generarReporte());
        filtros.add(generar, BorderLayout.SOUTH);
        opciones.add(filtros, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, opciones, new JScrollPane(tabla));
        split.setResizeWeight(0.30);
        add(split, BorderLayout.CENTER);
        JButton volver = new JButton("← Volver al Menú");
        volver.addActionListener(e -> pantallaPrincipal.mostrarPantalla("MENU"));
        add(volver, BorderLayout.SOUTH);
        actualizarAgrupaciones();
        mostrarVacio();
    }

    private JPanel crearCampo(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel(new BorderLayout(2, 2));
        panel.add(new JLabel(etiqueta), BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarAgrupaciones() {
        String seleccion = (String) mostrar.getSelectedItem();
        agruparPor.removeAllItems();
        if ("Compras".equals(seleccion)) {
            agruparPor.addItem("Cantidad de beneficios usados");
            agruparPor.addItem("Monto");
        } else if ("Clientes".equals(seleccion)) {
            agruparPor.addItem("Compras");
        } else {
            agruparPor.addItem("Activos e inactivos");
        }
    }

    private void generarReporte() {
        try {
            List<Compra> compras = gestor.generarReporte(
                    leerFecha(desde, "Desde"), leerFecha(hasta, "Hasta"),
                    leerMonto(montoMinimo, "Monto mínimo"), leerMonto(montoMaximo, "Monto máximo"),
                    soloActivos.isSelected());
            String tipo = (String) mostrar.getSelectedItem();
            String agrupacion = (String) agruparPor.getSelectedItem();
            if ("Clientes".equals(tipo)) {
                mostrarClientes(compras);
            } else if ("Beneficios".equals(tipo)) {
                mostrarBeneficios(compras);
            } else if ("Monto".equals(agrupacion)) {
                mostrarPorMonto(compras);
            } else {
                mostrarPorCantidadDeBeneficios(compras);
            }
        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Revisá el formato de fechas y montos.",
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate leerFecha(JTextField campo, String nombre) {
        return campo.getText().isBlank() ? null : LocalDate.parse(campo.getText().trim());
    }

    private Double leerMonto(JTextField campo, String nombre) {
        return campo.getText().isBlank() ? null : Double.valueOf(campo.getText().trim().replace(',', '.'));
    }

    private void mostrarPorCantidadDeBeneficios(List<Compra> compras) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (Compra compra : compras) {
            int cantidad = compra.getBeneficiosAplicados().size();
            String grupo = cantidad >= 3 ? "3 o más" : String.valueOf(cantidad);
            cantidades.merge(grupo, 1, Integer::sum);
        }
        cargarColumnas(new String[] {"Beneficios usados", "Compras"});
        cantidades.forEach((grupo, cantidad) -> modelo.addRow(new Object[] {grupo, cantidad}));
    }

    private void mostrarPorMonto(List<Compra> compras) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (Compra compra : compras) {
            double monto = compra.getMonto();
            String grupo = monto < 50 ? "Menos de $50"
                    : monto < 100 ? "$50 a $99,99"
                    : monto < 250 ? "$100 a $249,99" : "$250 o más";
            cantidades.merge(grupo, 1, Integer::sum);
        }
        cargarColumnas(new String[] {"Segmento de monto", "Compras"});
        cantidades.forEach((grupo, cantidad) -> modelo.addRow(new Object[] {grupo, cantidad}));
    }

    private void mostrarClientes(List<Compra> compras) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (Compra compra : compras) {
            String cliente = compra.getCliente().getNombreCompleto() + " (DNI "
                    + compra.getCliente().getDni() + ")";
            cantidades.merge(cliente, 1, Integer::sum);
        }
        cargarColumnas(new String[] {"Cliente", "Compras"});
        cantidades.forEach((cliente, cantidad) -> modelo.addRow(new Object[] {cliente, cantidad}));
    }

    private void mostrarBeneficios(List<Compra> compras) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (Compra compra : compras) {
            for (BeneficioAplicado aplicado : compra.getBeneficiosAplicados()) {
                String estado = aplicado.getBeneficio().isActivo() ? "Activos" : "Inactivos";
                cantidades.merge(estado, 1, Integer::sum);
            }
        }
        cargarColumnas(new String[] {"Estado del beneficio", "Beneficios usados"});
        cantidades.forEach((estado, cantidad) -> modelo.addRow(new Object[] {estado, cantidad}));
    }

    private void cargarColumnas(Object[] columnas) {
        modelo.setColumnIdentifiers(columnas);
        modelo.setRowCount(0);
    }

    private void mostrarVacio() {
        cargarColumnas(new Object[] {"Resultado", "Valor"});
        modelo.addRow(new Object[] {"Generá un reporte para ver resultados", ""});
    }
}
