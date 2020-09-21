/*
 * Creation : 3 janv. 2019
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.BIT_MASK;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.DISCRETE;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.ECU_ADDRESS_EXTENSION;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.MATRIX_DIM;
import static constante.SecondaryKeywords.MAX_REFRESH;
import static constante.SecondaryKeywords.PHYS_UNIT;
import static constante.SecondaryKeywords.REF_MEMORY_SEGMENT;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constante.DataType;
import constante.SecondaryKeywords;

public final class Measurement implements A2lObject, Comparable<Measurement> {

    private String name;
    private char[] longIdentifier;
    private DataType dataType;
    private int conversionId;
    @SuppressWarnings("unused")
    private byte resolution;
    @SuppressWarnings("unused")
    private float accuracy;
    private float lowerLimit;
    private float upperLimit;

    private CompuMethod compuMethod;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Measurement(List<String> parameters, int beginLine, int endLine) {

        initOptionalsParameters();

        build(parameters, beginLine, endLine);
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(ANNOTATION, null);
        optionalsParameters.put(BIT_MASK, null);
        optionalsParameters.put(BYTE_ORDER, null);
        optionalsParameters.put(DISCRETE, null); // ToDo
        optionalsParameters.put(DISPLAY_IDENTIFIER, null);
        optionalsParameters.put(ECU_ADDRESS_EXTENSION, null); // ToDo
        optionalsParameters.put(FORMAT, null);
        optionalsParameters.put(MATRIX_DIM, null);
        optionalsParameters.put(MAX_REFRESH, null);
        optionalsParameters.put(PHYS_UNIT, null);
        optionalsParameters.put(REF_MEMORY_SEGMENT, null);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        try {
            if (nbParams >= 8) {
                this.name = parameters.get(2);
                this.longIdentifier = parameters.get(3).toCharArray();
                this.dataType = DataType.getDataType(parameters.get(4));
                this.conversionId = parameters.get(5).hashCode();
                this.resolution = (byte) Integer.parseInt(parameters.get(6));
                this.accuracy = Float.parseFloat(parameters.get(7));
                this.lowerLimit = Float.parseFloat(parameters.get(8));
                if (parameters.get(9).startsWith("0x")) { // Test pour A2L AW
                    this.upperLimit = Integer.parseInt(parameters.get(9).substring(2), 16);
                } else {
                    this.upperLimit = Float.parseFloat(parameters.get(9));
                }

                int n = 10;

                SecondaryKeywords keyWord;
                for (int nPar = n; nPar < nbParams; nPar++) {
                    keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                    if (optionalsParameters.containsKey(keyWord)) {
                        switch (keyWord) {
                        case ANNOTATION:
                            n = nPar + 1;
                            do {
                            } while (!parameters.get(++nPar).equals(ANNOTATION.name()));
                            optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3)));
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
                            optionalsParameters.put(FORMAT, parameters.get(nPar + 1).toCharArray());
                            nPar += 1;
                            break;
                        case MATRIX_DIM:
                            List<Short> dim = new ArrayList<Short>();

                            try {
                                nPar += 1;
                                do {
                                    dim.add((short) Integer.parseInt(parameters.get(nPar)));
                                    nPar += 1;
                                } while (nPar < parameters.size());
                            } catch (NumberFormatException nfe) {
                                nPar += 1;
                            }
                            optionalsParameters.put(MATRIX_DIM, dim.toArray());
                            dim.clear();
                            break;
                        case PHYS_UNIT:
                            optionalsParameters.put(PHYS_UNIT, parameters.get(nPar + 1).toCharArray());
                            nPar += 1;
                            break;
                        default:
                            break;
                        }
                    }
                }

            } else {
                throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
            }
        } catch (IllegalArgumentException e) {
            throw new A2lObjectParsingException("Parsing error on " + this.name, beginLine, endLine);
        }

    }

    public final void assignComputMethod(HashMap<Integer, CompuMethod> compuMethods) {
        this.compuMethod = compuMethods.get(this.conversionId);
    }

    public final String getUnit() {
        Object oPhysUnit = optionalsParameters.get(PHYS_UNIT);

        return (oPhysUnit != null && "NO_COMPU_METHOD".equals(conversionId)) ? new String((char[]) oPhysUnit) : compuMethod.getUnit();
    }

    @Override
    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + new String(longIdentifier) + "\n");
        sb.append("<li><b>Data type: </b>" + dataType + "\n");
        sb.append("<li><b>Conversion: </b><a href=" + compuMethod.toString() + ">" + compuMethod.toString() + "</a>\n");
        sb.append("<li><b>Unit: </b>" + "[" + getUnit() + "]\n");
        sb.append("<li><b>Lower limit: </b>" + lowerLimit + "\n");
        sb.append("<li><b>Upper limit: </b>" + upperLimit + "\n");
        sb.append("</ul>");

        sb.append("<b><u>OPTIONALS PARAMETERS :\n</u></b>");

        sb.append("</html>");

        return sb.toString();
    }

    @Override
    public int compareTo(Measurement measurement) {
        return this.name.compareToIgnoreCase(measurement.name);
    }

}
