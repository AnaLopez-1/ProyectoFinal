package estructuras;

public class TablaHash<K, V> {

    private static class Entrada<K, V> {
        private final K clave;
        private V valor;
        private Entrada<K, V> siguiente;

        private Entrada(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private Entrada<K, V>[] cubetas;
    private int tamanio;
    private static final int CAPACIDAD_INICIAL = 31;

    @SuppressWarnings("unchecked")
    public TablaHash() {
        this.cubetas = (Entrada<K, V>[]) new Entrada[CAPACIDAD_INICIAL];
        this.tamanio = 0;
    }

    private int indiceHash(K clave) {
        return (clave.hashCode() & 0x7fffffff) % cubetas.length;
    }

    public void put(K clave, V valor) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }
        int indice = indiceHash(clave);
        Entrada<K, V> actual = cubetas[indice];

        while (actual != null) {
            if (actual.clave.equals(clave)) {
                actual.valor = valor; // Actualiza valor en O(1)
                return;
            }
            actual = actual.siguiente;
        }

        Entrada<K, V> nueva = new Entrada<>(clave, valor);
        nueva.siguiente = cubetas[indice];
        cubetas[indice] = nueva;
        tamanio++;
    }

    public V get(K clave) {
        if (clave == null) {
            return null;
        }
        int indice = indiceHash(clave);
        Entrada<K, V> actual = cubetas[indice];

        while (actual != null) {
            if (actual.clave.equals(clave)) {
                return actual.valor;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean containsKey(K clave) {
        return get(clave) != null;
    }

    public ListaEnlazada<V> valores() {
        ListaEnlazada<V> lista = new ListaEnlazada<>();
        for (Entrada<K, V> cubeta : cubetas) {
            Entrada<K, V> actual = cubeta;
            while (actual != null) {
                lista.agregar(actual.valor);
                actual = actual.siguiente;
            }
        }
        return lista;
    }

    public int getTamanio() {
        return tamanio;
    }
}