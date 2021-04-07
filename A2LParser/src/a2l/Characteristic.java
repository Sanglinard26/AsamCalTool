/*
 * Creation : 2 mars 2018
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.AXIS_DESCR;
import static constante.SecondaryKeywords.BIT_MASK;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.MATRIX_DIM;
import static constante.SecondaryKeywords.NUMBER;
import static constante.SecondaryKeywords.PHYS_UNIT;
import static constante.SecondaryKeywords.READ_ONLY;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a2l.AxisDescr.Attribute;
import constante.ConversionType;
import constante.SecondaryKeywords;

public final class Characteristic extends AdjustableObject {

    private int type;

    private AxisDescr[] axisDescrs;

    public Characteristic(List<String> parameters, int beginLine, int endLine) {

        optionalsParameters = new HashMap<SecondaryKeywords, Object>();

        build(parameters, beginLine, endLine);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final AxisDescr[] getAxisDescrs() {
        if (axisDescrs == null) {
            return new AxisDescr[0];
        }
        return axisDescrs;
    }

    public final CharacteristicType getType() {
        return CharacteristicType.getCharacteristicType(type);
    }

    public final short getDim() {
        Object oByte = optionalsParameters.get(NUMBER);

        if (oByte == null) {
            oByte = optionalsParameters.get(MATRIX_DIM);
            return (short) ((Object[]) oByte)[0];
        }
        return (short) oByte;
    }

    public final short[] getDimArray() {
        Object numberParam = optionalsParameters.get(NUMBER);
        Object matrixDimParam = optionalsParameters.get(MATRIX_DIM);

        if (matrixDimParam != null) {
            Object[] arrMatrixDim = (Object[]) matrixDimParam;

            switch (arrMatrixDim.length) {
            case 1:
                return new short[] { (short) arrMatrixDim[0] };
            case 2:
                return new short[] { (short) arrMatrixDim[0], (short) arrMatrixDim[1] };
            case 3:
                return new short[] { (short) arrMatrixDim[0], (short) arrMatrixDim[1], (short) arrMatrixDim[2] };
            default:
                return new short[] { 0 };
            }
        }
        return new short[] { (short) numberParam };
    }

    public final boolean hasBitMask() {
        return optionalsParameters.get(BIT_MASK) != null;
    }

    public final double applyBitMask(long value) {
        long bitMask = (long) optionalsParameters.get(BIT_MASK);

        long maskedValue = value & bitMask;

        String bits = Long.toBinaryString(maskedValue);

        int shift = 0;

        for (int i = 0; i < bits.length(); i++) {
            int j = bits.length() - 1 - i;
            if (bits.charAt(j) == '1') {
                shift = i;
                break;
            }
        }

        return maskedValue >> shift;
    }

    public final void assignAxisPts(HashMap<Integer, AdjustableObject> adjustableObjects) {

        AdjustableObject axisPts;

        if (axisDescrs != null) {
            for (int idx = 0; idx < axisDescrs.length; idx++) {
                Attribute axisType = axisDescrs[idx].getAttribute();
                if (axisType.equals(Attribute.COM_AXIS) || axisType.equals(Attribute.RES_AXIS)) {
                    axisPts = adjustableObjects.get(axisDescrs[idx].getAxisRef(axisType));
                    axisDescrs[idx].setAxisPts(axisPts);
                    ((AxisPts) axisPts).assignCharacteristic(this);
                }
                if (axisType.equals(Attribute.CURVE_AXIS)) {
                    axisDescrs[idx].setCurveAxis(adjustableObjects.get(axisDescrs[idx].getAxisRef(axisType)));
                }
            }
        }
    }

    public enum CharacteristicType {

        ASCII(0), CURVE(1), MAP(2), CUBOID(3), CUBE_4(5), CUBE_5(6), VAL_BLK(7), VALUE(8), UNKNOWN(-1);

        private int idx;

        private CharacteristicType(int idx) {
            this.idx = idx;
        }

        private static final Map<Integer, CharacteristicType> nameIndex = new HashMap<>(CharacteristicType.values().length);

        static {
            for (CharacteristicType characteristicType : CharacteristicType.values()) {
                nameIndex.put(characteristicType.idx, characteristicType);
            }
        }

        public static final CharacteristicType getCharacteristicType(int idx) {
            CharacteristicType characteristicType = nameIndex.get(idx);
            return characteristicType != null ? characteristicType : UNKNOWN;
        }

        private static CharacteristicType getCharacteristicType(String name) {
            switch (name) {
            case "ASCII":
                return ASCII;
            case "CURVE":
                return CURVE;
            case "MAP":
                return MAP;
            case "CUBOID":
                return CUBOID;
            case "CUBE_4":
                return CUBE_4;
            case "CUBE_5":
                return CUBE_5;
            case "VAL_BLK":
                return VAL_BLK;
            case "VALUE":
                return VALUE;
            default:
                return UNKNOWN;
            }
        }

        private static byte getNbAxis(CharacteristicType type) {
            switch (type) {
            case ASCII:
                return 0;
            case CURVE:
                return 1;
            case MAP:
                return 2;
            case CUBOID:
                return 3;
            case CUBE_4:
                return 4;
            case CUBE_5:
                return 5;
            case VAL_BLK:
                return 0;
            case VALUE:
                return 0;
            default:
                return 0;
            }
        }

    }

    @Override
    public final void assignComputMethod(HashMap<Integer, CompuMethod> compuMethods) {
        this.compuMethod = compuMethods.get(this.conversionId);
        if (this.compuMethod == null) {
            this.compuMethod = CompuMethod.createNoCompuMethod();
        }
        if (axisDescrs != null) {
            for (int idx = 0; idx < axisDescrs.length; idx++) {
                axisDescrs[idx].setCompuMethod(compuMethods.get(axisDescrs[idx].getConversion()));
            }
        }
    }

    @Override
    public String[] getUnit() {
        String[] unit;

        switch (getType()) {
        case VALUE:
            unit = new String[1];

            if (this.compuMethod != null) {
                unit[0] = this.compuMethod.getUnit();
            } else {
                unit[0] = "";
            }

            break;
        case CURVE:
            unit = new String[2];
            AxisDescr axisDescr = this.axisDescrs[0];
            if (axisDescr.getPhysUnit().length() > 0) {
                unit[0] = axisDescr.getPhysUnit().length() > 0 ? axisDescr.getPhysUnit() : axisDescr.getCompuMethod().getUnit();
            } else {
                if (axisDescr.getCompuMethod() != null) {
                    unit[0] = axisDescr.getCompuMethod().getUnit();
                } else {
                    if (axisDescr.getAttribute().compareTo(Attribute.CURVE_AXIS) == 0) {
                        unit[0] = "";
                    } else {
                        unit[0] = "";
                    }
                }
            }

            if (this.compuMethod != null) {
                unit[1] = this.compuMethod.getUnit();
            } else {
                unit[1] = "";
            }

            break;
        case MAP:
            unit = new String[3];

            if (this.axisDescrs[0].getPhysUnit().length() > 0) {
                unit[0] = this.axisDescrs[0].getPhysUnit();
            } else {
                if (this.axisDescrs[0].getCompuMethod() != null) {
                    unit[0] = this.axisDescrs[0].getCompuMethod().getUnit();
                } else {
                    unit[0] = "";
                }
            }

            if (this.axisDescrs[1].getPhysUnit().length() > 0) {
                unit[1] = this.axisDescrs[1].getPhysUnit();
            } else {
                if (this.axisDescrs[1].getCompuMethod() != null) {
                    unit[1] = this.axisDescrs[1].getCompuMethod().getUnit();
                } else {
                    unit[1] = "";
                }
            }

            if (this.compuMethod != null) {
                unit[2] = this.compuMethod.getUnit();
            } else {
                unit[2] = "";
            }
            break;
        case VAL_BLK:
            unit = new String[1];

            if (this.compuMethod != null) {
                unit[0] = this.compuMethod.getUnit();
            } else {
                unit[0] = "";
            }
            break;
        default:
            unit = new String[] { "" };
            break;
        }
        return unit;
    }

    @Override
    public boolean isValid() {
        return validParsing;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws A2lObjectParsingException {

        final int nbParams = parameters.size();

        try {
            if (nbParams >= 9) {

                this.name = parameters.get(2);
                this.longIdentifier = parameters.get(3).toCharArray();
                this.type = CharacteristicType.getCharacteristicType(parameters.get(4)).idx;
                this.adress = Long.parseLong(parameters.get(5).substring(2), 16);
                this.depositId = parameters.get(6).hashCode();
                this.maxDiff = Float.parseFloat(parameters.get(7));
                this.conversionId = parameters.get(8).hashCode();
                this.lowerLimit = Float.parseFloat(parameters.get(9));
                this.upperLimit = Float.parseFloat(parameters.get(10));

                int n = 11;

                byte cntAxis = 0;

                SecondaryKeywords keyWord;
                for (int nPar = n; nPar < nbParams; nPar++) {
                    keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                    switch (keyWord) {
                    case ANNOTATION:
                        n = nPar + 1;
                        do {
                        } while (!parameters.get(++nPar).equals(ANNOTATION.name()));
                        optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3)));
                        n = nPar + 1;
                        break;
                    case AXIS_DESCR:
                        if (axisDescrs == null) {
                            axisDescrs = new AxisDescr[CharacteristicType.getNbAxis(getType())];
                            optionalsParameters.put(AXIS_DESCR, axisDescrs);
                        }
                        n = nPar + 1;
                        do {
                        } while (!parameters.get(++nPar).equals(AXIS_DESCR.name()));
                        axisDescrs[cntAxis++] = new AxisDescr(parameters.subList(n, nPar - 1));
                        n = nPar + 1;
                        break;
                    case BIT_MASK:
                        String bitMask = parameters.get(nPar + 1);
                        if (bitMask.startsWith("0x")) {
                            optionalsParameters.put(BIT_MASK, Long.parseLong(bitMask.substring(2), 16));
                        } else {
                            optionalsParameters.put(BIT_MASK, Long.parseLong(bitMask));
                        }
                        nPar += 1;
                        break;
                    case BYTE_ORDER:
                        optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case DISPLAY_IDENTIFIER:
                        optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1).toCharArray());
                        nPar += 1;
                        break;
                    case FORMAT:
                        optionalsParameters.put(FORMAT, new Format(parameters.get(nPar + 1)));
                        nPar += 1;
                        break;
                    case MATRIX_DIM:
                        List<Short> dim = new ArrayList<Short>();

                        try {
                            nPar += 1;
                            do {
                                dim.add(Short.parseShort(parameters.get(nPar)));
                                nPar += 1;
                            } while (nPar < parameters.size());
                        } catch (NumberFormatException nfe) {
                            nPar += 1;
                        }
                        optionalsParameters.put(MATRIX_DIM, dim.toArray());
                        dim.clear();
                        break;
                    case NUMBER:
                        optionalsParameters.put(NUMBER, Short.parseShort(parameters.get(nPar + 1)));
                        nPar += 1;
                        break;
                    case PHYS_UNIT:
                        optionalsParameters.put(PHYS_UNIT, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case READ_ONLY:
                        optionalsParameters.put(READ_ONLY, true);
                        break;
                    default:
                        break;
                    }
                }

            } else {
                validParsing = false;
                throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
            }
        } catch (IllegalArgumentException e) {
            validParsing = false;
            throw new A2lObjectParsingException("Parsing error on " + this.name, beginLine, endLine);
        }

        validParsing = true;

    }

    @Override
    protected void formatValues() {

        if (values != null) {
            final DecimalFormat df = new DecimalFormat();
            final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            String separator = new String(new char[] { dfs.getGroupingSeparator() });
            df.setDecimalFormatSymbols(dfs);

            for (short y = 0; y < values.getDimY(); y++) {
                for (short x = 0; x < values.getDimX(); x++) {
                    try {
                        double doubleValue = Double.parseDouble(values.getValue(y, x).toString());

                        if (y == 0 && (getType().equals(CharacteristicType.CURVE) || getType().equals(CharacteristicType.MAP))) {
                            df.setMaximumFractionDigits(axisDescrs[0].getNbDecimal());
                        } else if (x == 0 && getType().equals(CharacteristicType.MAP)) {
                            df.setMaximumFractionDigits(axisDescrs[1].getNbDecimal());
                        } else {
                            df.setMaximumFractionDigits(getNbDecimal());
                        }

                        values.setValue(df.format(doubleValue).replace(separator, ""), y, x);
                    } catch (Exception e) {
                        // Nothing
                    }
                }
            }
        }

    }

    @Override
    public double[] getResolution() {

        double[] tabResol;

        CompuMethod cmX;
        CompuMethod cmY;

        double val0 = 0;
        double val1 = 0;

        switch (getType()) {
        case VALUE:
            val0 = this.compuMethod.compute(1);
            val1 = this.compuMethod.compute(2);
            tabResol = new double[] { ConversionType.TAB_VERB.compareTo(this.compuMethod.getConversionType()) != 0 ? val1 - val0 : Double.NaN };
            break;
        case CURVE:
            tabResol = new double[2];

            cmX = this.axisDescrs[0].getCompuMethod();
            if (cmX != null) {
                val0 = cmX.compute(1);
                val1 = cmX.compute(2);
                tabResol[0] = ConversionType.TAB_VERB.compareTo(cmX.getConversionType()) != 0 ? val1 - val0 : Double.NaN;
            } else {
                tabResol[0] = Double.NaN;
            }

            val0 = this.compuMethod.compute(1);
            val1 = this.compuMethod.compute(2);
            tabResol[1] = ConversionType.TAB_VERB.compareTo(this.compuMethod.getConversionType()) != 0 ? val1 - val0 : Double.NaN;
            break;
        case MAP:
            tabResol = new double[3];

            cmX = this.axisDescrs[0].getCompuMethod();
            if (cmX != null) {
                val0 = cmX.compute(1);
                val1 = cmX.compute(2);
                tabResol[0] = ConversionType.TAB_VERB.compareTo(cmX.getConversionType()) != 0 ? val1 - val0 : Double.NaN;
            } else {
                tabResol[0] = Double.NaN;
            }

            cmY = this.axisDescrs[1].getCompuMethod();
            if (cmY != null) {
                val0 = cmY.compute(1);
                val1 = cmY.compute(2);
                tabResol[1] = ConversionType.TAB_VERB.compareTo(cmY.getConversionType()) != 0 ? val1 - val0 : Double.NaN;
            } else {
                tabResol[1] = Double.NaN;
            }

            val0 = this.compuMethod.compute(1);
            val1 = this.compuMethod.compute(2);
            tabResol[2] = ConversionType.TAB_VERB.compareTo(this.compuMethod.getConversionType()) != 0 ? val1 - val0 : Double.NaN;
            break;
        case VAL_BLK:
            val0 = this.compuMethod.compute(1);
            val1 = this.compuMethod.compute(2);
            tabResol = new double[] { ConversionType.TAB_VERB.compareTo(this.compuMethod.getConversionType()) != 0 ? val1 - val0 : Double.NaN };
            break;
        default:
            tabResol = new double[] { Double.NaN };
            break;
        }
        return tabResol;

    }

}
