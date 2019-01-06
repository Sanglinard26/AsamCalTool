/*
 * Creation : 3 janv. 2019
 */
package a2l;

import java.util.Collections;
import java.util.List;

public final class Measurement {

    /*
     * /begin MEASUREMENT ident Name string LongIdentifier datatype Datatype ident Conversion uint Resolution float Accuracy float LowerLimit float
     * UpperLimit [-> ADDRESS_TYPE] [-> ANNOTATION]* [-> ARRAY_SIZE] [-> BIT_MASK] [-> BIT_OPERATION] [-> BYTE_ORDER] [-> DISCRETE] [->
     * DISPLAY_IDENTIFIER] [-> ECU_ADDRESS] [-> ECU_ADDRESS_EXTENSION] [-> ERROR_MASK] [-> FORMAT] [-> FUNCTION_LIST] [-> IF_DATA]* [-> LAYOUT] [->
     * MATRIX_DIM] [-> MAX_REFRESH] [-> MODEL_LINK] [-> PHYS_UNIT] [-> READ_WRITE] [-> REF_MEMORY_SEGMENT] [-> SYMBOL_LINK] [-> VIRTUAL] /end
     * MEASUREMENT
     */

    private String name;
    private String longIdentifier;
    private String dataType;
    private String conversion;
    private int resolution;
    private float accuracy;
    private float lowerLimit;
    private float upperLimit;

    private CompuMethod compuMethod;

    public Measurement(List<String> parameters) {

        if (parameters.size() >= 8) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    // System.out.println(this.name);
                    break;
                case 1:
                    this.longIdentifier = parameters.get(n);
                    break;
                case 2:
                    this.dataType = parameters.get(n);
                    break;
                case 3:
                    this.conversion = parameters.get(n);
                    break;
                case 4:
                    this.resolution = Integer.parseInt(parameters.get(n));
                    break;
                case 5:
                    this.accuracy = Float.parseFloat(parameters.get(n));
                    break;
                case 6:
                    this.lowerLimit = Float.parseFloat(parameters.get(n));
                    break;
                case 7:
                    this.upperLimit = Float.parseFloat(parameters.get(n));
                    break;
                default: // Cas de parametres optionels
                    break;
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

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name : " + name + "\n");
        sb.append("LongIdentifier : " + longIdentifier + "\n");
        sb.append("DataType : " + dataType + "\n");
        sb.append("Conversion : " + conversion + "\n");
        sb.append("Resolution : " + resolution + "\n");
        sb.append("Accuracy : " + accuracy + "\n");
        sb.append("LowerLimit : " + lowerLimit + "\n");
        sb.append("UpperLimit : " + upperLimit + "\n");

        return sb.toString();
    }

    public final void assignComputMethod(List<CompuMethod> compuMethods) {

        int idx = Collections.binarySearch(compuMethods, CompuMethod.createEmptyCompuMethod(conversion));

        if (idx > -1) {
            this.compuMethod = compuMethods.get(idx);
        }

    }
}
