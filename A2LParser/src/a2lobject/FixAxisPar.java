/*
 * Creation : 12 mars 2019
 */
package a2lobject;

import java.util.List;

public final class FixAxisPar {

    private float offset;
    private float shift;
    private int numberapo;

    public FixAxisPar(List<String> parameters) {

        for (int n = 0; n < parameters.size(); n++) {
            switch (n) {
            case 0:
                this.offset = Float.parseFloat(parameters.get(n));
                break;
            case 1:
                this.shift = Float.parseFloat(parameters.get(n));
                break;
            case 2:
                this.numberapo = Integer.parseInt(parameters.get(n));
                break;

            default:
                break;
            }
        }
    }

    public final double compute(int numVal) {
        return offset + (numVal << (int) shift);
    }

    public float getOffset() {
        return offset;
    }

    public float getShift() {
        return shift;
    }

    public int getNumberapo() {
        return numberapo;
    }
}
