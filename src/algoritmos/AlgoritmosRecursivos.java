package algoritmos;

import estructuras.ListaEnlazada;
import modelo.Transaccion;
import modelo.TipoTransaccion;

public final class AlgoritmosRecursivos {

    private AlgoritmosRecursivos() {
    }

    public static boolean verificarIntegridadHistorial(ListaEnlazada<Transaccion> historial) {
        if (historial == null || historial.estaVacia()) {
            return true;
        }
        return verificarIntegridadRec(historial, 0);
    }

    private static boolean verificarIntegridadRec(ListaEnlazada<Transaccion> historial, int indice) {
        if (indice == historial.getTamanio()) {
            return true;
        }

        Transaccion actual = historial.obtener(indice);
        boolean valida = actual != null
                && actual.getIdTransaccion() != null
                && !actual.getIdTransaccion().isEmpty()
                && actual.getMonto() > 0
                && actual.getComisionAplicada() >= 0
                && actual.getPuntosGenerados() >= 0;

        if (!valida) {
            return false;
        }
        return verificarIntegridadRec(historial, indice + 1);
    }

    public static int calcularPuntosActivosRecursivo(ListaEnlazada<Transaccion> historial) {
        if (historial == null || historial.estaVacia()) {
            return 0;
        }
        return sumarPuntosRec(historial, 0);
    }

    private static int sumarPuntosRec(ListaEnlazada<Transaccion> historial, int indice) {
        if (indice == historial.getTamanio()) {
            return 0;
        }
        Transaccion t = historial.obtener(indice);
        int aporte = t.isRevertida() ? 0 : t.getPuntosGenerados();
        return aporte + sumarPuntosRec(historial, indice + 1);
    }

    public static double gastoTotalPorCategoria(ListaEnlazada<Transaccion> historial, String categoria) {
        if (historial == null || categoria == null || historial.estaVacia()) {
            return 0.0;
        }
        return gastoCategoriaRec(historial, categoria, 0);
    }

    private static double gastoCategoriaRec(ListaEnlazada<Transaccion> historial, String categoria, int indice) {
        if (indice == historial.getTamanio()) {
            return 0.0;
        }
        Transaccion t = historial.obtener(indice);
        boolean aplica = !t.isRevertida()
                && (t.getTipo() == TipoTransaccion.RETIRO || t.getTipo() == TipoTransaccion.TRANSFERENCIA)
                && t.getCategoriaGasto().equalsIgnoreCase(categoria);

        double aporte = aplica ? t.getMonto() : 0.0;
        return aporte + gastoCategoriaRec(historial, categoria, indice + 1);
    }

    public static Transaccion transaccionMayorMonto(ListaEnlazada<Transaccion> historial) {
        if (historial == null || historial.estaVacia()) {
            return null;
        }
        return mayorMontoDV(historial, 0, historial.getTamanio() - 1);
    }

    private static Transaccion mayorMontoDV(ListaEnlazada<Transaccion> historial, int inicio, int fin) {
        if (inicio == fin) {
            return historial.obtener(inicio);
        }

        int medio = inicio + (fin - inicio) / 2;

        Transaccion maxIzq = mayorMontoDV(historial, inicio, medio);
        Transaccion maxDer = mayorMontoDV(historial, medio + 1, fin);

        if (maxIzq.getMonto() >= maxDer.getMonto()) {
            return maxIzq;
        }
        return maxDer;
    }
}
