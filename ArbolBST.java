import java.util.ArrayList;
import java.util.List;

/**
 * Arbol Binario de Busqueda (TDA) implementado con nodos enlazados.
 *
 * Regla de orden: para todo nodo, las claves de su subarbol izquierdo son MENORES
 * y las de su subarbol derecho son MAYORES. No se admiten claves repetidas.
 *
 * En la bodega de Asoflor Oriente la clave es el codigo de lote ("ABO-002", "NPK-010"...).
 * Como el codigo empieza por el prefijo del insumo, el orden alfabetico agrupa los lotes
 * por categoria: el recorrido inorden entrega el inventario ya clasificado.
 *
 * Complejidad (h = altura del arbol):
 *   insertar, buscar, eliminar: O(h)  ->  O(log n) si el arbol esta balanceado, O(n) si degenera.
 *   recorridos: O(n).
 */
public class ArbolBST<K extends Comparable<K>, V> {

    private NodoArbol<K, V> raiz;
    private int tamanio;
    private int ultimasComparaciones;   // comparaciones de la ultima busqueda (para medir)

    // ==================================================================
    // Insercion
    // ==================================================================

    /** Inserta sin imprimir. Devuelve false si la clave ya existia. */
    public boolean insertar(K clave, V valor) {
        return insertar(clave, valor, false);
    }

    /**
     * Inserta de forma iterativa (no recursiva) para que un arbol degenerado de miles de
     * nodos no agote la pila de llamadas de la JVM.
     * Con traza=true imprime el camino que sigue la clave desde la raiz.
     */
    public boolean insertar(K clave, V valor, boolean traza) {
        NodoArbol<K, V> nuevo = new NodoArbol<>(clave, valor);
        if (raiz == null) {
            raiz = nuevo;
            tamanio++;
            if (traza) System.out.printf("  [INSERT] %-8s -> arbol vacio: queda como RAIZ%n", clave);
            return true;
        }
        StringBuilder camino = new StringBuilder();
        NodoArbol<K, V> actual = raiz;
        while (true) {
            int cmp = clave.compareTo(actual.clave);
            if (cmp == 0) {
                if (traza) System.out.printf("  [INSERT] %-8s -> ya existe, se rechaza (clave duplicada)%n", clave);
                return false;
            }
            camino.append(actual.clave).append(cmp < 0 ? " <izq " : " der> ");
            if (cmp < 0) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = nuevo;
                    break;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = nuevo;
                    break;
                }
                actual = actual.derecho;
            }
        }
        tamanio++;
        if (traza) {
            String lado = actual.izquierdo == nuevo ? "IZQUIERDO" : "DERECHO";
            System.out.printf("  [INSERT] %-8s -> camino: %s| hijo %s de %s%n", clave, camino, lado, actual.clave);
        }
        return true;
    }

    // ==================================================================
    // Busqueda
    // ==================================================================

    /** Busqueda iterativa: en cada paso descarta un subarbol completo. Devuelve null si no existe. */
    public V buscar(K clave) {
        ultimasComparaciones = 0;
        NodoArbol<K, V> actual = raiz;
        while (actual != null) {
            ultimasComparaciones++;
            int cmp = clave.compareTo(actual.clave);
            if (cmp == 0) return actual.valor;
            actual = cmp < 0 ? actual.izquierdo : actual.derecho;
        }
        return null;
    }

    /** Igual que buscar, pero imprime cada nodo visitado y la decision tomada. */
    public V buscarConTraza(K clave) {
        System.out.println("  [BUSCAR] " + clave);
        ultimasComparaciones = 0;
        NodoArbol<K, V> actual = raiz;
        while (actual != null) {
            ultimasComparaciones++;
            int cmp = clave.compareTo(actual.clave);
            if (cmp == 0) {
                System.out.printf("    paso %d: %-8s == %-8s -> ENCONTRADO%n", ultimasComparaciones, clave, actual.clave);
                return actual.valor;
            }
            System.out.printf("    paso %d: %-8s %s %-8s -> sigo por la %s%n", ultimasComparaciones, clave,
                    cmp < 0 ? "<" : ">", actual.clave, cmp < 0 ? "IZQUIERDA" : "DERECHA");
            actual = cmp < 0 ? actual.izquierdo : actual.derecho;
        }
        System.out.println("    se llego a un enlace vacio -> NO EXISTE (" + ultimasComparaciones + " comparaciones)");
        return null;
    }

    public boolean contiene(K clave)       { return buscar(clave) != null; }
    public int getUltimasComparaciones()   { return ultimasComparaciones; }

    /**
     * Busqueda por rango [desde, hasta] en orden. Poda los subarboles que no pueden
     * contener claves del rango, asi que no recorre todo el arbol.
     * Sirve para listar una categoria: rango("NPK-000", "NPK-999").
     */
    public List<V> rango(K desde, K hasta) {
        List<V> resultado = new ArrayList<>();
        rango(raiz, desde, hasta, resultado);
        return resultado;
    }

    private void rango(NodoArbol<K, V> nodo, K desde, K hasta, List<V> resultado) {
        if (nodo == null) return;
        if (desde.compareTo(nodo.clave) < 0) rango(nodo.izquierdo, desde, hasta, resultado);
        if (desde.compareTo(nodo.clave) <= 0 && hasta.compareTo(nodo.clave) >= 0) resultado.add(nodo.valor);
        if (hasta.compareTo(nodo.clave) > 0) rango(nodo.derecho, desde, hasta, resultado);
    }

    // ==================================================================
    // Eliminacion (cuando una caja sale de la bodega)
    // ==================================================================

    /** Elimina la clave si existe. Devuelve true si se elimino. */
    public boolean eliminar(K clave) {
        int antes = tamanio;
        raiz = eliminar(raiz, clave);
        return tamanio < antes;
    }

    private NodoArbol<K, V> eliminar(NodoArbol<K, V> nodo, K clave) {
        if (nodo == null) return null;                       // no estaba
        int cmp = clave.compareTo(nodo.clave);
        if (cmp < 0) {
            nodo.izquierdo = eliminar(nodo.izquierdo, clave);
        } else if (cmp > 0) {
            nodo.derecho = eliminar(nodo.derecho, clave);
        } else {
            tamanio--;
            // Caso 1 y 2: hoja o un solo hijo -> el hijo (o null) ocupa su lugar
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho == null) return nodo.izquierdo;
            // Caso 3: dos hijos -> se reemplaza por el sucesor (minimo del subarbol derecho)
            NodoArbol<K, V> sucesor = nodo.derecho;
            while (sucesor.izquierdo != null) sucesor = sucesor.izquierdo;
            nodo.clave = sucesor.clave;
            nodo.valor = sucesor.valor;
            tamanio++;                                       // se descuenta al borrar el sucesor
            nodo.derecho = eliminar(nodo.derecho, sucesor.clave);
        }
        return nodo;
    }

    // ==================================================================
    // Recorridos en profundidad (recursivos)
    // ==================================================================

    /** Preorden: Raiz - Izquierdo - Derecho. Sirve para copiar o reconstruir el arbol. */
    public List<K> preorden() {
        List<K> lista = new ArrayList<>();
        preorden(raiz, lista);
        return lista;
    }

    private void preorden(NodoArbol<K, V> nodo, List<K> lista) {
        if (nodo == null) return;
        lista.add(nodo.clave);
        preorden(nodo.izquierdo, lista);
        preorden(nodo.derecho, lista);
    }

    /** Inorden: Izquierdo - Raiz - Derecho. En un BST entrega las claves ORDENADAS. */
    public List<K> inorden() {
        List<K> lista = new ArrayList<>();
        inorden(raiz, lista);
        return lista;
    }

    private void inorden(NodoArbol<K, V> nodo, List<K> lista) {
        if (nodo == null) return;
        inorden(nodo.izquierdo, lista);
        lista.add(nodo.clave);
        inorden(nodo.derecho, lista);
    }

    /** Postorden: Izquierdo - Derecho - Raiz. Procesa los hijos antes que el padre (p. ej. liberar memoria). */
    public List<K> postorden() {
        List<K> lista = new ArrayList<>();
        postorden(raiz, lista);
        return lista;
    }

    private void postorden(NodoArbol<K, V> nodo, List<K> lista) {
        if (nodo == null) return;
        postorden(nodo.izquierdo, lista);
        postorden(nodo.derecho, lista);
        lista.add(nodo.clave);
    }

    /**
     * Inorden ITERATIVO usando la Pila de la EA2 (sin recursion).
     * Se usa en el balanceo, donde el arbol puede estar degenerado y tener miles de niveles.
     */
    private List<NodoArbol<K, V>> nodosEnInorden() {
        List<NodoArbol<K, V>> lista = new ArrayList<>(tamanio);
        Pila<NodoArbol<K, V>> pendientes = new Pila<>("inorden", Math.max(1, tamanio));
        NodoArbol<K, V> actual = raiz;
        while (actual != null || !pendientes.estaVacia()) {
            while (actual != null) {           // bajar todo a la izquierda
                pendientes.push(actual);
                actual = actual.izquierdo;
            }
            actual = pendientes.pop();         // visitar
            lista.add(actual);
            actual = actual.derecho;           // seguir por la derecha
        }
        return lista;
    }

    // ==================================================================
    // Balanceo
    // ==================================================================

    /**
     * Reconstruye el arbol con altura minima: toma los nodos en inorden (ya ordenados)
     * y elige siempre el elemento del medio como raiz de cada subarbol.
     * Tiempo O(n). Memoria extra O(n) por la lista temporal (solo referencias, los nodos se reutilizan).
     */
    public void balancear() {
        List<NodoArbol<K, V>> nodos = nodosEnInorden();
        raiz = construirBalanceado(nodos, 0, nodos.size() - 1);
    }

    private NodoArbol<K, V> construirBalanceado(List<NodoArbol<K, V>> nodos, int inicio, int fin) {
        if (inicio > fin) return null;
        int medio = (inicio + fin) >>> 1;      // evita desbordamiento de (inicio + fin) / 2
        NodoArbol<K, V> nodo = nodos.get(medio);
        nodo.izquierdo = construirBalanceado(nodos, inicio, medio - 1);
        nodo.derecho = construirBalanceado(nodos, medio + 1, fin);
        return nodo;
    }

    // ==================================================================
    // Consultas y presentacion
    // ==================================================================

    /** Altura en niveles (arbol vacio = 0, solo raiz = 1). Iterativa por niveles con la Cola de la EA2. */
    public int altura() {
        if (raiz == null) return 0;
        Cola<NodoArbol<K, V>> cola = new Cola<>();
        cola.enqueue(raiz);
        int niveles = 0;
        while (!cola.estaVacia()) {
            int enEsteNivel = cola.tamanio();
            for (int i = 0; i < enEsteNivel; i++) {
                NodoArbol<K, V> n = cola.dequeue();
                if (n.izquierdo != null) cola.enqueue(n.izquierdo);
                if (n.derecho != null) cola.enqueue(n.derecho);
            }
            niveles++;
        }
        return niveles;
    }

    /** Altura minima posible para n nodos: ceil(log2(n + 1)). */
    public static int alturaMinima(int n) {
        return n == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(n);
    }

    public int tamanio()       { return tamanio; }
    public boolean estaVacio() { return raiz == null; }
    public K getRaiz()         { return raiz == null ? null : raiz.clave; }

    /** Dibuja el arbol "acostado": la raiz a la izquierda, el subarbol derecho arriba. */
    public void imprimir() {
        if (raiz == null) {
            System.out.println("  (arbol vacio)");
            return;
        }
        imprimir(raiz, "", true, true);
    }

    private void imprimir(NodoArbol<K, V> nodo, String prefijo, boolean esDerecho, boolean esRaiz) {
        if (nodo.derecho != null) {
            imprimir(nodo.derecho, prefijo + (esRaiz ? "    " : esDerecho ? "        " : "|       "), true, false);
        }
        String conector = esRaiz ? "" : (esDerecho ? "/------ " : "\\------ ");
        System.out.println("  " + prefijo + conector + nodo.clave);
        if (nodo.izquierdo != null) {
            imprimir(nodo.izquierdo, prefijo + (esRaiz ? "    " : esDerecho ? "|       " : "        "), false, false);
        }
    }

    /** Exporta el arbol en formato DOT (Graphviz) para generar el grafico del documento. */
    public String aDot() {
        StringBuilder sb = new StringBuilder("digraph BST {\n  node [shape=circle];\n");
        aDot(raiz, sb);
        return sb.append("}\n").toString();
    }

    private void aDot(NodoArbol<K, V> n, StringBuilder sb) {
        if (n == null) return;
        sb.append("  \"").append(n.clave).append("\";\n");
        if (n.izquierdo != null) sb.append("  \"").append(n.clave).append("\" -> \"").append(n.izquierdo.clave).append("\" [label=\"izq\"];\n");
        if (n.derecho != null)   sb.append("  \"").append(n.clave).append("\" -> \"").append(n.derecho.clave).append("\" [label=\"der\"];\n");
        aDot(n.izquierdo, sb);
        aDot(n.derecho, sb);
    }
}
