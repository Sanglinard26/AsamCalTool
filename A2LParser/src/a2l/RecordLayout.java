/*
 * Creation : 20 févr. 2019
 */
package a2l;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import constante.AdressType;
import constante.DataSize;
import constante.DataType;
import constante.IndexMode;
import constante.IndexOrder;

public final class RecordLayout implements A2lObject, Comparable<RecordLayout> {

    private String name;

    private List<OptionalParameterRL> optionalsParameters;

    public RecordLayout(List<String> parameters, int beginLine, int endLine) {

        optionalsParameters = new ArrayList<OptionalParameterRL>();

        build(parameters, beginLine, endLine);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public FncValues getFncValues() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof FncValues) {
                return (FncValues) optParam;
            }
        }
        return null;
    }

    public NoAxisPtsX getNoAxisPtsX() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof NoAxisPtsX) {
                return (NoAxisPtsX) optParam;
            }
        }
        return null;
    }

    public AxisPtsX getAxisPtsX() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof AxisPtsX) {
                return (AxisPtsX) optParam;
            }
        }
        return null;
    }

    public NoAxisPtsY getNoAxisPtsY() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof NoAxisPtsY) {
                return (NoAxisPtsY) optParam;
            }
        }
        return null;
    }

    public AxisPtsY getAxisPtsY() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof AxisPtsY) {
                return (AxisPtsY) optParam;
            }
        }
        return null;
    }

    public SrcAddrX getSrcAddrX() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof SrcAddrX) {
                return (SrcAddrX) optParam;
            }
        }
        return null;
    }

    public SrcAddrY getSrcAddrY() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof SrcAddrY) {
                return (SrcAddrY) optParam;
            }
        }
        return null;
    }

    public NoRescaleX getNoRescaleX() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof NoRescaleX) {
                return (NoRescaleX) optParam;
            }
        }
        return null;
    }

    public List<Reserved> getReserved() {

        ArrayList<Reserved> listReserved = new ArrayList<Reserved>();

        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof Reserved) {
                listReserved.add((Reserved) optParam);
            }
        }
        return listReserved;
    }

    public final Alignment getAlignment() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof Alignment) {
                return (Alignment) optParam;
            }
        }
        return null;
    }

    public AxisRescaleX getAxisRescaleX() {
        for (OptionalParameterRL optParam : optionalsParameters) {
            if (optParam instanceof AxisRescaleX) {
                return (AxisRescaleX) optParam;
            }
        }
        return null;
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

    public final class Alignment extends OptionalParameterRL {
        int byteAlignment;

        public Alignment(List<String> parameters) {
            this.byteAlignment = Byte.parseByte(parameters.get(0));
            ;
            this.position = -1;
        }

        public final int getByteAlignment() {
            return byteAlignment;
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
                        optionalsParameters.add(new FncValues(subList));
                        n += 4;
                        break;
                    case "AXIS_PTS_X":
                        subList = parameters.subList(n + 1, n + 5);
                        optionalsParameters.add(new AxisPtsX(subList));
                        n += 4;
                        break;
                    case "AXIS_PTS_Y":
                        subList = parameters.subList(n + 1, n + 5);
                        optionalsParameters.add(new AxisPtsY(subList));
                        n += 4;
                        break;
                    case "NO_AXIS_PTS_X":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.add(new NoAxisPtsX(subList));
                        n += 2;
                        break;
                    case "NO_AXIS_PTS_Y":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.add(new NoAxisPtsY(subList));
                        n += 2;
                        break;
                    case "AXIS_RESCALE_X":
                        subList = parameters.subList(n + 1, n + 6);
                        optionalsParameters.add(new AxisRescaleX(subList));
                        n += 5;
                        break;
                    case "NO_RESCALE_X":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.add(new NoRescaleX(subList));
                        n += 2;
                        break;
                    case "RESERVED":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.add(new Reserved(subList));
                        n += 2;
                        break;
                    case "SRC_ADDR_X":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.add(new SrcAddrX(subList));
                        n += 2;
                        break;
                    case "SRC_ADDR_Y":
                        subList = parameters.subList(n + 1, n + 3);
                        optionalsParameters.add(new SrcAddrY(subList));
                        n += 2;
                        break;
                    case "ALIGNMENT_BYTE":
                        subList = parameters.subList(n + 1, n + 2);
                        optionalsParameters.add(new Alignment(subList));
                        n += 1;
                        break;
                    case "ALIGNMENT_FLOAT16_IEEE":
                        subList = parameters.subList(n + 1, n + 2);
                        optionalsParameters.add(new Alignment(subList));
                        n += 1;
                        break;
                    case "ALIGNMENT_FLOAT32_IEEE":
                        subList = parameters.subList(n + 1, n + 2);
                        optionalsParameters.add(new Alignment(subList));
                        n += 1;
                        break;
                    case "ALIGNMENT_FLOAT64_IEEE":
                        subList = parameters.subList(n + 1, n + 2);
                        optionalsParameters.add(new Alignment(subList));
                        n += 1;
                        break;
                    case "ALIGNMENT_INT64":
                        subList = parameters.subList(n + 1, n + 2);
                        optionalsParameters.add(new Alignment(subList));
                        n += 1;
                        break;
                    case "ALIGNMENT_LONG":
                        subList = parameters.subList(n + 1, n + 2);
                        optionalsParameters.add(new Alignment(subList));
                        n += 1;
                        break;
                    case "ALIGNMENT_WORD":
                        subList = parameters.subList(n + 1, n + 2);
                        optionalsParameters.add(new Alignment(subList));
                        n += 1;
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
