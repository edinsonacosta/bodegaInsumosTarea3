/**
 * Cola (FIFO) implementada con nodos enlazados.
 * Guarda las solicitudes de los invernaderos: la primera que llega
 * es la primera que se atiende.
 */
public class Cola<T> {

    private Nodo<T> frente;   // primera solicitud (la que se atiende)
    private Nodo<T> fin;      // ultima solicitud (la que acaba de llegar)
    private int tamanio;

    public Cola() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    /** enqueue: agrega un elemento al final de la cola. O(1). */
    public void enqueue(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamanio++;
    }

    /** dequeue: retira y devuelve el elemento del frente. O(1). */
    public T dequeue() {
        if (estaVacia()) {
            throw new IllegalStateException("No hay solicitudes pendientes");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;   // la cola quedo vacia
        }
        tamanio--;
        return dato;
    }

    /** peek: consulta el frente sin retirarlo. O(1). */
    public T peek() {
        if (estaVacia()) {
            throw new IllegalStateException("No hay solicitudes pendientes");
        }
        return frente.dato;
    }

    public boolean estaVacia() { return frente == null; }
    public int tamanio()       { return tamanio; }

    /** Devuelve el elemento en la posicion indicada desde el frente (0 = frente). Solo para mostrar. */
    public T verDesdeFrente(int posicion) {
        Nodo<T> actual = frente;
        for (int i = 0; i < posicion && actual != null; i++) {
            actual = actual.siguiente;
        }
        return actual == null ? null : actual.dato;
    }
}
