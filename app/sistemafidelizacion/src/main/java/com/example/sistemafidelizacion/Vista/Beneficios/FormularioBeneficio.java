package com.example.sistemafidelizacion.Vista.Beneficios;

import com.example.sistemafidelizacion.Control.GestorBeneficios;
import com.example.sistemafidelizacion.Modelo.Beneficio;
import com.example.sistemafidelizacion.Vista.PantallaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class FormularioBeneficio extends JPanel {
    private final GestorBeneficios gestor = new GestorBeneficios();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[] {"ID", "Nombre", "Descripción", "Activo"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextField txtBuscar = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextArea txtDescripcion = new JTextArea(5, 20);
    private final JCheckBox chkActivo = new JCheckBox("Activo");
    private Beneficio beneficioSeleccionado;

    public FormularioBeneficio(PantallaPrincipal pantallaPrincipal) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(new JLabel("Administración de Beneficios", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel catalogo = new JPanel(new BorderLayout(6, 6));
        catalogo.setBorder(BorderFactory.createTitledBorder("Catálogo"));
        JPanel accionesCatalogo = new JPanel(new BorderLayout(6, 6));
        accionesCatalogo.add(txtBuscar, BorderLayout.CENTER);
        JButton nuevo = new JButton("Nuevo beneficio");
        nuevo.addActionListener(e -> prepararNuevoBeneficio());
        accionesCatalogo.add(nuevo, BorderLayout.EAST);
        catalogo.add(accionesCatalogo, BorderLayout.NORTH);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filtrar() { sorter.setRowFilter(txtBuscar.getText().isBlank() ? null
                    : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(txtBuscar.getText()))); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        catalogo.add(new JScrollPane(tabla), BorderLayout.CENTER);
        tabla.getSelectionModel().addListSelectionListener(e -> seleccionar());

        JPanel detalle = new JPanel(new GridBagLayout());
        detalle.setBorder(BorderFactory.createTitledBorder("Detalle y edición"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0; c.gridy = 0; detalle.add(new JLabel("Nombre"), c);
        c.gridy++; detalle.add(txtNombre, c);
        c.gridy++; detalle.add(new JLabel("Descripción"), c);
        c.gridy++; c.weighty = 1; c.fill = GridBagConstraints.BOTH; detalle.add(new JScrollPane(txtDescripcion), c);
        c.gridy++; c.weighty = 0; c.fill = GridBagConstraints.HORIZONTAL; detalle.add(chkActivo, c);
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> guardar());
        c.gridy++; detalle.add(guardar, c);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, catalogo, detalle);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);
        JButton volver = new JButton("← Volver al Menú");
        volver.addActionListener(e -> pantallaPrincipal.mostrarPantalla("MENU"));
        add(volver, BorderLayout.SOUTH);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            modelo.setRowCount(0);
            List<Beneficio> beneficios = gestor.consultarTodosLosBeneficios();
            for (Beneficio b : beneficios) modelo.addRow(new Object[] {b.getIdBeneficio(), b.getNombre(),
                    b.getDescripcion(), b.isActivo() ? "SI" : "NO"});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        try {
            int id = (int) modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0);
            beneficioSeleccionado = gestor.consultarTodosLosBeneficios().stream()
                    .filter(b -> b.getIdBeneficio() == id).findFirst().orElse(null);
            if (beneficioSeleccionado != null) {
                txtNombre.setText(beneficioSeleccionado.getNombre());
                txtDescripcion.setText(beneficioSeleccionado.getDescripcion());
                chkActivo.setSelected(beneficioSeleccionado.isActivo());
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void guardar() {
        try {
            if (beneficioSeleccionado == null) {
                gestor.crearBeneficio(txtNombre.getText().trim(), txtDescripcion.getText().trim());
            } else {
                beneficioSeleccionado.setNombre(txtNombre.getText().trim());
                beneficioSeleccionado.setDescripcion(txtDescripcion.getText().trim());
                beneficioSeleccionado.setActivo(chkActivo.isSelected());
                gestor.modificarBeneficio(beneficioSeleccionado);
            }
            cargarDatos();
            prepararNuevoBeneficio();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void prepararNuevoBeneficio() {
        beneficioSeleccionado = null;
        tabla.clearSelection();
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
        txtNombre.requestFocusInWindow();
    }
}
