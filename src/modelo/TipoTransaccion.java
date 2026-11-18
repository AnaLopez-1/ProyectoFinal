package modelo;

public enum TipoTransaccion {
    DEPOSITO("Depósito", 1),
    RETIRO("Retiro", 2),
    TRANSFERENCIA("Transferencia", 3),
    CANJE_BONO("Bono por Canje de Puntos", 0),
    TRASPASO_MONEDERO("Traspaso entre Submonederos", 1);

    private final String etiqueta;
    private final int puntosPorCadaCien;

    TipoTransaccion(String etiqueta, int puntosPorCadaCien) {
        this.etiqueta = etiqueta;
        this.puntosPorCadaCien = puntosPorCadaCien;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public int calcularPuntos(double monto) {
        if (monto < 100 || puntosPorCadaCien == 0) {
            return 0;
        }
        return ((int) (monto / 100.0)) * puntosPorCadaCien;
    }
}