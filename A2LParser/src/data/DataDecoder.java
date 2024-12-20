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
import a2l.ArrayValue;
import a2l.AxisDescr;
import a2l.AxisPts;
import a2l.Characteristic;
import a2l.Characteristic.CharacteristicType;
import a2l.CompuMethod;
import a2l.DataValue;
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
import a2l.SingleValue;
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

        if (a2l.getModCommon() == null) { // Cas d'un A2l où le MOD_COMMUN n'est pas défini
            this.modCommon = new ModCommon(hex);
        } else {
            this.modCommon = a2l.getModCommon();
        }
    }

    public final boolean checkEPK() {
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

        final ByteOrder byteOrder = modCommon.getByteOrder();

        for (AdjustableObject adjustableObject : a2l.getAdjustableObjects().values()) {
            if (adjustableObject instanceof AxisPts && adjustableObject.isValid()) {
                readAxisPts(byteOrder, (AxisPts) adjustableObject);
            }
        }

        Characteristic characteristic;
        FncValues fncValues;
        CompuMethod compuMethod;

        for (AdjustableObject adjustableObject : a2l.getAdjustableObjects().values()) {

            if (adjustableObject instanceof Characteristic && adjustableObject.isValid()) {

                characteristic = (Characteristic) adjustableObject;

                fncValues = characteristic.getRecordLayout().getFncValues();
                compuMethod = characteristic.getCompuMethod();

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
                    readMap(byteOrder, characteristic, adress, compuMethod, characteristic.getRecordLayout());
                    break;
                case CUBOID:
                    characteristic.setValues(new SingleValue("Not yet implemented"));
                    break;
                case CUBE_4:
                    characteristic.setValues(new SingleValue("Not yet implemented"));
                    break;
                case CUBE_5:
                    characteristic.setValues(new SingleValue("Not yet implemented"));
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
        final List<Reserved> listReserved = recordLayout.getReserved();
        final AxisRescaleX axisRescaleX = recordLayout.getAxisRescaleX();

        IndexOrder indexOrder;
        short nbValue = axisPts.getMaxAxisPoints();

        double physValue = 0;
        DataValue values = null;
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

        } else {// Axis rescale

            indexOrder = axisRescaleX.getIndexOrder();

            adress = setAlignment(adress, noRescaleX.getDataType());
            nbValue = (short) Converter.readHexValue(dataFile, adress, noRescaleX.getDataType(), byteOrder);
            adress += noRescaleX.getDataType().getNbByte();

            // Skip reserved
            for (Reserved reserved : listReserved) {
                adress += (reserved.getDataSize().getNbByte());
            }

            adress = setAlignment(adress, axisRescaleX.getDataType());
            hexValuesAxisPts = Converter.readHexValuesPairs(dataFile, adress, axisRescaleX.getDataType(), byteOrder, nbValue);

        }

        values = new ArrayValue(nbValue, (short) 1);

        if (!compuMethod.isVerbal()) {

            for (short n = 0; n < nbValue; n++) {
                physValue = compuMethod.compute(hexValuesAxisPts[n]);
                if (indexOrder.equals(IndexOrder.INDEX_INCR)) {
                    values.setValue(physValue, 0, n);
                } else {
                    values.setValue(physValue, 0, (nbValue - 1) - n);
                }
            }
        } else {
            for (short n = 0; n < nbValue; n++) {
                if (indexOrder.equals(IndexOrder.INDEX_INCR)) {
                    values.setValue(compuMethod.computeString(hexValuesAxisPts[n]), 0, n);
                } else {
                    values.setValue(compuMethod.computeString(hexValuesAxisPts[n]), 0, (nbValue - 1) - n);
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

        final DataValue value;

        if (!compuMethod.isVerbal()) {

            if (characteristic.hasBitMask()) {
                hexValue = characteristic.applyBitMask((long) hexValue);
            }

            physValue = compuMethod.compute(hexValue);
            value = new SingleValue(physValue);

        } else {
            if (characteristic.hasBitMask()) {
                hexValue = characteristic.applyBitMask((long) hexValue);
            }

            value = new SingleValue(compuMethod.computeString(hexValue));
        }

        characteristic.setValues(value);
    }

    private final void readAscii(Characteristic characteristic, long adress) {

        final short nByte = characteristic.getDim();

        final DataValue value;

        final String ascii = dataFile.readString(adress, nByte);
        if (ascii != null) {
            value = new SingleValue(ascii);
            characteristic.setValues(value);
        }
    }

    private final Object[] readFixAxis(AxisDescr axisDescr) {

        final Set<Entry<SecondaryKeywords, Object>> entrySet = axisDescr.getOptionalsParameters().entrySet();
        final Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

        Object[] values = null;

        while (it.hasNext()) {
            Map.Entry<SecondaryKeywords, Object> entry = it.next();
            if (entry.getValue() instanceof FixAxisParDist) {
                FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                values = new Object[axisDist.getNumberapo()];
                for (short n = 0; n < axisDist.getNumberapo(); n++) {
                    values[n] = axisDist.compute(n);
                }
                return values;
            } else if (entry.getValue() instanceof FixAxisPar) {
                FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                values = new Object[axisDist.getNumberapo()];
                for (short n = 0; n < axisDist.getNumberapo(); n++) {
                    values[n] = axisDist.compute(n);
                }
                return values;
            } else if (entry.getValue() instanceof FixAxisParList) {
                FixAxisParList axisDist = (FixAxisParList) entry.getValue();
                values = new Object[axisDist.getNbValue()];
                for (short n = 0; n < axisDist.getNbValue(); n++) {
                    values[n] = axisDist.compute(n);
                }
                return values;
            }
        }

        return new Object[] { Double.NaN };
    }

    private final Object[] readStdAxis(Characteristic characteristic, long adress, byte idxAxis, ByteOrder commonByteOrder) {

        Object[] values = null;

        final AxisDescr axisDescrStdAxis = characteristic.getAxisDescrs()[idxAxis];
        final ByteOrder byteOrder = axisDescrStdAxis.getByteOrder() != null ? axisDescrStdAxis.getByteOrder() : commonByteOrder;
        final CompuMethod compuMethod = axisDescrStdAxis.getCompuMethod();
        final String depositMode = axisDescrStdAxis.getDepositMode();
        final IndexMode indexMode = characteristic.getRecordLayout().getFncValues().getIndexMode();

        short nbValue = 0;
        DataType axisDataType;
        IndexOrder indexOrder;

        if ("A580_SHIFT_SCHED_TBL".equals(characteristic.toString())) {
            int stop = 0;
        }

        if (characteristic.getType().equals(CharacteristicType.MAP)) {
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

            for (Reserved reserved : characteristic.getRecordLayout().getReserved()) {
                adress += reserved.getDataSize().getNbByte();
            }
        }

        values = new Object[nbValue];

        adress = setAlignment(adress, axisDataType);
        double[] hexValues;

        if (indexMode == IndexMode.ALTERNATE_WITH_X) {
            hexValues = Converter.readHexValuesPairs(dataFile, adress, axisDataType, byteOrder, nbValue);
        } else {
            hexValues = Converter.readHexValues(dataFile, adress, axisDataType, byteOrder, nbValue);
        }

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
                if (indexOrder.equals(IndexOrder.INDEX_INCR)) {
                    values[n] = physValue;
                } else {
                    values[(nbValue - 1) - n] = physValue;
                }
            }
        } else {
            for (short n = 0; n < nbValue; n++) {
                if (indexOrder.equals(IndexOrder.INDEX_INCR)) {
                    values[n] = compuMethod.computeString(hexValues[n]);
                } else {
                    values[(nbValue - 1) - n] = compuMethod.computeString(hexValues[n]);
                }
            }
        }
        return values;
    }

    private final Object[] readCurveAxis(Characteristic characteristic) {

        Object[] values = null;

        DataValue curveValues = characteristic.getValues();
        values = new Object[curveValues.getDimX()];

        for (short n = 0; n < values.length; n++) {
            values[n] = curveValues.getValue(0, n);
        }

        return values;
    }

    private final void readCurve(ByteOrder commonByteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod,
            FncValues fncValues) {

        final AxisDescr axisDescr = characteristic.getAxisDescrs()[0];
        final ByteOrder byteOrder = axisDescr.getByteOrder() != null ? axisDescr.getByteOrder() : commonByteOrder;

        short nbValue = axisDescr.getMaxAxisPoints();

        DataValue values = null;

        switch (axisDescr.getAttribute()) {
        case FIX_AXIS:
            Object[] fixAxisValues = readFixAxis(axisDescr);
            nbValue = (short) fixAxisValues.length;
            values = new ArrayValue(nbValue, (short) 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(fixAxisValues[i], 0, i);
            }
            break;

        case STD_AXIS:
            Object[] stdAxisValues = readStdAxis(characteristic, adress, (byte) 0, byteOrder);
            nbValue = (short) stdAxisValues.length;
            values = new ArrayValue(nbValue, (short) 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(stdAxisValues[i], 0, i);
            }

            adress = tmpAdress;
            break;

        case COM_AXIS:
            Object[] comAxisValues = ((AxisPts) axisDescr.getAxisPts()).getZValues();
            nbValue = (short) comAxisValues.length;

            values = new ArrayValue(nbValue, (short) 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(comAxisValues[i], 0, i);
            }
            break;
        case RES_AXIS:
            Object[] resAxisValues = ((AxisPts) axisDescr.getAxisPts()).getZValues();
            nbValue = (short) resAxisValues.length;

            values = new ArrayValue(nbValue, (short) 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(resAxisValues[i], 0, i);
            }
            break;
        case CURVE_AXIS:

            Characteristic curve = (Characteristic) characteristic.getAxisDescrs()[0].getCurveAxis();

            if (curve.getValues() == null) {
                readCurve(byteOrder, curve, curve.getAdress(), curve.getCompuMethod(), curve.getRecordLayout().getFncValues());
            }

            Object[] curveAxisValue = readCurveAxis(curve);
            nbValue = (short) curveAxisValue.length;

            values = new ArrayValue(nbValue, (short) 2);
            for (short i = 0; i < nbValue; i++) {
                values.setValue(curveAxisValue[i], 0, i);
            }
            break;

        default:
            return;
        }

        adress = setAlignment(adress, fncValues.getDataType());

        double[] hexValues;

        if (fncValues.getIndexMode() == IndexMode.ALTERNATE_WITH_X) {
            adress -= characteristic.getRecordLayout().getAxisPtsX().getDataType().getNbByte() * nbValue;
            adress += characteristic.getRecordLayout().getAxisPtsX().getDataType().getNbByte();
            hexValues = Converter.readHexValuesPairs(dataFile, adress, fncValues.getDataType(), byteOrder, nbValue);
        } else {
            hexValues = Converter.readHexValues(dataFile, adress, fncValues.getDataType(), byteOrder, nbValue);
        }

        double physValue = 0;

        if (!compuMethod.isVerbal()) {

            for (short n = 0; n < nbValue; n++) {
                physValue = compuMethod.compute(hexValues[n]);
                values.setValue(physValue, 1, n);
            }

        } else {

            if (characteristic.hasBitMask()) {
                for (short i = 0; i < nbValue; i++) {
                    hexValues[i] = characteristic.applyBitMask((long) hexValues[i]);
                }
            }
            for (short n = 0; n < nbValue; n++) {
                values.setValue(compuMethod.computeString(hexValues[n]), 1, n);
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

        DataValue values;

        if (dim.length < 2 || dim[1] == 1) {
            values = new ArrayValue((short) (dim[0] + 1), (short) 2);
            values.setValue("X", 0, 0);
        } else {
            values = new ArrayValue((short) (dim[0] + 1), (short) (dim[1] + 1));
            values.setValue("Y\\X", 0, 0);
            for (short y = 0; y < dim[1]; y++) { // Patch pour les VAL_BLK qui n'ont qu'une colonne
                values.setValue(y + 1, 0, y);
            }
        }

        for (short x = 0; x < dim[0]; x++) {
            values.setValue(x, 0, x + 1);
        }

        double[] hexValuesValBlk;

        if (dim.length < 2 || dim[1] == 1) {

            adress = setAlignment(adress, fncValues.getDataType());
            hexValuesValBlk = Converter.readHexValues(dataFile, adress, fncValues.getDataType(), byteOrder, dim[0]);

            values.setValue("Z", 1, 0);

            if (!compuMethod.isVerbal()) {

                for (short n = 0; n < dim[0]; n++) {
                    physValue = compuMethod.compute(hexValuesValBlk[n]);
                    values.setValue(physValue, 1, n + 1);
                }
            } else {
                if (characteristic.hasBitMask()) {
                    for (short i = 0; i < dim[0]; i++) {
                        hexValuesValBlk[i] = characteristic.applyBitMask((long) hexValuesValBlk[i]);
                    }
                }
                for (short n = 0; n < dim[0]; n++) {
                    values.setValue(compuMethod.computeString(hexValuesValBlk[n]), 1, n + 1);
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
                    if (indexModeValBlk.equals(IndexMode.COLUMN_DIR)) {
                        if (n % dim[1] == 0) {
                            row += 1;
                        }
                        col = n % dim[1];
                        physValue = compuMethod.compute(hexValuesValBlk[n]);
                        values.setValue(physValue, col + 1, row);
                    } else {
                        if (n % dim[0] == 0) { // => OK pour ROW_DIR
                            values.setValue(row + 1, 0, row);
                            row += 1;
                        }
                        col = n % dim[0];
                        physValue = compuMethod.compute(hexValuesValBlk[n]);
                        values.setValue(physValue, row, col + 1);
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
                    if (indexModeValBlk.equals(IndexMode.COLUMN_DIR)) {
                        if (n % dim[1] == 0) {
                            row += 1;
                        }
                        col = n % dim[1];
                        values.setValue(compuMethod.computeString(hexValuesValBlk[n]), col + 1, row);
                    } else {
                        if (n % dim[0] == 0) { // => OK pour ROW_DIR
                            values.setValue(row + 1, 0, row);
                            row += 1;
                        }
                        col = n % dim[0];
                        values.setValue(compuMethod.computeString(hexValuesValBlk[n]), row, col + 1);
                    }
                }
            }
        }

        characteristic.setValues(values);
    }

    private void readMap(ByteOrder commonByteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, RecordLayout recordLayout) {

        final ByteOrder byteOrder = characteristic.getByteOrder() != null ? characteristic.getByteOrder() : commonByteOrder;
        double physValue;

        FncValues fncValues = recordLayout.getFncValues();

        int nbValueMap = 0;
        int dimX = 0;
        int dimY = 0;

        final List<Object[]> listAxisValues = new ArrayList<Object[]>(2);

        boolean stdAxis = false;

        byte idxAxis = 0;

        for (AxisDescr axisDescr : characteristic.getAxisDescrs()) {
            switch (axisDescr.getAttribute()) {
            case FIX_AXIS:

                listAxisValues.add(readFixAxis(axisDescr));

                break;
            case STD_AXIS:

                stdAxis = true;

                listAxisValues.add(readStdAxis(characteristic, adress, idxAxis, byteOrder));
                if (idxAxis > 0) {
                    adress = tmpAdress;
                }
                idxAxis++;

                break;
            case COM_AXIS:

                AxisPts axisPts = (AxisPts) axisDescr.getAxisPts();
                listAxisValues.add(axisPts.getZValues());

                break;

            case RES_AXIS:

                AxisPts resAxis = (AxisPts) axisDescr.getAxisPts();
                listAxisValues.add(resAxis.getZValues());

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

        DataValue values = new ArrayValue((short) (dimX + 1), (short) (dimY + 1));
        values.setValue("Y\\X", 0, 0);

        for (byte i = 0; i < 2; i++) {
            Object[] axisValues = listAxisValues.get(i);
            for (short n = 0; n < axisValues.length; n++) {
                if (i == 0) {
                    values.setValue(axisValues[n], 0, n + 1);
                } else {
                    values.setValue(axisValues[n], n + 1, 0);
                }
            }
        }

        listAxisValues.clear();

        if (nbValueMap > 0) {

            final IndexMode indexModeMap = fncValues.getIndexMode();
            final NoAxisPtsY noAxisPtsY = recordLayout.getNoAxisPtsY();
            final NoAxisPtsX noAxisPtsX = recordLayout.getNoAxisPtsX();
            final List<Reserved> listReserved = recordLayout.getReserved();

            if (!stdAxis) {
                if (noAxisPtsY != null) {
                    adress += noAxisPtsY.getDataType().getNbByte();
                }

                if (noAxisPtsX != null) {
                    adress += noAxisPtsX.getDataType().getNbByte();
                }

                // Skip reserved
                for (Reserved reserved : listReserved) {
                    adress += (reserved.getDataSize().getNbByte());
                }
            }

            adress = setAlignment(adress, fncValues.getDataType());
            double[] hexValues = Converter.readHexValues(dataFile, adress, fncValues.getDataType(), byteOrder, nbValueMap);

            if (!compuMethod.isVerbal()) {

                int row = 0;
                int col = 0;
                for (short n = 0; n < nbValueMap; n++) {
                    if (indexModeMap.equals(IndexMode.COLUMN_DIR)) {
                        if (n % dimY == 0) {
                            row += 1;
                        }
                        col = n % dimY;
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(physValue, col + 1, row);
                    } else {
                        if (n % dimX == 0) {
                            row += 1;
                        }
                        col = n % dimX;
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(physValue, row, col + 1);
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
                    if (indexModeMap.equals(IndexMode.COLUMN_DIR)) {
                        if (n % dimY == 0) {
                            row += 1;
                        }
                        col = n % dimY;
                        values.setValue(compuMethod.computeString(hexValues[n]), col + 1, row);
                    } else {
                        if (n % dimX == 0) {
                            row += 1;
                        }
                        col = n % dimX;
                        values.setValue(compuMethod.computeString(hexValues[n]), row, col + 1);
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
