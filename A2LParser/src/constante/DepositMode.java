/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum DepositMode {

	ABSOLUTE, DIFFERENCE, UNKNOWN;

    public static DepositMode getDepositMode(String type) {
        switch (type) {
        case "ABSOLUTE":
            return ABSOLUTE;
        case "DIFFERENCE":
            return DIFFERENCE;
        default:
            return UNKNOWN;
        }

    }

}
