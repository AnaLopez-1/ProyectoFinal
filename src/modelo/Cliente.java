package modelo;

import estructuras.ListaCircular;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import estructuras.TablaHash;

public class Cliente implements Comparable<Cliente> {
    private final String idCliente;
    private final String nombre;
    private int puntosAcumulados;
    private RangoCliente rango;

    // Beneficios activos por canje de puntos (Requisito 2 del Sistema de Puntos)
    private boolean descuentoTransferencia10Activo; // Canje de 100 pts
    private boolean mesSinCargosRetiroActivo;       // Canje de 500 pts

    // Estructuras propias asociadas al cliente
    private final TablaHash<String, Monedero> monederos;
    private final ListaEnlazada<Transaccion> historialTransacciones;
    private final Pila<Transaccion> pilaReversion;
    private final ListaCircular<String> notificaciones;

    public Cliente(String idCliente, String nombre, double saldoInicial) {
        if (idCliente == null || idCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del cliente no puede ser vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede ser vacío");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }

        this.idCliente = idCliente.trim();
        this.nombre = nombre.trim();
        this.puntosAcumulados = 0;
        this.rango = RangoCliente.BRONCE;

        this.monederos = new TablaHash<>();
        this.historialTransacciones = new ListaEnlazada<>();
        this.pilaReversion = new Pila<>();
        this.notificaciones = new ListaCircular<>();

        // Creación de submonederos por defecto (Multimonedero)
        this.monederos.put("Principal", new Monedero(idCliente + "-PRIN", "Principal", saldoInicial));
        this.monederos.put("Ahorros", new Monedero(idCliente + "-AHOR", "Ahorros", 0.0));
        this.monederos.put("Gastos Diarios", new Monedero(idCliente + "-GAST", "Gastos Diarios", 0.0));

        this.notificaciones.agregar("Bienvenido " + this.nombre + " a tu Monedero Virtual. Rango inicial: BRONCE.");
    }

    public Monedero obtenerMonedero(String nombreMonedero) {
        Monedero m = monederos.get(nombreMonedero);
        if (m == null) {
            throw new IllegalArgumentException("No existe el submonedero '" + nombreMonedero + "'");
        }
        return m;
    }

    public void crearSubmonedero(String nombreMonedero, double saldoInicial) {
        if (monederos.containsKey(nombreMonedero)) {
            throw new IllegalArgumentException("Ya existe el submonedero " + nombreMonedero);
        }
        monederos.put(nombreMonedero, new Monedero(idCliente + "-" + nombreMonedero.toUpperCase(), nombreMonedero, saldoInicial));
        agregarNotificacion("Submonedero '" + nombreMonedero + "' creado exitosamente.");
    }

    public double getSaldoPrincipal() {
        return obtenerMonedero("Principal").getSaldo();
    }

    public double getSaldoTotalGlobal() {
        double total = 0;
        for (Monedero m : monederos.valores()) {
            total += m.getSaldo();
        }
        return total;
    }

    public void sumarPuntos(int puntos) {
        if (puntos > 0) {
            this.puntosAcumulados += puntos;
            actualizarRango();
        }
    }

    public void restarPuntos(int puntos) {
        this.puntosAcumulados = Math.max(0, this.puntosAcumulados - puntos);
        actualizarRango();
    }

    private void actualizarRango() {
        RangoCliente nuevoRango = RangoCliente.calcularPorPuntos(this.puntosAcumulados);
        if (nuevoRango != this.rango) {
            this.rango = nuevoRango;
            agregarNotificacion("¡Felicidades! Tu rango se actualizó a " + nuevoRango.name() + " (" + puntosAcumulados + " pts).");
        }
    }

    public void agregarNotificacion(String mensaje) {
        notificaciones.agregar(mensaje);
    }

    public String getIdCliente() { return idCliente; }
    public String getNombre() { return nombre; }
    public int getPuntosAcumulados() { return puntosAcumulados; }
    public RangoCliente getRango() { return rango; }
    public boolean isDescuentoTransferencia10Activo() { return descuentoTransferencia10Activo; }
    public void setDescuentoTransferencia10Activo(boolean activo) { this.descuentoTransferencia10Activo = activo; }
    public boolean isMesSinCargosRetiroActivo() { return mesSinCargosRetiroActivo; }
    public void setMesSinCargosRetiroActivo(boolean activo) { this.mesSinCargosRetiroActivo = activo; }

    public ListaEnlazada<Transaccion> getHistorialTransacciones() { return historialTransacciones; }
    public Pila<Transaccion> getPilaReversion() { return pilaReversion; }
    public ListaCircular<String> getNotificaciones() { return notificaciones; }
    public ListaEnlazada<Monedero> getListaMonederos() { return monederos.valores(); }

    @Override
    public int compareTo(Cliente otro) {
        int cmpPuntos = Integer.compare(this.puntosAcumulados, otro.puntosAcumulados);
        if (cmpPuntos != 0) {
            return cmpPuntos;
        }
        return this.idCliente.compareTo(otro.idCliente);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente otro = (Cliente) o;
        return idCliente.equals(otro.idCliente);
    }

    @Override
    public int hashCode() {
        return idCliente.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s - %s | Saldo Principal: $%,.2f | Puntos: %d | Rango: %s",
                idCliente, nombre, getSaldoPrincipal(), puntosAcumulados, rango.name());
    }
}