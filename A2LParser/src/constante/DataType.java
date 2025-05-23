/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum DataType {

    UBYTE(1, true), SBYTE(1, true), UWORD(2, true), SWORD(2, true), ULONG(4, true), SLONG(4, true), FLOAT16_IEEE(2, false), FLOAT32_IEEE(4,
            false), FLOAT64_IEEE(8, false), A_UINT64(8, true), A_INT64(8, true), UNKNOWN(0, false);

    private byte nbByte;
    private boolean integer;

    // Constructeur
    DataType(int nbByte, boolean integer) {
        this.nbByte = (byte) nbByte;
        this.integer = integer;
    }

    public final byte getNbByte() {
        return nbByte;
    }

    public boolean isInteger() {
        return integer;
    }

    public static DataType getDataType(String type) {
        switch (type) {
        case "UBYTE":
            return UBYTE;
        case "SBYTE":
            return SBYTE;
        case "UWORD":
            return UWORD;
        case "SWORD":
            return SWORD;
        case "ULONG":
            return ULONG;
        case "SLONG":
            return SLONG;
        case "FLOAT16_IEEE":
            return FLOAT16_IEEE;
        case "FLOAT32_IEEE":
            return FLOAT32_IEEE;
        case "FLOAT64_IEEE":
            return FLOAT64_IEEE;
        case "A_UINT64":
            return A_UINT64;
        case "A_INT64":
            return A_INT64;
        default:
            return UNKNOWN;
        }
    }

}
