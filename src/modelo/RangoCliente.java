package modelo;

public enum RangoCliente {
    BRONCE("Bronce (0 - 500 pts)", 0.0),
    PLATA("Plata (501 - 1000 pts)", 0.05),
    ORO("Oro (1001 - 5000 pts)", 0.10),
    PLATINO("Platino (> 5000 pts)", 0.20);

    private final String descripcion;
    private final double descuentoComisionBase;

    RangoCliente(String descripcion, double descuentoComisionBase) {
        this.descripcion = descripcion;
        this.descuentoComisionBase = descuentoComisionBase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getDescuentoComisionBase() {
        return descuentoComisionBase;
    }

    public static RangoCliente calcularPorPuntos(int puntos) {
        if (puntos <= 500) {
            return BRONCE;
        } else if (puntos <= 1000) {
            return PLATA;
        } else if (puntos <= 5000) {
            return ORO;
        } else {
            return PLATINO;
        }
    }
}