package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaCircular<T> implements Iterable<T> {

    private static class Nodo<E> {
        private E dato;
        private Nodo<E> siguiente;

        private Nodo(E dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private Nodo<T> cursorRotacion;
    private int tamanio;

    public ListaCircular() {
        this.primero = null;
        this.ultimo = null;
        this.cursorRotacion = null;
        this.tamanio = 0;
    }

    public void agregar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
            ultimo.siguiente = primero; // Enlace circular
            cursorRotacion = primero;
        } else {
            ultimo.siguiente = nuevo;
            ultimo = nuevo;
            ultimo.siguiente = primero; // Mantiene el ciclo cerrado
        }
        tamanio++;
    }

    // Avanza circularmente para mostrar alertas rotativas en la interfaz
    public T siguienteRotacion() {
        if (estaVacia()) {
            return null;
        }
        T dato = cursorRotacion.dato;
        cursorRotacion = cursorRotacion.siguiente;
        return dato;
    }

    public boolean eliminar(T dato) {
        if (estaVacia() || dato == null) {
            return false;
        }
        Nodo<T> actual = primero;
        Nodo<T> anterior = ultimo;

        for (int i = 0; i < tamanio; i++) {
            if (actual.dato.equals(dato)) {
                if (tamanio == 1) {
                    primero = null;
                    ultimo = null;
                    cursorRotacion = null;
                } else {
                    anterior.siguiente = actual.siguiente;
                    if (actual == primero) {
                        primero = actual.siguiente;
                    }
                    if (actual == ultimo) {
                        ultimo = anterior;
                    }
                    if (cursorRotacion == actual) {
                        cursorRotacion = actual.siguiente;
                    }
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int getTamanio() {
        return tamanio;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = primero;
            private int visitados = 0;

            @Override
            public boolean hasNext() {
                return visitados < tamanio;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                visitados++;
                return dato;
            }
        };
    }
}