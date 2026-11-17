package estructuras;

public class ArbolABB<T extends Comparable<T>> {

    private static class NodoArbol<E> {
        private E dato;
        private NodoArbol<E> izquierdo;
        private NodoArbol<E> derecho;

        private NodoArbol(E dato) {
            this.dato = dato;
        }
    }

    private NodoArbol<T> raiz;
    private int tamanio;

    public void insertar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
        raiz = insertarRec(raiz, dato);
    }

    private NodoArbol<T> insertarRec(NodoArbol<T> actual, T dato) {
        if (actual == null) {
            tamanio++;
            return new NodoArbol<>(dato);
        }
        int cmp = dato.compareTo(actual.dato);
        if (cmp < 0) {
            actual.izquierdo = insertarRec(actual.izquierdo, dato);
        } else if (cmp > 0) {
            actual.derecho = insertarRec(actual.derecho, dato);
        } else {
            actual.dato = dato; // Actualiza si ya existe la misma clave
        }
        return actual;
    }

    public T buscar(T muestra) {
        return buscarRec(raiz, muestra);
    }

    private T buscarRec(NodoArbol<T> actual, T muestra) {
        if (actual == null) {
            return null;
        }
        int cmp = muestra.compareTo(actual.dato);
        if (cmp == 0) {
            return actual.dato;
        }
        return (cmp < 0) ? buscarRec(actual.izquierdo, muestra) : buscarRec(actual.derecho, muestra);
    }

    public void eliminar(T dato) {
        raiz = eliminarRec(raiz, dato);
    }

    private NodoArbol<T> eliminarRec(NodoArbol<T> actual, T dato) {
        if (actual == null) {
            return null;
        }
        int cmp = dato.compareTo(actual.dato);
        if (cmp < 0) {
            actual.izquierdo = eliminarRec(actual.izquierdo, dato);
        } else if (cmp > 0) {
            actual.derecho = eliminarRec(actual.derecho, dato);
        } else {
            tamanio--;
            // Caso 1 y 2: sin hijos o un solo hijo
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            if (actual.derecho == null) {
                return actual.izquierdo;
            }
            // Caso 3: dos hijos (reemplazo por el mayor de la izquierda - predecesor, como en clase)
            NodoArbol<T> predecesor = encontrarMaximo(actual.izquierdo);
            actual.dato = predecesor.dato;
            tamanio++; // Compensar el decremento de la llamada recursiva
            actual.izquierdo = eliminarRec(actual.izquierdo, predecesor.dato);
        }
        return actual;
    }

    private NodoArbol<T> encontrarMaximo(NodoArbol<T> nodo) {
        while (nodo.derecho != null) {
            nodo = nodo.derecho;
        }
        return nodo;
    }

    public ListaEnlazada<T> recorridoInorden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        inordenRec(raiz, resultado);
        return resultado;
    }

    private void inordenRec(NodoArbol<T> actual, ListaEnlazada<T> lista) {
        if (actual != null) {
            inordenRec(actual.izquierdo, lista);
            lista.agregar(actual.dato);
            inordenRec(actual.derecho, lista);
        }
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}