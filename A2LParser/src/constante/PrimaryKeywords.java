/*
 * Creation : 2 mars 2018
 */
package constante;

import java.util.HashMap;
import java.util.Map;

public enum PrimaryKeywords {
    MOD_PAR, MOD_COMMON, AXIS_PTS, CHARACTERISTIC, COMPU_METHOD, COMPU_TAB, COMPU_VTAB, COMPU_VTAB_RANGE, MEASUREMENT, RECORD_LAYOUT, FUNCTION, UNIT, UNKNOWN;

    private static final Map<String, PrimaryKeywords> nameIndex = new HashMap<>(PrimaryKeywords.values().length);

    static {
        for (PrimaryKeywords primaryKeywords : PrimaryKeywords.values()) {
            nameIndex.put(primaryKeywords.name(), primaryKeywords);
        }
    }

    public static final PrimaryKeywords getPrimaryKeywords(String name) {
        PrimaryKeywords primaryKeywords = nameIndex.get(name);
        return primaryKeywords != null ? primaryKeywords : UNKNOWN;
    }

}
