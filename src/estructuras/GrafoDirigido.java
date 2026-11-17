package estructuras;

public class GrafoDirigido<T> {

    public static class Arista<E> {
        private final E destino;
        private double pesoAcumulado;
        private int cantidadOperaciones;

        public Arista(E destino, double pesoInicial) {
            this.destino = destino;
            this.pesoAcumulado = pesoInicial;
            this.cantidadOperaciones = 1;
        }

        public void sumarPeso(double monto) {
            this.pesoAcumulado += monto;
            this.cantidadOperaciones++;
        }

        public E getDestino() { return destino; }
        public double getPesoAcumulado() { return pesoAcumulado; }
        public int getCantidadOperaciones() { return cantidadOperaciones; }

        @Override
        public String toString() {
            return String.format("-> %s ($%,.2f en %d op.)", destino, pesoAcumulado, cantidadOperaciones);
        }
    }

    private final TablaHash<T, ListaEnlazada<Arista<T>>> adyacencia;

    public GrafoDirigido() {
        this.adyacencia = new TablaHash<>();
    }

    public void agregarVertice(T vertice) {
        if (!adyacencia.containsKey(vertice)) {
            adyacencia.put(vertice, new ListaEnlazada<>());
        }
    }

    public void registrarArista(T origen, T destino, double monto) {
        agregarVertice(origen);
        agregarVertice(destino);

        ListaEnlazada<Arista<T>> lista = adyacencia.get(origen);
        for (Arista<T> arista : lista) {
            if (arista.getDestino().equals(destino)) {
                arista.sumarPeso(monto);
                return;
            }
        }
        lista.agregar(new Arista<>(destino, monto));
    }

    public ListaEnlazada<Arista<T>> obtenerAdyacentes(T origen) {
        ListaEnlazada<Arista<T>> lista = adyacencia.get(origen);
        return (lista != null) ? lista : new ListaEnlazada<>();
    }
}