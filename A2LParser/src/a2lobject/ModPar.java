/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.ADDR_EPK;
import static constante.SecondaryKeywords.ECU_CALIBRATION_OFFSET;
import static constante.SecondaryKeywords.EPK;
import static constante.SecondaryKeywords.SYSTEM_CONSTANT;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;

public final class ModPar {

    private String comment;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public ModPar(List<String> parameters) {

        initOptionalsParameters();

        final int nbParams = parameters.size();

        List<SystemConstant> systemConstant = new ArrayList<SystemConstant>();

        if (nbParams >= 1) {
            for (int n = 2; n < nbParams; n++) {
                switch (n) {
                case 2:
                    this.comment = parameters.get(n);
                    break;
                default: // Cas de parametres optionels
                    Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                    for (int nPar = n; nPar < nbParams; nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "ADDR_EPK":
                                optionalsParameters.put(ADDR_EPK, parameters.get(nPar + 1));
                                nPar += 1;
                                break;
                            case "ECU_CALIBRATION_OFFSET":
                                optionalsParameters.put(ECU_CALIBRATION_OFFSET, parameters.get(nPar + 1));
                                nPar += 1;
                                break;
                            case "EPK":
                                optionalsParameters.put(EPK, parameters.get(nPar + 1));
                                nPar += 1;
                                break;
                            case "SYSTEM_CONSTANT":
                                if (systemConstant.isEmpty()) {
                                    optionalsParameters.put(SYSTEM_CONSTANT, systemConstant);
                                }
                                systemConstant.add(new SystemConstant(parameters.subList(nPar + 1, nPar + 3)));
                                nPar += 2;
                                break;
                            default:
                                break;
                            }
                        }

                    }
                    n = nbParams;
                    break;
                }
            }

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(ADDR_EPK, null);
        optionalsParameters.put(ECU_CALIBRATION_OFFSET, null);
        optionalsParameters.put(EPK, null);
        optionalsParameters.put(SYSTEM_CONSTANT, null);
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

    public final String getComment() {
        return this.comment;
    }

    @SuppressWarnings("unchecked")
    public final List<SystemConstant> getSystemConstant() {
        Object object = optionalsParameters.get(SYSTEM_CONSTANT);
        return object != null ? (List<SystemConstant>) object : new ArrayList<SystemConstant>();
    }

}
