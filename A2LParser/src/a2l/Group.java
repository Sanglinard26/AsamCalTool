/*
 * Creation : 20 févr. 2025
 */
package a2l;

import static constante.SecondaryKeywords.REF_CHARACTERISTIC;
import static constante.SecondaryKeywords.REF_MEASUREMENT;
import static constante.SecondaryKeywords.ROOT;
import static constante.SecondaryKeywords.SUB_GROUP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import constante.SecondaryKeywords;

public class Group implements A2lObject, Comparable<Group> {

    private String name;
    private char[] longIdentifier;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Group(List<String> parameters, int beginLine, int endLine) {

        optionalsParameters = new HashMap<SecondaryKeywords, Object>();

        build(parameters, beginLine, endLine);
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws A2lObjectParsingException {

        final int nbParams = parameters.size();

        if (nbParams >= 2) {

            this.name = parameters.get(2).intern();
            this.longIdentifier = parameters.get(3).toCharArray();

            int n = 4;

            SecondaryKeywords keyWord;
            for (int nPar = n; nPar < nbParams; nPar++) {
                keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                switch (keyWord) {
                case ROOT:
                    optionalsParameters.put(ROOT, "ROOT");
                    break;
                case REF_CHARACTERISTIC:
                    Vector<String> refCharacteristic = new Vector<String>();
                    optionalsParameters.put(REF_CHARACTERISTIC, refCharacteristic);
                    nPar++;
                    do {
                        if (parameters.get(nPar).equals("/end")) {
                            break;
                        }
                        refCharacteristic.add(parameters.get(nPar));
                        nPar++;
                    } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                    nPar++;
                    break;
                case FUNCTION_LIST:
                    // optionalsParameters.put(FUNCTION_LIST, parameters.get(++nPar));
                    nPar++;
                    break;
                case REF_MEASUREMENT:
                    Vector<String> outMeasurement = new Vector<String>();
                    optionalsParameters.put(REF_MEASUREMENT, outMeasurement);
                    nPar++;
                    do {
                        if (parameters.get(nPar).equals("/end")) {
                            break;
                        }
                        outMeasurement.add(parameters.get(nPar));
                        nPar++;
                    } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                    nPar++;
                    break;
                case SUB_GROUP:
                    Vector<String> subFunction = new Vector<String>();
                    optionalsParameters.put(SUB_GROUP, subFunction);
                    nPar++;
                    do {
                        if (parameters.get(nPar).equals("/end")) {
                            break;
                        }
                        subFunction.add(parameters.get(nPar));
                        nPar++;
                    } while (nPar < nbParams - 1 && !parameters.get(nPar).equals("/end"));
                    nPar++;
                    break;

                default:
                    break;
                }
            }

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    @SuppressWarnings("unchecked")
    public final Vector<String> getSubGroup() {
        Object object = optionalsParameters.get(SUB_GROUP);

        return object != null ? (Vector<String>) object : new Vector<String>();
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
    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + new String(longIdentifier) + "\n");

        if (getSubGroup().size() > 0) {
            sb.append("<li><b>Sub Group: </b>");
            sb.append("<ul>");
            for (String subFunction : getSubGroup()) {
                sb.append("<li><a href=" + subFunction + ">" + subFunction);
            }
        }

        sb.append("</u>");

        return sb.toString();
    }

    @Override
    public int compareTo(Group group) {
        return this.name.compareToIgnoreCase(group.toString());
    }

}
