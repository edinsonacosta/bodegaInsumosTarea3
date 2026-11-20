/**
 * Caja de insumo agricola almacenada en la bodega (datos ficticios).
 */
public class Caja {
    private final String lote;
    private final String insumo;

    public Caja(String lote, String insumo) {
        this.lote = lote;
        this.insumo = insumo;
    }

    public String getLote()   { return lote; }
    public String getInsumo() { return insumo; }

    @Override
    public String toString() {
        return lote + " (" + insumo + ")";
    }
}
