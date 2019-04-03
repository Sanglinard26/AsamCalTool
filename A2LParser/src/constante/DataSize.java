/*
 * Creation : 3 avr. 2019
 */
package constante;

public enum DataSize {

    BYTE(8), WORD(16), LONG(32);

    private int nbBits;

    private DataSize(int nbBits) {
        this.nbBits = nbBits;
    }

    public final int getNbBits() {
        return nbBits;
    }

    public static DataSize getDataSize(String type) {
        switch (type) {
        case "BYTE":
            return DataSize.BYTE;
        case "WORD":
            return DataSize.WORD;
        case "LONG":
            return DataSize.LONG;
        default:
            return null;
        }
    }

}
