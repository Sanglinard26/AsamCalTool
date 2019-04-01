/*
 * Creation : 5 janv. 2019
 */
package a2lobject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class CompuVTabRange extends ConversionTable {

    private String name;
    @SuppressWarnings("unused")
    private String longIdentifier;
    @SuppressWarnings("unused")
    private int numberValueTriples;
    private Map<Range, String> valueTriples;
    private String defaultValue; // DEFAULT_VALUE

    public CompuVTabRange(List<String> parameters) {

        parameters.remove(0); // Remove /begin
        parameters.remove(0); // Remove COMPU_VTAB_RANGE

        if (parameters.size() >= 4) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    break;
                case 1:
                    this.longIdentifier = parameters.get(n);
                    break;
                case 2:
                    this.numberValueTriples = Integer.parseInt(parameters.get(n));
                    break;
                case 3:
                    this.valueTriples = new LinkedHashMap<Range, String>();

                    int lastIdx = parameters.indexOf("DEFAULT_VALUE");

                    final List<String> listValuePairs;

                    if (lastIdx > -1) {
                        listValuePairs = parameters.subList(n, lastIdx);
                    } else {
                        listValuePairs = parameters.subList(n, parameters.size());
                    }

                    for (int i = 0; i < listValuePairs.size(); i++) {
                        if (i % 3 == 0) {
                            valueTriples.put(new Range(Float.parseFloat(listValuePairs.get(i)), Float.parseFloat(listValuePairs.get(i + 1))),
                                    listValuePairs.get(i + 2));
                        }
                    }

                    break;

                default: // Cas de parametres optionels
                    n = parameters.size();
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    @Override
    public String toString() {
        return this.name;
    }

    public final String getStringValue(double hex) {
        Set<Entry<Range, String>> entries = valueTriples.entrySet();
        Iterator<Entry<Range, String>> it = entries.iterator();
        while (it.hasNext()) {
            Entry<Range, String> entry = it.next();
            Range range = entry.getKey();
            if (range.getMin() >= hex && hex < range.getMax()) {
                return entry.getValue();
            }
        }
        return defaultValue;

    }
}

final class Range {

    private float min;
    private float max;

    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

}
