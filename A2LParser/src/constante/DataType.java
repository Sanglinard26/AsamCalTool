/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum DataType {

    UBYTE(1), SBYTE(1), UWORD(2), SWORD(2), ULONG(4), SLONG(4), FLOAT16_IEEE(2), FLOAT32_IEEE(4), FLOAT64_IEEE(8), A_UINT64(8), A_INT64(8), UNKNOWN(0);

    private int nbByte;

    // Constructeur
    DataType(int nbByte) {
        this.nbByte = nbByte;
    }

    public final int getNbByte() {
        return nbByte;
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
