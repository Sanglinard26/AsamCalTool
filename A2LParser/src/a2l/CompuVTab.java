/*
 * Creation : 5 janv. 2019
 */
package a2l;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import constante.ConversionType;
import utils.NumeralString;

public final class CompuVTab extends ConversionTable {

    private int numberValuePairs;
    private Map<Float, String> valuePairs;
    private char[] defaultValue; // DEFAULT_VALUE

    public CompuVTab(List<String> parameters, int beginLine, int endLine) {

        build(parameters, beginLine, endLine);

    }

    public final Map<Float, String> getValuePairs() {
        return valuePairs;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        if (nbParams >= 5) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3).toCharArray();
            this.conversionType = ConversionType.getConversionType(parameters.get(4));
            this.numberValuePairs = Integer.parseInt(parameters.get(5));

            this.valuePairs = new LinkedHashMap<Float, String>(numberValuePairs);

            int lastIdx = parameters.indexOf("DEFAULT_VALUE");

            final List<String> listValuePairs;

            if (lastIdx > -1) {
                listValuePairs = parameters.subList(6, lastIdx);
                this.defaultValue = parameters.get(lastIdx + 1).toCharArray();
            } else {
                listValuePairs = parameters.subList(6, parameters.size());
                this.defaultValue = new char[0];
            }

            Float key = null;
            String value;

            for (int i = 0; i < listValuePairs.size() - 1; i++) {

                try {
                    key = Float.parseFloat(listValuePairs.get(i));
                    value = listValuePairs.get(i + 1);
                    valuePairs.put(key, value);
                    i++;
                } catch (NumberFormatException e) {

                    String oldVal;

                    do {
                        oldVal = valuePairs.get(key);
                        valuePairs.put(key, oldVal + " " + listValuePairs.get(i++));
                    } while (!NumeralString.isNumber(listValuePairs.get(i)) && i < listValuePairs.size() - 1);

                    i--;
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
        sb.append("<li><b>Conversion type: </b>" + conversionType.name() + "\n");
        sb.append("<li><b>Number of value pairs: </b>" + numberValuePairs + "\n");
        sb.append("<li><b>Value pairs: </b>");
        sb.append("<ul>");
        for (Entry<Float, String> entry : valuePairs.entrySet()) {
            sb.append("<li>" + entry.getKey() + " => " + entry.getValue() + "\n");
        }
        sb.append("</ul>");
        sb.append("<li><b>Default value: </b>" + new String(defaultValue) + "\n");
        sb.append("</u></html>");

        return sb.toString();
    }

    @Override
    Map<?, ?> getMap() {
        // TODO Auto-generated method stub
        return valuePairs;
    }
}
