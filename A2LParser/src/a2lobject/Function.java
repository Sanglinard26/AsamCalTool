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

    private Set<String> defCharacteristic;
    private Set<String> inMeasurement;
    private Set<String> outMeasurement;
    private Set<String> locMeasurement;
    private Set<String> subFunction;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Function(List<String> parameters) {
    	
    	initOptionalsParameters();

        parameters.remove("/begin"); // Remove /begin
        parameters.remove("FUNCTION"); // Remove FUNCTION

        if (parameters.size() >= 2) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    break;
                case 1:
                    this.longIdentifier = parameters.get(n);
                    break;
                default: // Cas de parametres optionels

                    Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                    for (int nPar = n; nPar < parameters.size(); nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "DEF_CHARACTERISTIC":
                                if (defCharacteristic == null) {
                                    defCharacteristic = new HashSet<String>();
                                    optionalsParameters.put(DEF_CHARACTERISTIC, defCharacteristic);
                                }
                                nPar++;
                                do {
                                    defCharacteristic.add(parameters.get(nPar));
                                    nPar++;
                                } while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
                                nPar++;
                                break;
                            case "FUNCTION_VERSION":
                            	optionalsParameters.put(FUNCTION_VERSION, parameters.get(++nPar));
                            	nPar++;
                                break;
                            case "IN_MEASUREMENT":
                                if (inMeasurement == null) {
                                    inMeasurement = new HashSet<String>();
                                    optionalsParameters.put(IN_MEASUREMENT, inMeasurement);
                                }
                                nPar++;
                                do {
                                    inMeasurement.add(parameters.get(nPar));
                                    nPar++;
                                } while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
                                nPar++;
                                break;
                            case "LOC_MEASUREMENT":
                                if (locMeasurement == null) {
                                	locMeasurement = new HashSet<String>();
                                    optionalsParameters.put(LOC_MEASUREMENT, locMeasurement);
                                }
                                nPar++;
                                do {
                                	locMeasurement.add(parameters.get(nPar));
                                    nPar++;
                                } while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
                                nPar++;
                                break;
                            case "OUT_MEASUREMENT":
                                if (outMeasurement == null) {
                                    outMeasurement = new HashSet<String>();
                                    optionalsParameters.put(OUT_MEASUREMENT, outMeasurement);
                                }
                                nPar++;
                                do {
                                    outMeasurement.add(parameters.get(nPar));
                                    nPar++;
                                } while (nPar < parameters.size()-1 && !parameters.get(nPar).equals("/end"));
                                nPar++;
                                break;
                            case "SUB_FUNCTION":
                            	if (subFunction == null) {
                            		subFunction = new HashSet<String>();
                                    optionalsParameters.put(SUB_FUNCTION, subFunction);
                                }
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
                    n = parameters.size();
                    break;
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
		optionalsParameters = new HashMap<SecondaryKeywords, Object>() {

			private static final long serialVersionUID = 1L;

			{
				put(DEF_CHARACTERISTIC, null);
	            put(FUNCTION_VERSION, null);
	            put(IN_MEASUREMENT, null);
	            put(OUT_MEASUREMENT, null);
	            put(SUB_FUNCTION, null);
	            put(LOC_MEASUREMENT, null);
			}
		};
	}

    @Override
    public String toString() {
        return this.name;
    }
}
