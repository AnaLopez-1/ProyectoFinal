package modelo;

import java.time.LocalDate;

public class TransaccionProgramada implements Comparable<TransaccionProgramada> {
    private final String idProgramada;
    private final TipoTransaccion tipo;
    private final String idClienteOrigen;
    private final String idClienteDestino;
    private final String categoriaGasto;
    private final double monto;
    private final LocalDate fechaEjecucion;
    private final String frecuencia; // Ej: "Única", "Semanal", "Mensual"

    public TransaccionProgramada(String idProgramada, TipoTransaccion tipo, String idClienteOrigen,
                                 String idClienteDestino, String categoriaGasto, double monto,
                                 LocalDate fechaEjecucion, String frecuencia) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto programado debe ser mayor que cero");
        }
        if (fechaEjecucion == null) {
            throw new IllegalArgumentException("La fecha de ejecución es obligatoria");
        }
        this.idProgramada = idProgramada;
        this.tipo = tipo;
        this.idClienteOrigen = idClienteOrigen;
        this.idClienteDestino = idClienteDestino;
        this.categoriaGasto = (categoriaGasto == null) ? "Programado" : categoriaGasto;
        this.monto = monto;
        this.fechaEjecucion = fechaEjecucion;
        this.frecuencia = (frecuencia == null) ? "Mensual" : frecuencia;
    }

    public String getIdProgramada() { return idProgramada; }
    public TipoTransaccion getTipo() { return tipo; }
    public String getIdClienteOrigen() { return idClienteOrigen; }
    public String getIdClienteDestino() { return idClienteDestino; }
    public String getCategoriaGasto() { return categoriaGasto; }
    public double getMonto() { return monto; }
    public LocalDate getFechaEjecucion() { return fechaEjecucion; }
    public String getFrecuencia() { return frecuencia; }

    @Override
    public int compareTo(TransaccionProgramada otra) {
        int cmpFecha = this.fechaEjecucion.compareTo(otra.fechaEjecucion);
        if (cmpFecha != 0) {
            return cmpFecha; // Prioridad a la fecha más cercana
        }
        return this.idProgramada.compareTo(otra.idProgramada);
    }

    @Override
    public String toString() {
        return String.format("[%s | Fecha: %s (%s) | %s | Cliente: %s | Monto: $%,.2f]",
                idProgramada, fechaEjecucion, frecuencia, tipo.getEtiqueta(), idClienteOrigen, monto);
    }
}