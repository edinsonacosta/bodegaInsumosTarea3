/**
 * Pruebas del prototipo. Cada prueba imprime PASA o FALLA.
 * Ejecutar:  javac *.java  &&  java PruebasBodega
 */
public class PruebasBodega {

    private static int pasan = 0;
    private static int fallan = 0;

    public static void main(String[] args) {
        System.out.println("===== PRUEBAS UNITARIAS DE LA PILA Y LA COLA =====");
        pruebaPilaLIFO();
        pruebaPilaLlena();
        pruebaPilaVacia();
        pruebaColaFIFO();
        pruebaColaVacia();

        System.out.println();
        System.out.println("===== ESCENARIO COMPLETO DE LA BODEGA =====");
        escenarioBodega();

        System.out.println();
        System.out.println("===== RESULTADO: " + pasan + " pruebas pasan, " + fallan + " fallan =====");
    }

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) pasan++; else fallan++;
        System.out.println((condicion ? "  PASA  " : "  FALLA ") + nombre);
    }

    // ---------------- Pila ----------------

    private static void pruebaPilaLIFO() {
        Pila<String> p = new Pila<>("prueba", 5);
        p.push("NPK-01");
        p.push("FUN-01");
        p.push("ABO-01");
        String primero = p.pop();
        String segundo = p.pop();
        verificar("P1 La pila devuelve en orden LIFO (ABO-01, luego FUN-01)",
                primero.equals("ABO-01") && segundo.equals("FUN-01") && p.peek().equals("NPK-01"));
    }

    private static void pruebaPilaLlena() {
        Pila<String> p = new Pila<>("A", 5);
        for (int i = 1; i <= 5; i++) p.push("caja-" + i);
        boolean rechazo = false;
        try {
            p.push("caja-6");
        } catch (IllegalStateException e) {
            rechazo = true;
            System.out.println("        mensaje: " + e.getMessage());
        }
        verificar("P2 Una columna con 5 cajas rechaza la sexta", rechazo && p.tamanio() == 5);
    }

    private static void pruebaPilaVacia() {
        Pila<String> p = new Pila<>("B", 5);
        boolean rechazo = false;
        try {
            p.pop();
        } catch (IllegalStateException e) {
            rechazo = true;
            System.out.println("        mensaje: " + e.getMessage());
        }
        verificar("P3 pop sobre una columna vacia se controla sin cerrar el programa", rechazo);
    }

    // ---------------- Cola ----------------

    private static void pruebaColaFIFO() {
        Cola<String> c = new Cola<>();
        c.enqueue("INV-3");
        c.enqueue("INV-1");
        c.enqueue("INV-5");
        verificar("P4 La cola atiende en orden FIFO (INV-3, INV-1, INV-5)",
                c.dequeue().equals("INV-3") && c.dequeue().equals("INV-1") && c.dequeue().equals("INV-5"));
    }

    private static void pruebaColaVacia() {
        Cola<String> c = new Cola<>();
        boolean rechazo = false;
        try {
            c.dequeue();
        } catch (IllegalStateException e) {
            rechazo = true;
            System.out.println("        mensaje: " + e.getMessage());
        }
        verificar("P5 dequeue sobre una cola vacia se controla", rechazo && c.estaVacia());
    }

    // ---------------- Escenario integrado ----------------

    private static void escenarioBodega() {
        Bodega bodega = new Bodega(3, 5);

        System.out.println("\n-- 1. Llega el pedido del proveedor (8 cajas) --");
        Main.cargarDatosDePrueba(bodega);
        bodega.mostrarBodega();
        verificar("E1 Se apilaron 8 cajas repartidas en las 3 columnas",
                bodega.getColumna(0).tamanio() + bodega.getColumna(1).tamanio() + bodega.getColumna(2).tamanio() == 8);

        System.out.println("-- 2. Los invernaderos registran solicitudes --");
        bodega.registrarSolicitud("INV-2", 3);   // Insecticida
        bodega.registrarSolicitud("INV-4", 0);   // Fertilizante NPK
        bodega.registrarSolicitud("INV-1", 4);   // Cal agricola
        bodega.registrarSolicitud("INV-5", 3);   // Insecticida (ya no habra)
        bodega.mostrarSolicitudes();
        verificar("E2 Hay 4 solicitudes en espera", bodega.solicitudesEnEspera() == 4);

        System.out.println("\n-- 3. Se atiende la primera solicitud (INV-2, Insecticida) --");
        Caja c1 = bodega.atenderSiguienteSolicitud();
        verificar("E3 Se atiende primero a INV-2 (FIFO) y se entrega Insecticida",
                c1 != null && c1.getInsumo().equals("Insecticida"));

        System.out.println("\n-- 4. Se atiende la segunda solicitud (INV-4, Fertilizante NPK) --");
        Caja c2 = bodega.atenderSiguienteSolicitud();
        verificar("E4 Se entrega la caja de NPK que exige menos movimientos",
                c2 != null && c2.getInsumo().equals("Fertilizante NPK"));
        bodega.mostrarBodega();

        System.out.println("-- 5. Se atiende la tercera solicitud (INV-1, Cal agricola) --");
        Caja c3 = bodega.atenderSiguienteSolicitud();
        verificar("E5 Se entrega Cal agricola", c3 != null && c3.getInsumo().equals("Cal agricola"));

        System.out.println("\n-- 6. Se atiende la cuarta solicitud (INV-5, Insecticida sin existencias) --");
        Caja c4 = bodega.atenderSiguienteSolicitud();
        verificar("E6 Sin existencias: se informa y no se rompe el programa", c4 == null);

        System.out.println("\n-- 7. Se intenta atender con la cola vacia --");
        Caja c5 = bodega.atenderSiguienteSolicitud();
        verificar("E7 Cola vacia: se informa que no hay solicitudes", c5 == null && bodega.solicitudesEnEspera() == 0);

        System.out.println("\n-- 8. Se llena la bodega (15 cajas) y llega una caja mas --");
        int recibidas = 0;
        while (bodega.recibirCaja(1)) recibidas++;
        bodega.mostrarBodega();
        verificar("E8 Con las 15 posiciones ocupadas, la caja adicional se rechaza",
                bodega.getColumna(0).estaLlena() && bodega.getColumna(1).estaLlena() && bodega.getColumna(2).estaLlena());

        System.out.println("-- 9. Con la bodega llena, INV-3 pide Fertilizante NPK (esta al fondo de la columna A) --");
        bodega.registrarSolicitud("INV-3", 0);
        Caja c6 = bodega.atenderSiguienteSolicitud();
        verificar("E9 Sin espacio para mover las 4 cajas de encima, el sistema no desarma la columna",
                c6 == null && bodega.existencias("Fertilizante NPK") == 1);

        System.out.println();
        bodega.mostrarResumen();
    }
}
