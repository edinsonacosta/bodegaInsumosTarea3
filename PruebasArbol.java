import java.util.Arrays;
import java.util.List;

/**
 * Pruebas del Arbol Binario de Busqueda (EA3). Cada prueba imprime PASA o FALLA.
 * Ejecutar:  javac *.java  &&  java PruebasArbol
 */
public class PruebasArbol {

    private static int pasan = 0;
    private static int fallan = 0;

    public static void main(String[] args) {
        System.out.println("===== PRUEBAS DEL ARBOL BINARIO DE BUSQUEDA =====");

        // Arbol de referencia construido con el pedido de prueba de la EA2
        ArbolBST<String, String> a = arbolDePrueba();

        verificar("A1 Se insertan 8 lotes y la raiz es el primero en llegar (NPK-001)",
                a.tamanio() == 8 && a.getRaiz().equals("NPK-001"));
        verificar("A2 Una clave duplicada se rechaza", !a.insertar("FUN-001", "x") && a.tamanio() == 8);
        verificar("A3 Preorden esperado (R-I-D)", a.preorden().equals(lista(
                "NPK-001", "FUN-001", "ABO-001", "CAL-001", "ABO-002", "INS-001", "FUN-002", "NPK-002")));
        verificar("A4 Inorden esperado: claves ordenadas y agrupadas por insumo", a.inorden().equals(lista(
                "ABO-001", "ABO-002", "CAL-001", "FUN-001", "FUN-002", "INS-001", "NPK-001", "NPK-002")));
        verificar("A5 Postorden esperado (I-D-R)", a.postorden().equals(lista(
                "ABO-002", "CAL-001", "ABO-001", "FUN-002", "INS-001", "FUN-001", "NPK-002", "NPK-001")));
        verificar("A6 Busqueda de un lote existente (CAL-001) en 4 comparaciones",
                "CAL-001".equals(a.buscar("CAL-001")) && a.getUltimasComparaciones() == 4);
        verificar("A7 Busqueda de un lote inexistente devuelve null", a.buscar("INS-007") == null);
        verificar("A8 Rango de una categoria (FUN) devuelve sus 2 lotes en orden",
                a.rango("FUN-000", "FUN-999").equals(lista("FUN-001", "FUN-002")));

        ArbolBST<String, String> b = arbolDePrueba();
        verificar("A9 Eliminar una hoja (ABO-002)", b.eliminar("ABO-002") && !b.contiene("ABO-002") && b.tamanio() == 7);
        verificar("A10 Eliminar un nodo con un hijo (INS-001)", b.eliminar("INS-001") && b.contiene("FUN-002") && b.tamanio() == 6);
        verificar("A11 Eliminar la raiz con dos hijos (NPK-001): la reemplaza su sucesor",
                b.eliminar("NPK-001") && b.getRaiz().equals("NPK-002") && b.tamanio() == 5);
        verificar("A12 Despues de eliminar, el inorden sigue ordenado",
                b.inorden().equals(lista("ABO-001", "CAL-001", "FUN-001", "FUN-002", "NPK-002")));
        verificar("A13 Eliminar un lote que no existe no cambia el arbol", !b.eliminar("ZZZ-001") && b.tamanio() == 5);

        ArbolBST<String, String> c = arbolDePrueba();
        List<String> antes = c.inorden();
        c.balancear();
        verificar("A14 Balancear deja la altura minima (8 nodos -> 4) sin perder ni desordenar datos",
                c.altura() == ArbolBST.alturaMinima(8) && c.inorden().equals(antes) && c.tamanio() == 8);

        ArbolBST<Integer, Integer> d = new ArbolBST<>();
        for (int i = 1; i <= 20_000; i++) d.insertar(i, i);
        boolean sinDesborde = d.altura() == 20_000;
        d.balancear();
        verificar("A15 Arbol degenerado de 20.000 nodos: se inserta y balancea sin StackOverflowError",
                sinDesborde && d.altura() == 15 && d.buscar(12_345) == 12_345);

        List<String> ordenada = a.inorden();
        verificar("A16 Busqueda binaria encuentra FUN-002 en la posicion 4",
                Busquedas.binaria(ordenada, "FUN-002") == 4 && Busquedas.getUltimasComparaciones() == 3);
        verificar("A17 Busqueda binaria y lineal coinciden en un codigo inexistente",
                Busquedas.binaria(ordenada, "INS-007") == -1 && Busquedas.lineal(ordenada, "INS-007") == -1);

        ArbolBST<String, String> vacio = new ArbolBST<>();
        verificar("A18 Arbol vacio: altura 0, recorridos vacios y busqueda null",
                vacio.altura() == 0 && vacio.inorden().isEmpty() && vacio.buscar("NPK-001") == null);

        System.out.println();
        System.out.println("===== RESULTADO: " + pasan + " pruebas pasan, " + fallan + " fallan =====");
    }

    static ArbolBST<String, String> arbolDePrueba() {
        ArbolBST<String, String> a = new ArbolBST<>();
        for (String lote : new String[]{"NPK-001", "FUN-001", "ABO-001", "NPK-002", "INS-001", "CAL-001", "FUN-002", "ABO-002"}) {
            a.insertar(lote, lote);
        }
        return a;
    }

    private static List<String> lista(String... v) { return Arrays.asList(v); }

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) pasan++; else fallan++;
        System.out.println((condicion ? "  PASA  " : "  FALLA ") + nombre);
    }
}
