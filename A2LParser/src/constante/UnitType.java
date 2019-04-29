/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum UnitType {

    DERIVED, EXTENDED_SI, UNKNOWN;

    public static UnitType getUnitType(String type) {
        switch (type) {
        case "DERIVED":
            return DERIVED;
        case "EXTENDED_SI":
            return EXTENDED_SI;
        default:
            return UNKNOWN;
        }

    }

}
