package com.example.sistemafidelizacion.Vista;

import javax.swing.*;
import java.awt.*;

public class PantallaPrincipal extends JFrame {

    // Atributos para controlar el CardLayout y los paneles
    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;

    public PantallaPrincipal() {
        setTitle("Sistema de Fidelización");
        setSize(1280, 1000); // todavia no decido que tamaño es mejor
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Inicializar el CardLayout y el panel contenedor
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);

        // 2. Crear el panel del Menú 
        JPanel panelMenu = crearPanelMenu();

        // 3. Crear las instancias de los paneles secundarios (deben extender de JPanel)
        FormularioCliente panelClientes = new FormularioCliente(this);
        // FormularioBeneficio panelBeneficios = new FormularioBeneficio(this);
        // FormularioCompra panelCompra = new FormularioCompra(this);
        // PantallaReportes panelReportes = new PantallaReportes(this);

        // 4. Agregar los paneles al contenedor asignándoles un nombre clave (String)
        contenedorPrincipal.add(panelMenu, "MENU");
        contenedorPrincipal.add(panelClientes, "CLIENTES");
        // contenedorPrincipal.add(panelBeneficios, "BENEFICIOS");
        // contenedorPrincipal.add(panelCompra, "COMPRAS");
        // contenedorPrincipal.add(panelReportes, "REPORTES");

        // 5. Agregar el contenedor al JFrame
        add(contenedorPrincipal);
        
        // Mostrar el menú de entrada por defecto
        mostrarPantalla("MENU");
    }

    /**
     * Construye el panel visual del Menú Principal
     */
    private JPanel crearPanelMenu() {
        Box mainPanel = new Box(BoxLayout.Y_AXIS);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de título
        JPanel panelTitulo = new JPanel();
        JLabel titulo = new JLabel("Sistema de Fidelización", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelTitulo.add(titulo);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 15, 15));

        //  crear botones 
        JButton btnPantallaClientes = new JButton("Clientes");
        JButton btnPantallaBeneficios = new JButton("Beneficios");
        JButton btnPantallaRegistrarCompra = new JButton("Registrar Compra");
        JButton btnPantallaReportes = new JButton("Reportes");

        // Eventos para cambiar la "tarjeta" o pantalla visible
        btnPantallaClientes.addActionListener(e -> mostrarPantalla("CLIENTES"));
        btnPantallaBeneficios.addActionListener(e -> mostrarPantalla("BENEFICIOS"));
        btnPantallaRegistrarCompra.addActionListener(e -> mostrarPantalla("COMPRAS"));
        btnPantallaReportes.addActionListener(e -> mostrarPantalla("REPORTES"));

        // agregar botones al panel de botones
        panelBotones.add(btnPantallaClientes);
        panelBotones.add(btnPantallaBeneficios);
        panelBotones.add(btnPantallaRegistrarCompra);
        panelBotones.add(btnPantallaReportes);

        // Agregar los subpaneles al panel principal
        mainPanel.add(panelTitulo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciado
        mainPanel.add(panelBotones);

        // Envolvemos el Box en un JPanel para retener la estructura
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(mainPanel, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Método público para cambiar de vista desde cualquier lugar
     */
    public void mostrarPantalla(String nombrePantalla) {
        cardLayout.show(contenedorPrincipal, nombrePantalla);
    }
}