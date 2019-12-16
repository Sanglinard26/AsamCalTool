/*
 * Creation : 2 avr. 2019
 */
package a2l;

import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.READ_ONLY;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import a2l.AxisDescr.Attribute;
import a2l.Characteristic.CharacteristicType;
import constante.SecondaryKeywords;

public abstract class AdjustableObject implements A2lObject, Comparable<AdjustableObject> {

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

    public final String getDimension() {
        StringBuilder stringDimension = new StringBuilder();
        if (this instanceof AxisPts) {
            stringDimension.append("[" + ((AxisPts) this).getMaxAxisPoints() + " x 1]");
        } else {
            Characteristic characteristic = (Characteristic) this;

            stringDimension.append("[");
            if (values != null) {
                if (characteristic.getType().compareTo(CharacteristicType.VALUE) == 0) {
                    stringDimension.append("1 x 1");
                } else if (characteristic.getType().compareTo(CharacteristicType.CURVE) == 0) {
                    stringDimension.append(values.getDimX() + " x 2");
                } else {
                    stringDimension.append((values.getDimX() - 1) + " x " + (values.getDimY() - 1));
                }
            } else {
                stringDimension.append("? x ?");
            }
            stringDimension.append("]");

            if (characteristic.getType().compareTo(CharacteristicType.CURVE) == 0) {
                int dimMaxX = characteristic.getAxisDescrs().get(0).getMaxAxisPoints();
                stringDimension.append(" (Max : [" + dimMaxX + " x 2])");
            } else if (characteristic.getType().compareTo(CharacteristicType.MAP) == 0) {
                int dimMaxX = characteristic.getAxisDescrs().get(0).getMaxAxisPoints();
                int dimMaxY = characteristic.getAxisDescrs().get(1).getMaxAxisPoints();
                stringDimension.append(" (Max : [" + dimMaxX + " x " + dimMaxY + "])");
            }
        }
        return stringDimension.toString();
    }

    public final String getFormat() {
        Object oFormat = optionalsParameters.get(FORMAT);
        return oFormat != null ? oFormat.toString() : compuMethod.getFormat();
    }

    protected abstract void formatValues();

    public final Values getValues() {
        formatValues();
        return this.values;
    }

    public final boolean isReadOnly() {
        return (boolean) optionalsParameters.get(READ_ONLY);
    }

    public final void setValues(Values values) {
        this.values = values;
    }

    public final void setFunction(String function) {
        this.functionRef = function == null ? "" : function;
    }

    public abstract void assignComputMethod(HashMap<String, CompuMethod> compuMethods);

    public abstract String[] getUnit();
    
    public abstract Double[] getResolution();

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
        sb.append("<li><b>Conversion: </b><a href=" + conversion + ">" + conversion + "</a>\n");
        sb.append("<li><b>Dimensions [X x Y]: </b>" + getDimension() + "\n");

        if (this instanceof Characteristic) {
            Characteristic characteristic = (Characteristic) this;
            AxisDescr axis;
            for (int i = 0; i < characteristic.getAxisDescrs().size(); i++) {
                sb.append("<li><b>Axis " + (i + 1) + ": </b>");
                axis = characteristic.getAxisDescrs().get(i);
                sb.append("<ul><li><b>Type: </b>" + axis.getAttribute().name());
                if (axis.getAttribute().compareTo(Attribute.COM_AXIS) == 0) {
                    sb.append("<li><b>Axis Pts Ref: </b><a href=" + axis.getAxisPts() + ">" + axis.getAxisPts());
                }
                sb.append("</ul>");
            }
            for (double resol : getResolution()) {
                System.out.print("[" + resol + "]");
            }
            
        }

        if (this instanceof AxisPts) {
            AxisPts axisPts = (AxisPts) this;
            sb.append("<li><b>Used by: </b>");
            for (Characteristic characteristic : axisPts.getCharacteristicsDependency()) {
                sb.append(characteristic.name + " | ");
            }
        }

        sb.append("</ul>");

        sb.append("<b><u>OPTIONALS PARAMETERS :\n</u></b>");
        sb.append("<ul><li><b>Format : </b>" + getFormat() + "\n");
        sb.append("<li><b>Read only : </b>" + isReadOnly() + "\n");

        sb.append("</html>");

        return sb.toString();
    }

}
