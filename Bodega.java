import java.util.ArrayList;
import java.util.List;

/**
 * Bodega de insumos de Asoflor Oriente (asociacion ficticia).
 *  - Cada columna de cajas es una Pila (LIFO): solo se saca la caja de arriba.
 *  - Las solicitudes de los invernaderos van en una Cola (FIFO): se atienden en orden de llegada.
 *  - Al sacar una caja que no esta arriba, las cajas que se mueven temporalmente se recuerdan
 *    en otra Pila, para devolverlas en el orden inverso y dejar la columna como estaba.
 *  - EA3: un Arbol Binario de Busqueda (indice de lotes) guarda cada lote con la columna donde
 *    esta. Asi, para saber donde esta un lote no hay que revisar todas las pilas caja por caja.
 */
public class Bodega {

    public static final String[] INSUMOS  = {"Fertilizante NPK", "Fungicida", "Abono organico", "Insecticida", "Cal agricola"};
    private static final String[] PREFIJOS = {"NPK", "FUN", "ABO", "INS", "CAL"};

    private final List<Pila<Caja>> columnas = new ArrayList<>();
    private final Cola<Solicitud> solicitudes = new Cola<>();
    private final int[] contadorLotes = new int[INSUMOS.length];
    private final ArbolBST<String, RegistroLote> indice = new ArbolBST<>();   // EA3
    private boolean trazaIndice = false;

    private int solicitudesAtendidas = 0;
    private int solicitudesNoAtendidas = 0;
    private int operacionesPila = 0;

    public Bodega(int numeroColumnas, int capacidadPorColumna) {
        for (int i = 0; i < numeroColumnas; i++) {
            String nombre = String.valueOf((char) ('A' + i));
            columnas.add(new Pila<>(nombre, capacidadPorColumna));
        }
    }

    // ------------------------------------------------------------------
    // Operaciones con traza: cada push, pop, enqueue y dequeue se imprime
    // para dejar evidencia de ejecucion.
    // ------------------------------------------------------------------

    private void apilar(Pila<Caja> columna, Caja caja) {
        columna.push(caja);
        operacionesPila++;
        System.out.printf("  [PUSH]    Columna %s <- %-26s | altura %d/%d%n",
                columna.getNombre(), caja, columna.tamanio(), columna.getCapacidad());
    }

    private Caja desapilar(Pila<Caja> columna) {
        Caja caja = columna.pop();
        operacionesPila++;
        System.out.printf("  [POP]     Columna %s -> %-26s | altura %d/%d%n",
                columna.getNombre(), caja, columna.tamanio(), columna.getCapacidad());
        return caja;
    }

    // ------------------------------------------------------------------
    // Casos de uso
    // ------------------------------------------------------------------

    /** Recibe una caja del proveedor y la apila en la columna con mas espacio libre. */
    public boolean recibirCaja(int indiceInsumo) {
        Pila<Caja> destino = columnaConMasEspacio(null);
        if (destino == null) {
            System.out.println("  [AVISO]   La bodega esta llena. No se puede recibir la caja.");
            return false;
        }
        contadorLotes[indiceInsumo]++;
        String lote = String.format("%s-%03d", PREFIJOS[indiceInsumo], contadorLotes[indiceInsumo]);
        Caja caja = new Caja(lote, INSUMOS[indiceInsumo]);
        apilar(destino, caja);
        indice.insertar(lote, new RegistroLote(caja, destino.getNombre()), trazaIndice);   // EA3
        return true;
    }

    /** Un invernadero registra una solicitud: entra al final de la cola. */
    public Solicitud registrarSolicitud(String invernadero, int indiceInsumo) {
        Solicitud s = new Solicitud(invernadero, INSUMOS[indiceInsumo]);
        solicitudes.enqueue(s);
        System.out.printf("  [ENQUEUE] %-36s | en espera: %d%n", s, solicitudes.tamanio());
        return s;
    }

    /**
     * Atiende la solicitud que esta al frente de la cola.
     * Busca la caja del insumo que requiera menos movimientos y la retira.
     * @return la caja entregada, o null si no se pudo atender.
     */
    public Caja atenderSiguienteSolicitud() {
        if (solicitudes.estaVacia()) {
            System.out.println("  [AVISO]   No hay solicitudes pendientes.");
            return null;
        }
        Solicitud s = solicitudes.dequeue();
        System.out.printf("  [DEQUEUE] %-36s | en espera: %d%n", s, solicitudes.tamanio());

        // 1. Elegir la columna donde la caja tenga menos cajas encima
        Pila<Caja> origen = null;
        int cajasEncima = Integer.MAX_VALUE;
        for (Pila<Caja> columna : columnas) {
            int encima = columna.elementosEncimaDe(c -> c.getInsumo().equals(s.getInsumo()));
            boolean hayEspacioAuxiliar = espacioLibreFuera(columna) >= encima;
            if (encima >= 0 && encima < cajasEncima && hayEspacioAuxiliar) {
                origen = columna;
                cajasEncima = encima;
            }
        }
        if (origen == null) {
            solicitudesNoAtendidas++;
            if (existencias(s.getInsumo()) == 0) {
                System.out.println("  [AVISO]   No hay existencias de " + s.getInsumo()
                        + ". La solicitud S-" + s.getNumero() + " no se atendio.");
            } else {
                System.out.println("  [AVISO]   Hay " + s.getInsumo() + ", pero no hay espacio libre en otras columnas"
                        + " para mover las cajas de encima. La solicitud S-" + s.getNumero() + " no se atendio.");
            }
            return null;
        }
        System.out.println("  [PLAN]    " + s.getInsumo() + " esta en la columna " + origen.getNombre()
                + " con " + cajasEncima + " caja(s) encima -> " + (2 * cajasEncima + 1) + " movimiento(s)");

        // 2. Mover temporalmente las cajas de encima y recordar a donde se fue cada una
        Pila<Pila<Caja>> movimientosTemporales = new Pila<>("temporal", cajasEncima);
        for (int i = 0; i < cajasEncima; i++) {
            Caja caja = desapilar(origen);
            Pila<Caja> auxiliar = columnaConMasEspacio(origen);
            apilar(auxiliar, caja);
            movimientosTemporales.push(auxiliar);
        }

        // 3. Retirar la caja solicitada (ahora esta en la cima)
        Caja entregada = desapilar(origen);
        System.out.println("  [ENTREGA] " + entregada + " para " + s.getInvernadero());
        indice.eliminar(entregada.getLote());   // EA3: el lote ya no esta en la bodega
        System.out.println("  [INDICE]  " + entregada.getLote() + " eliminado del arbol | lotes indexados: " + indice.tamanio());

        // 4. Devolver las cajas en orden inverso (LIFO) para dejar la columna como estaba
        while (!movimientosTemporales.estaVacia()) {
            Pila<Caja> auxiliar = movimientosTemporales.pop();
            apilar(origen, desapilar(auxiliar));
        }

        solicitudesAtendidas++;
        return entregada;
    }

    // ------------------------------------------------------------------
    // EA3: consultas sobre el indice de lotes (Arbol Binario de Busqueda)
    // ------------------------------------------------------------------

    /**
     * Ubica un lote por su codigo con el arbol (O(log n)) y luego calcula
     * cuantas cajas tiene encima solo en SU columna.
     */
    public RegistroLote localizarLote(String lote) {
        RegistroLote r = indice.buscarConTraza(lote.trim().toUpperCase());
        if (r == null) {
            System.out.println("  [AVISO]   El lote " + lote + " no esta en la bodega.");
            return null;
        }
        Pila<Caja> columna = columnas.get(r.getColumna().charAt(0) - 'A');
        int encima = columna.elementosEncimaDe(c -> c.getLote().equals(r.getCaja().getLote()));
        System.out.println("  [UBICADO] " + r + " | cajas encima: " + encima
                + " | comparaciones en el arbol: " + indice.getUltimasComparaciones());
        return r;
    }

    /** Lista los lotes de un insumo usando una busqueda por rango en el arbol. */
    public void listarCategoria(int indiceInsumo) {
        String p = PREFIJOS[indiceInsumo];
        java.util.List<RegistroLote> lotes = indice.rango(p + "-000", p + "-999");
        System.out.println("  Lotes de " + INSUMOS[indiceInsumo] + " (" + lotes.size() + "):");
        for (RegistroLote r : lotes) System.out.println("    " + r);
    }

    public void mostrarIndice() {
        System.out.println("  Indice de lotes (BST) - " + indice.tamanio() + " nodos, altura " + indice.altura()
                + " (minima posible " + ArbolBST.alturaMinima(indice.tamanio()) + ")");
        indice.imprimir();
    }

    public void mostrarRecorridos() {
        System.out.println("  Preorden  (R-I-D): " + indice.preorden());
        System.out.println("  Inorden   (I-R-D): " + indice.inorden());
        System.out.println("  Postorden (I-D-R): " + indice.postorden());
    }

    public void balancearIndice() {
        int antes = indice.altura();
        indice.balancear();
        System.out.println("  [BALANCEO] altura " + antes + " -> " + indice.altura() + " | nueva raiz: " + indice.getRaiz());
    }

    public void setTrazaIndice(boolean traza)        { this.trazaIndice = traza; }
    public ArbolBST<String, RegistroLote> getIndice() { return indice; }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /** Columna con mas espacio libre, excluyendo una (puede ser null). */
    private Pila<Caja> columnaConMasEspacio(Pila<Caja> excluir) {
        Pila<Caja> mejor = null;
        for (Pila<Caja> columna : columnas) {
            if (columna == excluir || columna.estaLlena()) continue;
            if (mejor == null || columna.espacioLibre() > mejor.espacioLibre()) {
                mejor = columna;
            }
        }
        return mejor;
    }

    /** Espacio libre en todas las columnas excepto la indicada. */
    private int espacioLibreFuera(Pila<Caja> columna) {
        int libre = 0;
        for (Pila<Caja> otra : columnas) {
            if (otra != columna) libre += otra.espacioLibre();
        }
        return libre;
    }

    public int existencias(String insumo) {
        int total = 0;
        for (Pila<Caja> columna : columnas) {
            total += columna.contar(c -> c.getInsumo().equals(insumo));
        }
        return total;
    }

    // ------------------------------------------------------------------
    // Reportes
    // ------------------------------------------------------------------

    public void mostrarBodega() {
        int altura = columnas.get(0).getCapacidad();
        System.out.println();
        System.out.println("  Estado de la bodega (la fila de arriba es la cima de cada columna)");
        StringBuilder encabezado = new StringBuilder("        ");
        for (Pila<Caja> c : columnas) encabezado.append(String.format("%-14s", "Columna " + c.getNombre()));
        System.out.println(encabezado);
        for (int nivel = altura; nivel >= 1; nivel--) {
            StringBuilder fila = new StringBuilder(String.format("  N%d    ", nivel));
            for (Pila<Caja> c : columnas) {
                int posicionDesdeCima = c.tamanio() - nivel;
                String texto = posicionDesdeCima >= 0 ? "[" + c.verDesdeCima(posicionDesdeCima).getLote() + "]" : "   .";
                fila.append(String.format("%-14s", texto));
            }
            System.out.println(fila);
        }
        System.out.println();
    }

    public void mostrarSolicitudes() {
        System.out.println("  Solicitudes en espera (" + solicitudes.tamanio() + "), de la primera a la ultima:");
        for (int i = 0; i < solicitudes.tamanio(); i++) {
            System.out.println("    " + (i + 1) + ". " + solicitudes.verDesdeFrente(i));
        }
    }

    public void mostrarResumen() {
        System.out.println("  Resumen de existencias:");
        for (String insumo : INSUMOS) {
            System.out.printf("    %-18s %d caja(s)%n", insumo, existencias(insumo));
        }
        System.out.println("  Solicitudes atendidas: " + solicitudesAtendidas
                + " | no atendidas: " + solicitudesNoAtendidas
                + " | operaciones push/pop: " + operacionesPila);
    }

    public int solicitudesEnEspera()      { return solicitudes.tamanio(); }
    public int getOperacionesPila()    { return operacionesPila; }
    public Pila<Caja> getColumna(int i)   { return columnas.get(i); }
}
