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
import static constante.SecondaryKeywords.FNC_VALUES;
import static constante.SecondaryKeywords.STATIC_RECORD_LAYOUT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import constante.AdressType;
import constante.DataType;
import constante.IndexMode;
import constante.SecondaryKeywords;

public final class RecordLayout implements Comparable<RecordLayout> {

    private String name;

    private final Map<SecondaryKeywords, Object> optionalsParameters = new HashMap<SecondaryKeywords, Object>() {
        {
            put(ALIGNMENT_BYTE, null);
            put(ALIGNMENT_FLOAT16_IEEE, null);
            put(ALIGNMENT_FLOAT32_IEEE, null);
            put(ALIGNMENT_FLOAT64_IEEE, null);
            put(ALIGNMENT_INT64, null);
            put(ALIGNMENT_LONG, null);
            put(ALIGNMENT_WORD, null);
            put(FNC_VALUES, null);
            put(STATIC_RECORD_LAYOUT, null);
        }
    };

    public RecordLayout(List<String> parameters) {

        parameters.remove("/begin"); // Remove /begin
        parameters.remove("RECORD_LAYOUT"); // Remove RECORD_LAYOUT

        if (parameters.size() >= 1) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    // System.out.println(this.name);
                    break;
                default: // Cas de parametres optionels
                    switch (parameters.get(n)) {
                    case "FNC_VALUES":
                        List<String> subList = parameters.subList(n + 1, n + 5);
                        optionalsParameters.put(FNC_VALUES, new FncValues(subList));
                        n += 4;
                        break;
                    }
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

    public static RecordLayout createEmptyRecordLayout(String name) {
        List<String> parameters = new ArrayList<String>();
        parameters.add(name);
        return new RecordLayout(parameters);
    }

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(obj.toString());
    }

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name : " + name + "\n");

        return sb.toString();
    }

    @Override
    public int compareTo(RecordLayout o) {
        return this.name.compareTo(o.name);
    }

    private final class FncValues {

        private int position;
        private DataType dataType;
        private IndexMode indexMode;
        private AdressType adressType;

        public FncValues(List<String> parameters) {
            this.position = Integer.parseInt(parameters.get(0));
            this.dataType = DataType.getDataType(parameters.get(1));
            this.indexMode = IndexMode.getIndexMode(parameters.get(2));
            this.adressType = AdressType.getAdressType(parameters.get(3));
        }
    }

}
