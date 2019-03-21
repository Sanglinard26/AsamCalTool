/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.ADDR_EPK;
import static constante.SecondaryKeywords.ECU_CALIBRATION_OFFSET;
import static constante.SecondaryKeywords.EPK;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class ModPar {

    private String comment;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public ModPar(List<String> parameters) {
    	
    	initOptionalsParameters();

        parameters.remove("/begin"); // Remove /begin
        parameters.remove("MOD_PAR"); // Remove MOD_PAR

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
                            case "ADDR_EPK":
                                optionalsParameters.put(ADDR_EPK, parameters.get(nPar + 1));
                                break;
                            case "ECU_CALIBRATION_OFFSET":
                                optionalsParameters.put(ECU_CALIBRATION_OFFSET, parameters.get(nPar + 1));
                                break;
                            case "EPK":
                                optionalsParameters.put(EPK, parameters.get(nPar + 1));
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
				put(ADDR_EPK, null);
	            put(ECU_CALIBRATION_OFFSET, null);
	            put(EPK, null);
			}
		};
	}

    public final long getEPKAdress() {
        String addressEPK = ((String) optionalsParameters.get(ADDR_EPK));
        if (addressEPK != null) {
            return Long.parseLong(addressEPK.substring(2), 16);
        }
        return -1;
    }

    public final String getEPK() {
        return (String) optionalsParameters.get(EPK);
    }
    
    public final String getComment()
    {
    	return this.comment;
    }

}
