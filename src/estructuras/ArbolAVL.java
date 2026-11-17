package estructuras;

public class ArbolAVL<T extends Comparable<T>> {

    private static class NodoAVL<E> {
        private E dato;
        private int altura;
        private int fe; // Factor de Equilibrio: alt(der) - alt(izq)
        private NodoAVL<E> izquierdo;
        private NodoAVL<E> derecho;

        private NodoAVL(E dato) {
            this.dato = dato;
            this.altura = 1;
            this.fe = 0;
        }
    }

    private NodoAVL<T> raiz;
    private int tamanio;

    private int altura(NodoAVL<T> nodo) {
        return (nodo == null) ? 0 : nodo.altura;
    }

    private void actualizarAlturaYFE(NodoAVL<T> nodo) {
        if (nodo != null) {
            int altIzq = altura(nodo.izquierdo);
            int altDer = altura(nodo.derecho);
            nodo.altura = 1 + Math.max(altIzq, altDer);
            nodo.fe = altDer - altIzq; // Fórmula vista en clase
        }
    }

    // Rotación simple a la derecha (Caso Izquierda-Izquierda: FE == -2)
    private NodoAVL<T> rotacionDerecha(NodoAVL<T> y) {
        NodoAVL<T> x = y.izquierdo;
        NodoAVL<T> t2 = x.derecho;

        x.derecho = y;
        y.izquierdo = t2;

        actualizarAlturaYFE(y);
        actualizarAlturaYFE(x);
        return x;
    }

    // Rotación simple a la izquierda (Caso Derecha-Derecha: FE == +2)
    private NodoAVL<T> rotacionIzquierda(NodoAVL<T> x) {
        NodoAVL<T> y = x.derecho;
        NodoAVL<T> t2 = y.izquierdo;

        y.izquierdo = x;
        x.derecho = t2;

        actualizarAlturaYFE(x);
        actualizarAlturaYFE(y);
        return y;
    }

    private NodoAVL<T> balancear(NodoAVL<T> nodo) {
        actualizarAlturaYFE(nodo);

        // Desbalance hacia la izquierda (FE = -2)
        if (nodo.fe < -1) {
            if (nodo.izquierdo != null && nodo.izquierdo.fe > 0) {
                // Caso Izquierda-Derecha (Rotación doble)
                nodo.izquierdo = rotacionIzquierda(nodo.izquierdo);
            }
            return rotacionDerecha(nodo);
        }

        // Desbalance hacia la derecha (FE = +2)
        if (nodo.fe > 1) {
            if (nodo.derecho != null && nodo.derecho.fe < 0) {
                // Caso Derecha-Izquierda (Rotación doble)
                nodo.derecho = rotacionDerecha(nodo.derecho);
            }
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

    public void insertar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
        raiz = insertarRec(raiz, dato);
    }

    private NodoAVL<T> insertarRec(NodoAVL<T> actual, T dato) {
        if (actual == null) {
            tamanio++;
            return new NodoAVL<>(dato);
        }
        int cmp = dato.compareTo(actual.dato);
        if (cmp < 0) {
            actual.izquierdo = insertarRec(actual.izquierdo, dato);
        } else if (cmp > 0) {
            actual.derecho = insertarRec(actual.derecho, dato);
        } else {
            actual.dato = dato;
            return actual;
        }
        return balancear(actual);
    }

    public void eliminar(T dato) {
        if (dato != null) {
            raiz = eliminarRec(raiz, dato);
        }
    }

    private NodoAVL<T> eliminarRec(NodoAVL<T> actual, T dato) {
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
            if (actual.izquierdo == null || actual.derecho == null) {
                return (actual.izquierdo != null) ? actual.izquierdo : actual.derecho;
            }
            NodoAVL<T> predecesor = encontrarMaximo(actual.izquierdo);
            actual.dato = predecesor.dato;
            tamanio++;
            actual.izquierdo = eliminarRec(actual.izquierdo, predecesor.dato);
        }
        return balancear(actual);
    }

    private NodoAVL<T> encontrarMaximo(NodoAVL<T> nodo) {
        while (nodo.derecho != null) {
            nodo = nodo.derecho;
        }
        return nodo;
    }

    // Recorrido de mayor a menor puntaje (Inorden inverso: Der -> Raíz -> Izq)
    public ListaEnlazada<T> obtenerRankingDescendente() {
        ListaEnlazada<T> lista = new ListaEnlazada<>();
        inordenInverso(raiz, lista);
        return lista;
    }

    private void inordenInverso(NodoAVL<T> actual, ListaEnlazada<T> lista) {
        if (actual != null) {
            inordenInverso(actual.derecho, lista);
            lista.agregar(actual.dato);
            inordenInverso(actual.izquierdo, lista);
        }
    }

    public int getTamanio() {
        return tamanio;
    }
}