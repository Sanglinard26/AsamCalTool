/*
 * Creation : 2 mars 2018
 */
package constante;

public enum PrimaryKeywords {
    MOD_PAR, MOD_COMMON, AXIS_PTS, CHARACTERISTIC, COMPU_METHOD, COMPU_TAB, COMPU_VTAB, COMPU_VTAB_RANGE, MEASUREMENT, RECORD_LAYOUT, FUNCTION, UNIT, UNKNOWN;

    public static final PrimaryKeywords getPrimaryKeyWords(String name) {

        for (PrimaryKeywords enumKeyword : PrimaryKeywords.values()) {
            if (enumKeyword.name().equals(name)) {
                return enumKeyword;
            }
        }
        return UNKNOWN;

    }

}
