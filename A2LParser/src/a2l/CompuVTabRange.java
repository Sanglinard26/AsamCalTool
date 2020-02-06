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

    private short numberValueTriples;
    private Map<Range, String> valueTriples;
    private String defaultValue; // DEFAULT_VALUE

    public CompuVTabRange(List<String> parameters, int beginLine, int endLine) {

        build(parameters, beginLine, endLine);
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
    public boolean isValid() {
        return true;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        if (nbParams >= 4) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3).toCharArray();
            this.numberValueTriples = (short) Integer.parseInt(parameters.get(4));

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

    @Override
    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + new String(longIdentifier) + "\n");
        sb.append("<li><b>Number of value triples: </b>" + numberValueTriples + "\n");
        sb.append("<li><b>Value triples: </b>");
        sb.append("<ul>");
        for (Entry<Range, String> entry : valueTriples.entrySet()) {
            sb.append("<li>" + entry.getKey() + " => " + entry.getValue() + "\n");
        }
        sb.append("</ul>");
        sb.append("<li><b>Default value: </b>" + defaultValue + "\n");
        sb.append("</u></html>");

        return sb.toString();
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

    @Override
    public String toString() {
        return min + "-" + max;
    }

}
