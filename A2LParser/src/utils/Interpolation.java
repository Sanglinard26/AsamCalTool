/*
 * Creation : 25 juin 2018
 */
package utils;

public final class Interpolation {

    public static final double interpLinear(double x1, double x2, double y1, double y2, double xi) throws IllegalArgumentException {
        if (x1 == 0 && x2 == 0) {
            return Double.NaN;
        }
        return y1 + (xi - x1) * ((y2 - y1) / (x2 - x1));
    }

}
