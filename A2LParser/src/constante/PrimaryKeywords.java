/*
 * Creation : 2 mars 2018
 */
package constante;

import java.util.HashMap;
import java.util.Map;

public enum PrimaryKeywords {
    MOD_PAR, MOD_COMMON, AXIS_PTS, CHARACTERISTIC, COMPU_METHOD, COMPU_TAB, COMPU_VTAB, COMPU_VTAB_RANGE, MEASUREMENT, RECORD_LAYOUT, FUNCTION, GROUP, UNIT, UNKNOWN;

    private static final Map<Integer, PrimaryKeywords> nameIndex = new HashMap<>(PrimaryKeywords.values().length);

    static {
        for (PrimaryKeywords primaryKeywords : PrimaryKeywords.values()) {
            nameIndex.put(sumChar(primaryKeywords.name()), primaryKeywords);
        }
    }

    public static int sumChar(String name) {
        final int size = name.length();
        int sum = 0;

        for (int idx = 0; idx < size; idx++) {
            sum += (name.charAt(idx) * idx);
        }

        return sum;
    }

    public static final PrimaryKeywords getPrimaryKeywords(Integer code) {
        PrimaryKeywords primaryKeywords = nameIndex.get(code);
        return primaryKeywords != null ? primaryKeywords : UNKNOWN;
    }

}
