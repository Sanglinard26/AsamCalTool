package a2lobject;

import static constante.SecondaryKeywords.DEF_CHARACTERISTIC;
import static constante.SecondaryKeywords.FUNCTION_VERSION;
import static constante.SecondaryKeywords.IN_MEASUREMENT;
import static constante.SecondaryKeywords.LOC_MEASUREMENT;
import static constante.SecondaryKeywords.OUT_MEASUREMENT;
import static constante.SecondaryKeywords.SUB_FUNCTION;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class Function {

	private String name;
	@SuppressWarnings("unused")
	private String longIdentifier;

	private Map<SecondaryKeywords, Object> optionalsParameters;

	public Function(List<String> parameters) {

		initOptionalsParameters();

		parameters.remove("/begin"); // Remove /begin
		parameters.remove("FUNCTION"); // Remove FUNCTION

		if (parameters.size() >= 2) {

			this.name = parameters.get(0);
			this.longIdentifier = parameters.get(1);

			int n = 2;

			Set<SecondaryKeywords> keys = optionalsParameters.keySet();
			for (int nPar = n; nPar < parameters.size(); nPar++) {
				if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
					switch (parameters.get(nPar)) {
					case "DEF_CHARACTERISTIC":
						Map<String, String> defCharacteristic = new HashMap<String, String>();
						optionalsParameters.put(DEF_CHARACTERISTIC, defCharacteristic);
						nPar++;
						do {
							defCharacteristic.put(parameters.get(nPar), this.name);
							nPar++;
						} while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
						nPar++;
						break;
					case "FUNCTION_VERSION":
						optionalsParameters.put(FUNCTION_VERSION, parameters.get(++nPar));
						nPar++;
						break;
					case "IN_MEASUREMENT":
						Set<String> inMeasurement = new HashSet<String>();
						optionalsParameters.put(IN_MEASUREMENT, inMeasurement);

						nPar++;
						do {
							inMeasurement.add(parameters.get(nPar));
							nPar++;
						} while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
						nPar++;
						break;
					case "LOC_MEASUREMENT":
						Set<String> locMeasurement = new HashSet<String>();
						optionalsParameters.put(LOC_MEASUREMENT, locMeasurement);	
						nPar++;
						do {
							locMeasurement.add(parameters.get(nPar));
							nPar++;
						} while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
						nPar++;
						break;
					case "OUT_MEASUREMENT":
						Set<String> outMeasurement = new HashSet<String>();
						optionalsParameters.put(OUT_MEASUREMENT, outMeasurement);

						nPar++;
						do {
							outMeasurement.add(parameters.get(nPar));
							nPar++;
						} while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
						nPar++;
						break;
					case "SUB_FUNCTION":
						Set<String> subFunction = new HashSet<String>();
						optionalsParameters.put(SUB_FUNCTION, subFunction);
						nPar++;
						do {
							subFunction.add(parameters.get(nPar));
							nPar++;
						} while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
						nPar++;
						break;

					default:
						break;
					}
				}
			}

			// On vide la MAP de parametre non utilise
			Iterator<Map.Entry<SecondaryKeywords, Object>> iter = optionalsParameters.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<SecondaryKeywords, Object> entry = iter.next();
				if (entry.getValue() == null) {
					iter.remove();
				}
			}

		} else {
			throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
		}
	}

	private final void initOptionalsParameters()
	{
		optionalsParameters = new HashMap<SecondaryKeywords, Object>();
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
	public final Map<String, String> getDefCharacteristic()
	{
		Object object = optionalsParameters.get(DEF_CHARACTERISTIC);
		return (Map<String, String>) (object != null ? object : null);
	}
}
