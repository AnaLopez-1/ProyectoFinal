package estructuras;

public interface Coleccion<T> extends Iterable<T> {
    void agregar(T elemento);
    boolean eliminar(T elemento);
    T obtener(int posicion);
    int getTamanio();
    boolean estaVacia();
}