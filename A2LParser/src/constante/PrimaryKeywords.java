/*
 * Creation : 2 mars 2018
 */
package constante;

public enum PrimaryKeywords {
    AXIS_PTS, CHARACTERISTIC, COMPU_METHOD, COMPU_TAB, COMPU_VTAB, COMPU_VTAB_RANGE, MEASUREMENT, RECORD_LAYOUT, UNKNOWN;

    public static final PrimaryKeywords getPrimaryKeywords(String name) {
        for (PrimaryKeywords enumKeyword : PrimaryKeywords.values()) {
            if (enumKeyword.name().equals(name)) {
                return enumKeyword;
            }
        }
        return UNKNOWN;
    }

}
