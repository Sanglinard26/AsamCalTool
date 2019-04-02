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
import a2lobject.AdjustableObject;
import a2lobject.AxisDescr;
import a2lobject.AxisPts;
import a2lobject.Characteristic;
import a2lobject.CompuMethod;
import a2lobject.FixAxisPar;
import a2lobject.FixAxisParDist;
import a2lobject.FixAxisParList;
import a2lobject.ModCommon;
import a2lobject.ModPar;
import a2lobject.RecordLayout;
import a2lobject.RecordLayout.AxisPtsX;
import a2lobject.RecordLayout.AxisPtsY;
import a2lobject.RecordLayout.FncValues;
import a2lobject.RecordLayout.NoAxisPtsX;
import a2lobject.RecordLayout.NoAxisPtsY;
import a2lobject.Values;
import constante.ConversionType;
import constante.DataType;
import constante.DepositMode;
import constante.IndexMode;
import constante.IndexOrder;
import constante.SecondaryKeywords;
import utils.Converter;

public final class HexDecoder {

    private final A2l a2l;
    private final IntelHex hex;
    private final ModCommon modCommon;

    public HexDecoder(A2l a2l, IntelHex hex) {
        this.a2l = a2l;
        this.hex = hex;

        this.modCommon = a2l.getModCommon();
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

        ByteOrder byteOrder = modCommon.getByteOrder();

        for (Entry<String, AdjustableObject> entries : a2l.getAdjustableObjects().entrySet()) {
            if (entries.getValue() instanceof AxisPts) {
                readAxisPts(byteOrder, (AxisPts) entries.getValue());
            }
        }

        for (Entry<String, AdjustableObject> entries : a2l.getAdjustableObjects().entrySet()) {

            if (entries.getValue() instanceof Characteristic) {

                Characteristic characteristic = (Characteristic) entries.getValue();

                final FncValues fncValues = characteristic.getRecordLayout().getFncValues();
                final CompuMethod compuMethod = characteristic.getCompuMethod();

                long adress = characteristic.getAdress();

                if (characteristic.toString().equals("ASAM.C.SCALAR.SWORD.VTAB_RANGE_DEFAULT_VALUE"))
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
                    // Nothing
                    break;
                }

            }

        }

        return true;

    }

    private final void readAxisPts(ByteOrder byteOrder, AxisPts axisPts) {

        long adress = axisPts.getAdress();
        RecordLayout recordLayout = axisPts.getRecordLayout();
        CompuMethod compuMethod = axisPts.getCompuMethod();
        AxisPtsX axisPtsX = recordLayout.getAxisPtsX();
        IndexOrder indexOrder = axisPtsX.getIndexOrder();
        NoAxisPtsX noAxisPtsX = recordLayout.getNoAxisPtsX();
        String axisDisplayFormat = axisPts.getFormat();

        double physValue = 0;
        Values values = null;

        int nbValue = axisPts.getMaxAxisPoints();

        if (noAxisPtsX != null) {
            adress = setAlignment(adress, noAxisPtsX.getDataType());
            nbValue = (int) Converter.readHexValue(hex, adress, noAxisPtsX.getDataType(), byteOrder);
            adress += noAxisPtsX.getDataType().getNbByte();
        }

        values = new Values(nbValue, 1);

        adress = setAlignment(adress, axisPtsX.getDataType());
        double[] hexValuesComAxis = Converter.readHexValues(hex, adress, axisPtsX.getDataType(), byteOrder, nbValue);

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

            for (int n = 0; n < nbValue; n++) {
                physValue = compuMethod.compute(hexValuesComAxis[n]);
                if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                    values.setValue(0, n, String.format(axisDisplayFormat, physValue).trim());
                } else {
                    values.setValue(0, (nbValue - 1) - n, String.format(axisDisplayFormat, physValue).trim());
                }

            }

        } else {
            for (int n = 0; n < nbValue; n++) {
                if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                    values.setValue(0, n, compuMethod.computeString(hexValuesComAxis[n]));
                } else {
                    values.setValue(0, (nbValue - 1) - n, compuMethod.computeString(hexValuesComAxis[n]));
                }

            }
        }

        axisPts.setValues(values);

    }

    private final void readValue(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {

        adress = setAlignment(adress, fncValues.getDataType());
        double hexValue = Converter.readHexValue(hex, adress, fncValues.getDataType(), byteOrder);
        double physValue;

        String displayFormat = characteristic.getFormat();

        Values values = new Values(1, 1);

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

            if (characteristic.hasBitMask()) {
                hexValue = characteristic.applyBitMask((long) hexValue);
            }

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

    @SuppressWarnings("unused")
    private final String[] readFixAxis(Characteristic characteristic, int numAxe) {

        AxisDescr axisDescr = characteristic.getAxisDescrs().get(numAxe);

        Set<Entry<SecondaryKeywords, Object>> entrySet = axisDescr.getOptionalsParameters().entrySet();
        Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

        String[] strValues = null;

        while (it.hasNext()) {
            Map.Entry<SecondaryKeywords, Object> entry = it.next();
            if (entry.getValue() instanceof FixAxisParDist) {
                FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                strValues = new String[axisDist.getNumberapo()];
                for (int n = 0; n < axisDist.getNumberapo(); n++) {
                    strValues[n] = axisDist.compute(n) + "";
                }
                break;
            } else if (entry.getValue() instanceof FixAxisPar) {
                FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                strValues = new String[axisDist.getNumberapo()];
                for (int n = 0; n < axisDist.getNumberapo(); n++) {
                    strValues[n] = axisDist.compute(n) + "";
                }
            } else if (entry.getValue() instanceof FixAxisParList) {
                FixAxisParList axisDist = (FixAxisParList) entry.getValue();
                strValues = new String[axisDist.getNbValue()];
                for (int n = 0; n < axisDist.getNbValue(); n++) {
                    strValues[n] = axisDist.compute(n) + "";
                }
            }
        }

        return strValues;

    }

    @SuppressWarnings("unused")
    private final String[] readStdAxis() {

        String[] strValues = null;

        return strValues;

    }

    private final void readCurve(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {
        AxisDescr axisDescr = characteristic.getAxisDescrs().get(0);
        int nbValue = axisDescr.getMaxAxisPoints();

        String displayFormat = characteristic.getFormat();

        double physValue = 0;
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
                } else if (entry.getValue() instanceof FixAxisParList) {
                    FixAxisParList axisDist = (FixAxisParList) entry.getValue();
                    values = new Values(axisDist.getNbValue(), 2);
                    for (int n = 0; n < axisDist.getNbValue(); n++) {
                        values.setValue(0, n, axisDist.compute(n) + "");
                    }
                }
            }
            break;

        case STD_AXIS:
            AxisPtsX axisPtsXStdAxis = characteristic.getRecordLayout().getAxisPtsX();
            IndexOrder indexOrder = axisPtsXStdAxis.getIndexOrder();
            NoAxisPtsX noAxisPtsX = characteristic.getRecordLayout().getNoAxisPtsX();

            if (characteristic.getRecordLayout().getSrcAddrX() != null) {
                adress += characteristic.getRecordLayout().getSrcAddrX().getDataType().getNbByte();
            }

            adress = setAlignment(adress, noAxisPtsX.getDataType());
            nbValue = (int) Converter.readHexValue(hex, adress, noAxisPtsX.getDataType(), byteOrder);
            adress += noAxisPtsX.getDataType().getNbByte();

            values = new Values(nbValue, 2);

            AxisDescr axisDescrStdAxis = characteristic.getAxisDescrs().get(0);
            CompuMethod compuMethodStdAxis = axisDescrStdAxis.getCompuMethod();
            String depositMode = axisDescrStdAxis.getDepositMode();

            adress = setAlignment(adress, axisPtsXStdAxis.getDataType());
            double[] hexValues = Converter.readHexValues(hex, adress, axisPtsXStdAxis.getDataType(), byteOrder, nbValue);
            adress += axisPtsXStdAxis.getDataType().getNbByte() * nbValue;

            double predValue = 0;

            if (compuMethodStdAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethodStdAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethodStdAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                for (int n = 0; n < nbValue; n++) {
                    if (!depositMode.equals(DepositMode.DIFFERENCE.name())) {
                        physValue = compuMethodStdAxis.compute(hexValues[n]);
                    } else {
                        if (n == 0) {
                            physValue = compuMethodStdAxis.compute(hexValues[n]);
                            predValue = physValue;
                        }
                        if (n > 0 && n < nbValue - 1) {
                            physValue = compuMethodStdAxis.compute(hexValues[n]) + predValue;
                            predValue = physValue;
                        }
                        if (n == nbValue - 1) {
                            physValue = compuMethodStdAxis.compute(hexValues[n - 1]) + predValue;
                            predValue = physValue;
                        }
                    }
                    if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                        values.setValue(0, n, String.format(displayFormat, physValue).trim());
                    } else {
                        values.setValue(0, (nbValue - 1) - n, String.format(displayFormat, physValue).trim());
                    }

                }

            } else {
                for (int n = 0; n < nbValue; n++) {
                    if (indexOrder.compareTo(IndexOrder.INDEX_INCR) == 0) {
                        values.setValue(0, n, compuMethodStdAxis.computeString(hexValues[n]) + "");
                    } else {
                        values.setValue(0, (nbValue - 1) - n, compuMethodStdAxis.computeString(hexValues[n]) + "");
                    }

                }

            }

            break;

        case COM_AXIS:

            AxisPts axisPts = axisDescr.getAxisPts();
            AxisPtsX axisPtsX = axisPts.getRecordLayout().getAxisPtsX();
            IndexOrder indexOrder_ComAxis = axisPtsX.getIndexOrder();
            NoAxisPtsX noAxisPtsX_ComAxis = axisPts.getRecordLayout().getNoAxisPtsX();

            CompuMethod compuMethodAxis = axisPts.getCompuMethod();
            long adressAxis = axisPts.getAdress();

            if (noAxisPtsX_ComAxis != null) {
                adressAxis = setAlignment(adressAxis, noAxisPtsX_ComAxis.getDataType());
                nbValue = (int) Converter.readHexValue(hex, adressAxis, noAxisPtsX_ComAxis.getDataType(), byteOrder);
                adressAxis += noAxisPtsX_ComAxis.getDataType().getNbByte();
            }

            String axisDisplayFormat = axisPts.getFormat();

            values = new Values(nbValue, 2);

            adressAxis = setAlignment(adressAxis, axisPtsX.getDataType());
            double[] hexValuesComAxis = Converter.readHexValues(hex, adressAxis, axisPtsX.getDataType(), byteOrder, nbValue);

            if (compuMethodAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethodAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethodAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                for (int n = 0; n < nbValue; n++) {
                    physValue = compuMethodAxis.compute(hexValuesComAxis[n]);
                    if (indexOrder_ComAxis.compareTo(IndexOrder.INDEX_INCR) == 0) {
                        values.setValue(0, n, String.format(axisDisplayFormat, physValue).trim());
                    } else {
                        values.setValue(0, (nbValue - 1) - n, String.format(axisDisplayFormat, physValue).trim());
                    }

                }

            } else {
                for (int n = 0; n < nbValue; n++) {
                    if (indexOrder_ComAxis.compareTo(IndexOrder.INDEX_INCR) == 0) {
                        values.setValue(0, n, compuMethodAxis.computeString(hexValuesComAxis[n]));
                    } else {
                        values.setValue(0, (nbValue - 1) - n, compuMethodAxis.computeString(hexValuesComAxis[n]));
                    }

                }
            }

            break;

        default:
            return;
        }

        adress = setAlignment(adress, fncValues.getDataType());
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

        @SuppressWarnings("unused")
        IndexMode indexModeValBlk = fncValues.getIndexMode();

        double physValue;
        String displayFormat = characteristic.getFormat();

        int[] dim = characteristic.getDimArray();

        Values values;

        if (dim.length < 2 || dim[1] == 1) {
            values = new Values(dim[0] + 1, 2);
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

            adress = setAlignment(adress, fncValues.getDataType());
            hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, dim[0]);

            values.setValue(1, 0, "Z");

            if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                for (int n = 0; n < dim[0]; n++) {
                    physValue = compuMethod.compute(hexValuesValBlk[n]);
                    values.setValue(1, n + 1, String.format(displayFormat, physValue).trim());
                }
            } else {
                if (characteristic.hasBitMask()) {
                    for (int i = 0; i < dim[0]; i++) {
                        hexValuesValBlk[i] = characteristic.applyBitMask((long) hexValuesValBlk[i]);
                    }
                }
                for (int n = 0; n < dim[0]; n++) {
                    values.setValue(1, n + 1, compuMethod.computeString(hexValuesValBlk[n]) + "");
                }
            }

        } else {

            int nbValue = dim[0] * dim[1];

            adress = setAlignment(adress, fncValues.getDataType());
            hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValue);

            if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                int row = 0;
                int col = 0;
                for (int n = 0; n < hexValuesValBlk.length; n++) {
                    if (n % (values.getDimX() - 1) == 0) { // => OK pour ROW_DIR
                        values.setValue(row + 1, 0, row + "");
                        row += 1;

                    }
                    col = n % (values.getDimX() - 1);
                    physValue = compuMethod.compute(hexValuesValBlk[n]);
                    values.setValue(row, col + 1, String.format(displayFormat, physValue).trim());
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
                    if (n % (values.getDimX() - 1) == 0) { // => OK pour ROW_DIR
                        values.setValue(row + 1, 0, row + "");
                        row += 1;
                    }
                    col = n % (values.getDimX() - 1);
                    values.setValue(row, col + 1, compuMethod.computeString(hexValuesValBlk[n]));
                }

            }

        }

        characteristic.setValues(values);
    }

    private void readMap(ByteOrder byteOrder, Characteristic characteristic, long adress, CompuMethod compuMethod, FncValues fncValues) {

        String displayFormat = characteristic.getFormat();
        double physValue;

        int nbValueMap = 0;
        int[] dimMap = new int[2];

        int cnt = 0;

        long adressTmp = adress;

        for (AxisDescr axis : characteristic.getAxisDescrs()) {
            switch (axis.getAttribute()) {
            case FIX_AXIS:

                Set<Entry<SecondaryKeywords, Object>> entrySet = axis.getOptionalsParameters().entrySet();
                Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

                while (it.hasNext()) {
                    Map.Entry<SecondaryKeywords, Object> entry = it.next();
                    if (entry.getValue() instanceof FixAxisParDist) {
                        FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                        dimMap[cnt] = axisDist.getNumberapo();
                        break;
                    } else if (entry.getValue() instanceof FixAxisPar) {
                        FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                        dimMap[cnt] = axisDist.getNumberapo();
                    }
                }

                break;
            case STD_AXIS:
                if (cnt == 0) {

                    NoAxisPtsX noAxisPtsX = characteristic.getRecordLayout().getNoAxisPtsX();

                    if (characteristic.getRecordLayout().getSrcAddrX() != null) {
                        adressTmp += characteristic.getRecordLayout().getSrcAddrX().getDataType().getNbByte();
                    }

                    adressTmp = setAlignment(adressTmp, noAxisPtsX.getDataType());
                    int nbValueX = (int) Converter.readHexValue(hex, adressTmp, noAxisPtsX.getDataType(), byteOrder);
                    adressTmp += noAxisPtsX.getDataType().getNbByte();
                    dimMap[cnt] = nbValueX;

                    NoAxisPtsY noAxisPtsY = characteristic.getRecordLayout().getNoAxisPtsY();

                    if (characteristic.getRecordLayout().getSrcAddrY() != null) {
                        adressTmp += characteristic.getRecordLayout().getSrcAddrY().getDataType().getNbByte();
                    }

                    adressTmp = setAlignment(adressTmp, noAxisPtsY.getDataType());
                    int nbValueY = (int) Converter.readHexValue(hex, adressTmp, noAxisPtsY.getDataType(), byteOrder);
                    adressTmp += noAxisPtsY.getDataType().getNbByte();
                    dimMap[cnt + 1] = nbValueY;

                }
                break;
            case COM_AXIS:

                AxisPts axisPts = axis.getAxisPts();
                NoAxisPtsX noAxisPtsX_ComAxis = axisPts.getRecordLayout().getNoAxisPtsX();
                long adressAxis = axisPts.getAdress();

                int nbValueAxis = axisPts.getMaxAxisPoints();

                if (noAxisPtsX_ComAxis != null) {
                    adressAxis = setAlignment(adressAxis, noAxisPtsX_ComAxis.getDataType());
                    nbValueAxis = (int) Converter.readHexValue(hex, adressAxis, noAxisPtsX_ComAxis.getDataType(), byteOrder);
                    adressAxis += noAxisPtsX_ComAxis.getDataType().getNbByte();
                }

                dimMap[cnt] = nbValueAxis;

                break;
            default:
                return;
            }
            cnt++;
        }

        nbValueMap = dimMap[0] * dimMap[1];

        Values values = new Values(dimMap[0] + 1, dimMap[1] + 1);
        values.setValue(0, 0, "Y\\X");

        cnt = 0;

        for (AxisDescr axis : characteristic.getAxisDescrs()) {
            switch (axis.getAttribute()) {
            case FIX_AXIS:

                Set<Entry<SecondaryKeywords, Object>> entrySet = axis.getOptionalsParameters().entrySet();
                Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

                while (it.hasNext()) {
                    Map.Entry<SecondaryKeywords, Object> entry = it.next();
                    if (entry.getValue() instanceof FixAxisParDist) {
                        FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                        for (int n = 0; n < axisDist.getNumberapo(); n++) {
                            if (cnt == 0) {
                                values.setValue(0, n + 1, n + "");
                            } else {
                                values.setValue(n + 1, 0, n + "");
                            }
                        }
                        break;
                    } else if (entry.getValue() instanceof FixAxisPar) {
                        FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                        for (int n = 0; n < axisDist.getNumberapo(); n++) {
                            if (cnt == 0) {
                                values.setValue(0, n + 1, n + "");
                            } else {
                                values.setValue(n + 1, 0, n + "");
                            }
                        }
                    }
                }

                break;
            case STD_AXIS:

                CompuMethod compuMethodStdAxis = axis.getCompuMethod();
                double[] hexValues;

                if (cnt == 0) {

                    adress = adressTmp;

                    AxisPtsX axisPtsXStdAxis = characteristic.getRecordLayout().getAxisPtsX();
                    IndexOrder indexOrder_StdAxisX = axisPtsXStdAxis.getIndexOrder();
                    adress = setAlignment(adress, axisPtsXStdAxis.getDataType());
                    hexValues = Converter.readHexValues(hex, adress, axisPtsXStdAxis.getDataType(), byteOrder, dimMap[cnt]);
                    adress += axisPtsXStdAxis.getDataType().getNbByte() * dimMap[cnt];

                    if (compuMethodStdAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                        for (int n = 0; n < hexValues.length; n++) {
                            if (indexOrder_StdAxisX.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(0, n + 1, compuMethodStdAxis.compute(hexValues[n]) + "");
                            } else {
                                values.setValue(0, (hexValues.length - 1) - n + 1, compuMethodStdAxis.compute(hexValues[n]) + "");
                            }
                        }
                    } else {
                        for (int n = 0; n < hexValues.length; n++) {
                            if (indexOrder_StdAxisX.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(0, n + 1, compuMethodStdAxis.computeString(hexValues[n]));
                            } else {
                                values.setValue(0, (hexValues.length - 1) - n + 1, compuMethodStdAxis.computeString(hexValues[n]));
                            }
                        }
                    }

                } else {

                    AxisPtsY axisPtsYStdAxis = characteristic.getRecordLayout().getAxisPtsY();
                    IndexOrder indexOrder_StdAxisY = axisPtsYStdAxis.getIndexOrder();
                    adress = setAlignment(adress, axisPtsYStdAxis.getDataType());
                    hexValues = Converter.readHexValues(hex, adress, axisPtsYStdAxis.getDataType(), byteOrder, dimMap[cnt]);
                    adress += axisPtsYStdAxis.getDataType().getNbByte() * dimMap[cnt];

                    if (compuMethodStdAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                            || compuMethodStdAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                        for (int n = 0; n < hexValues.length; n++) {
                            if (indexOrder_StdAxisY.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(n + 1, 0, compuMethodStdAxis.compute(hexValues[n]) + "");
                            } else {
                                values.setValue((hexValues.length - 1) - n + 1, 0, compuMethodStdAxis.compute(hexValues[n]) + "");
                            }
                        }
                    } else {
                        for (int n = 0; n < hexValues.length; n++) {
                            if (indexOrder_StdAxisY.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(n + 1, 0, compuMethodStdAxis.computeString(hexValues[n]));
                            } else {
                                values.setValue((hexValues.length - 1) - n + 1, 0, compuMethodStdAxis.computeString(hexValues[n]));
                            }
                        }
                    }

                }

                break;

            case COM_AXIS:

                AxisPts axisPts = axis.getAxisPts();
                AxisPtsX axisPtsX = axisPts.getRecordLayout().getAxisPtsX();
                IndexOrder indexOrder_ComAxis = axisPtsX.getIndexOrder();
                NoAxisPtsX noAxisPtsX_ComAxis = axisPts.getRecordLayout().getNoAxisPtsX();
                CompuMethod compuMethodAxis = axisPts.getCompuMethod();
                long adressAxis = axisPts.getAdress();

                int nbValueAxis = dimMap[cnt];

                String axisDisplayFormat = axisPts.getFormat();

                if (noAxisPtsX_ComAxis != null) {
                    adressAxis = setAlignment(adressAxis, noAxisPtsX_ComAxis.getDataType());
                    nbValueAxis = (int) Converter.readHexValue(hex, adressAxis, noAxisPtsX_ComAxis.getDataType(), byteOrder);
                    adressAxis += noAxisPtsX_ComAxis.getDataType().getNbByte();
                }

                double[] hexValuesComAxis = Converter.readHexValues(hex, adressAxis, axisPtsX.getDataType(), byteOrder, nbValueAxis);

                if (compuMethodAxis.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                        || compuMethodAxis.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                        || compuMethodAxis.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                    if (cnt == 0) {
                        for (int n = 0; n < hexValuesComAxis.length; n++) {
                            physValue = compuMethodAxis.compute(hexValuesComAxis[n]);
                            if (indexOrder_ComAxis.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(0, n + 1, String.format(axisDisplayFormat, physValue).trim());
                            } else {
                                values.setValue(0, (hexValuesComAxis.length - 1) - n + 1, String.format(axisDisplayFormat, physValue).trim());
                            }

                        }
                    } else {
                        for (int n = 0; n < hexValuesComAxis.length; n++) {
                            physValue = compuMethodAxis.compute(hexValuesComAxis[n]);
                            if (indexOrder_ComAxis.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(n + 1, 0, String.format(axisDisplayFormat, physValue).trim());
                            } else {
                                values.setValue((hexValuesComAxis.length - 1) - n + 1, 0, String.format(axisDisplayFormat, physValue).trim());
                            }

                        }
                    }

                } else {

                    if (cnt == 0) {
                        for (int n = 0; n < hexValuesComAxis.length; n++) {
                            if (indexOrder_ComAxis.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(0, n + 1, compuMethodAxis.computeString(hexValuesComAxis[n]) + "");
                            } else {
                                values.setValue(0, (hexValuesComAxis.length - 1) - n + 1, compuMethodAxis.computeString(hexValuesComAxis[n]) + "");
                            }

                        }
                    } else {
                        for (int n = 0; n < hexValuesComAxis.length; n++) {
                            if (indexOrder_ComAxis.compareTo(IndexOrder.INDEX_INCR) == 0) {
                                values.setValue(n + 1, 0, compuMethodAxis.computeString(hexValuesComAxis[n]) + "");
                            } else {
                                values.setValue((hexValuesComAxis.length - 1) - n + 1, 0, compuMethodAxis.computeString(hexValuesComAxis[n]) + "");
                            }
                        }
                    }
                }

                break;

            default:
                break;
            }

            cnt++;
        }

        if (nbValueMap > 0) {

            IndexMode indexModeMap = fncValues.getIndexMode();

            adress = setAlignment(adress, fncValues.getDataType());
            double[] hexValues = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValueMap);

            cnt = 0;

            if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                    || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {

                int row = 0;
                int col = 0;
                for (int n = 0; n < hexValues.length; n++) {
                    if (indexModeMap.compareTo(IndexMode.COLUMN_DIR) == 0) {
                        if (n % (values.getDimY() - 1) == 0) {
                            row += 1;
                        }
                        col = n % (values.getDimY() - 1);
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(col + 1, row, String.format(displayFormat, physValue));
                    } else {
                        if (n % (values.getDimX() - 1) == 0) {
                            row += 1;
                        }
                        col = n % (values.getDimX() - 1);
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(row, col + 1, String.format(displayFormat, physValue));
                    }

                }

            } else {

                if (characteristic.hasBitMask()) {
                    for (int i = 0; i < hexValues.length; i++) {
                        hexValues[i] = characteristic.applyBitMask((long) hexValues[i]);
                    }
                }
                int row = 0;
                int col = 0;
                for (int n = 0; n < hexValues.length; n++) {
                    if (indexModeMap.compareTo(IndexMode.COLUMN_DIR) == 0) {
                        if (n % (values.getDimY() - 1) == 0) {
                            row += 1;
                        }
                        col = n % (values.getDimY() - 1);
                        values.setValue(col + 1, row, compuMethod.computeString(hexValues[n]));
                    } else {
                        if (n % (values.getDimX() - 1) == 0) {
                            row += 1;
                        }
                        col = n % (values.getDimX() - 1);
                        physValue = compuMethod.compute(hexValues[n]);
                        values.setValue(row, col + 1, compuMethod.computeString(hexValues[n]));
                    }

                }
            }
        }
        characteristic.setValues(values);
    }

    private final long setAlignment(long adress, DataType dataType) {
        int alignment = this.modCommon.getAlignment(dataType);
        return alignment > 1 ? adress + (adress & (alignment - 1)) : adress;
    }
}
