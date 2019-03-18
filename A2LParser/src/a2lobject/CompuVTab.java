/*
 * Creation : 5 janv. 2019
 */
package a2lobject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import constante.ConversionType;

public final class CompuVTab {

    private String name;
    private String longIdentifier;
    private ConversionType conversionType;
    private int numberValuePairs;
    private Map<Float, String> valuePairs;
    private String defaultValue; // DEFAULT_VALUE

    public CompuVTab(List<String> parameters) {

        parameters.remove("/begin"); // Remove /begin
        parameters.remove("COMPU_VTAB"); // Remove CHARACTERISTIC

        if (parameters.size() == 1 || parameters.size() >= 5) {
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
                    this.conversionType = ConversionType.getConversionType(parameters.get(n));
                    break;
                case 3:
                    this.numberValuePairs = Integer.parseInt(parameters.get(n));
                    break;
                case 4:
                    this.valuePairs = new LinkedHashMap<Float, String>();

                    int lastIdx = parameters.indexOf("DEFAULT_VALUE");

                    final List<String> listValuePairs;

                    if (lastIdx > -1) {
                        listValuePairs = parameters.subList(n, lastIdx);
                    } else {
                        listValuePairs = parameters.subList(n, parameters.size());
                    }

                    for (int i = 0; i < listValuePairs.size(); i++) {
                        if (i % 2 == 0) {
                            valuePairs.put(Float.parseFloat(listValuePairs.get(i)), listValuePairs.get(i + 1));
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

    @Override
    public String toString() {
        return this.name;
    }

    public Map<Float, String> getValuePairs() {
        return valuePairs;
    }
}
