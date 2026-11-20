import java.util.List;

/**
 * Algoritmos de busqueda sobre estructuras LINEALES, para comparar con el arbol.
 * Cada metodo cuenta las comparaciones que hace (ultimasComparaciones).
 */
public class Busquedas {

    private static int ultimasComparaciones;

    public static int getUltimasComparaciones() { return ultimasComparaciones; }

    /**
     * Busqueda lineal: revisa uno por uno. Funciona en cualquier lista, ordenada o no.
     * O(n). Devuelve la posicion o -1.
     */
    public static <K extends Comparable<K>> int lineal(List<K> lista, K objetivo) {
        ultimasComparaciones = 0;
        for (int i = 0; i < lista.size(); i++) {
            ultimasComparaciones++;
            if (lista.get(i).compareTo(objetivo) == 0) return i;
        }
        return -1;
    }

    /**
     * Busqueda binaria: exige una lista ORDENADA (por ejemplo, el inorden del arbol).
     * En cada paso compara con el elemento del medio y descarta la mitad. O(log n).
     * Devuelve la posicion o -1.
     */
    public static <K extends Comparable<K>> int binaria(List<K> ordenada, K objetivo) {
        ultimasComparaciones = 0;
        int inicio = 0;
        int fin = ordenada.size() - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) >>> 1;
            ultimasComparaciones++;
            int cmp = objetivo.compareTo(ordenada.get(medio));
            if (cmp == 0) return medio;
            if (cmp < 0) fin = medio - 1;
            else inicio = medio + 1;
        }
        return -1;
    }

    /** Busqueda binaria que imprime cada paso (para la evidencia por consola). */
    public static <K extends Comparable<K>> int binariaConTraza(List<K> ordenada, K objetivo) {
        System.out.println("  [BINARIA] " + objetivo + " en una lista ordenada de " + ordenada.size() + " elementos");
        ultimasComparaciones = 0;
        int inicio = 0;
        int fin = ordenada.size() - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) >>> 1;
            ultimasComparaciones++;
            int cmp = objetivo.compareTo(ordenada.get(medio));
            System.out.printf("    paso %d: rango [%d..%d], medio=%d (%s) -> %s%n", ultimasComparaciones, inicio, fin,
                    medio, ordenada.get(medio), cmp == 0 ? "ENCONTRADO" : cmp < 0 ? "mitad izquierda" : "mitad derecha");
            if (cmp == 0) return medio;
            if (cmp < 0) fin = medio - 1;
            else inicio = medio + 1;
        }
        System.out.println("    rango vacio -> NO EXISTE");
        return -1;
    }
}
