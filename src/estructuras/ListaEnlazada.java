package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaEnlazada<T> implements Coleccion<T> {

    private static class Nodo<E> {
        private E valor;
        private Nodo<E> siguiente;

        private Nodo(E valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public ListaEnlazada() {
        this.primero = null;
        this.ultimo = null;
        this.tamanio = 0;
    }

    @Override
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        }
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (estaVacia()) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.siguiente = nuevo;
            ultimo = nuevo;
        }
        tamanio++;
    }

    @Override
    public boolean eliminar(T elemento) {
        if (estaVacia() || elemento == null) {
            return false;
        }
        Nodo<T> actual = primero;
        Nodo<T> anterior = null;

        while (actual != null) {
            if (actual.valor.equals(elemento)) {
                if (anterior == null) {
                    primero = actual.siguiente;
                    if (primero == null) {
                        ultimo = null;
                    }
                } else {
                    anterior.siguiente = actual.siguiente;
                    if (actual == ultimo) {
                        ultimo = anterior;
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

    @Override
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + posicion);
        }
        Nodo<T> actual = primero;
        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    @Override
    public int getTamanio() {
        return tamanio;
    }

    @Override
    public boolean estaVacia() {
        return tamanio == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorLista();
    }

    private class IteradorLista implements Iterator<T> {
        private Nodo<T> actual = primero;

        @Override
        public boolean hasNext() {
            return actual != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No hay más elementos en la lista");
            }
            T dato = actual.valor;
            actual = actual.siguiente;
            return dato;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = primero;
        while (actual != null) {
            sb.append(actual.valor);
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }
}