/*
 * Creation : 2 avr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.FORMAT;

import java.util.HashMap;
import java.util.Map;

import constante.ConversionType;
import constante.SecondaryKeywords;

public abstract class AdjustableObject implements Comparable<AdjustableObject> {

    protected String name;
    protected String longIdentifier;
    protected String adress;
    protected String deposit;
    protected float maxDiff;
    protected String conversion;
    protected float lowerLimit;
    protected float upperLimit;
    
    protected String function;

    protected Values values;

    protected Map<SecondaryKeywords, Object> optionalsParameters;

    protected CompuMethod compuMethod;
    protected RecordLayout recordLayout;

    public final long getAdress() {
        return Long.parseLong(adress.substring(2), 16);
    }

    public final String getConversion() {
        return conversion;
    }

    public final CompuMethod getCompuMethod() {
        return compuMethod;
    }

    public final RecordLayout getRecordLayout() {
        return recordLayout;
    }
    
    public final String getFunction()
    {
    	return this.function;
    }

    public final void assignRecordLayout(HashMap<String, RecordLayout> recordLayouts) {
        this.recordLayout = recordLayouts.get(this.deposit);
    }

    public final String getFormat() {
        Object objectDisplayFormat = optionalsParameters.get(FORMAT);
        String displayFormat;

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {
            if (objectDisplayFormat == null) {
                displayFormat = compuMethod.getFormat();
            } else {
                displayFormat = objectDisplayFormat.toString();
            }
            if (displayFormat.charAt(1) == '0') {
                displayFormat = displayFormat.replaceFirst("0", "");
            }
            return displayFormat;
        }
        return "%16.16f";
    }

    public final String showValues() {

        StringBuilder sb = new StringBuilder("\n");

        for (short y = 0; y < values.getDimY(); y++) {
            for (short x = 0; x < values.getDimX(); x++) {
                sb.append(values.getValue(y, x) + " | ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
    
    public final Values getValues()
    {
    	return this.values;
    }

    public final void setValues(Values values) {
        this.values = values;
    }
    
    public final void setFunction(String function)
    {
    	this.function = function;
    }

    public abstract void assignComputMethod(HashMap<String, CompuMethod> compuMethods);
    
    public abstract String[] getUnit();

}
