/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum DataType {

    UBYTE(1), SBYTE(1), UWORD(2), SWORD(2), ULONG(4), SLONG(4), FLOAT32_IEEE(4);

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
            return DataType.UBYTE;
        case "SBYTE":
            return DataType.SBYTE;
        case "UWORD":
            return DataType.UWORD;
        case "SWORD":
            return DataType.SWORD;
        case "ULONG":
            return DataType.ULONG;
        case "SLONG":
            return DataType.SLONG;
        case "FLOAT32_IEEE":
            return DataType.FLOAT32_IEEE;
        default:
            return null;
        }

    }

}
