/**
 * Nodo generico usado por la Pila y por la Cola.
 * Guarda un dato y la referencia al siguiente nodo.
 */
public class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}
