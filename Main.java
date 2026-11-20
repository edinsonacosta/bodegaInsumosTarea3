import java.util.Scanner;

/**
 * Prototipo de consola: Bodega de insumos de Asoflor Oriente (datos ficticios).
 * EA2: Pilas (columnas) y Cola (solicitudes).  EA3: Arbol Binario de Busqueda (indice de lotes).
 * Ejecutar:  javac *.java  &&  java Main
 */
public class Main {

    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        Bodega bodega = new Bodega(3, 5);   // 3 columnas (A, B, C) de 5 cajas cada una
        int opcion;
        do {
            System.out.println("=============================================");
            System.out.println(" BODEGA DE INSUMOS - ASOFLOR ORIENTE");
            System.out.println("=============================================");
            System.out.println(" 1. Recibir caja del proveedor        (push)");
            System.out.println(" 2. Registrar solicitud de invernadero (enqueue)");
            System.out.println(" 3. Atender siguiente solicitud  (dequeue + pop)");
            System.out.println(" 4. Ver bodega");
            System.out.println(" 5. Ver solicitudes en espera");
            System.out.println(" 6. Ver resumen de existencias");
            System.out.println(" 7. Cargar datos de prueba");
            System.out.println(" --- EA3: indice de lotes (Arbol Binario de Busqueda) ---");
            System.out.println(" 8. Buscar un lote por codigo          (BST)");
            System.out.println(" 9. Ver el arbol del indice");
            System.out.println("10. Ver recorridos Pre/In/Postorden");
            System.out.println("11. Listar lotes de un insumo        (rango en el BST)");
            System.out.println("12. Balancear el indice");
            System.out.println("13. Comparar busquedas con 10.000 lotes sinteticos");
            System.out.println(" 0. Salir");
            opcion = leerEntero(" Opcion: ", 0, 13);

            switch (opcion) {
                case 1:
                    bodega.recibirCaja(elegirInsumo());
                    break;
                case 2:
                    int inv = leerEntero(" Numero de invernadero (1-5): ", 1, 5);
                    bodega.registrarSolicitud("INV-" + inv, elegirInsumo());
                    break;
                case 3:
                    bodega.atenderSiguienteSolicitud();
                    break;
                case 4:
                    bodega.mostrarBodega();
                    break;
                case 5:
                    bodega.mostrarSolicitudes();
                    break;
                case 6:
                    bodega.mostrarResumen();
                    break;
                case 7:
                    cargarDatosDePrueba(bodega);
                    break;
                case 8:
                    System.out.print(" Codigo de lote (ej. NPK-002): ");
                    bodega.localizarLote(teclado.nextLine());
                    break;
                case 9:
                    bodega.mostrarIndice();
                    break;
                case 10:
                    bodega.mostrarRecorridos();
                    break;
                case 11:
                    bodega.listarCategoria(elegirInsumo());
                    break;
                case 12:
                    bodega.balancearIndice();
                    break;
                case 13:
                    ComparacionBusquedas.comparar(10_000);
                    break;
                default:
                    System.out.println(" Hasta luego.");
            }
        } while (opcion != 0);
    }

    private static int elegirInsumo() {
        for (int i = 0; i < Bodega.INSUMOS.length; i++) {
            System.out.println("   " + (i + 1) + ". " + Bodega.INSUMOS[i]);
        }
        return leerEntero(" Insumo: ", 1, Bodega.INSUMOS.length) - 1;
    }

    /** Lee un entero dentro de un rango y vuelve a preguntar si el dato no es valido. */
    private static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = teclado.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) return valor;
            } catch (NumberFormatException e) {
                // se vuelve a preguntar
            }
            System.out.println(" Valor no valido. Escriba un numero entre " + min + " y " + max + ".");
        }
    }

    /** Llegada de un pedido del proveedor: 8 cajas en este orden. */
    public static void cargarDatosDePrueba(Bodega bodega) {
        int[] llegada = {0, 1, 2, 0, 3, 4, 1, 2};   // NPK, FUN, ABO, NPK, INS, CAL, FUN, ABO
        for (int insumo : llegada) {
            bodega.recibirCaja(insumo);
        }
    }
}
