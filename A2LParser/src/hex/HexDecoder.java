/*
 * Creation : 6 mars 2019
 */
package hex;

import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import a2lobject.A2l;
import a2lobject.AxisDescr;
import a2lobject.AxisDescr.Attribute;
import a2lobject.AxisPts;
import a2lobject.Characteristic;
import a2lobject.CompuMethod;
import a2lobject.FixAxisPar;
import a2lobject.FixAxisParDist;
import a2lobject.ModCommon;
import a2lobject.ModPar;
import a2lobject.RecordLayout.AxisPtsX;
import a2lobject.RecordLayout.FncValues;
import a2lobject.RecordLayout.NoAxisPtsX;
import constante.ConversionType;
import constante.IndexMode;
import constante.SecondaryKeywords;
import utils.Converter;

public final class HexDecoder {

    private A2l a2l;
    private IntelHex hex;

    public HexDecoder(A2l a2l, IntelHex hex) {
        this.a2l = a2l;
        this.hex = hex;
    }

    public final boolean checkEPK() {
        // Check EPK
        final ModPar modPar = a2l.getModPar();
        long adressEPK = modPar.getEPKAdress();

        if (adressEPK > 0) {

            String mEPK = modPar.getEPK();
            String data = hex.readString(adressEPK, mEPK.length());

            return mEPK.equals(data);
        }
        return true;
    }

    public final boolean readDataFromHex() {

        ModCommon modCommon = a2l.getModCommon();
        ByteOrder byteOrder = modCommon.getByteOrder();

        for (Characteristic characteristic : a2l.getCharacteristics()) {

            FncValues fncValues = characteristic.getRecordLayout().getFncValues();
            CompuMethod compuMethod = characteristic.getCompuMethod();

            long adress = characteristic.getAdress();

            // System.out.println(characteristic);

            switch (characteristic.getType()) {
            case VALUE:
                readValue(byteOrder, characteristic, adress, compuMethod, fncValues);
                break;
            case ASCII:
                readAscii(characteristic, adress);
                break;
            case CURVE:
                readCurve(byteOrder, characteristic, adress, compuMethod, fncValues);
                break;
            case VAL_BLK:
                readValBlk(byteOrder, characteristic, adress, compuMethod, fncValues);
                break;
            case MAP:
                readMap(byteOrder, characteristic, adress, compuMethod, fncValues);
                break;
            default:
                break;
            }
        }

        return true;

    }

    private final void readValue(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {

        double hexValue = Converter.readHexValue(hex, adress, fncValues.getDataType(), byteOrder);
        double physValue;

        if (characteristic.hasBitMask()) {
            hexValue = characteristic.applyBitMask((long) hexValue);
        }

        String displayFormat = characteristic.getFormat();

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

            physValue = compuMethod.compute(hexValue);
            characteristic.setValues(String.format(displayFormat, physValue).trim());
        } else {

            characteristic.setValues(compuMethod.computeString(hexValue));
        }
    }

    private final void readAscii(Characteristic characteristic, long adress) {

        int nByte = characteristic.getDim();

        String ascii = hex.readString(adress, nByte);
        if (ascii != null) {
            characteristic.setValues(ascii);
        }
    }

    private final void readCurve(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {
        AxisDescr axisDescr = characteristic.getAxisDescrs().get(0);
        int nbValue = axisDescr.getMaxAxisPoints();

        StringBuilder sb = new StringBuilder();

        switch (axisDescr.getAttribute()) {
        case FIX_AXIS:
            Set<Entry<SecondaryKeywords, Object>> entrySet = axisDescr.getOptionalsParameters().entrySet();
            Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

            sb.append(Attribute.FIX_AXIS.name() + "_X = ");

            while (it.hasNext()) {
                Map.Entry<SecondaryKeywords, Object> entry = it.next();
                if (entry.getValue() instanceof FixAxisParDist) {
                    FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                    for (int n = 0; n < axisDist.getNumberapo(); n++) {
                        sb.append(axisDist.compute(n) + " | ");
                    }
                    sb.append("\n");
                    break;
                } else if (entry.getValue() instanceof FixAxisPar) {
                    FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                    for (int n = 0; n < axisDist.getNumberapo(); n++) {
                        sb.append(axisDist.compute(n) + " | ");
                    }
                    sb.append("\n");
                }
            }
            break;

        case STD_AXIS:
            AxisPtsX axisPtsXStdAxis = (AxisPtsX) characteristic.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
            NoAxisPtsX noAxisPtsX = (NoAxisPtsX) characteristic.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.NO_AXIS_PTS_X);

            nbValue = (int) Converter.readHexValue(hex, adress, noAxisPtsX.getDataType(), byteOrder);
            adress += noAxisPtsX.getDataType().getNbByte();

            sb.append(Attribute.STD_AXIS.name() + "_X = ");

            AxisDescr axisDescrStdAxis = characteristic.getAxisDescrs().get(0);
            CompuMethod compuMethodStdAxis = axisDescrStdAxis.getCompuMethod();

            double[] hexValues = Converter.readHexValues(hex, adress, axisPtsXStdAxis.getDataType(), byteOrder, nbValue);
            adress += axisPtsXStdAxis.getDataType().getNbByte() * nbValue;

            for (double val : hexValues) {
                sb.append(compuMethodStdAxis.compute(val) + " | ");
            }
            sb.append("\n");

            break;

        case COM_AXIS:

            AxisPts axisPts = axisDescr.getAxisPts();
            AxisPtsX axisPtsX = (AxisPtsX) axisPts.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
            CompuMethod compuMethodAxis = axisPts.getCompuMethod();
            long adressAxis = axisPts.getAdress();

            sb.append(Attribute.COM_AXIS.name() + "_X = ");

            double[] hexValuesComAxis = Converter.readHexValues(hex, adressAxis, axisPtsX.getDataType(), byteOrder, nbValue);

            for (double val : hexValuesComAxis) {
                sb.append(compuMethodAxis.compute(val) + " | ");
            }
            sb.append("\n");

            break;

        default:
            break;
        }

        sb.append("         Z = ");

        double[] hexValues = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValue);

        for (double val : hexValues) {
            sb.append(compuMethod.compute(val) + " | ");
        }
        sb.append("\n");

        characteristic.setValues(sb.toString());
    }

    private final void readValBlk(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {
        StringBuilder sb1 = new StringBuilder();

        Object numberParam = characteristic.getOptionalsParameters().get(SecondaryKeywords.NUMBER);
        Object matrixDimParam = characteristic.getOptionalsParameters().get(SecondaryKeywords.MATRIX_DIM);

        IndexMode indexModeValBlk = fncValues.getIndexMode();

        int[] dim;

        if (matrixDimParam != null) {
            Object[] arrMatrixDim = (Object[]) matrixDimParam;

            switch (arrMatrixDim.length) {
            case 1:
                dim = new int[] { (int) arrMatrixDim[0] };
                break;
            case 2:
                dim = new int[] { (int) arrMatrixDim[0], (int) arrMatrixDim[1] };
                break;
            case 3:
                dim = new int[] { (int) arrMatrixDim[0], (int) arrMatrixDim[1], (int) arrMatrixDim[2] };
                break;

            default:
                dim = new int[] { 0 };
                break;
            }

        } else {
            dim = new int[] { (int) numberParam };
        }

        sb1.append("X = ");
        for (int x = 0; x < dim[0]; x++) {
            sb1.append(x + " | ");
        }

        double[] hexValuesValBlk;

        if (dim.length < 2 || dim[1] == 1) {
            sb1.append("\n");
            sb1.append("Z = ");

            hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, dim[0]);

            for (double val : hexValuesValBlk) {
                sb1.append(compuMethod.compute(val) + " | ");
            }

        } else {

            int nbValue = dim[0] * dim[1];

            hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValue);

            int cnt = 0;

            for (double val : hexValuesValBlk) {

                if (cnt % dim[0] == 0) {
                    sb1.append("\nZ" + (cnt / dim[0]) + " = ");
                }
                sb1.append(compuMethod.compute(val) + " | ");
                cnt++;
            }
        }

        sb1.append("\n");

        characteristic.setValues(sb1.toString());
    }

    private void readMap(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {
        StringBuilder sb2 = new StringBuilder();

        int nbValueMap = 0;
        int[] dimMap = new int[2];

        IndexMode indexModeMap = fncValues.getIndexMode();

        int cnt = 0;

        for (AxisDescr axis : characteristic.getAxisDescrs()) {
            switch (axis.getAttribute()) {
            case FIX_AXIS:
                dimMap[cnt] = axis.getMaxAxisPoints();

                Set<Entry<SecondaryKeywords, Object>> entrySet = axis.getOptionalsParameters().entrySet();
                Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

                if (cnt == 0) {
                    sb2.append(Attribute.FIX_AXIS.name() + "_X = ");
                } else {
                    sb2.append(Attribute.FIX_AXIS.name() + "_Y = ");
                }

                while (it.hasNext()) {
                    Map.Entry<SecondaryKeywords, Object> entry = it.next();
                    if (entry.getValue() instanceof FixAxisParDist) {
                        FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                        for (int n = 0; n < axisDist.getNumberapo(); n++) {
                            sb2.append(axisDist.compute(n) + " | ");
                        }
                        sb2.append("\n");
                        break;
                    } else if (entry.getValue() instanceof FixAxisPar) {
                        FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                        for (int n = 0; n < axisDist.getNumberapo(); n++) {
                            sb2.append(axisDist.compute(n) + " | ");
                        }
                        sb2.append("\n");
                    }
                }

                break;
            case STD_AXIS:

                break;
            case COM_AXIS:

                AxisPts axisPts = axis.getAxisPts();
                AxisPtsX axisPtsX = (AxisPtsX) axisPts.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
                CompuMethod compuMethodAxis = axisPts.getCompuMethod();
                long adressAxis = axisPts.getAdress();

                int nbValueAxis = axisPts.getMaxAxisPoints();

                dimMap[cnt] = axis.getMaxAxisPoints();

                if (cnt == 0) {
                    sb2.append(Attribute.COM_AXIS.name() + "_X = ");
                } else {
                    sb2.append(Attribute.COM_AXIS.name() + "_Y = ");
                }

                double[] hexValuesComAxis = Converter.readHexValues(hex, adressAxis, axisPtsX.getDataType(), byteOrder, nbValueAxis);

                for (double val : hexValuesComAxis) {
                    sb2.append(compuMethodAxis.compute(val) + " | ");
                }
                sb2.append("\n");

                break;

            default:
                break;
            }

            cnt++;
        }

        nbValueMap = dimMap[0] * dimMap[1];

        if (nbValueMap > 0) {

            double[] hexValues = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValueMap);

            cnt = 0;

            for (double val : hexValues) {

                if (cnt % dimMap[0] == 0) {
                    sb2.append("\nZ" + (cnt / dimMap[0]) + " = ");
                }
                sb2.append(compuMethod.compute(val) + " | ");
                cnt++;
            }

            sb2.append("\n");
        }

        characteristic.setValues(sb2.toString());
    }

}
