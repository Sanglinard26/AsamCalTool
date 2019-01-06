/*
 * Creation : 2 mars 2018
 */
package constante;

public enum PrimaryKeywords {
    CHARACTERISTIC("CHARACTERISTIC"), COMPU_METHOD("COMPU_METHOD"), COMPU_TAB("COMPU_TAB"), COMPU_VTAB("COMPU_VTAB");

    private final String name;

    private PrimaryKeywords(String name) {
        this.name = name;
    }

}
