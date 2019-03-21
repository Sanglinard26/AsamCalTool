package a2lobject;

import static constante.SecondaryKeywords.DEF_CHARACTERISTIC;
import static constante.SecondaryKeywords.FUNCTION_VERSION;
import static constante.SecondaryKeywords.IN_MEASUREMENT;
import static constante.SecondaryKeywords.LOC_MEASUREMENT;
import static constante.SecondaryKeywords.OUT_MEASUREMENT;
import static constante.SecondaryKeywords.SUB_FUNCTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class Function {

    private String name;
    private String longIdentifier;

    private List<String> defCharacteristic;
    private List<String> inMeasurement;
    private List<String> outMeasurement;
    private List<String> locMeasurement;

    private final Map<SecondaryKeywords, Object> optionalsParameters = new HashMap<SecondaryKeywords, Object>() {
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

    public Function(List<String> parameters) {

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
                    // for (SecondaryKeywords key : keys) {
                    // if (parameters.contains(key.name())) {
                    // int i = 0;
                    // }
                    // }
                    for (int nPar = n; nPar < parameters.size(); nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "DEF_CHARACTERISTIC":
                                if (defCharacteristic == null) {
                                    defCharacteristic = new ArrayList<String>();
                                    optionalsParameters.put(DEF_CHARACTERISTIC, defCharacteristic);
                                }
                                n = nPar + 1;
                                do {
                                    defCharacteristic.add(parameters.get(++nPar));
                                } while (!parameters.get(nPar).equals("/end"));
                                n = nPar + 1;
                                break;
                            case "FUNCTION_VERSION":
                                break;
                            case "IN_MEASUREMENT":
                                if (inMeasurement == null) {
                                    inMeasurement = new ArrayList<String>();
                                    optionalsParameters.put(IN_MEASUREMENT, inMeasurement);
                                }
                                n = nPar + 1;
                                do {
                                    inMeasurement.add(parameters.get(++nPar));
                                } while (!parameters.get(nPar).equals("/end"));
                                n = nPar + 1;
                                break;
                            case "OUT_MEASUREMENT":
                                if (outMeasurement == null) {
                                    outMeasurement = new ArrayList<String>();
                                    optionalsParameters.put(OUT_MEASUREMENT, outMeasurement);
                                }
                                n = nPar + 1;
                                do {
                                    outMeasurement.add(parameters.get(++nPar));
                                } while (!parameters.get(nPar).equals("/end"));
                                n = nPar + 1;
                                break;
                            case "SUB_FUNCTION":
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

    @Override
    public String toString() {
        return this.name;
    }

}
