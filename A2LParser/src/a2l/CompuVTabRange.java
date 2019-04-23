/*
 * Creation : 5 janv. 2019
 */
package a2l;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class CompuVTabRange extends ConversionTable {

    @SuppressWarnings("unused")
	private int numberValueTriples;
    private Map<Range, String> valueTriples;
    private String defaultValue; // DEFAULT_VALUE

    public CompuVTabRange(List<String> parameters) {

        build(parameters);
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

	@Override
	public void build(List<String> parameters) throws IllegalArgumentException {
		
		final int nbParams = parameters.size();

        if (nbParams >= 4) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3);
            this.numberValueTriples = Integer.parseInt(parameters.get(4));

            this.valueTriples = new LinkedHashMap<Range, String>();

            int lastIdx = parameters.indexOf("DEFAULT_VALUE");

            final List<String> listValuePairs;

            if (lastIdx > -1) {
                listValuePairs = parameters.subList(5, lastIdx);
            } else {
                listValuePairs = parameters.subList(5, parameters.size());
            }

            for (int i = 0; i < listValuePairs.size(); i++) {
                if (i % 3 == 0) {
                    valueTriples.put(new Range(Float.parseFloat(listValuePairs.get(i)), Float.parseFloat(listValuePairs.get(i + 1))),
                            listValuePairs.get(i + 2));
                }
            }
        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }
		
	}
}

final class Range {

    private final float min;
    private final float max;

    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public final float getMin() {
        return min;
    }

    public final float getMax() {
        return max;
    }

}
