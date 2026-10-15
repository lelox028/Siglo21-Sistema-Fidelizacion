package com.example.sistemafidelizacion.Vista.Cliente;

import com.example.sistemafidelizacion.Modelo.Cliente;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PanelTablaCliente extends JPanel {

    private DefaultTableModel modeloTablaClientes;
    private JTable tablaClientes;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;

    public PanelTablaCliente() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Buscador de Clientes"));

        // agregar panel de barra de busqueda al panel izquierdo anclado al norte
        JPanel panelBarraBusqueda = createBarraBusqueda();
        add(panelBarraBusqueda, BorderLayout.NORTH);

        // agregar panel de tabla de clientes al panel izquierdo anclado al centro
        JPanel panelTablaClientes = createPanelTablaClientes();
        add(panelTablaClientes, BorderLayout.CENTER);

    }

    private JPanel createBarraBusqueda() {
        JPanel panelBarraBusqueda = new JPanel(new BorderLayout(5, 5));
        // centrar el contenido del panel de barra de busqueda dejando espacio a los
        // lados
        panelBarraBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(0, 30));

        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBuscar.selectAll();
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
            }
        });

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
        
        // agregar componentes al panel de barra de busqueda
        panelBarraBusqueda.add(lblBuscar, BorderLayout.NORTH);
        panelBarraBusqueda.add(txtBuscar, BorderLayout.CENTER);
        return panelBarraBusqueda;
    }

    private JPanel createPanelTablaClientes() {
        JPanel panelTablaClientes = new JPanel(new BorderLayout(5, 5));
        panelTablaClientes.setPreferredSize(new Dimension(170,0));
        panelTablaClientes.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // declaracion de la tabla y su modelo de datos
        String[] columnas = { "DNI", "Nombre Completo", "Email" };

        modeloTablaClientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que el usuario edite las celdas directamente en la tabla
            }
        };

        tablaClientes = new JTable(modeloTablaClientes);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(modeloTablaClientes);
        tablaClientes.setRowSorter(sorter);

        panelTablaClientes.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        return panelTablaClientes;
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
        }
    }

    public void cargarDatosTabla(List<Cliente> listaClientes) {
        // Limpiar filas existentes para evitar duplicados
        modeloTablaClientes.setRowCount(0);

        // Iterar sobre los datos y agregar cada fila
        for (Cliente c : listaClientes) {
            Object[] fila = new Object[] {
                    c.getDni(),
                    c.getNombreCompleto(),
                    c.getEmail()
            };
            modeloTablaClientes.addRow(fila);
        }
    }

    public int getDniSeleccionado() {
        int filaVista = tablaClientes.getSelectedRow();
        if (filaVista != -1) {
            int filaModelo = tablaClientes.convertRowIndexToModel(filaVista);
            return (int) modeloTablaClientes.getValueAt(filaModelo, 0);
        }
        return -1;
    }

    public JTable getTable() {
        return tablaClientes;
    }
}