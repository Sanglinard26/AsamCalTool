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

public final class Annotation {

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Annotation(List<String> parameters) {

        initOptionalsParameters();

        build(parameters);
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(ANNOTATION_LABEL, null);
        optionalsParameters.put(ANNOTATION_ORIGIN, null);
        optionalsParameters.put(ANNOTATION_TEXT, null);
    }

    private void build(List<String> parameters) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        Set<SecondaryKeywords> keys = optionalsParameters.keySet();
        SecondaryKeywords keyWord;
        for (int nPar = 0; nPar < nbParams; nPar++) {
            keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
            if (keys.contains(keyWord)) {
                switch (keyWord) {
                case ANNOTATION_LABEL:
                    optionalsParameters.put(ANNOTATION_LABEL, parameters.get(nPar + 1));
                    break;
                case ANNOTATION_ORIGIN:
                    optionalsParameters.put(ANNOTATION_ORIGIN, parameters.get(nPar + 1));
                    break;
                case ANNOTATION_TEXT:
                    optionalsParameters.put(ANNOTATION_TEXT, parameters.get(nPar + 1));
                    break;
                default:
                    break;
                }
            }
        }
    }
}
