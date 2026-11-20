/**
 * Solicitud de un insumo hecha por un invernadero (datos ficticios).
 */
public class Solicitud {
    private static int consecutivo = 1;

    private final int numero;
    private final String invernadero;
    private final String insumo;

    public Solicitud(String invernadero, String insumo) {
        this.numero = consecutivo++;
        this.invernadero = invernadero;
        this.insumo = insumo;
    }

    public int getNumero()         { return numero; }
    public String getInvernadero() { return invernadero; }
    public String getInsumo()      { return insumo; }

    @Override
    public String toString() {
        return "S-" + numero + " " + invernadero + " pide " + insumo;
    }
}
