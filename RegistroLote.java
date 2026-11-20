/**
 * Valor que guarda el indice de lotes: la caja y la columna fisica donde quedo apilada.
 * La columna no cambia mientras la caja este en la bodega, porque cuando se mueven cajas
 * temporalmente (EA2) siempre se devuelven a su columna de origen.
 */
public class RegistroLote {
    private final Caja caja;
    private final String columna;

    public RegistroLote(Caja caja, String columna) {
        this.caja = caja;
        this.columna = columna;
    }

    public Caja getCaja()      { return caja; }
    public String getColumna() { return columna; }

    @Override
    public String toString() {
        return caja + " en columna " + columna;
    }
}
