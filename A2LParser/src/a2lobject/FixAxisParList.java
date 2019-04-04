/*
 * Creation : 12 mars 2019
 */
package a2lobject;

import java.util.List;

public final class FixAxisParList {

    private final float[] axisPtsValue;

    public FixAxisParList(List<String> parameters) {

        axisPtsValue = new float[parameters.size()];

        for (int n = 0; n < axisPtsValue.length; n++) {
            axisPtsValue[n] = Float.parseFloat(parameters.get(n));
        }
    }

    public final int getNbValue() {
        return axisPtsValue.length;
    }

    public final double compute(int numVal) {
        return axisPtsValue[numVal];
    }
}