import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Experimento con datos SINTETICOS: catalogo de lotes de toda la asociacion
 * (todas las bodegas y el historico), mucho mas grande que una sola bodega.
 *
 * Compara, buscando los mismos codigos:
 *   1. Busqueda lineal en una lista sin ordenar (lo que se hacia en EA1/EA2).
 *   2. Busqueda binaria en la lista ordenada que entrega el inorden del arbol.
 *   3. BST con lotes que llegan en orden aleatorio.
 *   4. BST con lotes que llegan en orden cronologico (consecutivos): el arbol se degenera.
 *   5. El mismo arbol del punto 4 despues de balancear().
 */
public class ComparacionBusquedas {

    private static final String[] PREFIJOS = {"NPK", "FUN", "ABO", "INS", "CAL"};
    private static final int BUSQUEDAS = 2_000;

    public static void main(String[] args) {
        comparar(1_000);
        comparar(10_000);
    }

    public static void comparar(int n) {
        Random azar = new Random(2026);   // semilla fija: los resultados se pueden repetir

        // Llegada cronologica: NPK-00001, FUN-00001, ABO-00001, ..., NPK-00002, ...
        List<String> cronologico = new ArrayList<>(n);
        for (int i = 0; cronologico.size() < n; i++) {
            for (String p : PREFIJOS) {
                if (cronologico.size() < n) cronologico.add(String.format("%s-%05d", p, i + 1));
            }
        }
        List<String> aleatorio = new ArrayList<>(cronologico);
        Collections.shuffle(aleatorio, azar);

        // Estructuras
        List<String> listaSinOrden = new ArrayList<>(aleatorio);
        ArbolBST<String, String> arbolAleatorio = new ArbolBST<>();
        for (String c : aleatorio) arbolAleatorio.insertar(c, c);
        ArbolBST<String, String> arbolDegenerado = new ArbolBST<>();
        for (String c : cronologico) arbolDegenerado.insertar(c, c);
        int alturaDegenerado = arbolDegenerado.altura();
        ArbolBST<String, String> arbolBalanceado = new ArbolBST<>();
        for (String c : cronologico) arbolBalanceado.insertar(c, c);
        arbolBalanceado.balancear();
        List<String> listaOrdenada = arbolAleatorio.inorden();   // el inorden ya viene ordenado

        // Codigos a buscar: 90 % existen, 10 % no existen
        String[] objetivos = new String[BUSQUEDAS];
        for (int i = 0; i < BUSQUEDAS; i++) {
            objetivos[i] = (i % 10 == 9) ? "ZZZ-" + i : aleatorio.get(azar.nextInt(n));
        }

        System.out.println();
        System.out.println("=== Catalogo sintetico de " + n + " lotes | " + BUSQUEDAS + " busquedas (10 % inexistentes) ===");
        System.out.printf("  %-44s %8s %12s %12s%n", "Estructura / algoritmo", "altura", "comp. prom.", "us/busqueda");
        imprimirFila("1. Lista sin orden + busqueda lineal", "-", medirLineal(listaSinOrden, objetivos));
        imprimirFila("2. Lista ordenada (inorden) + binaria", "-", medirBinaria(listaOrdenada, objetivos));
        imprimirFila("3. BST, llegada aleatoria", "" + arbolAleatorio.altura(), medirArbol(arbolAleatorio, objetivos));
        imprimirFila("4. BST, llegada cronologica (degenerado)", "" + alturaDegenerado, medirArbol(arbolDegenerado, objetivos));
        imprimirFila("5. BST del punto 4 despues de balancear()", "" + arbolBalanceado.altura(), medirArbol(arbolBalanceado, objetivos));
        System.out.println("  Altura minima teorica para " + n + " nodos: ceil(log2(n+1)) = " + ArbolBST.alturaMinima(n));
    }

    private static void imprimirFila(String nombre, String altura, double[] m) {
        System.out.printf("  %-44s %8s %12.1f %12.3f%n", nombre, altura, m[0], m[1]);
    }

    // Cada medicion repite las busquedas varias veces; la primera ronda calienta la JVM (JIT).
    private static double[] medirLineal(List<String> lista, String[] objetivos) {
        long comp = 0, tiempo = 0;
        for (int ronda = 0; ronda < 3; ronda++) {
            comp = 0;
            long t0 = System.nanoTime();
            for (String o : objetivos) { Busquedas.lineal(lista, o); comp += Busquedas.getUltimasComparaciones(); }
            tiempo = System.nanoTime() - t0;
        }
        return new double[]{(double) comp / objetivos.length, tiempo / 1000.0 / objetivos.length};
    }

    private static double[] medirBinaria(List<String> lista, String[] objetivos) {
        long comp = 0, tiempo = 0;
        for (int ronda = 0; ronda < 3; ronda++) {
            comp = 0;
            long t0 = System.nanoTime();
            for (String o : objetivos) { Busquedas.binaria(lista, o); comp += Busquedas.getUltimasComparaciones(); }
            tiempo = System.nanoTime() - t0;
        }
        return new double[]{(double) comp / objetivos.length, tiempo / 1000.0 / objetivos.length};
    }

    private static double[] medirArbol(ArbolBST<String, String> arbol, String[] objetivos) {
        long comp = 0, tiempo = 0;
        for (int ronda = 0; ronda < 3; ronda++) {
            comp = 0;
            long t0 = System.nanoTime();
            for (String o : objetivos) { arbol.buscar(o); comp += arbol.getUltimasComparaciones(); }
            tiempo = System.nanoTime() - t0;
        }
        return new double[]{(double) comp / objetivos.length, tiempo / 1000.0 / objetivos.length};
    }
}
