package estructuras;

public class Pila<T> {

    private static class Nodo<E> {
        private E dato;
        private Nodo<E> siguiente;

        private Nodo(E dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> cima;
    private int tamanio;

    public Pila() {
        this.cima = null;
        this.tamanio = 0;
    }

    public void push(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se puede apilar un dato nulo");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamanio++;
    }

    public T pop() {
        if (estaVacia()) {
            return null;
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        tamanio--;
        return dato;
    }

    public T cima() {
        if (estaVacia()) {
            return null;
        }
        return cima.dato;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Tope] -> ");
        Nodo<T> actual = cima;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }
        return sb.toString();
    }
}