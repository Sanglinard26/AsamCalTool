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
import a2lobject.RecordLayout.AxisPtsY;
import a2lobject.RecordLayout.FncValues;
import a2lobject.RecordLayout.NoAxisPtsX;
import a2lobject.RecordLayout.NoAxisPtsY;
import a2lobject.Values;
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
            
            if(characteristic.toString().equals("EcoCoach_noGearLowCurve_T"))
            System.out.println(characteristic);

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

        String displayFormat = characteristic.getFormat();

        Values values = new Values(1, 1);

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

            physValue = compuMethod.compute(hexValue);
            values.setValue(0, 0, String.format(displayFormat, physValue).trim());

        } else {
            if (characteristic.hasBitMask()) {
                hexValue = characteristic.applyBitMask((long) hexValue);
            }

            values.setValue(0, 0, compuMethod.computeString(hexValue));
        }

        characteristic.setValues(values);
    }

    private final void readAscii(Characteristic characteristic, long adress) {

        int nByte = characteristic.getDim();

        Values values = new Values(1, 1);

        String ascii = hex.readString(adress, nByte);
        if (ascii != null) {
            values.setValue(0, 0, ascii);
            characteristic.setValues(values);
        }
    }

    private final void readCurve(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {
        AxisDescr axisDescr = characteristic.getAxisDescrs().get(0);
        int nbValue = axisDescr.getMaxAxisPoints();
        
        String displayFormat = characteristic.getFormat();

        double physValue;
        Values values = null;

        switch (axisDescr.getAttribute()) {
        case FIX_AXIS:
            Set<Entry<SecondaryKeywords, Object>> entrySet = axisDescr.getOptionalsParameters().entrySet();
            Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

            while (it.hasNext()) {
                Map.Entry<SecondaryKeywords, Object> entry = it.next();
                if (entry.getValue() instanceof FixAxisParDist) {
                    FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                    values = new Values(axisDist.getNumberapo(), 2);
                    for (int n = 0; n < axisDist.getNumberapo(); n++) {
                        values.setValue(0, n, axisDist.compute(n) + "");
                    }
                    break;
                } else if (entry.getValue() instanceof FixAxisPar) {
                    FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                    values = new Values(axisDist.getNumberapo(), 2);
                    for (int n = 0; n < axisDist.getNumberapo(); n++) {
                        values.setValue(0, n, axisDist.compute(n) + "");
                    }
                }
            }
            break;

        case STD_AXIS:
            AxisPtsX axisPtsXStdAxis = characteristic.getRecordLayout().getAxisPtsX();
            NoAxisPtsX noAxisPtsX = characteristic.getRecordLayout().getNoAxisPtsX();

            nbValue = (int) Converter.readHexValue(hex, adress, noAxisPtsX.getDataType(), byteOrder);
            adress += noAxisPtsX.getDataType().getNbByte();

            values = new Values(nbValue, 2);

            AxisDescr axisDescrStdAxis = characteristic.getAxisDescrs().get(0);
            CompuMethod compuMethodStdAxis = axisDescrStdAxis.getCompuMethod();

            double[] hexValues = Converter.readHexValues(hex, adress, axisPtsXStdAxis.getDataType(), byteOrder, nbValue);
            adress += axisPtsXStdAxis.getDataType().getNbByte() * nbValue;

            if (compuMethodStdAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethodStdAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethodStdAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                for (int n = 0; n < nbValue; n++) {
                	physValue = compuMethodStdAxis.compute(hexValues[n]);
                    values.setValue(0, n, String.format(displayFormat, physValue).trim());
                }

            } else {
                for (int n = 0; n < nbValue; n++) {
                    values.setValue(0, n, compuMethodStdAxis.computeString(hexValues[n]) + "");
                }

            }

            break;

        case COM_AXIS:

            AxisPts axisPts = axisDescr.getAxisPts();
            AxisPtsX axisPtsX = (AxisPtsX) axisPts.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
            CompuMethod compuMethodAxis = axisPts.getCompuMethod();
            long adressAxis = axisPts.getAdress();

            values = new Values(nbValue, 2);

            double[] hexValuesComAxis = Converter.readHexValues(hex, adressAxis, axisPtsX.getDataType(), byteOrder, nbValue);
            
            if (compuMethodAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethodAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethodAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {
            	
            	for (int n = 0; n < nbValue; n++) {
                	physValue = compuMethodAxis.compute(hexValuesComAxis[n]);
                	//Todo Prise du compte du format pour les COM_AXIS
                    values.setValue(0, n, String.format(displayFormat, physValue).trim());
                }
            	
            }else{
            	for (int n = 0; n < nbValue; n++) {
                    values.setValue(0, n, compuMethodAxis.computeString(hexValuesComAxis[n]));
                }
            }

            

            break;

        default:
            break;
        }

        double[] hexValues = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValue);

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

            for (int n = 0; n < nbValue; n++) {
            	physValue = compuMethod.compute(hexValues[n]);
                values.setValue(1, n, String.format(displayFormat, physValue).trim());
            }

        } else {

            if (characteristic.hasBitMask()) {
                for (int i = 0; i < nbValue; i++) {
                    hexValues[i] = characteristic.applyBitMask((long) hexValues[i]);
                }
            }
            for (int n = 0; n < nbValue; n++) {
                values.setValue(1, n, compuMethod.computeString(hexValues[n]) + "");
            }
        }

        characteristic.setValues(values);
    }

    private final void readValBlk(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {

        IndexMode indexModeValBlk = fncValues.getIndexMode();
        
        double physValue;
        String displayFormat = characteristic.getFormat();

        int[] dim = characteristic.getDimArray();

        Values values;

        if (dim.length < 2 || dim[1] == 1) {
            values = new Values(dim[0]+1, 2);
            values.setValue(0, 0, "X");
        } else {
            values = new Values(dim[0] + 1, dim[1] + 1);
            values.setValue(0, 0, "Y\\X");
        }

        for (int x = 0; x < dim[0]; x++) {
            values.setValue(0, x + 1, x + "");
        }

        double[] hexValuesValBlk;

        if (dim.length < 2 || dim[1] == 1) {

            hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, dim[0]);
            
            values.setValue(1, 0, "Z");

            if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                for (int n = 0; n < dim[0]; n++) {
                	physValue = compuMethod.compute(hexValuesValBlk[n]);
                    values.setValue(1, n+1, String.format(displayFormat, physValue).trim());
                }
            } else {
                if (characteristic.hasBitMask()) {
                    for (int i = 0; i < dim[0]; i++) {
                        hexValuesValBlk[i] = characteristic.applyBitMask((long) hexValuesValBlk[i]);
                    }
                }
                for (int n = 0; n < dim[0]; n++) {
                    values.setValue(1, n+1, compuMethod.computeString(hexValuesValBlk[n]) + "");
                }
            }

        } else {

            int nbValue = dim[0] * dim[1];

            hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValue);

            if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                int row = 0;
                int col = 0;
                for (int n = 0; n < hexValuesValBlk.length; n++) {
                    if (n % (values.getDimX() - 1) == 0) {
                    	values.setValue(row+1, 0, row + "");
                        row += 1;
                        
                    }
                    col = n % (values.getDimX() - 1);
                    physValue = compuMethod.compute(hexValuesValBlk[n]);
                    values.setValue(row, col+1, String.format(displayFormat, physValue).trim());
                }

            } else {

                if (characteristic.hasBitMask()) {
                    for (int i = 0; i < hexValuesValBlk.length; i++) {
                        hexValuesValBlk[i] = characteristic.applyBitMask((long) hexValuesValBlk[i]);
                    }
                }
                int row = 0;
                int col = 0;
                for (int n = 0; n < hexValuesValBlk.length; n++) {
                    if (n % (values.getDimX() - 1) == 0) {
                    	values.setValue(row+1, 0, row + "");
                        row += 1; 
                    }
                    col = n % (values.getDimX() - 1);
                    values.setValue(row, col+1, compuMethod.compute(hexValuesValBlk[n]) + "");
                }

            }

        }

        characteristic.setValues(values);
    }

    private void readMap(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {
        StringBuilder sb2 = new StringBuilder();
        
        String displayFormat = characteristic.getFormat();

        int nbValueMap = 0;
        int[] dimMap = new int[2];

        Values values;

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

                CompuMethod compuMethodStdAxis = axis.getCompuMethod();
                double[] hexValues;

                if (cnt == 0) {
                    sb2.append(Attribute.STD_AXIS.name() + "_X = ");

                    AxisPtsX axisPtsXStdAxis = characteristic.getRecordLayout().getAxisPtsX();
                    NoAxisPtsX noAxisPtsX = characteristic.getRecordLayout().getNoAxisPtsX();

                    int nbValueX = (int) Converter.readHexValue(hex, adress, noAxisPtsX.getDataType(), byteOrder);
                    adress += noAxisPtsX.getDataType().getNbByte();

                    dimMap[cnt] = nbValueX;

                    NoAxisPtsY noAxisPtsY = characteristic.getRecordLayout().getNoAxisPtsY();

                    int nbValueY = (int) Converter.readHexValue(hex, adress, noAxisPtsX.getDataType(), byteOrder);
                    adress += noAxisPtsY.getDataType().getNbByte();

                    dimMap[cnt + 1] = nbValueY;

                    hexValues = Converter.readHexValues(hex, adress, axisPtsXStdAxis.getDataType(), byteOrder, nbValueX);
                    adress += axisPtsXStdAxis.getDataType().getNbByte() * nbValueX;

                    if (compuMethodStdAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                        for (double val : hexValues) {
                            sb2.append(compuMethodStdAxis.compute(val) + " | ");
                        }
                        sb2.append("\n");
                    } else {
                        for (double val : hexValues) {
                            sb2.append(compuMethodStdAxis.computeString(val) + " | ");
                        }
                        sb2.append("\n");
                    }

                } else {
                    sb2.append(Attribute.STD_AXIS.name() + "_Y = ");

                    AxisPtsY axisPtsYStdAxis = characteristic.getRecordLayout().getAxisPtsY();

                    hexValues = Converter.readHexValues(hex, adress, axisPtsYStdAxis.getDataType(), byteOrder, dimMap[cnt]);
                    adress += axisPtsYStdAxis.getDataType().getNbByte() * dimMap[cnt];

                    if (compuMethodStdAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                        for (double val : hexValues) {
                            sb2.append(compuMethodStdAxis.compute(val) + " | ");
                        }
                        sb2.append("\n");
                    } else {
                        for (double val : hexValues) {
                            sb2.append(compuMethodStdAxis.computeString(val) + " | ");
                        }
                        sb2.append("\n");
                    }

                }

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

            if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                for (double val : hexValues) {

                    if (cnt % dimMap[0] == 0) {
                        sb2.append("\nZ" + (cnt / dimMap[0]) + " = ");
                    }
                    sb2.append(compuMethod.compute(val) + " | ");
                    cnt++;
                }

                sb2.append("\n");
            } else {

                if (characteristic.hasBitMask()) {
                    for (int i = 0; i < hexValues.length; i++) {
                        hexValues[i] = characteristic.applyBitMask((long) hexValues[i]);
                    }
                }
                for (int i = 0; i < hexValues.length; i++) {
                    if (i % dimMap[0] == 0) {
                        sb2.append("\nZ" + (i / dimMap[0]) + " = ");
                    }
                    sb2.append(compuMethod.computeString(hexValues[i]) + " | ");
                }
            }
        }
        // characteristic.setValues(values);
    }

}
