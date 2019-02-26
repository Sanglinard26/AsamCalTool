/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum IndexMode {

    ALTERNATE_CURVES, ALTERNATE_WITH_X, ALTERNATE_WITH_Y, COLUMN_DIR, ROW_DIR;

    public static IndexMode getIndexMode(String type) {
        switch (type) {
        case "ALTERNATE_CURVES":
            return IndexMode.ALTERNATE_CURVES;
        case "ALTERNATE_WITH_X":
            return IndexMode.ALTERNATE_WITH_X;
        case "ALTERNATE_WITH_Y":
            return IndexMode.ALTERNATE_WITH_Y;
        case "COLUMN_DIR":
            return IndexMode.COLUMN_DIR;
        case "ROW_DIR":
            return IndexMode.ROW_DIR;
        default:
            return null;
        }

    }

}
