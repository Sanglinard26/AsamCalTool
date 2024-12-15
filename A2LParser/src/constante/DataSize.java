/*
 * Creation : 3 avr. 2019
 */
package constante;

public enum DataSize {

    BYTE(8), WORD(16), LONG(32), UNKNOWN(0);

    private int nbBits;

    private DataSize(int nbBits) {
        this.nbBits = nbBits;
    }

    public final int getNbBits() {
        return nbBits;
    }

    public final int getNbByte() {
        return this.nbBits >> 3;
    }

    public static DataSize getDataSize(String type) {
        switch (type) {
        case "BYTE":
            return BYTE;
        case "WORD":
            return WORD;
        case "LONG":
            return LONG;
        default:
            return UNKNOWN;
        }
    }

}
