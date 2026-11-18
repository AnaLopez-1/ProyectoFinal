package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaccion {
    private final String idTransaccion;
    private final TipoTransaccion tipo;
    private final String idClienteOrigen;
    private final String idClienteDestino; // null si es depósito o retiro propio
    private final String nombreMonedero;
    private final String categoriaGasto;   // Ej: "Alimentación", "Servicios", "Ahorro", "General"
    private final double monto;
    private final double comisionAplicada;
    private final int puntosGenerados;
    private final LocalDateTime fechaHora;
    private boolean revertida;

    public Transaccion(String idTransaccion, TipoTransaccion tipo, String idClienteOrigen, String idClienteDestino, String nombreMonedero, String categoriaGasto, double monto, double comisionAplicada, int puntosGenerados) {
        if (idTransaccion == null || idTransaccion.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de transacción es obligatorio");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto de la transacción debe ser mayor que cero");
        }
        this.idTransaccion = idTransaccion.trim();
        this.tipo = tipo;
        this.idClienteOrigen = idClienteOrigen;
        this.idClienteDestino = idClienteDestino;
        this.nombreMonedero = (nombreMonedero == null || nombreMonedero.isEmpty()) ? "Principal" : nombreMonedero;
        this.categoriaGasto = (categoriaGasto == null || categoriaGasto.isEmpty()) ? "General" : categoriaGasto;
        this.monto = monto;
        this.comisionAplicada = comisionAplicada;
        this.puntosGenerados = puntosGenerados;
        this.fechaHora = LocalDateTime.now();
        this.revertida = false;
    }

    public String getIdTransaccion() { return idTransaccion; }
    public TipoTransaccion getTipo() { return tipo; }
    public String getIdClienteOrigen() { return idClienteOrigen; }
    public String getIdClienteDestino() { return idClienteDestino; }
    public String getNombreMonedero() { return nombreMonedero; }
    public String getCategoriaGasto() { return categoriaGasto; }
    public double getMonto() { return monto; }
    public double getComisionAplicada() { return comisionAplicada; }
    public int getPuntosGenerados() { return puntosGenerados; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public boolean isRevertida() { return revertida; }

    public void marcarRevertida() {
        this.revertida = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaccion)) return false;
        Transaccion otra = (Transaccion) o;
        return idTransaccion.equals(otra.idTransaccion);
    }

    @Override
    public int hashCode() {
        return idTransaccion.hashCode();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String estado = revertida ? " [REVERTIDA]" : "";
        return String.format("[%s | %s | %s | Monedero: %s | Cat: %s | Monto: $%,.2f | Com: $%,.2f | +%d pts | %s]%s",
                idTransaccion, tipo.getEtiqueta(), idClienteOrigen, nombreMonedero,
                categoriaGasto, monto, comisionAplicada, puntosGenerados, fechaHora.format(fmt), estado);
    }
}
