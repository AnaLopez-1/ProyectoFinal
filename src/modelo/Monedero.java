package modelo;

public class Monedero {
    private final String idMonedero;
    private final String nombre; // Ej: "Principal", "Ahorros", "Gastos Diarios"
    private double saldo;

    public Monedero(String idMonedero, String nombre, double saldoInicial) {
        if (idMonedero == null || idMonedero.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del monedero no puede ser vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del monedero no puede ser vacío");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        this.idMonedero = idMonedero.trim();
        this.nombre = nombre.trim();
        this.saldo = saldoInicial;
    }

    public void acreditar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a acreditar debe ser positivo");
        }
        this.saldo += monto;
    }

    public void debitar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a debitar debe ser positivo");
        }
        if (monto > this.saldo) {
            throw new IllegalStateException("Saldo insuficiente en el monedero " + nombre);
        }
        this.saldo -= monto;
    }

    public String getIdMonedero() { return idMonedero; }
    public String getNombre() { return nombre; }
    public double getSaldo() { return saldo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Monedero)) return false;
        Monedero otro = (Monedero) o;
        return idMonedero.equals(otro.idMonedero);
    }

    @Override
    public int hashCode() {
        return idMonedero.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s ($%,.2f)", nombre, saldo);
    }
}