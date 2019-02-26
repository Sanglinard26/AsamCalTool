/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum AdressType {

    PBYTE(1), PWORD(2), PLONG(4), PLONGLONG(8), DIRECT(0);

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
            return AdressType.PBYTE;
        case "PWORD":
            return AdressType.PWORD;
        case "PLONG":
            return AdressType.PLONG;
        case "PLONGLONG":
            return AdressType.PLONGLONG;
        case "DIRECT":
            return AdressType.DIRECT;
        default:
            return null;
        }

    }

}
