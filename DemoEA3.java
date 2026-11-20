import java.util.List;

/**
 * Recorrido guiado de la EA3 para la evidencia por consola (capturas del documento y del video).
 * Ejecutar:  javac *.java  &&  java DemoEA3
 */
public class DemoEA3 {

    public static void main(String[] args) {
        Bodega bodega = new Bodega(3, 5);
        bodega.setTrazaIndice(true);

        titulo("1. INSERCION: llega el pedido del proveedor (8 cajas) y cada lote entra al indice");
        Main.cargarDatosDePrueba(bodega);
        bodega.mostrarBodega();

        titulo("2. ESTRUCTURA: arbol del indice (raiz a la izquierda, subarbol derecho arriba)");
        bodega.mostrarIndice();

        titulo("3. RECORRIDOS DEL ARBOL");
        bodega.mostrarRecorridos();

        titulo("4. BUSQUEDA EN EL BST: registro existente y registro inexistente");
        bodega.localizarLote("CAL-001");
        System.out.println();
        bodega.localizarLote("INS-007");

        titulo("5. BUSQUEDA BINARIA sobre la lista ordenada que entrega el inorden");
        List<String> ordenada = bodega.getIndice().inorden();
        int pos = Busquedas.binariaConTraza(ordenada, "FUN-002");
        System.out.println("  Resultado: posicion " + pos + " | comparaciones: " + Busquedas.getUltimasComparaciones());
        Busquedas.lineal(ordenada, "FUN-002");
        System.out.println("  La busqueda lineal del mismo codigo necesito " + Busquedas.getUltimasComparaciones() + " comparaciones");

        titulo("6. CATEGORIA: lotes de Fertilizante NPK (busqueda por rango en el arbol)");
        bodega.listarCategoria(0);

        titulo("7. ELIMINACION: se atiende una solicitud y el lote entregado sale del indice");
        bodega.registrarSolicitud("INV-4", 0);   // Fertilizante NPK
        bodega.atenderSiguienteSolicitud();
        bodega.mostrarIndice();

        titulo("8. BALANCEO del indice");
        bodega.balancearIndice();
        bodega.mostrarIndice();
        bodega.mostrarRecorridos();

        titulo("9. ESCALA: comparacion con un catalogo sintetico de 10.000 lotes");
        ComparacionBusquedas.comparar(10_000);
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("==========================================================================");
        System.out.println(" " + texto);
        System.out.println("==========================================================================");
    }
}
