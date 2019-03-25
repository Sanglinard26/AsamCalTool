/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum DepositMode {

	ABSOLUTE, DIFFERENCE;

    public static DepositMode getDepositMode(String type) {
        switch (type) {
        case "ABSOLUTE":
            return DepositMode.ABSOLUTE;
        case "DIFFERENCE":
            return DepositMode.DIFFERENCE;
        default:
            return null;
        }

    }

}
