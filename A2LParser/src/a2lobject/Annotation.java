/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.ANNOTATION_LABEL;
import static constante.SecondaryKeywords.ANNOTATION_ORIGIN;
import static constante.SecondaryKeywords.ANNOTATION_TEXT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class Annotation {

	private Map<SecondaryKeywords, Object> optionalsParameters;

	public Annotation(List<String> parameters) {

		initOptionalsParameters();

		Set<SecondaryKeywords> keys = optionalsParameters.keySet();
		for (int nPar = 0; nPar < parameters.size(); nPar++) {
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

	private final void initOptionalsParameters()
	{
		optionalsParameters = new HashMap<SecondaryKeywords, Object>() {

			private static final long serialVersionUID = 1L;

			{
				put(ANNOTATION_LABEL, null);
				put(ANNOTATION_ORIGIN, null);
				put(ANNOTATION_TEXT, null);
			}
		};
	}
}
