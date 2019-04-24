/*
 * Creation : 20 févr. 2019
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION_LABEL;
import static constante.SecondaryKeywords.ANNOTATION_ORIGIN;
import static constante.SecondaryKeywords.ANNOTATION_TEXT;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class Annotation implements A2lObjectBuilder {

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Annotation(List<String> parameters, int beginLine, int endLine) {

        initOptionalsParameters();

        build(parameters, beginLine, endLine);
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(ANNOTATION_LABEL, null);
        optionalsParameters.put(ANNOTATION_ORIGIN, null);
        optionalsParameters.put(ANNOTATION_TEXT, null);
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        Set<SecondaryKeywords> keys = optionalsParameters.keySet();
        for (int nPar = 0; nPar < nbParams; nPar++) {
            if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                switch (parameters.get(nPar)) {
                case "ANNOTATION_LABEL":
                    optionalsParameters.put(ANNOTATION_LABEL, parameters.get(nPar + 1));
                    break;
                case "ANNOTATION_ORIGIN":
                    optionalsParameters.put(ANNOTATION_ORIGIN, parameters.get(nPar + 1));
                    break;
                case "ANNOTATION_TEXT":
                    optionalsParameters.put(ANNOTATION_TEXT, parameters.get(nPar + 1));
                    break;
                default:
                    break;
                }
            }
        }
    }
}
