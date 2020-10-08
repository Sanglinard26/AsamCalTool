/*
 * Creation : 20 févr. 2019
 */
package a2l;

import static constante.SecondaryKeywords.AXIS_PTS_REF;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.CURVE_AXIS_REF;
import static constante.SecondaryKeywords.DEPOSIT;
import static constante.SecondaryKeywords.FIX_AXIS_PAR;
import static constante.SecondaryKeywords.FIX_AXIS_PAR_DIST;
import static constante.SecondaryKeywords.FIX_AXIS_PAR_LIST;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.PHYS_UNIT;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constante.SecondaryKeywords;

public final class AxisDescr {

    private Attribute attribute;
    @SuppressWarnings("unused")
    private char[] inputQuantity;
    private int conversionId;
    private short maxAxisPoints;
    @SuppressWarnings("unused")
    private float lowerLimit;
    @SuppressWarnings("unused")
    private float upperLimit;

    private CompuMethod compuMethod;
    private RecordLayout recordLayout;
    private AdjustableObject axisPts;
    private AdjustableObject curveAxis;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public AxisDescr(List<String> parameters) {

        optionalsParameters = new HashMap<SecondaryKeywords, Object>();

        build(parameters);
    }

    public final Attribute getAttribute() {
        return attribute;
    }

    public final void setCompuMethod(CompuMethod compuMethod) {
        this.compuMethod = compuMethod;
    }

    public final void setAxisPts(AdjustableObject axisPts) {
        this.axisPts = axisPts;
    }

    public final AdjustableObject getAxisPts() {
        return axisPts;
    }

    public final void setCurveAxis(AdjustableObject adjustableObject) {
        this.curveAxis = adjustableObject;
    }

    public final AdjustableObject getCurveAxis() {
        return curveAxis;
    }

    public final int getConversion() {
        return conversionId;
    }

    public final CompuMethod getCompuMethod() {
        return compuMethod;
    }

    public final RecordLayout getRecordLayout() {
        return recordLayout;
    }

    public final short getMaxAxisPoints() {
        if (attribute.equals(Attribute.COM_AXIS)) {
            return ((AxisPts) getAxisPts()).getMaxAxisPoints();
        }
        return maxAxisPoints;
    }

    public final String getDepositMode() {
        Object oDeposit = optionalsParameters.get(DEPOSIT);
        return oDeposit != null ? oDeposit.toString() : "";
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

    public final int getAxisRef(Attribute type) {
        Object object = null;
        switch (type) {
        case COM_AXIS:
            object = optionalsParameters.get(AXIS_PTS_REF);
            break;
        case RES_AXIS:
            object = optionalsParameters.get(AXIS_PTS_REF);
            break;
        case CURVE_AXIS:
            object = optionalsParameters.get(CURVE_AXIS_REF);
            break;
        default:
            break;
        }
        return (int) (object != null ? object : 0);
    }

    public final Map<SecondaryKeywords, Object> getOptionalsParameters() {
        return optionalsParameters;
    }

    public final byte getNbDecimal() {
        Object objectDisplayFormat = optionalsParameters.get(FORMAT);
        String displayFormat;

        if (!compuMethod.isVerbal()) {
            if (objectDisplayFormat == null) {
                displayFormat = compuMethod.getFormat();
            } else {
                displayFormat = new String((char[]) objectDisplayFormat);
            }
            // NPE si par exemple "%8"
            int idxPoint = displayFormat.indexOf(".");
            if (idxPoint > -1) {
                return Byte.parseByte(displayFormat.substring(idxPoint + 1, displayFormat.length()));
            }
        }
        return 0;
    }

    public final String getPhysUnit() {
        Object oPhysUnit = optionalsParameters.get(PHYS_UNIT);
        return oPhysUnit != null ? new String((char[]) oPhysUnit) : "";
    }

    public enum Attribute {
        CURVE_AXIS, COM_AXIS, FIX_AXIS, RES_AXIS, STD_AXIS, UNKNOWN;

        public static Attribute getAttribute(String name) {
            switch (name) {
            case "CURVE_AXIS":
                return CURVE_AXIS;
            case "COM_AXIS":
                return COM_AXIS;
            case "FIX_AXIS":
                return FIX_AXIS;
            case "RES_AXIS":
                return RES_AXIS;
            case "STD_AXIS":
                return STD_AXIS;
            default:
                return UNKNOWN;
            }
        }
    }

    private void build(List<String> parameters) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        if (nbParams >= 6) {

            this.attribute = Attribute.getAttribute(parameters.get(0));
            this.inputQuantity = parameters.get(1).toCharArray();
            this.conversionId = parameters.get(2).hashCode();
            this.maxAxisPoints = Short.parseShort(parameters.get(3));
            this.lowerLimit = Float.parseFloat(parameters.get(4));
            this.upperLimit = Float.parseFloat(parameters.get(5));

            int n = 6;

            SecondaryKeywords keyWord;
            for (int nPar = n; nPar < nbParams; nPar++) {
                keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                switch (keyWord) {
                case AXIS_PTS_REF:
                    optionalsParameters.put(AXIS_PTS_REF, parameters.get(nPar + 1).hashCode());
                    nPar += 1;
                    break;
                case BYTE_ORDER:
                    optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
                    nPar += 1;
                    break;
                case CURVE_AXIS_REF:
                    optionalsParameters.put(CURVE_AXIS_REF, parameters.get(nPar + 1).hashCode());
                    nPar += 1;
                    break;
                case DEPOSIT:
                    optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
                    nPar += 1;
                    break;
                case FIX_AXIS_PAR:
                    n = nPar + 1;
                    optionalsParameters.put(FIX_AXIS_PAR, new FixAxisPar(parameters.subList(n, n + 3)));
                    nPar += 3;
                    break;
                case FIX_AXIS_PAR_DIST:
                    n = nPar + 1;
                    optionalsParameters.put(FIX_AXIS_PAR_DIST, new FixAxisParDist(parameters.subList(n, n + 3)));
                    nPar += 3;
                    break;
                case FIX_AXIS_PAR_LIST:
                    n = nPar + 1;
                    do {
                    } while (!parameters.get(++nPar).equals(FIX_AXIS_PAR_LIST.name()));
                    optionalsParameters.put(FIX_AXIS_PAR_LIST, new FixAxisParList(parameters.subList(n, nPar - 1)));
                    n = nPar + 1;
                    break;
                case FORMAT:
                    optionalsParameters.put(FORMAT, parameters.get(nPar + 1).toCharArray());
                    nPar += 1;
                    break;
                case PHYS_UNIT:
                    optionalsParameters.put(PHYS_UNIT, parameters.get(nPar + 1).toCharArray());
                    nPar += 1;
                    break;
                default:
                    break;
                }
            }

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }
}
