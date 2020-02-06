package a2l;

import static constante.SecondaryKeywords.DEF_CHARACTERISTIC;
import static constante.SecondaryKeywords.FUNCTION_VERSION;
import static constante.SecondaryKeywords.IN_MEASUREMENT;
import static constante.SecondaryKeywords.LOC_MEASUREMENT;
import static constante.SecondaryKeywords.OUT_MEASUREMENT;
import static constante.SecondaryKeywords.SUB_FUNCTION;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import constante.SecondaryKeywords;

public final class Function implements A2lObject, Comparable<Function> {

    private String name;
    private char[] longIdentifier;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Function(List<String> parameters, int beginLine, int endLine) {

        initOptionalsParameters();

        build(parameters, beginLine, endLine);
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(DEF_CHARACTERISTIC, null);
        optionalsParameters.put(FUNCTION_VERSION, null);
        optionalsParameters.put(IN_MEASUREMENT, null);
        optionalsParameters.put(OUT_MEASUREMENT, null);
        optionalsParameters.put(SUB_FUNCTION, null);
        optionalsParameters.put(LOC_MEASUREMENT, null);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @SuppressWarnings("unchecked")
    public final Map<String, String> getDefCharacteristic() {
        Object object = optionalsParameters.get(DEF_CHARACTERISTIC);
        return (Map<String, String>) (object != null ? object : new HashMap<String, String>());
    }

    public final Vector<String> getInMeasurement() {
        Object object = optionalsParameters.get(IN_MEASUREMENT);
        if (object != null) {
            @SuppressWarnings("unchecked")
            Vector<String> v = new Vector<String>(((Set<String>) object));
            Collections.sort(v);
            return v;
        }
        return new Vector<String>();
    }

    public final Vector<String> getLocMeasurement() {
        Object object = optionalsParameters.get(LOC_MEASUREMENT);
        if (object != null) {
            @SuppressWarnings("unchecked")
            Vector<String> v = new Vector<String>(((Set<String>) object));
            Collections.sort(v);
            return v;
        }
        return new Vector<String>();
    }

    public final Vector<String> getOutMeasurement() {
        Object object = optionalsParameters.get(OUT_MEASUREMENT);
        if (object != null) {
            @SuppressWarnings("unchecked")
            Vector<String> v = new Vector<String>(((Set<String>) object));
            Collections.sort(v);
            return v;
        }
        return new Vector<String>();
    }

    @Override
    public int compareTo(Function function) {
        return this.name.compareToIgnoreCase(function.toString());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        if (nbParams >= 2) {

            this.name = parameters.get(2).intern();
            this.longIdentifier = parameters.get(3).toCharArray();

            int n = 4;

            Set<SecondaryKeywords> keys = optionalsParameters.keySet();
            SecondaryKeywords keyWord;
            for (int nPar = n; nPar < nbParams; nPar++) {
                keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                if (keys.contains(keyWord)) {
                    switch (keyWord) {
                    case DEF_CHARACTERISTIC:
                        Map<String, String> defCharacteristic = new HashMap<String, String>();
                        optionalsParameters.put(DEF_CHARACTERISTIC, defCharacteristic);
                        nPar++;
                        do {
                            defCharacteristic.put(parameters.get(nPar), this.name);
                            nPar++;
                        } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                        nPar++;
                        break;
                    case FUNCTION_VERSION:
                        optionalsParameters.put(FUNCTION_VERSION, parameters.get(++nPar));
                        nPar++;
                        break;
                    case IN_MEASUREMENT:
                        Set<String> inMeasurement = new HashSet<String>();
                        optionalsParameters.put(IN_MEASUREMENT, inMeasurement);

                        nPar++;
                        do {
                            inMeasurement.add(parameters.get(nPar));
                            nPar++;
                        } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                        nPar++;
                        break;
                    case LOC_MEASUREMENT:
                        Set<String> locMeasurement = new HashSet<String>();
                        optionalsParameters.put(LOC_MEASUREMENT, locMeasurement);
                        nPar++;
                        do {
                            locMeasurement.add(parameters.get(nPar));
                            nPar++;
                        } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                        nPar++;
                        break;
                    case OUT_MEASUREMENT:
                        Set<String> outMeasurement = new HashSet<String>();
                        optionalsParameters.put(OUT_MEASUREMENT, outMeasurement);

                        nPar++;
                        do {
                            outMeasurement.add(parameters.get(nPar));
                            nPar++;
                        } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                        nPar++;
                        break;
                    case SUB_FUNCTION:
                        Set<String> subFunction = new HashSet<String>();
                        optionalsParameters.put(SUB_FUNCTION, subFunction);
                        nPar++;
                        do {
                            subFunction.add(parameters.get(nPar));
                            nPar++;
                        } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                        nPar++;
                        break;

                    default:
                        break;
                    }
                }
            }

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    @SuppressWarnings("unchecked")
    public final Set<String> getSubFunction() {
        Object object = optionalsParameters.get(SUB_FUNCTION);

        return object != null ? (Set<String>) object : new HashSet<String>();
    }

    @Override
    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + new String(longIdentifier) + "\n");

        if (getSubFunction().size() > 0) {
            sb.append("<li><b>Sub Function: </b>");
            sb.append("<ul>");
            for (String subFunction : getSubFunction()) {
                sb.append("<li><a href=" + subFunction + ">" + subFunction);
            }
        }

        sb.append("</u>");

        return sb.toString();
    }
}
