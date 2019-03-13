/*
 * Creation : 12 mars 2019
 */
package a2lobject;

import java.util.List;

public final class FixAxisParDist {

    private float offset;
    private float distance;
    private int numberapo;

    public FixAxisParDist(List<String> parameters) {

        for (int n = 0; n < parameters.size(); n++) {
            switch (n) {
            case 0:
                this.offset = Float.parseFloat(parameters.get(n));
                break;
            case 1:
                this.distance = Float.parseFloat(parameters.get(n));
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
        return offset + (numVal * distance);
    }

    public float getOffset() {
        return offset;
    }

    public float getDistance() {
        return distance;
    }

    public int getNumberapo() {
        return numberapo;
    }
}