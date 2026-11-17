package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ColaPrioridad<T extends Comparable<T>> implements Iterable<T> {

    private static class Nodo<E> {
        private E dato;
        private Nodo<E> siguiente;

        private Nodo(E dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> frente;
    private int tamanio;

    public ColaPrioridad() {
        this.frente = null;
        this.tamanio = 0;
    }

    // Inserta ordenadamente según la prioridad natural (ej. fecha más próxima primero)
    public void encolar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        }
        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia() || dato.compareTo(frente.dato) < 0) {
            nuevo.siguiente = frente;
            frente = nuevo;
        } else {
            Nodo<T> actual = frente;
            while (actual.siguiente != null && dato.compareTo(actual.siguiente.dato) >= 0) {
                actual = actual.siguiente;
            }
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        }
        tamanio++;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        tamanio--;
        return dato;
    }

    public T frente() {
        return estaVacia() ? null : frente.dato;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}