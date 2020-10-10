/*
 * Creation : 20 févr. 2019
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION_LABEL;
import static constante.SecondaryKeywords.ANNOTATION_ORIGIN;
import static constante.SecondaryKeywords.ANNOTATION_TEXT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constante.SecondaryKeywords;

public final class Annotation {

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Annotation(List<String> parameters) {

        optionalsParameters = new HashMap<SecondaryKeywords, Object>();

        build(parameters);
    }

    private void build(List<String> parameters) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        SecondaryKeywords keyWord;
        for (int nPar = 0; nPar < nbParams; nPar++) {
            keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
            switch (keyWord) {
            case ANNOTATION_LABEL:
                optionalsParameters.put(ANNOTATION_LABEL, parameters.get(nPar + 1).toCharArray());
                break;
            case ANNOTATION_ORIGIN:
                optionalsParameters.put(ANNOTATION_ORIGIN, parameters.get(nPar + 1).toCharArray());
                break;
            case ANNOTATION_TEXT:
                optionalsParameters.put(ANNOTATION_TEXT, parameters.get(nPar + 1).toCharArray());
                break;
            default:
                break;
            }
        }
    }

    public final String getAnnotationParam(SecondaryKeywords keyword) {
        Object oText = optionalsParameters.get(keyword);
        return oText != null ? new String((char[]) oText) : "";
    }
}
