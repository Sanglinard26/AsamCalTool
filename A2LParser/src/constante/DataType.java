/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum DataType {

    UBYTE("unsigned 8 Bit", 8), SBYTE("signed 8 Bit", 8), UWORD("unsigned integer 16 Bit", 16), SWORD("signed integer 16 Bit",
            16), ULONG("unsigned integer 32 Bit", 32), SLONG("signed integer 32 Bit", 32), FLOAT32_IEEE("float 32 Bit", 32);

    private int nbbits;

    // Constructeur
    DataType(String name, int nbbits) {
        this.setNbbits(nbbits);

    }

    public int getNbbits() {
        return nbbits;
    }

    public void setNbbits(int nbbits) {
        this.nbbits = nbbits;
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
