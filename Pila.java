import java.util.function.Predicate;

/**
 * Pila (LIFO) implementada con nodos enlazados.
 * Representa UNA columna fisica de cajas en la bodega:
 * la ultima caja que se pone encima es la primera que se puede sacar.
 */
public class Pila<T> {

    private Nodo<T> cima;          // caja que esta arriba
    private int tamanio;           // cuantas cajas hay
    private final int capacidad;   // maximo de cajas que caben en la columna
    private final String nombre;   // nombre de la columna (A, B, C)

    public Pila(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.cima = null;
        this.tamanio = 0;
    }

    /** push: pone un elemento en la cima. O(1). */
    public void push(T dato) {
        if (estaLlena()) {
            throw new IllegalStateException("La columna " + nombre + " esta llena (" + capacidad + " cajas)");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cima;   // el nuevo nodo queda encima del anterior
        cima = nuevo;
        tamanio++;
    }

    /** pop: retira y devuelve el elemento de la cima. O(1). */
    public T pop() {
        if (estaVacia()) {
            throw new IllegalStateException("La columna " + nombre + " esta vacia");
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        tamanio--;
        return dato;
    }

    /** peek: consulta la cima sin retirarla. O(1). */
    public T peek() {
        if (estaVacia()) {
            throw new IllegalStateException("La columna " + nombre + " esta vacia");
        }
        return cima.dato;
    }

    /**
     * Consulta de solo lectura (como java.util.Stack.search):
     * devuelve cuantos elementos hay ENCIMA del primero que cumple la condicion,
     * o -1 si no existe. No modifica la pila. O(n).
     */
    public int elementosEncimaDe(Predicate<T> condicion) {
        Nodo<T> actual = cima;
        int encima = 0;
        while (actual != null) {
            if (condicion.test(actual.dato)) {
                return encima;
            }
            encima++;
            actual = actual.siguiente;
        }
        return -1;
    }

    /** Cuenta los elementos que cumplen la condicion (solo lectura). */
    public int contar(Predicate<T> condicion) {
        int total = 0;
        Nodo<T> actual = cima;
        while (actual != null) {
            if (condicion.test(actual.dato)) {
                total++;
            }
            actual = actual.siguiente;
        }
        return total;
    }

    public boolean estaVacia() { return cima == null; }
    public boolean estaLlena() { return tamanio == capacidad; }
    public int espacioLibre()  { return capacidad - tamanio; }
    public int tamanio()       { return tamanio; }
    public int getCapacidad()  { return capacidad; }
    public String getNombre()  { return nombre; }

    /** Devuelve el elemento en la posicion indicada contando desde la cima (0 = cima). Solo para mostrar. */
    public T verDesdeCima(int posicion) {
        Nodo<T> actual = cima;
        for (int i = 0; i < posicion && actual != null; i++) {
            actual = actual.siguiente;
        }
        return actual == null ? null : actual.dato;
    }
}
