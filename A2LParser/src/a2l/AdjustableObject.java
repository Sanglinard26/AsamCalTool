/*
 * Creation : 2 avr. 2019
 */
package a2l;

import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.FORMAT;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import constante.SecondaryKeywords;

public abstract class AdjustableObject implements A2lObjectBuilder, Comparable<AdjustableObject> {

    protected String name;
    protected String longIdentifier;
    protected String adress;
    protected String deposit;
    protected float maxDiff;
    protected String conversion;
    protected float lowerLimit;
    protected float upperLimit;

    protected String functionRef;

    protected Values values;

    protected Map<SecondaryKeywords, Object> optionalsParameters;

    protected CompuMethod compuMethod;
    protected RecordLayout recordLayout;

    @Override
    public final int compareTo(AdjustableObject o) {
        return this.name.compareToIgnoreCase(o.toString());
    }

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

    public final String getFunction() {
        return this.functionRef;
    }

    public final void assignRecordLayout(HashMap<String, RecordLayout> recordLayouts) {
        this.recordLayout = recordLayouts.get(this.deposit);
    }

    public final byte getNbDecimal() {
        Object objectDisplayFormat = optionalsParameters.get(FORMAT);
        String displayFormat;

        if (!compuMethod.isVerbal()) {
            if (objectDisplayFormat == null) {
                displayFormat = compuMethod.getFormat();
            } else {
                displayFormat = objectDisplayFormat.toString();
            }

            return (byte) Integer.parseInt(displayFormat.substring(displayFormat.indexOf(".") + 1, displayFormat.length()));
        }
        return 0;
    }

    public final ByteOrder getByteOrder() {
        String sByteOrder = (String) optionalsParameters.get(BYTE_ORDER);
        if (sByteOrder != null) {
            if ("MSB_LAST".equals(sByteOrder) || "BIG_ENDIAN".equals(sByteOrder)) {
                return ByteOrder.LITTLE_ENDIAN;
            }
            return ByteOrder.BIG_ENDIAN;
        }
        return null;
    }

    protected abstract void formatValues();

    public final Values getValues() {
        formatValues();
        return this.values;
    }

    public final void setValues(Values values) {
        this.values = values;
    }

    public final void setFunction(String function) {
        this.functionRef = function;
    }

    public abstract void assignComputMethod(HashMap<String, CompuMethod> compuMethods);

    public abstract String[] getUnit();

    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + longIdentifier + "\n");
        sb.append("<li><b>Function: </b><a href=" + functionRef + ">" + functionRef + "</a>\n");
        sb.append("<li><b>Unit: </b>");
        for (String unit : getUnit()) {
            sb.append("[" + unit + "]");
        }
        sb.append("\n");
        sb.append("<li><b>Lower limit: </b>" + lowerLimit + "\n");
        sb.append("<li><b>Upper limit: </b>" + upperLimit + "\n");
        sb.append("<li><b>Max diff: </b>" + maxDiff + "\n");
        sb.append("<li><b>Adress: </b>" + adress + "\n");
        sb.append("<li><b>Deposit: </b><a href=" + deposit + ">" + deposit + "</a>\n");
        sb.append("<li><b>Conversion: </b><a href=" + conversion + ">" + conversion + "</a>\n</ul>");
        sb.append("<b><u>VALUES :\n</u></b></html>");

        return sb.toString();
    }

}
