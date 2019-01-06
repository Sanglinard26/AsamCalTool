/*
 * Creation : 5 janv. 2019
 */
package a2l;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CompuVTabRange {

    private String name;
    private String longIdentifier;
    private int numberValueTriples;
    private Map<Range, String> valueTriples;
    private String defaultValue; // DEFAULT_VALUE

    public CompuVTabRange(List<String> parameters) {

        if (parameters.size() >= 4) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    // System.out.println(this.name);
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
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name : " + name + "\n");
        sb.append("LongIdentifier : " + longIdentifier + "\n");
        sb.append("NumberValuePairs : " + numberValueTriples + "\n");
        sb.append("ValuePairs : " + valueTriples + "\n");

        return sb.toString();
    }
}

final class Range {

    private float min;
    private float max;

    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return min + "," + max;
    }

}
