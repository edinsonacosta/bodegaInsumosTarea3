/**
 * Nodo del Arbol Binario de Busqueda (EA3).
 * A diferencia del Nodo de la Pila y la Cola (un solo enlace "siguiente"),
 * este nodo tiene DOS enlaces: hijo izquierdo (claves menores) e hijo derecho (claves mayores).
 */
public class NodoArbol<K, V> {
    K clave;                    // codigo de lote, por ejemplo "NPK-003"
    V valor;                    // registro asociado (caja + columna donde esta)
    NodoArbol<K, V> izquierdo;  // subarbol con claves menores
    NodoArbol<K, V> derecho;    // subarbol con claves mayores

    public NodoArbol(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
