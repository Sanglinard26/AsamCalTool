/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.ALIGNMENT_BYTE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT16_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT32_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT64_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_INT64;
import static constante.SecondaryKeywords.ALIGNMENT_LONG;
import static constante.SecondaryKeywords.ALIGNMENT_WORD;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.DATA_SIZE;
import static constante.SecondaryKeywords.DEPOSIT;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class ModCommon {

    private String comment;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public ModCommon(List<String> parameters) {
    	
    	initOptionalsParameters();

        parameters.remove("/begin"); // Remove /begin
        parameters.remove("MOD_COMMON"); // Remove RECORD_LAYOUT

        if (parameters.size() >= 1) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.comment = parameters.get(n);
                    break;
                default: // Cas de parametres optionels
                    Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                    for (int nPar = n; nPar < parameters.size(); nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "ALIGNMENT_BYTE":
                                optionalsParameters.put(ALIGNMENT_BYTE, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "ALIGNMENT_FLOAT16_IEEE":
                                optionalsParameters.put(ALIGNMENT_FLOAT16_IEEE, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "ALIGNMENT_FLOAT32_IEEE":
                                optionalsParameters.put(ALIGNMENT_FLOAT32_IEEE, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "ALIGNMENT_FLOAT64_IEEE":
                                optionalsParameters.put(ALIGNMENT_FLOAT64_IEEE, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "ALIGNMENT_INT64":
                                optionalsParameters.put(ALIGNMENT_INT64, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "ALIGNMENT_LONG":
                                optionalsParameters.put(ALIGNMENT_LONG, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "ALIGNMENT_WORD":
                                optionalsParameters.put(ALIGNMENT_WORD, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "BYTE_ORDER":
                                optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
                                break;
                            case "DATA_SIZE":
                                optionalsParameters.put(DATA_SIZE, Integer.parseInt(parameters.get(nPar + 1)));
                                break;
                            case "DEPOSIT":
                                optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
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
				put(ALIGNMENT_BYTE, null);
	            put(ALIGNMENT_FLOAT16_IEEE, null);
	            put(ALIGNMENT_FLOAT32_IEEE, null);
	            put(ALIGNMENT_FLOAT64_IEEE, null);
	            put(ALIGNMENT_INT64, null);
	            put(ALIGNMENT_LONG, null);
	            put(ALIGNMENT_WORD, null);
	            put(BYTE_ORDER, null); // MSB_LAST ==> LITTLE_ENDIAN
	            put(DATA_SIZE, null);
	            put(DEPOSIT, null);
			}
		};
	}

    public final ByteOrder getByteOrder() {
        String sByteOrder = (String) optionalsParameters.get(SecondaryKeywords.BYTE_ORDER);
        if ("MSB_LAST".equals(sByteOrder) || "BIG_ENDIAN".equals(sByteOrder)) {
            return ByteOrder.LITTLE_ENDIAN;
        }
        return ByteOrder.BIG_ENDIAN;
    }
    
    public final String getComment()
    {
    	return this.comment;
    }

}
