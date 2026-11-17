package servicio;

import algoritmos.AlgoritmosRecursivos;
import estructuras.ArbolABB;
import estructuras.ArbolAVL;
import estructuras.ColaPrioridad;
import estructuras.GrafoDirigido;
import estructuras.ListaEnlazada;
import estructuras.TablaHash;
import java.time.LocalDate;
import modelo.Cliente;
import modelo.Monedero;
import modelo.Transaccion;
import modelo.TransaccionProgramada;
import modelo.TipoTransaccion;

public class GestorMonedero {

    private final TablaHash<String, Cliente> clientesPorId;
    private final TablaHash<String, Integer> puntosPorClienteHash;
    private final ArbolABB<Cliente> arbolPuntosABB;
    private final ArbolAVL<Cliente> arbolRangosAVL;
    private final ColaPrioridad<TransaccionProgramada> colaProgramadas;

    private final GrafoDirigido<String> grafoTransferenciasClientes;
    private final GrafoDirigido<String> grafoPatronesGasto;
    private final GrafoDirigido<String> grafoMultimonedero;

    private int consecutivoTx = 100;
    private static final double UMBRAL_SALDO_BAJO = 50.0;

    public GestorMonedero() {
        this.clientesPorId = new TablaHash<>();
        this.puntosPorClienteHash = new TablaHash<>();
        this.arbolPuntosABB = new ArbolABB<>();
        this.arbolRangosAVL = new ArbolAVL<>();
        this.colaProgramadas = new ColaPrioridad<>();

        this.grafoTransferenciasClientes = new GrafoDirigido<>();
        this.grafoPatronesGasto = new GrafoDirigido<>();
        this.grafoMultimonedero = new GrafoDirigido<>();
    }

    private String generarIdTx(String prefijo) {
        consecutivoTx++;
        return prefijo + "-" + consecutivoTx;
    }

    public Cliente registrarCliente(String idCliente, String nombre, double saldoInicial) {
        if (clientesPorId.containsKey(idCliente)) {
            throw new IllegalArgumentException("Ya existe un cliente con ID: " + idCliente);
        }
        Cliente nuevo = new Cliente(idCliente, nombre, saldoInicial);
        clientesPorId.put(idCliente, nuevo);
        puntosPorClienteHash.put(idCliente, 0);
        arbolPuntosABB.insertar(nuevo);
        arbolRangosAVL.insertar(nuevo);
        return nuevo;
    }

    public Cliente buscarCliente(String idCliente) {
        Cliente c = clientesPorId.get(idCliente);
        if (c == null) {
            throw new IllegalArgumentException("Cliente no encontrado: " + idCliente);
        }
        return c;
    }

    public int consultarPuntosHashO1(String idCliente) {
        Integer pts = puntosPorClienteHash.get(idCliente);
        return (pts != null) ? pts : 0;
    }

    private void sincronizarPuntosEnEstructuras(Cliente cliente, int deltaPuntos, boolean esSuma) {
        // 1. Retirar del ABB y AVL antes de cambiar la clave de comparación (puntos)
        arbolPuntosABB.eliminar(cliente);
        arbolRangosAVL.eliminar(cliente);

        if (esSuma) {
            cliente.sumarPuntos(deltaPuntos);
        } else {
            cliente.restarPuntos(deltaPuntos);
        }

        puntosPorClienteHash.put(cliente.getIdCliente(), cliente.getPuntosAcumulados());
        arbolPuntosABB.insertar(cliente);
        arbolRangosAVL.insertar(cliente);
    }

    public Transaccion realizarDeposito(String idCliente, String nombreMonedero, double monto) {
        Cliente cliente = buscarCliente(idCliente);
        Monedero monedero = cliente.obtenerMonedero(nombreMonedero);
        monedero.acreditar(monto);

        int puntos = TipoTransaccion.DEPOSITO.calcularPuntos(monto);
        sincronizarPuntosEnEstructuras(cliente, puntos, true);

        Transaccion tx = new Transaccion(
                generarIdTx("DEP"), TipoTransaccion.DEPOSITO, idCliente, null,
                nombreMonedero, "Ingreso", monto, 0.0, puntos
        );
        cliente.getHistorialTransacciones().agregar(tx);
        cliente.getPilaReversion().push(tx);
        cliente.agregarNotificacion("Depósito exitoso de $" + String.format("%,.2f", monto) + " (+" + puntos + " pts).");
        return tx;
    }

    public Transaccion realizarRetiro(String idCliente, String nombreMonedero, String categoriaGasto, double monto) {
        Cliente cliente = buscarCliente(idCliente);
        Monedero monedero = cliente.obtenerMonedero(nombreMonedero);

        double comision = cliente.isMesSinCargosRetiroActivo() ? 0.0 : (monto * 0.01);
        double totalDebitar = monto + comision;

        monedero.debitar(totalDebitar);

        int puntos = TipoTransaccion.RETIRO.calcularPuntos(monto);
        sincronizarPuntosEnEstructuras(cliente, puntos, true);

        Transaccion tx = new Transaccion(
                generarIdTx("RET"), TipoTransaccion.RETIRO, idCliente, null,
                nombreMonedero, categoriaGasto, monto, comision, puntos
        );
        cliente.getHistorialTransacciones().agregar(tx);
        cliente.getPilaReversion().push(tx);

        grafoPatronesGasto.registrarArista(cliente.getNombre(), "Categoría: " + categoriaGasto, monto);

        verificarAlertaSaldoBajo(cliente, monedero);
        return tx;
    }

    public Transaccion realizarTransferencia(String idOrigen, String idDestino, String categoriaGasto, double monto) {
        if (idOrigen.equals(idDestino)) {
            throw new IllegalArgumentException("El cliente destino debe ser diferente al origen");
        }
        Cliente origen = buscarCliente(idOrigen);
        Cliente destino = buscarCliente(idDestino);

        double tasaBase = 0.02 * (1.0 - origen.getRango().getDescuentoComisionBase());
        if (origen.isDescuentoTransferencia10Activo()) {
            tasaBase *= 0.90;
        }
        double comision = monto * tasaBase;

        Monedero monOrigen = origen.obtenerMonedero("Principal");
        Monedero monDestino = destino.obtenerMonedero("Principal");

        monOrigen.debitar(monto + comision);
        monDestino.acreditar(monto);

        int puntos = TipoTransaccion.TRANSFERENCIA.calcularPuntos(monto);
        sincronizarPuntosEnEstructuras(origen, puntos, true);

        Transaccion tx = new Transaccion(
                generarIdTx("TRF"), TipoTransaccion.TRANSFERENCIA, idOrigen, idDestino,
                "Principal", categoriaGasto, monto, comision, puntos
        );
        origen.getHistorialTransacciones().agregar(tx);
        destino.getHistorialTransacciones().agregar(tx);
        origen.getPilaReversion().push(tx);

        grafoTransferenciasClientes.registrarArista(origen.getNombre(), destino.getNombre(), monto);
        grafoPatronesGasto.registrarArista(origen.getNombre(), "Categoría: " + categoriaGasto, monto);

        origen.agregarNotificacion("Transferencia enviada a " + destino.getNombre() + " por $" + String.format("%,.2f", monto));
        destino.agregarNotificacion("Transferencia recibida de " + origen.getNombre() + " por $" + String.format("%,.2f", monto));
        verificarAlertaSaldoBajo(origen, monOrigen);
        return tx;
    }

    public Transaccion transferirEntreSubmonederos(String idCliente, String monederoOrigen, String monederoDestino, double monto) {
        if (monederoOrigen.equalsIgnoreCase(monederoDestino)) {
            throw new IllegalArgumentException("Seleccione dos submonederos distintos");
        }
        Cliente cliente = buscarCliente(idCliente);
        Monedero origen = cliente.obtenerMonedero(monederoOrigen);
        Monedero destino = cliente.obtenerMonedero(monederoDestino);

        origen.debitar(monto);
        destino.acreditar(monto);

        int puntos = TipoTransaccion.TRASPASO_MONEDERO.calcularPuntos(monto);
        sincronizarPuntosEnEstructuras(cliente, puntos, true);

        Transaccion tx = new Transaccion(
                generarIdTx("SUB"), TipoTransaccion.TRASPASO_MONEDERO, idCliente, idCliente,
                monederoOrigen + "->" + monederoDestino, "Distribución Interna", monto, 0.0, puntos
        );
        cliente.getHistorialTransacciones().agregar(tx);
        grafoMultimonedero.registrarArista(
                cliente.getIdCliente() + ":" + monederoOrigen,
                cliente.getIdCliente() + ":" + monederoDestino,
                monto
        );
        cliente.agregarNotificacion("Traspaso interno de " + monederoOrigen + " a " + monederoDestino + " ($" + String.format("%,.2f", monto) + ").");
        return tx;
    }

    public Transaccion revertirUltimaTransaccion(String idCliente) {
        Cliente cliente = buscarCliente(idCliente);
        Transaccion ultima = cliente.getPilaReversion().pop();
        if (ultima == null) {
            throw new IllegalStateException("No hay transacciones reversibles en la pila del cliente");
        }
        if (ultima.isRevertida()) {
            throw new IllegalStateException("La transacción ya fue revertida anteriormente");
        }

        Monedero monedero = cliente.obtenerMonedero(ultima.getNombreMonedero());
        switch (ultima.getTipo()) {
            case DEPOSITO:
                monedero.debitar(ultima.getMonto());
                break;
            case RETIRO:
                monedero.acreditar(ultima.getMonto() + ultima.getComisionAplicada());
                break;
            case TRANSFERENCIA:
                Cliente destino = buscarCliente(ultima.getIdClienteDestino());
                destino.obtenerMonedero("Principal").debitar(ultima.getMonto());
                monedero.acreditar(ultima.getMonto() + ultima.getComisionAplicada());
                destino.agregarNotificacion("Se revirtió transferencia de " + cliente.getNombre() + " por $" + ultima.getMonto());
                break;
            default:
                throw new IllegalStateException("Este tipo de operación no admite reversión automática");
        }

        ultima.marcarRevertida();
        // Recálculo dinámico de puntos y rango en ABB, AVL y Tabla Hash
        sincronizarPuntosEnEstructuras(cliente, ultima.getPuntosGenerados(), false);
        cliente.agregarNotificacion("Transacción " + ultima.getIdTransaccion() + " revertida. Puntos ajustados (-" + ultima.getPuntosGenerados() + " pts).");
        return ultima;
    }

    public String canjearPuntos(String idCliente, int opcionPuntos) {
        Cliente cliente = buscarCliente(idCliente);
        if (cliente.getPuntosAcumulados() < opcionPuntos) {
            throw new IllegalStateException("Puntos insuficientes. Tienes " + cliente.getPuntosAcumulados() + " pts y requieres " + opcionPuntos + " pts.");
        }

        String beneficio;
        switch (opcionPuntos) {
            case 100:
                cliente.setDescuentoTransferencia10Activo(true);
                beneficio = "Reducción del 10% en comisión por transferencias activada";
                break;
            case 500:
                cliente.setMesSinCargosRetiroActivo(true);
                beneficio = "Un mes sin cargos por retiros activado";
                break;
            case 1000:
                cliente.obtenerMonedero("Principal").acreditar(50.0);
                Transaccion bono = new Transaccion(
                        generarIdTx("BONO"), TipoTransaccion.CANJE_BONO, idCliente, null,
                        "Principal", "Recompensa", 50.0, 0.0, 0
                );
                cliente.getHistorialTransacciones().agregar(bono);
                beneficio = "Bono de +$50.00 unidades acreditado en tu monedero Principal";
                break;
            default:
                throw new IllegalArgumentException("Opción de canje no válida (use 100, 500 o 1000)");
        }

        sincronizarPuntosEnEstructuras(cliente, opcionPuntos, false);
        cliente.agregarNotificacion("Canje exitoso (-" + opcionPuntos + " pts): " + beneficio);
        return beneficio;
    }

    public TransaccionProgramada programarTransaccion(TipoTransaccion tipo, String idOrigen, String idDestino, String categoria, double monto, LocalDate fecha, String frecuencia) {
        buscarCliente(idOrigen);
        TransaccionProgramada tp = new TransaccionProgramada(
                generarIdTx("PRG"), tipo, idOrigen, idDestino, categoria, monto, fecha, frecuencia
        );
        colaProgramadas.encolar(tp);
        buscarCliente(idOrigen).agregarNotificacion("Recordatorio: Transacción programada para el " + fecha + " ($" + String.format("%,.2f", monto) + ").");
        return tp;
    }

    public Transaccion ejecutarSiguienteProgramada() {
        TransaccionProgramada tp = colaProgramadas.desencolar();
        if (tp == null) {
            throw new IllegalStateException("No hay transacciones programadas pendientes en la Cola de Prioridad");
        }
        switch (tp.getTipo()) {
            case DEPOSITO:
                return realizarDeposito(tp.getIdClienteOrigen(), "Principal", tp.getMonto());
            case RETIRO:
                return realizarRetiro(tp.getIdClienteOrigen(), "Principal", tp.getCategoriaGasto(), tp.getMonto());
            case TRANSFERENCIA:
                return realizarTransferencia(tp.getIdClienteOrigen(), tp.getIdClienteDestino(), tp.getCategoriaGasto(), tp.getMonto());
            default:
                throw new IllegalStateException("Tipo programado no soportado");
        }
    }

    private void verificarAlertaSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.getSaldo() < UMBRAL_SALDO_BAJO) {
            cliente.agregarNotificacion("ALERTA SALDO BAJO: Tu monedero '" + monedero.getNombre()
                    + "' tiene $" + String.format("%,.2f", monedero.getSaldo()) + " (menor a $" + UMBRAL_SALDO_BAJO + ").");
        }
    }

    public boolean auditarIntegridadCliente(String idCliente) {
        Cliente c = buscarCliente(idCliente);
        return AlgoritmosRecursivos.verificarIntegridadHistorial(c.getHistorialTransacciones());
    }

    public ListaEnlazada<Cliente> obtenerClientesPorPuntosABB() {
        return arbolPuntosABB.recorridoInorden();
    }

    public ListaEnlazada<Cliente> obtenerRankingRangosAVL() {
        return arbolRangosAVL.obtenerRankingDescendente();
    }

    public ColaPrioridad<TransaccionProgramada> getColaProgramadas() {
        return colaProgramadas;
    }

    public GrafoDirigido<String> getGrafoTransferenciasClientes() {
        return grafoTransferenciasClientes;
    }

    public GrafoDirigido<String> getGrafoPatronesGasto() {
        return grafoPatronesGasto;
    }

    public GrafoDirigido<String> getGrafoMultimonedero() {
        return grafoMultimonedero;
    }

    public ListaEnlazada<Cliente> obtenerTodosLosClientes() {
        return clientesPorId.valores();
    }
}