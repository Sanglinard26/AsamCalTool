/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum IndexMode {

    ALTERNATE_CURVES, ALTERNATE_WITH_X, ALTERNATE_WITH_Y, COLUMN_DIR, ROW_DIR, UNKNOWN;

    public static IndexMode getIndexMode(String type) {
        switch (type) {
        case "ALTERNATE_CURVES":
            return ALTERNATE_CURVES;
        case "ALTERNATE_WITH_X":
            return ALTERNATE_WITH_X;
        case "ALTERNATE_WITH_Y":
            return ALTERNATE_WITH_Y;
        case "COLUMN_DIR":
            return COLUMN_DIR;
        case "ROW_DIR":
            return ROW_DIR;
        default:
            return UNKNOWN;
        }

    }

}
