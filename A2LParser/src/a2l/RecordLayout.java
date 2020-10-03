/*
 * Creation : 20 févr. 2019
 */
package a2l;

import static constante.SecondaryKeywords.AXIS_PTS_X;
import static constante.SecondaryKeywords.AXIS_PTS_Y;
import static constante.SecondaryKeywords.AXIS_RESCALE_X;
import static constante.SecondaryKeywords.FNC_VALUES;
import static constante.SecondaryKeywords.NO_AXIS_PTS_X;
import static constante.SecondaryKeywords.NO_AXIS_PTS_Y;
import static constante.SecondaryKeywords.NO_RESCALE_X;
import static constante.SecondaryKeywords.RESERVED;
import static constante.SecondaryKeywords.SRC_ADDR_X;
import static constante.SecondaryKeywords.SRC_ADDR_Y;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import constante.AdressType;
import constante.DataSize;
import constante.DataType;
import constante.IndexMode;
import constante.IndexOrder;
import constante.SecondaryKeywords;

public final class RecordLayout implements A2lObject, Comparable<RecordLayout> {

    private String name;

    private Map<SecondaryKeywords, OptionalParameterRL> optionalsParameters;

    public RecordLayout(List<String> parameters, int beginLine, int endLine) {

        optionalsParameters = new EnumMap<SecondaryKeywords, OptionalParameterRL>(SecondaryKeywords.class);

        build(parameters, beginLine, endLine);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final Map<Byte, OptionalParameterRL> getSortedObject() {
        TreeMap<Byte, OptionalParameterRL> treeMap = new TreeMap<Byte, OptionalParameterRL>();

        Iterator<Map.Entry<SecondaryKeywords, OptionalParameterRL>> iter = optionalsParameters.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<SecondaryKeywords, OptionalParameterRL> entry = iter.next();
            treeMap.put(entry.getValue().getPosition(), entry.getValue());
        }

        return treeMap;
    }

    public FncValues getFncValues() {
        return (FncValues) optionalsParameters.get(FNC_VALUES);
    }

    public NoAxisPtsX getNoAxisPtsX() {
        Object object = optionalsParameters.get(NO_AXIS_PTS_X);
        return object != null ? (NoAxisPtsX) object : null;
    }

    public AxisPtsX getAxisPtsX() {
        Object object = optionalsParameters.get(AXIS_PTS_X);
        return object != null ? (AxisPtsX) object : null;
    }

    public NoAxisPtsY getNoAxisPtsY() {
        Object object = optionalsParameters.get(NO_AXIS_PTS_Y);
        return object != null ? (NoAxisPtsY) object : null;
    }

    public AxisPtsY getAxisPtsY() {
        Object object = optionalsParameters.get(AXIS_PTS_Y);
        return object != null ? (AxisPtsY) object : null;
    }

    public SrcAddrX getSrcAddrX() {
        Object object = optionalsParameters.get(SRC_ADDR_X);
        return object != null ? (SrcAddrX) object : null;
    }

    public SrcAddrY getSrcAddrY() {
        Object object = optionalsParameters.get(SRC_ADDR_Y);
        return object != null ? (SrcAddrY) object : null;
    }

    public NoRescaleX getNoRescaleX() {
        Object object = optionalsParameters.get(NO_RESCALE_X);
        return object != null ? (NoRescaleX) object : null;
    }

    public Reserved getReserved() {
        Object object = optionalsParameters.get(RESERVED);
        return object != null ? (Reserved) object : null;
    }

    public AxisRescaleX getAxisRescaleX() {
        Object object = optionalsParameters.get(AXIS_RESCALE_X);
        return object != null ? (AxisRescaleX) object : null;
    }

    public final class FncValues extends OptionalParameterRL {

        private final DataType dataType;
        private final IndexMode indexMode;
        private final AdressType adressType;

        public FncValues(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
            this.indexMode = IndexMode.getIndexMode(parameters.get(2));
            this.adressType = AdressType.getAdressType(parameters.get(3));
        }

        public final DataType getDataType() {
            return dataType;
        }

        public final IndexMode getIndexMode() {
            return indexMode;
        }

        public final AdressType getAdressType() {
            return adressType;
        }
    }

    public final class AxisPtsX extends OptionalParameterRL {

        private final DataType dataType;
        private final IndexOrder indexOrder;
        private final AdressType adressType;

        public AxisPtsX(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
            this.indexOrder = IndexOrder.getIndexOrder(parameters.get(2));
            this.adressType = AdressType.getAdressType(parameters.get(3));
        }

        public final DataType getDataType() {
            return dataType;
        }

        public final IndexOrder getIndexOrder() {
            return indexOrder;
        }

        public final AdressType getAdressType() {
            return adressType;
        }
    }

    public final class NoAxisPtsX extends OptionalParameterRL {

        private final DataType dataType;

        public NoAxisPtsX(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
        }

        public final DataType getDataType() {
            return dataType;
        }

    }

    public final class AxisPtsY extends OptionalParameterRL {

        private final DataType dataType;
        private final IndexOrder indexOrder;
        private final AdressType adressType;

        public AxisPtsY(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
            this.indexOrder = IndexOrder.getIndexOrder(parameters.get(2));
            this.adressType = AdressType.getAdressType(parameters.get(3));
        }

        public final DataType getDataType() {
            return dataType;
        }

        public final IndexOrder getIndexOrder() {
            return indexOrder;
        }

        public final AdressType getAdressType() {
            return adressType;
        }
    }

    public final class NoAxisPtsY extends OptionalParameterRL {

        private final DataType dataType;

        public NoAxisPtsY(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
        }

        public final DataType getDataType() {
            return dataType;
        }

    }

    public final class SrcAddrX extends OptionalParameterRL {

        private final DataType dataType;

        public SrcAddrX(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
        }

        public final DataType getDataType() {
            return dataType;
        }

    }

    public final class SrcAddrY extends OptionalParameterRL {

        private final DataType dataType;

        public SrcAddrY(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
        }

        public final DataType getDataType() {
            return dataType;
        }

    }

    public final class NoRescaleX extends OptionalParameterRL {

        private final DataType dataType;

        public NoRescaleX(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
        }

        public final DataType getDataType() {
            return dataType;
        }

    }

    public final class Reserved extends OptionalParameterRL {

        private final DataSize dataSize;

        public Reserved(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataSize = DataSize.getDataSize(parameters.get(1));
        }

        public final DataSize getDataSize() {
            return dataSize;
        }
    }

    public final class AxisRescaleX extends OptionalParameterRL {

        private final DataType dataType;
        private final short maxNumberOfRescalePairs;
        private final IndexOrder indexOrder;
        private final AdressType adressType;

        public AxisRescaleX(List<String> parameters) {
            this.position = Byte.parseByte(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
            this.maxNumberOfRescalePairs = Short.parseShort(parameters.get(2));
            this.indexOrder = IndexOrder.getIndexOrder(parameters.get(3));
            this.adressType = AdressType.getAdressType(parameters.get(4));
        }

        public final DataType getDataType() {
            return dataType;
        }

        public final short getMaxNumberOfRescalePairs() {
            return maxNumberOfRescalePairs;
        }

        public final IndexOrder getIndexOrder() {
            return indexOrder;
        }

        public final AdressType getAdressType() {
            return adressType;
        }
    }

    @Override
    public int compareTo(RecordLayout o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        List<String> subList = Collections.emptyList();

        if (nbParams >= 1) {
            for (int n = 2; n < nbParams; n++) {
                switch (n) {
                case 2:
                    this.name = parameters.get(n);
                    break;
                default: // Cas de parametres optionels
                    switch (parameters.get(n)) {
                    case "FNC_VALUES":
                        subList = parameters.subList(n + 1, n + 5);
                        optionalsParameters.put(FNC_VALUES, new FncValues(subList));
                        n += 4;
                        break;
                    case "AXIS_PTS_X":
                        subList = parameters.subList(n + 1, n + 5);
                        optionalsParameters.put(AXIS_PTS_X, new AxisPtsX(subList));
                        n += 4;
                        break;
                    case "AXIS_PTS_Y":
                        subList = parameters.subList(n + 1, n + 5);
                        optionalsParameters.put(AXIS_PTS_Y, new AxisPtsY(subList));
                        n += 4;
                        break;
                    case "NO_AXIS_PTS_X":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.put(NO_AXIS_PTS_X, new NoAxisPtsX(subList));
                        n += 2;
                        break;
                    case "NO_AXIS_PTS_Y":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.put(NO_AXIS_PTS_Y, new NoAxisPtsY(subList));
                        n += 2;
                        break;
                    case "AXIS_RESCALE_X":
                        subList = parameters.subList(n + 1, n + 6);
                        optionalsParameters.put(AXIS_RESCALE_X, new AxisRescaleX(subList));
                        n += 5;
                        break;
                    case "NO_RESCALE_X":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.put(NO_RESCALE_X, new NoRescaleX(subList));
                        n += 2;
                        break;
                    case "RESERVED":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.put(RESERVED, new Reserved(subList));
                        n += 2;
                        break;
                    case "SRC_ADDR_X":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.put(SRC_ADDR_X, new SrcAddrX(subList));
                        n += 2;
                        break;
                    case "SRC_ADDR_Y":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.put(SRC_ADDR_Y, new SrcAddrY(subList));
                        n += 2;
                        break;
                    default:
                        break;
                    }
                    break;
                }
            }

            subList.clear();

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    @Override
    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("</u></html>");

        return sb.toString();
    }

}
