/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum AdressType {

    PBYTE(1), PWORD(2), PLONG(4), PLONGLONG(8), DIRECT(0), UNKNOWN(-1);

    private int nbByte;

    // Constructeur
    AdressType(int nbbits) {
        this.setNbByte(nbbits);
    }

    public int getNbByte() {
        return nbByte;
    }

    public void setNbByte(int nbbits) {
        this.nbByte = nbbits;
    }

    public static AdressType getAdressType(String type) {
        switch (type) {
        case "PBYTE":
            return PBYTE;
        case "PWORD":
            return PWORD;
        case "PLONG":
            return PLONG;
        case "PLONGLONG":
            return PLONGLONG;
        case "DIRECT":
            return DIRECT;
        default:
            return UNKNOWN;
        }

    }

}
