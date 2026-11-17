package vista;

import algoritmos.AlgoritmosRecursivos;
import estructuras.GrafoDirigido;
import estructuras.ListaEnlazada;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.*;
import servicio.GestorMonedero;

public class VentanaMonedero extends JFrame {

    private final GestorMonedero gestor;

    // Paleta de colores limpia (estilo menú lateral y tarjetas claras)
    private final Color verdePrincipal  = new Color(0, 155, 58);
    private final Color verdeOscuro     = new Color(15, 82, 35);
    private final Color naranjaAcento   = new Color(243, 112, 33);
    private final Color azulAcento      = new Color(41, 128, 185);
    private final Color fondoGrisSuave  = new Color(245, 247, 248);
    private final Color blanco          = Color.WHITE;
    private final Color bordeSuave      = new Color(218, 226, 222);
    private final Color textoSecundario = new Color(90, 105, 95);

    // Selector de cliente y alerta superior
    private JComboBox<String> comboClientes;
    private JLabel lblNotificacion;

    // Contenedor central que cambia según el botón del menú izquierdo
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private JButton botonMenuSeleccionado;

    // Etiquetas de las tarjetas de Inicio
    private JLabel lblValorSaldoPrincipal;
    private JLabel lblValorSaldoTotal;
    private JLabel lblValorPuntos;
    private JLabel lblValorRango;

    // Modelos de tablas
    private DefaultTableModel modeloHistorial;
    private DefaultTableModel modeloRanking;
    private DefaultTableModel modeloSubmonederos;
    private DefaultTableModel modeloProgramadas;
    private JTextArea areaReporteGastos;

    public VentanaMonedero(GestorMonedero gestor) {
        this.gestor = gestor;

        setTitle("Simulador de Monedero Virtual con Sistema de Puntos");
        setSize(1180, 740);
        setMinimumSize(new Dimension(1000, 660));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondoGrisSuave);

        // 1. ENCABEZADO SUPERIOR
        add(crearEncabezado(), BorderLayout.NORTH);

        // 2. MENÚ LATERAL IZQUIERDO
        add(crearMenuLateral(), BorderLayout.WEST);

        // 3. CONTENIDO CENTRAL POR SECCIONES
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setBackground(fondoGrisSuave);

        panelContenedor.add(crearVistaInicio(), "INICIO");
        panelContenedor.add(crearVistaTransacciones(), "TRANSACCIONES");
        panelContenedor.add(crearVistaPuntosYRangos(), "PUNTOS");
        panelContenedor.add(crearVistaMultimonedero(), "SUBMONEDEROS");
        panelContenedor.add(crearVistaProgramadasYGastos(), "PROGRAMADAS");

        add(panelContenedor, BorderLayout.CENTER);

        actualizarDatosEnPantalla();
    }

    // =========================================================================
    // ENCABEZADO Y MENÚ LATERAL (Mismo estilo de tu proyecto anterior)
    // =========================================================================
    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(20, 0));
        encabezado.setBackground(blanco);
        encabezado.setPreferredSize(new Dimension(1180, 76));
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, bordeSuave),
                new EmptyBorder(10, 24, 10, 24)
        ));

        JPanel identidad = new JPanel();
        identidad.setOpaque(false);
        identidad.setLayout(new BoxLayout(identidad, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Monedero virtual");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(verdeOscuro);

        JLabel subtitulo = new JLabel("Administra tu dinero y tus puntos");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(textoSecundario);

        identidad.add(titulo);
        identidad.add(Box.createVerticalStrut(3));
        identidad.add(subtitulo);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 7));
        panelDerecho.setOpaque(false);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(new Font("Arial", Font.BOLD, 13));
        lblCliente.setForeground(verdeOscuro);

        comboClientes = new JComboBox<>();
        comboClientes.setFont(new Font("Arial", Font.PLAIN, 13));
        comboClientes.setPreferredSize(new Dimension(210, 34));
        recargarComboClientes();
        comboClientes.addActionListener(e -> actualizarDatosEnPantalla());

        JButton btnNuevoCliente = crearBotonAccion("+ Nuevo cliente", verdePrincipal);
        btnNuevoCliente.setPreferredSize(new Dimension(145, 36));
        btnNuevoCliente.addActionListener(e -> dialogoNuevoCliente());

        panelDerecho.add(lblCliente);
        panelDerecho.add(comboClientes);
        panelDerecho.add(btnNuevoCliente);

        encabezado.add(identidad, BorderLayout.WEST);
        encabezado.add(panelDerecho, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel(new BorderLayout(0, 18));
        menu.setBackground(verdeOscuro);
        menu.setPreferredSize(new Dimension(220, 0));
        menu.setBorder(new EmptyBorder(22, 14, 18, 14));

        JPanel encabezadoMenu = new JPanel();
        encabezadoMenu.setOpaque(false);
        encabezadoMenu.setLayout(new BoxLayout(encabezadoMenu, BoxLayout.Y_AXIS));

        JLabel tituloMenu = new JLabel("MENÚ PRINCIPAL");
        tituloMenu.setFont(new Font("Arial", Font.BOLD, 12));
        tituloMenu.setForeground(new Color(220, 238, 225));

        JLabel ayudaMenu = new JLabel("Secciones del monedero");
        ayudaMenu.setFont(new Font("Arial", Font.PLAIN, 11));
        ayudaMenu.setForeground(new Color(190, 215, 197));

        encabezadoMenu.add(tituloMenu);
        encabezadoMenu.add(Box.createVerticalStrut(5));
        encabezadoMenu.add(ayudaMenu);

        JPanel opciones = new JPanel();
        opciones.setOpaque(false);
        opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));

        agregarBotonMenu(opciones, "Inicio", "INICIO");
        agregarBotonMenu(opciones, "Transacciones", "TRANSACCIONES");
        agregarBotonMenu(opciones, "Puntos y rangos", "PUNTOS");
        agregarBotonMenu(opciones, "Mis monederos", "SUBMONEDEROS");
        agregarBotonMenu(opciones, "Programaciones y gastos", "PROGRAMADAS");

        menu.add(encabezadoMenu, BorderLayout.NORTH);
        menu.add(opciones, BorderLayout.CENTER);
        return menu;
    }

    private void agregarBotonMenu(JPanel menu, String texto, String claveVista) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setForeground(blanco);
        boton.setBackground(verdeOscuro);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(12, 14, 12, 8));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (botonMenuSeleccionado == null) {
            botonMenuSeleccionado = boton;
            boton.setBackground(verdePrincipal);
        }

        boton.addActionListener(e -> {
            if (botonMenuSeleccionado != null) {
                botonMenuSeleccionado.setBackground(verdeOscuro);
            }
            botonMenuSeleccionado = boton;
            boton.setBackground(verdePrincipal);
            actualizarDatosEnPantalla();
            cardLayout.show(panelContenedor, claveVista);
        });

        menu.add(boton);
        menu.add(Box.createVerticalStrut(8));
    }

    // =========================================================================
    // SECCIÓN 1: INICIO (Tarjetas grandes de Saldo, Puntos, Rango y Alertas)
    // =========================================================================
    private JPanel crearVistaInicio() {
        JPanel contenido = crearPanelVista(
                "Resumen de tu cuenta",
                "Consulta tus saldos, tus puntos acumulados y las notificaciones."
        );

        JPanel indicadores = new JPanel(new GridLayout(2, 2, 16, 16));
        indicadores.setOpaque(false);

        lblValorSaldoPrincipal = new JLabel("$0.00", SwingConstants.CENTER);
        lblValorSaldoTotal = new JLabel("$0.00", SwingConstants.CENTER);
        lblValorPuntos = new JLabel("0 pts", SwingConstants.CENTER);
        lblValorRango = new JLabel("BRONCE", SwingConstants.CENTER);

        indicadores.add(crearTarjeta("Saldo principal", lblValorSaldoPrincipal, verdePrincipal));
        indicadores.add(crearTarjeta("Saldo total", lblValorSaldoTotal, azulAcento));
        indicadores.add(crearTarjeta("Puntos acumulados", lblValorPuntos, naranjaAcento));
        indicadores.add(crearTarjeta("Rango actual", lblValorRango, verdeOscuro));

        JPanel panelNotificacionContenido = new JPanel(new BorderLayout(12, 0));
        panelNotificacionContenido.setOpaque(false);

        lblNotificacion = new JLabel("Bienvenido a tu monedero virtual.");
        lblNotificacion.setFont(new Font("Arial", Font.PLAIN, 13));
        lblNotificacion.setForeground(verdeOscuro);

        JButton btnSiguienteAlerta = crearBotonAccion("Siguiente aviso", verdePrincipal);
        btnSiguienteAlerta.setPreferredSize(new Dimension(150, 38));
        btnSiguienteAlerta.addActionListener(e -> rotarAlerta());

        panelNotificacionContenido.add(lblNotificacion, BorderLayout.CENTER);
        panelNotificacionContenido.add(btnSiguienteAlerta, BorderLayout.EAST);

        JPanel tarjetaNotificacion = crearPanelSeccion("Notificaciones", panelNotificacionContenido);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(indicadores, BorderLayout.CENTER);
        centro.add(tarjetaNotificacion, BorderLayout.SOUTH);

        contenido.add(centro, BorderLayout.CENTER);
        return contenido;
    }

    // =========================================================================
    // SECCIÓN 2: TRANSACCIONES BÁSICAS, HISTORIAL Y REVERSIÓN
    // =========================================================================
    private JPanel crearVistaTransacciones() {
        JPanel panel = crearPanelVista(
                "Transacciones",
                "Realiza movimientos de dinero y consulta el historial del cliente seleccionado."
        );

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);

        JButton btnDeposito = crearBotonAccion("Depositar", verdePrincipal);
        JButton btnRetiro = crearBotonAccion("Retirar", azulAcento);
        JButton btnTransferir = crearBotonAccion("Transferir", naranjaAcento);
        JButton btnRevertir = crearBotonAccion("Deshacer último", new Color(192, 57, 43));

        btnDeposito.addActionListener(e -> accionDeposito());
        btnRetiro.addActionListener(e -> accionRetiro());
        btnTransferir.addActionListener(e -> accionTransferencia());
        btnRevertir.addActionListener(e -> accionRevertir());

        botones.add(btnDeposito);
        botones.add(btnRetiro);
        botones.add(btnTransferir);
        botones.add(btnRevertir);

        JLabel notaPuntos = new JLabel("Los movimientos generan puntos automáticamente según el tipo y el monto.");
        notaPuntos.setFont(new Font("Arial", Font.PLAIN, 12));
        notaPuntos.setForeground(textoSecundario);

        JPanel zonaAcciones = new JPanel(new BorderLayout(0, 8));
        zonaAcciones.setOpaque(false);
        zonaAcciones.add(botones, BorderLayout.NORTH);
        zonaAcciones.add(notaPuntos, BorderLayout.SOUTH);

        String[] cols = {"Código", "Tipo", "Origen", "Destino", "Monedero", "Categoría", "Monto", "Comisión", "Puntos", "Estado"};
        modeloHistorial = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloHistorial);
        estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel historial = crearPanelSeccion("Historial de movimientos", scroll);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(crearPanelSeccion("Acciones disponibles", zonaAcciones), BorderLayout.NORTH);
        centro.add(historial, BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // SECCIÓN 3: SISTEMA DE PUNTOS, CANJE DE BENEFICIOS Y RANGOS
    // =========================================================================
    private JPanel crearVistaPuntosYRangos() {
        JPanel panel = crearPanelVista(
                "Puntos y rangos",
                "Canjea tus puntos por beneficios y consulta la clasificación de clientes."
        );

        JPanel panelCanjes = new JPanel(new GridLayout(1, 3, 12, 0));
        panelCanjes.setOpaque(false);

        JButton btn100 = crearBotonAccion("100 pts · 10% menos comisión", verdePrincipal);
        JButton btn500 = crearBotonAccion("500 pts · Retiros gratis", azulAcento);
        JButton btn1000 = crearBotonAccion("1.000 pts · Bono de $50", naranjaAcento);

        btn100.setPreferredSize(new Dimension(220, 48));
        btn500.setPreferredSize(new Dimension(220, 48));
        btn1000.setPreferredSize(new Dimension(220, 48));

        btn100.addActionListener(e -> ejecutarCanje(100));
        btn500.addActionListener(e -> ejecutarCanje(500));
        btn1000.addActionListener(e -> ejecutarCanje(1000));

        panelCanjes.add(btn100);
        panelCanjes.add(btn500);
        panelCanjes.add(btn1000);

        JLabel rangos = new JLabel("Bronce: 0–500   |   Plata: 501–1.000   |   Oro: 1.001–5.000   |   Platino: más de 5.000 puntos");
        rangos.setFont(new Font("Arial", Font.PLAIN, 12));
        rangos.setForeground(textoSecundario);

        JPanel contenidoCanjes = new JPanel(new BorderLayout(0, 10));
        contenidoCanjes.setOpaque(false);
        contenidoCanjes.add(panelCanjes, BorderLayout.NORTH);
        contenidoCanjes.add(rangos, BorderLayout.SOUTH);

        String[] cols = {"Puesto", "ID cliente", "Nombre", "Puntos", "Rango", "Beneficios canjeados"};
        modeloRanking = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloRanking);
        estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(crearPanelSeccion("Canjear beneficios", contenidoCanjes), BorderLayout.NORTH);
        centro.add(crearPanelSeccion("Clasificación de clientes", scroll), BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // SECCIÓN 4: MULTIMONEDERO (Ahorros, Principal, Gastos Diarios)
    // =========================================================================
    private JPanel crearVistaMultimonedero() {
        JPanel panel = crearPanelVista(
                "Mis monederos",
                "Organiza tu dinero en diferentes monederos, por ejemplo: principal, ahorros o gastos diarios."
        );

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        barra.setOpaque(false);

        JButton btnCrear = crearBotonAccion("+ Nuevo monedero", verdePrincipal);
        JButton btnPasar = crearBotonAccion("Mover dinero", naranjaAcento);

        btnCrear.addActionListener(e -> accionCrearSubmonedero());
        btnPasar.addActionListener(e -> accionTraspasoSubmonederos());

        barra.add(btnCrear);
        barra.add(btnPasar);

        String[] cols = {"Código", "Nombre", "Saldo disponible"};
        modeloSubmonederos = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloSubmonederos);
        estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(crearPanelSeccion("Administrar monederos", barra), BorderLayout.NORTH);
        centro.add(crearPanelSeccion("Mis saldos", scroll), BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // SECCIÓN 5: TRANSACCIONES PROGRAMADAS Y PATRONES DE GASTO
    // =========================================================================
    private JPanel crearVistaProgramadasYGastos() {
        JPanel panel = crearPanelVista(
                "Programaciones y gastos",
                "Consulta los movimientos futuros y revisa en qué categorías utilizas tu dinero."
        );

        JPanel panelProgContenido = new JPanel(new BorderLayout(0, 10));
        panelProgContenido.setOpaque(false);

        JPanel botonesProg = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botonesProg.setOpaque(false);
        JButton btnAgendar = crearBotonAccion("+ Programar movimiento", verdePrincipal);
        JButton btnEjecutar = crearBotonAccion("Ejecutar siguiente", naranjaAcento);

        btnAgendar.addActionListener(e -> accionProgramarTransaccion());
        btnEjecutar.addActionListener(e -> accionEjecutarProgramada());

        botonesProg.add(btnAgendar);
        botonesProg.add(btnEjecutar);

        String[] cols = {"ID", "Fecha", "Frecuencia", "Tipo", "Cliente", "Destino", "Categoría", "Monto"};
        modeloProgramadas = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaProg = new JTable(modeloProgramadas);
        estilizarTabla(tablaProg);
        JScrollPane scrollProg = new JScrollPane(tablaProg);
        scrollProg.setBorder(BorderFactory.createEmptyBorder());

        panelProgContenido.add(botonesProg, BorderLayout.NORTH);
        panelProgContenido.add(scrollProg, BorderLayout.CENTER);

        areaReporteGastos = new JTextArea();
        areaReporteGastos.setEditable(false);
        areaReporteGastos.setFont(new Font("Arial", Font.PLAIN, 13));
        areaReporteGastos.setForeground(new Color(55, 65, 60));
        areaReporteGastos.setBackground(blanco);
        areaReporteGastos.setBorder(new EmptyBorder(10, 10, 10, 10));
        areaReporteGastos.setLineWrap(true);
        areaReporteGastos.setWrapStyleWord(true);
        JScrollPane scrollReporte = new JScrollPane(areaReporteGastos);
        scrollReporte.setBorder(BorderFactory.createEmptyBorder());

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 14));
        centro.setOpaque(false);
        centro.add(crearPanelSeccion("Transacciones programadas", panelProgContenido));
        centro.add(crearPanelSeccion("Análisis de gastos e integridad", scrollReporte));

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // COMPONENTES VISUALES AUXILIARES
    // =========================================================================
    private JPanel crearPanelVista(String titulo, String descripcion) {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(fondoGrisSuave);
        panel.setBorder(new EmptyBorder(24, 26, 24, 26));
        panel.add(crearEncabezadoSeccion(titulo, descripcion), BorderLayout.NORTH);
        return panel;
    }

    private JPanel crearEncabezadoSeccion(String titulo, String descripcion) {
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 23));
        lblTitulo.setForeground(verdeOscuro);

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDescripcion.setForeground(textoSecundario);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(lblDescripcion);
        return encabezado;
    }

    private JPanel crearPanelSeccion(String titulo, JComponent contenido) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new BordeRedondeado(bordeSuave, 16, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(verdeOscuro);

        contenido.setBackground(blanco);
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(contenido, BorderLayout.CENTER);
        return tarjeta;
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setForeground(new Color(45, 55, 49));
        tabla.setBackground(blanco);
        tabla.setGridColor(new Color(235, 239, 236));
        tabla.setSelectionBackground(new Color(225, 241, 230));
        tabla.setSelectionForeground(verdeOscuro);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);

        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setForeground(verdeOscuro);
        tabla.getTableHeader().setBackground(new Color(239, 244, 240));
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    private JPanel crearTarjeta(String titulo, JLabel etiquetaValor, Color colorCifra) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new BordeRedondeado(bordeSuave, 18, 1),
                new EmptyBorder(20, 16, 20, 16)
        ));

        JLabel texto = new JLabel(titulo, SwingConstants.CENTER);
        texto.setFont(new Font("Arial", Font.BOLD, 14));
        texto.setForeground(textoSecundario);

        etiquetaValor.setFont(new Font("Arial", Font.BOLD, 29));
        etiquetaValor.setForeground(colorCifra);

        tarjeta.add(texto, BorderLayout.NORTH);
        tarjeta.add(etiquetaValor, BorderLayout.CENTER);
        return tarjeta;
    }

    private JButton crearBotonAccion(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(9, 14, 9, 14));
        btn.setPreferredSize(new Dimension(170, 40));
        return btn;
    }

    // =========================================================================
    // ACTUALIZACIÓN AUTOMÁTICA DE DATOS EN LA INTERFAZ
    // =========================================================================
    private String getIdClienteSeleccionado() {
        String item = (String) comboClientes.getSelectedItem();
        if (item == null) return null;
        return item.split(" - ")[0].trim();
    }

    private void recargarComboClientes() {
        String actual = getIdClienteSeleccionado();
        comboClientes.removeAllItems();
        for (Cliente c : gestor.obtenerTodosLosClientes()) {
            comboClientes.addItem(c.getIdCliente() + " - " + c.getNombre());
        }
        if (actual != null) {
            for (int i = 0; i < comboClientes.getItemCount(); i++) {
                if (comboClientes.getItemAt(i).startsWith(actual + " ")) {
                    comboClientes.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void actualizarDatosEnPantalla() {
        String id = getIdClienteSeleccionado();
        if (id == null) return;
        Cliente c = gestor.buscarCliente(id);

        // 1. Tarjetas de Inicio
        lblValorSaldoPrincipal.setText(String.format("$%,.2f", c.getSaldoPrincipal()));
        lblValorSaldoTotal.setText(String.format("$%,.2f", c.getSaldoTotalGlobal()));
        lblValorPuntos.setText(gestor.consultarPuntosHashO1(id) + " pts");
        lblValorRango.setText(c.getRango().name());

        String nota = c.getNotificaciones().siguienteRotacion();
        if (nota != null) {
            lblNotificacion.setText("Notificación: " + nota);
        }

        // 2. Tabla de Historial
        modeloHistorial.setRowCount(0);
        for (Transaccion t : c.getHistorialTransacciones()) {
            modeloHistorial.addRow(new Object[]{
                    t.getIdTransaccion(),
                    t.getTipo().getEtiqueta(),
                    t.getIdClienteOrigen(),
                    t.getIdClienteDestino() == null ? "-" : t.getIdClienteDestino(),
                    t.getNombreMonedero(),
                    t.getCategoriaGasto(),
                    String.format("$%,.2f", t.getMonto()),
                    String.format("$%,.2f", t.getComisionAplicada()),
                    "+" + t.getPuntosGenerados() + " pts",
                    t.isRevertida() ? "REVERTIDA" : "ACTIVA"
            });
        }

        // 3. Tabla de Rangos y Puntos
        modeloRanking.setRowCount(0);
        int puesto = 1;
        for (Cliente cli : gestor.obtenerRankingRangosAVL()) {
            String ben = (cli.isDescuentoTransferencia10Activo() ? "[-10% Comisión] " : "")
                    + (cli.isMesSinCargosRetiroActivo() ? "[Retiros Gratis]" : "");
            if (ben.isEmpty()) ben = "Ninguno canjeado aún";
            modeloRanking.addRow(new Object[]{
                    puesto++,
                    cli.getIdCliente(),
                    cli.getNombre(),
                    cli.getPuntosAcumulados() + " pts",
                    cli.getRango().getDescripcion(),
                    ben
            });
        }

        // 4. Tabla de Submonederos
        modeloSubmonederos.setRowCount(0);
        for (Monedero m : c.getListaMonederos()) {
            modeloSubmonederos.addRow(new Object[]{
                    m.getIdMonedero(),
                    m.getNombre(),
                    String.format("$%,.2f", m.getSaldo())
            });
        }

        // 5. Tabla de Transacciones Programadas
        modeloProgramadas.setRowCount(0);
        for (TransaccionProgramada tp : gestor.getColaProgramadas()) {
            modeloProgramadas.addRow(new Object[]{
                    tp.getIdProgramada(),
                    tp.getFechaEjecucion(),
                    tp.getFrecuencia(),
                    tp.getTipo().getEtiqueta(),
                    tp.getIdClienteOrigen(),
                    tp.getIdClienteDestino() == null ? "-" : tp.getIdClienteDestino(),
                    tp.getCategoriaGasto(),
                    String.format("$%,.2f", tp.getMonto())
            });
        }

        // 6. Resumen de Patrones de Gasto e Integridad
        StringBuilder sb = new StringBuilder();
        sb.append("=== PATRONES DE GASTO DE ").append(c.getNombre().toUpperCase()).append(" ===\n");
        ListaEnlazada<GrafoDirigido.Arista<String>> gastos = gestor.getGrafoPatronesGasto().obtenerAdyacentes(c.getNombre());
        if (gastos.estaVacia()) {
            sb.append(" • Aún no registra retiros ni transferencias por categoría.\n");
        } else {
            for (GrafoDirigido.Arista<String> a : gastos) {
                sb.append(" • ").append(a.getDestino()).append(": $")
                  .append(String.format("%,.2f", a.getPesoAcumulado()))
                  .append(" (").append(a.getCantidadOperaciones()).append(" operaciones)\n");
            }
        }

        Transaccion mayor = AlgoritmosRecursivos.transaccionMayorMonto(c.getHistorialTransacciones());
        boolean integra = gestor.auditarIntegridadCliente(id);
        sb.append("\n=== VERIFICACIÓN DE INTEGRIDAD (ALGORITMOS RECURSIVOS) ===\n");
        sb.append(" • Estado de integridad de las transacciones: ").append(integra ? "VÁLIDA ✔" : "CON ERRORES").append("\n");
        sb.append(" • Transacción de mayor valor: ").append(mayor != null ? mayor.toString() : "Ninguna").append("\n");

        areaReporteGastos.setText(sb.toString());
    }

    private void rotarAlerta() {
        String id = getIdClienteSeleccionado();
        if (id == null) return;
        String msg = gestor.buscarCliente(id).getNotificaciones().siguienteRotacion();
        if (msg != null) {
            lblNotificacion.setText("Notificación: " + msg);
        }
    }

    // =========================================================================
    // ACCIONES DE LOS BOTONES
    // =========================================================================
    private void dialogoNuevoCliente() {
        try {
            String id = JOptionPane.showInputDialog(this, "ID del nuevo cliente (ej. C05):");
            if (id == null || id.trim().isEmpty()) return;
            String nombre = JOptionPane.showInputDialog(this, "Nombre del cliente:");
            if (nombre == null || nombre.trim().isEmpty()) return;
            String saldo = JOptionPane.showInputDialog(this, "Saldo inicial ($):", "1000");
            if (saldo == null) return;

            gestor.registrarCliente(id.trim(), nombre.trim(), Double.parseDouble(saldo));
            recargarComboClientes();
            comboClientes.setSelectedIndex(comboClientes.getItemCount() - 1);
            actualizarDatosEnPantalla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionDeposito() {
        try {
            String id = getIdClienteSeleccionado();
            String[] bolsillos = {"Principal", "Ahorros", "Gastos Diarios"};
            String monedero = (String) JOptionPane.showInputDialog(this, "Seleccione el monedero:",
                    "Depositar", JOptionPane.QUESTION_MESSAGE, null, bolsillos, bolsillos[0]);
            if (monedero == null) return;
            String monto = JOptionPane.showInputDialog(this, "Monto a depositar ($):", "500");
            if (monto == null) return;

            Transaccion tx = gestor.realizarDeposito(id, monedero, Double.parseDouble(monto));
            actualizarDatosEnPantalla();
            JOptionPane.showMessageDialog(this, "¡Depósito exitoso!\nGanaste +" + tx.getPuntosGenerados() + " puntos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionRetiro() {
        try {
            String id = getIdClienteSeleccionado();
            String[] bolsillos = {"Principal", "Ahorros", "Gastos Diarios"};
            String monedero = (String) JOptionPane.showInputDialog(this, "Retirar desde el monedero:",
                    "Retirar", JOptionPane.QUESTION_MESSAGE, null, bolsillos, bolsillos[0]);
            if (monedero == null) return;
            String[] categorias = {"Alimentación", "Servicios", "Transporte", "Educación", "Ocio"};
            String cat = (String) JOptionPane.showInputDialog(this, "Categoría del gasto:",
                    "Categoría", JOptionPane.QUESTION_MESSAGE, null, categorias, categorias[0]);
            if (cat == null) return;
            String monto = JOptionPane.showInputDialog(this, "Monto a retirar ($):", "300");
            if (monto == null) return;

            Transaccion tx = gestor.realizarRetiro(id, monedero, cat, Double.parseDouble(monto));
            actualizarDatosEnPantalla();
            JOptionPane.showMessageDialog(this, "¡Retiro realizado!\nGanaste +" + tx.getPuntosGenerados() + " puntos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionTransferencia() {
        try {
            String idOrigen = getIdClienteSeleccionado();
            String idDestino = JOptionPane.showInputDialog(this, "ID del cliente destino (ej. C01, C02, C03, C04):", "C02");
            if (idDestino == null) return;
            String[] categorias = {"Pagos", "Familiar", "Arriendo y Servicios", "Comercio"};
            String cat = (String) JOptionPane.showInputDialog(this, "Categoría de la transferencia:",
                    "Transferir", JOptionPane.QUESTION_MESSAGE, null, categorias, categorias[0]);
            if (cat == null) return;
            String monto = JOptionPane.showInputDialog(this, "Monto a transferir ($):", "400");
            if (monto == null) return;

            Transaccion tx = gestor.realizarTransferencia(idOrigen, idDestino.trim(), cat, Double.parseDouble(monto));
            actualizarDatosEnPantalla();
            JOptionPane.showMessageDialog(this, "¡Transferencia enviada!\nGanaste +" + tx.getPuntosGenerados() + " puntos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionRevertir() {
        try {
            String id = getIdClienteSeleccionado();
            Transaccion rev = gestor.revertirUltimaTransaccion(id);
            actualizarDatosEnPantalla();
            JOptionPane.showMessageDialog(this, "Se deshizo la transacción " + rev.getIdTransaccion()
                    + "\nSe devolvió el saldo y se descontaron " + rev.getPuntosGenerados() + " puntos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void ejecutarCanje(int puntos) {
        try {
            String id = getIdClienteSeleccionado();
            String beneficio = gestor.canjearPuntos(id, puntos);
            actualizarDatosEnPantalla();
            JOptionPane.showMessageDialog(this, "¡Canje exitoso!\n" + beneficio);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Puntos insuficientes", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void accionCrearSubmonedero() {
        try {
            String id = getIdClienteSeleccionado();
            String nombre = JOptionPane.showInputDialog(this, "Nombre del nuevo submonedero (ej. Viajes):");
            if (nombre == null || nombre.trim().isEmpty()) return;
            String saldo = JOptionPane.showInputDialog(this, "Saldo inicial ($):", "0");
            if (saldo == null) return;

            gestor.buscarCliente(id).crearSubmonedero(nombre.trim(), Double.parseDouble(saldo));
            actualizarDatosEnPantalla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionTraspasoSubmonederos() {
        try {
            String id = getIdClienteSeleccionado();
            String origen = JOptionPane.showInputDialog(this, "Desde mi submonedero:", "Principal");
            if (origen == null) return;
            String destino = JOptionPane.showInputDialog(this, "Hacia mi submonedero (Ahorros, Gastos Diarios):", "Ahorros");
            if (destino == null) return;
            String monto = JOptionPane.showInputDialog(this, "Monto a pasar ($):", "200");
            if (monto == null) return;

            gestor.transferirEntreSubmonederos(id, origen.trim(), destino.trim(), Double.parseDouble(monto));
            actualizarDatosEnPantalla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionProgramarTransaccion() {
        try {
            String idOrigen = getIdClienteSeleccionado();
            String[] tipos = {"DEPOSITO", "RETIRO", "TRANSFERENCIA"};
            String tipoSel = (String) JOptionPane.showInputDialog(this, "Tipo:", "Programar",
                    JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
            if (tipoSel == null) return;

            TipoTransaccion tipo = TipoTransaccion.valueOf(tipoSel);
            String idDestino = null;
            if (tipo == TipoTransaccion.TRANSFERENCIA) {
                idDestino = JOptionPane.showInputDialog(this, "ID Cliente Destino:", "C02");
                if (idDestino == null) return;
            }
            String monto = JOptionPane.showInputDialog(this, "Monto ($):", "250");
            String fecha = JOptionPane.showInputDialog(this, "Fecha (AAAA-MM-DD):", LocalDate.now().plusDays(3).toString());

            gestor.programarTransaccion(tipo, idOrigen, idDestino, "Programada",
                    Double.parseDouble(monto), LocalDate.parse(fecha.trim()), "Mensual");
            actualizarDatosEnPantalla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEjecutarProgramada() {
        try {
            Transaccion tx = gestor.ejecutarSiguienteProgramada();
            actualizarDatosEnPantalla();
            JOptionPane.showMessageDialog(this, "Se ejecutó la transacción programada más próxima:\n" + tx);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================================================
    // BORDE REDONDEADO (El mismo de tu VentanaPrincipal anterior)
    // =========================================================================
    private static class BordeRedondeado extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int radio;
        private final int grosor;

        public BordeRedondeado(Color color, int radio, int grosor) {
            this.color = color;
            this.radio = radio;
            this.grosor = grosor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int ancho, int alto) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(grosor));
            g2.drawRoundRect(x + grosor, y + grosor, ancho - grosor * 2 - 1, alto - grosor * 2 - 1, radio, radio);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(12, 12, 12, 12);
        }
    }
}