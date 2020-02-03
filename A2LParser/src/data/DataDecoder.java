/*
 * Creation : 6 mars 2019
 */
package data;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import a2l.A2l;
import a2l.AdjustableObject;
import a2l.AxisDescr;
import a2l.AxisPts;
import a2l.Characteristic;
import a2l.Characteristic.CharacteristicType;
import a2l.CompuMethod;
import a2l.FixAxisPar;
import a2l.FixAxisParDist;
import a2l.FixAxisParList;
import a2l.ModCommon;
import a2l.ModPar;
import a2l.RecordLayout;
import a2l.RecordLayout.AxisPtsX;
import a2l.RecordLayout.AxisPtsY;
import a2l.RecordLayout.AxisRescaleX;
import a2l.RecordLayout.FncValues;
import a2l.RecordLayout.NoAxisPtsX;
import a2l.RecordLayout.NoAxisPtsY;
import a2l.RecordLayout.NoRescaleX;
import a2l.RecordLayout.Reserved;
import a2l.Values;
import constante.DataType;
import constante.DepositMode;
import constante.IndexMode;
import constante.IndexOrder;
import constante.SecondaryKeywords;
import utils.Converter;

public final class DataDecoder {

    private final A2l a2l;
    private final DataCalibration dataFile;
    private final ModCommon modCommon;

    private static long tmpAdress = 0;

    public DataDecoder(A2l a2l, DataCalibration hex) {
        this.a2l = a2l;
        this.dataFile = hex;

        this.modCommon = a2l.getModCommon();
    }

    private final boolean checkEPK() {
        // Check EPK
        final ModPar modPar = a2l.getModPar();
        final long adressEPK = modPar.getEPKAdress();

        if (adressEPK > 0) {

            String mEPK = modPar.getEPK();
            String data = dataFile.readString(adressEPK, mEPK.length());

            return mEPK.equals(data);
        }
        return true;
    }

    public final boolean readDataFromFile() {

        if (!checkEPK()) {
            return false;
        }

        final ByteOrder byteOrder = modCommon.getByteOrder();

        for (Entry<String, AdjustableObject> entries : a2l.getAdjustableObjects().entrySet()) {
            if (entries.getValue() instanceof AxisPts && entries.getValue().isValid()) {
                readAxisPts(byteOrder, (AxisPts) entries.getValue());
            }
        }

        for (Entry<String, AdjustableObject> entries : a2l.getAdjustableObjects().entrySet()) {

            if (entries.getValue() instanceof Characteristic && entries.getValue().isValid()) {

                Characteristic characteristic = (Characteristic) entries.getValue();

                final FncValues fncValues = characteristic.getRecordLayout().getFncValues();
                final CompuMethod compuMethod = characteristic.getCompuMethod();

                long adress = characteristic.getAdress();

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
                case CUBOID:
                    Values valuesCuboid = new Values(1, 1);
                    valuesCuboid.setValue(0, 0, "Not implemented");
                    characteristic.setValues(valuesCuboid);
                    break;
                case CUBE_4:
                    Values valuesCube4 = new Values(1, 1);
                    valuesCube4.setValue(0, 0, "Not implemented");
                    characteristic.setValues(valuesCube4);
                    break;
                case CUBE_5:
                    Values valuesCube5 = new Values(1, 1);
                    valuesCube5.setValue(0, 0, "Not implemented");
                    characteristic.setValues(valuesCube5);
                    break;
                default:
                    // Nothing
                    break;
                }
            }
        }

        return true;
    }

    private final void readAxisPts(ByteOrder commonByteOrder, AxisPts axisPts) {

        long adress = axisPts.getAdress();
        final ByteOrder byteOrder = axisPts.getByteOrder() != null ? axisPts.getByteOrder() : commonByteOrder;

        final RecordLayout recordLayout = axisPts.getRecordLayout();
        final CompuMethod compuMethod = axisPts.getCompuMethod();
        final NoAxisPtsX noAxisPtsX = recordLayout.getNoAxisPtsX();
        final AxisPtsX axisPtsX = recordLayout.getAxisPtsX();

        final NoRescaleX noRescaleX = recordLayout.getNoRescaleX();
        final Reserved reserved = recordLayout.getReserved();
        final AxisRescaleX axisRescaleX = recordLayout.getAxisRescaleX();

        IndexOrder indexOrder;
        short nbValue = axisPts.getMaxAxisPoints();

        double physValue = 0;
        Values values = null;
        double[] hexValuesAxisPts;

        if (axisPtsX != null) {

            indexOrder = axisPtsX.getIndexOrder();

            if (noAxisPtsX != null) {
                adress = setAlignment(adress, noAxisPtsX.getDataType());
                nbValue = (short) Converter.readHexValue(dataFile, adress, noAxisPtsX.getDataType(), byteOrder);
                adress += noAxisPtsX.getDataType().getNbByte();
            }

            adress = setAlignment(adress, axisPtsX.getDataType());
            hexValuesAxisPts = Converter.readHexValues(dataFile, adress, axisPtsX.getDataType(), byteOrder, nbValue);

        } else {

            indexOrder = axisRescaleX.getIndexOrder();

            adress = setAlignment(adress, noRescaleX.getDataType());
            nbValue = (short) Converter.readHexValue(dataFile, adress, noRescaleX.getDataType(), byteOrder);
            adress += noRescaleX.getDataType().getNbByte();

            // Skip reserved
            adress += (reserved.getDataSize().getNbBits() / 8);

            adress = setAlignment(adress, axisRescaleX.getDataType());
            hexValuesAxisPts = Converter.readHexValues(dataFile, adress, axisRescaleX.getDataType(), byteOrder, nbValue);
        }

        values = new Values(nbValue, 1);

        if (!compuMethod.isVerbal()) {

            for (short n = 0; n < nbValue; n++) {
                physValue = compuMethod.compute(hexValuesAxisPts[n]);
                if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                    values.setValue(0, n, Double.toString(physValue));
                } else {
                    values.setValue(0, (nbValue - 1) - n, Double.toString(physValue));
                }
            }
        } else {
            for (short n = 0; n < nbValue; n++) {
                if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                    values.setValue(0, n, compuMethod.computeString(hexValuesAxisPts[n]));
                } else {
                    values.setValue(0, (nbValue - 1) - n, compuMethod.computeString(hexValuesAxisPts[n]));
                }
            }
        }
        axisPts.setValues(values);
    }

    private final void readValue(ByteOrder commonByteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod,
            FncValues fncValues) {

        final ByteOrder byteOrder = characteristic.getByteOrder() != null ? characteristic.getByteOrder() : commonByteOrder;
        final long _adress = setAlignment(adress, fncValues.getDataType());
        double hexValue = Converter.readHexValue(dataFile, _adress, fncValues.getDataType(), byteOrder);
        double physValue;

        final Values values = new Values(1, 1);

        if (!compuMethod.isVerbal()) {

            if (characteristic.hasBitMask()) {
                hexValue = characteristic.applyBitMask((long) hexValue);
            }

            physValue = compuMethod.compute(hexValue);
            values.setValue(0, 0, Double.toString(physValue));

        } else {
            if (characteristic.hasBitMask()) {
                hexValue = characteristic.applyBitMask((long) hexValue);
            }

            values.setValue(0, 0, compuMethod.computeString(hexValue));
        }

        characteristic.setValues(values);
    }

    private final void readAscii(Characteristic characteristic, long adress) {

        final short nByte = characteristic.getDim();

        final Values values = new Values(1, 1);

        final String ascii = dataFile.readString(adress, nByte);
        if (ascii != null) {
            values.setValue(0, 0, ascii);
            characteristic.setValues(values);
        }
    }

    private final String[] readFixAxis(AxisDescr axisDescr) {

        final Set<Entry<SecondaryKeywords, Object>> entrySet = axisDescr.getOptionalsParameters().entrySet();
        final Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

        String[] strValues = null;

        while (it.hasNext()) {
            Map.Entry<SecondaryKeywords, Object> entry = it.next();
            if (entry.getValue() instanceof FixAxisParDist) {
                FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                strValues = new String[axisDist.getNumberapo()];
                for (short n = 0; n < axisDist.getNumberapo(); n++) {
                    strValues[n] = Double.toString(axisDist.compute(n));
                }
                return strValues;
            } else if (entry.getValue() instanceof FixAxisPar) {
                FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                strValues = new String[axisDist.getNumberapo()];
                for (short n = 0; n < axisDist.getNumberapo(); n++) {
                    strValues[n] = Double.toString(axisDist.compute(n));
                }
                return strValues;
            } else if (entry.getValue() instanceof FixAxisParList) {
                FixAxisParList axisDist = (FixAxisParList) entry.getValue();
                strValues = new String[axisDist.getNbValue()];
                for (short n = 0; n < axisDist.getNbValue(); n++) {
                    strValues[n] = Double.toString(axisDist.compute(n));
                }
                return strValues;
            }
        }

        return new String[] { "" };
    }

    private final String[] readStdAxis(Characteristic characteristic, long adress, int idxAxis, ByteOrder commonByteOrder) {

        String[] strValues = null;

        final AxisDescr axisDescrStdAxis = characteristic.getAxisDescrs().get(idxAxis);
        final ByteOrder byteOrder = axisDescrStdAxis.getByteOrder() != null ? axisDescrStdAxis.getByteOrder() : commonByteOrder;
        final CompuMethod compuMethod = axisDescrStdAxis.getCompuMethod();
        final String depositMode = axisDescrStdAxis.getDepositMode();

        short nbValue = 0;
        DataType axisDataType;
        IndexOrder indexOrder;

        if (characteristic.getType().compareTo(CharacteristicType.MAP) == 0) {
            if (idxAxis == 0) {
                NoAxisPtsX noAxisPtsX = characteristic.getRecordLayout().getNoAxisPtsX();
                if (characteristic.getRecordLayout().getSrcAddrX() != null) {
                    adress += characteristic.getRecordLayout().getSrcAddrX().getDataType().getNbByte();
                }

                adress = setAlignment(adress, noAxisPtsX.getDataType());
                nbValue = (short) Converter.readHexValue(dataFile, adress, noAxisPtsX.getDataType(), byteOrder);
                adress += noAxisPtsX.getDataType().getNbByte();

                NoAxisPtsY noAxisPtsY = characteristic.getRecordLayout().getNoAxisPtsY();
                if (characteristic.getRecordLayout().getSrcAddrY() != null) {
                    adress += characteristic.getRecordLayout().getSrcAddrY().getDataType().getNbByte();
                }
                adress += noAxisPtsY.getDataType().getNbByte();

                AxisPtsX axisPtsXStdAxis = characteristic.getRecordLayout().getAxisPtsX();
                axisDataType = axisPtsXStdAxis.getDataType();
                indexOrder = axisPtsXStdAxis.getIndexOrder();
            } else {

                NoAxisPtsX noAxisPtsX = characteristic.getRecordLayout().getNoAxisPtsX();
                if (characteristic.getRecordLayout().getSrcAddrX() != null) {
                    adress += characteristic.getRecordLayout().getSrcAddrX().getDataType().getNbByte();
                }
                adress = setAlignment(adress, noAxisPtsX.getDataType());
                short nbValueX = (short) Converter.readHexValue(dataFile, adress, noAxisPtsX.getDataType(), byteOrder);
                adress += noAxisPtsX.getDataType().getNbByte();

                NoAxisPtsY noAxisPtsY = characteristic.getRecordLayout().getNoAxisPtsY();
                if (characteristic.getRecordLayout().getSrcAddrY() != null) {
                    adress += characteristic.getRecordLayout().getSrcAddrY().getDataType().getNbByte();
                }
                adress = setAlignment(adress, noAxisPtsY.getDataType());
                nbValue = (short) Converter.readHexValue(dataFile, adress, noAxisPtsY.getDataType(), byteOrder);
                adress += noAxisPtsY.getDataType().getNbByte();

                AxisPtsX axisPtsXStdAxis = characteristic.getRecordLayout().getAxisPtsX();
                axisDataType = axisPtsXStdAxis.getDataType();
                adress += (axisDataType.getNbByte() * nbValueX);

                AxisPtsY axisPtsYStdAxis = characteristic.getRecordLayout().getAxisPtsY();
                axisDataType = axisPtsYStdAxis.getDataType();
                indexOrder = axisPtsYStdAxis.getIndexOrder();
            }
        } else {
            AxisPtsX axisPtsXStdAxis = characteristic.getRecordLayout().getAxisPtsX();
            axisDataType = axisPtsXStdAxis.getDataType();
            indexOrder = axisPtsXStdAxis.getIndexOrder();
            NoAxisPtsX noAxisPts = characteristic.getRecordLayout().getNoAxisPtsX();

            if (characteristic.getRecordLayout().getSrcAddrX() != null) {
                adress += characteristic.getRecordLayout().getSrcAddrX().getDataType().getNbByte();
            }

            adress = setAlignment(adress, noAxisPts.getDataType());
            nbValue = (short) Converter.readHexValue(dataFile, adress, noAxisPts.getDataType(), byteOrder);
            adress += noAxisPts.getDataType().getNbByte();
        }

        strValues = new String[nbValue];

        adress = setAlignment(adress, axisDataType);
        double[] hexValues = Converter.readHexValues(dataFile, adress, axisDataType, byteOrder, nbValue);
        adress += axisDataType.getNbByte() * nbValue;

        tmpAdress = adress;

        double physValue = 0;
        double predValue = 0;

        if (!compuMethod.isVerbal()) {

            for (short n = 0; n < nbValue; n++) {
                if (!depositMode.equals(DepositMode.DIFFERENCE.name())) {
                    physValue = compuMethod.compute(hexValues[n]);
                } else {
                    if (n == 0) {
                        physValue = compuMethod.compute(hexValues[n]);
                        predValue = physValue;
                    }
                    if (n > 0 && n < nbValue - 1) {
                        physValue = compuMethod.compute(hexValues[n]) + predValue;
                        predValue = physValue;
                    }
                    if (n == nbValue - 1) {
                        physValue = compuMethod.compute(hexValues[n - 1]) + predValue;
                        predValue = physValue;
                    }
                }
                if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                    strValues[n] = Double.toString(physValue);
                } else {
                    strValues[(nbValue - 1) - n] = Double.toString(physValue);
                }
            }
        } else {
            for (short n = 0; n < nbValue; n++) {
                if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                    strValues[n] = compuMethod.computeString(hexValues[n]);
                } else {
                    strValues[(nbValue - 1) - n] = compuMethod.computeString(hexValues[n]);
                }
            }
        }
        return strValues;
    }

    private final String[] readCurveAxis(Characteristic characteristic) {

        String[] strValues = null;

        Values curveValues = characteristic.getValues();
        strValues = new String[curveValues.getDimX()];

        for (short n = 0; n < strValues.length; n++) {
            strValues[n] = curveValues.getValue(0, n);
        }

        return strValues;
    }

    private final void readCurve(ByteOrder commonByteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod,
            FncValues fncValues) {

        final AxisDescr axisDescr = characteristic.getAxisDescrs().get(0);
        final ByteOrder byteOrder = axisDescr.getByteOrder() != null ? axisDescr.getByteOrder() : commonByteOrder;

        int nbValue = axisDescr.getMaxAxisPoints();

        Values values = null;

        switch (axisDescr.getAttribute()) {
        case FIX_AXIS:
            String[] fixAxisValues = readFixAxis(axisDescr);
            nbValue = fixAxisValues.length;
            values = new Values(nbValue, 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(0, i, fixAxisValues[i]);
            }
            break;

        case STD_AXIS:
            String[] stdAxisValues = readStdAxis(characteristic, adress, 0, byteOrder);
            nbValue = stdAxisValues.length;
            values = new Values(nbValue, 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(0, i, stdAxisValues[i]);
            }

            adress = tmpAdress;
            break;

        case COM_AXIS:
            String[] comAxisValues = ((AxisPts) axisDescr.getAxisPts()).getStringValues();
            nbValue = comAxisValues.length;

            values = new Values(nbValue, 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(0, i, comAxisValues[i]);
            }
            break;
        case RES_AXIS:
            String[] resAxisValues = ((AxisPts) axisDescr.getAxisPts()).getStringValues();
            nbValue = resAxisValues.length;

            values = new Values(nbValue, 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(0, i, resAxisValues[i]);
            }
            break;
        case CURVE_AXIS:

            Characteristic curve = (Characteristic) characteristic.getAxisDescrs().get(0).getCurveAxis();

            if (curve.getValues() == null) {
                readCurve(byteOrder, curve, curve.getAdress(), curve.getCompuMethod(), curve.getRecordLayout().getFncValues());
            }

            String[] curveAxisValue = readCurveAxis(curve);
            nbValue = curveAxisValue.length;

            values = new Values(nbValue, 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(0, i, curveAxisValue[i]);
            }
            break;

        default:
            return;
        }

        adress = setAlignment(adress, fncValues.getDataType());
        double[] hexValues = Converter.readHexValues(dataFile, adress, fncValues.getDataType(), byteOrder, nbValue);
        double physValue = 0;

        if (!compuMethod.isVerbal()) {

            for (short n = 0; n < nbValue; n++) {
                physValue = compuMethod.compute(hexValues[n]);
                values.setValue(1, n, Double.toString(physValue));
            }

        } else {

            if (characteristic.hasBitMask()) {
                for (short i = 0; i < nbValue; i++) {
                    hexValues[i] = characteristic.applyBitMask((long) hexValues[i]);
                }
            }
            for (short n = 0; n < nbValue; n++) {
                values.setValue(1, n, compuMethod.computeString(hexValues[n]));
            }
        }

        characteristic.setValues(values);
    }

    private final void readValBlk(ByteOrder commonByteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod,
            FncValues fncValues) {

        final ByteOrder byteOrder = characteristic.getByteOrder() != null ? characteristic.getByteOrder() : commonByteOrder;

        final IndexMode indexModeValBlk = fncValues.getIndexMode();

        double physValue;
        final short[] dim = characteristic.getDimArray();

        Values values;

        if (dim.length < 2 || dim[1] == 1) {
            values = new Values(dim[0] + 1, 2);
            values.setValue(0, 0, "X");
        } else {
            values = new Values(dim[0] + 1, dim[1] + 1);
            values.setValue(0, 0, "Y\\X");
            for (short y = 0; y < dim[1]; y++) { // Patch pour les VAL_BLK qui n'ont qu'une colonne
                values.setValue(y + 1, 0, Integer.toString(y));
            }
        }

        for (short x = 0; x < dim[0]; x++) {
            values.setValue(0, x + 1, Integer.toString(x));
        }

        double[] hexValuesValBlk;

        if (dim.length < 2 || dim[1] == 1) {

            adress = setAlignment(adress, fncValues.getDataType());
            hexValuesValBlk = Converter.readHexValues(dataFile, adress, fncValues.getDataType(), byteOrder, dim[0]);

            values.setValue(1, 0, "Z");

            if (!compuMethod.isVerbal()) {

                for (short n = 0; n < dim[0]; n++) {
                    physValue = compuMethod.compute(hexValuesValBlk[n]);
                    values.setValue(1, n + 1, Double.toString(physValue));
                }
            } else {
                if (characteristic.hasBitMask()) {
                    for (short i = 0; i < dim[0]; i++) {
                        hexValuesValBlk[i] = characteristic.applyBitMask((long) hexValuesValBlk[i]);
                    }
                }
                for (short n = 0; n < dim[0]; n++) {
                    values.setValue(1, n + 1, compuMethod.computeString(hexValuesValBlk[n]));
                }
            }

        } else {

            final int nbValue = dim[0] * dim[1];

            adress = setAlignment(adress, fncValues.getDataType());
            hexValuesValBlk = Converter.readHexValues(dataFile, adress, fncValues.getDataType(), byteOrder, nbValue);

            if (!compuMethod.isVerbal()) {

                int row = 0;
                int col = 0;
                for (short n = 0; n < nbValue; n++) {
                    if (indexModeValBlk.compareTo(IndexMode.COLUMN_DIR) == 0) {
                        if (n % dim[1] == 0) {
                            row += 1;
                        }
                        col = n % dim[1];
                        physValue = compuMethod.compute(hexValuesValBlk[n]);
                        values.setValue(col + 1, row, Double.toString(physValue));
                    } else {
                        if (n % dim[0] == 0) { // => OK pour ROW_DIR
                            values.setValue(row + 1, 0, Integer.toString(row));
                            row += 1;
                        }
                        col = n % dim[0];
                        physValue = compuMethod.compute(hexValuesValBlk[n]);
                        values.setValue(row, col + 1, Double.toString(physValue));
                    }
                }

            } else {

                if (characteristic.hasBitMask()) {
                    for (short i = 0; i < nbValue; i++) {
                        hexValuesValBlk[i] = characteristic.applyBitMask((long) hexValuesValBlk[i]);
                    }
                }
                int row = 0;
                int col = 0;
                for (short n = 0; n < nbValue; n++) {
                    if (indexModeValBlk.compareTo(IndexMode.COLUMN_DIR) == 0) {
                        if (n % dim[1] == 0) {
                            row += 1;
                        }
                        col = n % dim[1];
                        values.setValue(col + 1, row, compuMethod.computeString(hexValuesValBlk[n]));
                    } else {
                        if (n % dim[0] == 0) { // => OK pour ROW_DIR
                            values.setValue(row + 1, 0, Integer.toString(row));
                            row += 1;
                        }
                        col = n % dim[0];
                        values.setValue(row, col + 1, compuMethod.computeString(hexValuesValBlk[n]));
                    }
                }
            }
        }

        characteristic.setValues(values);
    }

    private void readMap(ByteOrder commonByteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {

        final ByteOrder byteOrder = characteristic.getByteOrder() != null ? characteristic.getByteOrder() : commonByteOrder;
        double physValue;

        int nbValueMap = 0;
        int dimX = 0;
        int dimY = 0;

        final List<String[]> listAxisValues = new ArrayList<String[]>();

        byte idxAxis = 0;

        for (AxisDescr axisDescr : characteristic.getAxisDescrs()) {
            switch (axisDescr.getAttribute()) {
            case FIX_AXIS:

                listAxisValues.add(readFixAxis(axisDescr));

                break;
            case STD_AXIS:

                listAxisValues.add(readStdAxis(characteristic, adress, idxAxis, byteOrder));
                if (idxAxis > 0) {
                    adress = tmpAdress;
                }
                idxAxis++;

                break;
            case COM_AXIS:

                AxisPts axisPts = (AxisPts) axisDescr.getAxisPts();
                listAxisValues.add(axisPts.getStringValues());

                break;
            default:
                return;
            }
        }

        if (listAxisValues.size() == 2) {
            dimX = listAxisValues.get(0).length;
            dimY = listAxisValues.get(1).length;
        } else {
            return;
        }

        nbValueMap = dimX * dimY;

        Values values = new Values(dimX + 1, dimY + 1);
        values.setValue(0, 0, "Y\\X");

        for (byte i = 0; i < 2; i++) {
            String[] axisValues = listAxisValues.get(i);
            for (short n = 0; n < axisValues.length; n++) {
                if (i == 0) {
                    values.setValue(0, n + 1, axisValues[n]);
                } else {
                    values.setValue(n + 1, 0, axisValues[n]);
                }
            }
        }

        listAxisValues.clear();

        if (nbValueMap > 0) {

            final IndexMode indexModeMap = fncValues.getIndexMode();

            adress = setAlignment(adress, fncValues.getDataType());
            double[] hexValues = Converter.readHexValues(dataFile, adress, fncValues.getDataType(), byteOrder, nbValueMap);

            if (!compuMethod.isVerbal()) {

                int row = 0;
                int col = 0;
                for (short n = 0; n < nbValueMap; n++) {
                    if (indexModeMap.compareTo(IndexMode.COLUMN_DIR) == 0) {
                        if (n % dimY == 0) {
                            row += 1;
                        }
                        col = n % dimY;
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(col + 1, row, Double.toString(physValue));
                    } else {
                        if (n % dimX == 0) {
                            row += 1;
                        }
                        col = n % dimX;
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(row, col + 1, Double.toString(physValue));
                    }

                }

            } else {

                if (characteristic.hasBitMask()) {
                    for (short i = 0; i < nbValueMap; i++) {
                        hexValues[i] = characteristic.applyBitMask((long) hexValues[i]);
                    }
                }
                int row = 0;
                int col = 0;
                for (short n = 0; n < nbValueMap; n++) {
                    if (indexModeMap.compareTo(IndexMode.COLUMN_DIR) == 0) {
                        if (n % dimY == 0) {
                            row += 1;
                        }
                        col = n % dimY;
                        values.setValue(col + 1, row, compuMethod.computeString(hexValues[n]));
                    } else {
                        if (n % dimX == 0) {
                            row += 1;
                        }
                        col = n % dimX;
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(row, col + 1, compuMethod.computeString(hexValues[n]));
                    }
                }
            }
        }
        characteristic.setValues(values);
    }

    private final long setAlignment(long adress, DataType dataType) {
        final int alignment = this.modCommon.getAlignment(dataType);
        return alignment > 1 ? adress + (adress & (alignment - 1)) : adress;
    }

}
